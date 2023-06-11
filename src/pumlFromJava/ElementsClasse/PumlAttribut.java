package pumlFromJava.ElementsClasse;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import java.util.*;


public class PumlAttribut
{
    private Element classe;
    private List<String> champs;
    private List<VariableElement> attributs;


    public PumlAttribut(Element classe)
    {
        this.classe = classe;
        this.champs = new ArrayList<>();
        this.attributs = new ArrayList<>();
    }


    public String TraduisTypeEnUML_v2(TypeMirror typeMirror)
    {
        String res = "";

        String typeDeBase = getNomSimple( typeMirror );

        // Suppression des caractères inutiles
        typeDeBase = supprimeCaracteresInutiles(typeDeBase);

        if (typeMirror.getKind().equals(TypeKind.ARRAY))
        {

            // Traduction de typeDeBase en UML
            if( estInteger(typeDeBase ))
            {
                typeDeBase = "Integer";
            }
            else if( estReel(typeDeBase) )
            {
                typeDeBase = "Real";
            }
            else if( typeDeBase.equals("Character") || typeDeBase.equals("Char") || typeDeBase.equals("char") )
            {
                typeDeBase = "String";
            }


            res = typeDeBase+"[*]";
        }
        else if( estCollection(typeMirror) )
        {
            String typeDeBaseEnUML = "";
            if( estInteger(typeDeBase ))
            {
                typeDeBaseEnUML = "Integer";
            }
            else if( estReel(typeDeBase) )
            {
                typeDeBaseEnUML = "Real";
            }
            else if( typeDeBase.equals("Character") || typeDeBase.equals("Char") || typeDeBase.equals("char") )
            {
                typeDeBaseEnUML = "String";
            }
            else
            {
                typeDeBaseEnUML = typeDeBase;
            }

            res = typeDeBaseEnUML + "[*]";

        }

        else if (typeMirror.getKind() == TypeKind.VOID) {
            res = "void";
        }
        else if( estInteger(typeDeBase ))
        {
            res = "Integer";
        }
        else if( estReel(typeDeBase) )
        {
            res = "Real";
        }
        else if( typeDeBase.equals("Character") || typeDeBase.equals("Char") || typeDeBase.equals("char") )
        {
            res = "String";
        }
        else
        {
            res = premiereLettreEnMajuscule(getNomSimple(typeMirror));
        }

        return res;
    }

    public String getNomSimple(TypeMirror typeMirror)
    {
        // Nous n'utilisons pas la classe DeclaredType car le getSimpleName() ne fonctionne pas pour es type comme "? extens Nommable"

        // Obtenir le nom complet du type (par exemple "java.lang.String")
        String nomType = typeMirror.toString();

        // Extraire le nom simple du type (par exemple : "String")
        int lastIndex = nomType.lastIndexOf('.');
        if (lastIndex != -1)
        {
            nomType = nomType.substring(lastIndex + 1);
        }

        return nomType;
    }

    public String supprimeCaracteresInutiles(String typeDeBase)
    {
        String res = "";

        if( typeDeBase.contains("[") )
        {
            int posPremierCrochet = typeDeBase.indexOf('[');
            res = typeDeBase.substring(0,posPremierCrochet);
        }
        else if( typeDeBase.contains("<") || typeDeBase.contains(">") )
        {
            int posPremierChevron = typeDeBase.indexOf('<');
            if( posPremierChevron != -1 )
            {
                res = typeDeBase.substring(0,posPremierChevron);
            }
            else
            {
                posPremierChevron = typeDeBase.indexOf('>');
                res = typeDeBase.substring(0,posPremierChevron);
            }
        }
        else
        {
            res = typeDeBase;
        }

        return res;
    }

    public String getNomSimple(String nomType)
    {
        // Nous n'utilisons pas la classe DeclaredType car le getSimpleName() ne fonctionne pas pour es type comme "? extens Nommable"

        // Obtenir le nom complet du type (par exemple "java.lang.String")

        // Extraire le nom simple du type (par exemple : "String")
        int lastIndex = nomType.lastIndexOf('.');
        if (lastIndex != -1)
        {
            nomType = nomType.substring(lastIndex + 1);
        }

        return nomType;
    }

    public boolean estCollection(TypeMirror typeMirror)
    {
        boolean res = false;

        if (typeMirror.getKind().equals(TypeKind.DECLARED))
        {
            DeclaredType typeDeclare = (DeclaredType) typeMirror;
            String nomType = typeDeclare.asElement().toString();

            if( nomType.equals(List.class.getName())  || nomType.equals(Map.class.getName()) || nomType.equals(Set.class.getName()))      // Si le nom du type passé en paramètre est le même que celui de la classe List
            {
                res = true;
            }
        }

        return res;
    }

    // Nous faisons la distinction entre le DCA et DCC car dans le DCA il suffit de mettre le nom de l'attribut alors que dans le DCC, il faut la visibilité et le type de l'attribut en plus
    public StringBuilder ajouteChampsDCA(StringBuilder codePumlDeBase)
    {
        StringBuilder res = new StringBuilder(codePumlDeBase);

        attributs = getAttributs();

        for( VariableElement attribut : attributs )
        {
            String nomAttribut = attribut.getSimpleName().toString();

            // Si l'on a une contante d'énumération --> ajout du nom seul, sinon ajout de toutes les autres informations (visibilité etc...)
            if( attribut.getKind().equals(ElementKind.ENUM_CONSTANT) )
            {
                champs.add(nomAttribut);
            }
            else
            {

                String typeDeBase = getNomSimple(getBaseType(attribut.asType()));
                typeDeBase = supprimeCaracteresInutiles(typeDeBase);    // Car il se peut qu'il reste des crochets ou des chevrons (pour récupérer seulement le type)
                System.out.println("Attribut = "+ nomAttribut +" - Le type de base est : "+getNomSimple(typeDeBase));

                if( estPrimitif(typeDeBase) )
                {
                    champs.add(nomAttribut);
                }
            }




        }

        for( String champ : champs )
        {
            res.append("\t"+champ+"\n");
        }

        return res;
    }


    public StringBuilder ajouteChampsDCC(StringBuilder codePumlDeBase)
    {
        StringBuilder res = new StringBuilder(codePumlDeBase);

        attributs = getAttributs();

        for( VariableElement attribut : attributs )
        {
            String nomAttribut = attribut.getSimpleName().toString();

                // Si l'on a une contante d'énumération --> ajout du nom seul, sinon ajout de toutes les autres informations (visibilité etc...)
                if( attribut.getKind().equals(ElementKind.ENUM_CONSTANT) )
                {
                    champs.add(nomAttribut);
                }
                else
                {
                    String visibiliteAttribut = getVisibiliteAttribut(attribut);
                    String attributStatic = "";
                    String attributFinal = "";
                    String typeAttribut = "";

                    // Dictionnaire où nous stockerons les modifiers static et final s'ils existent
                    Map<String,String> attributStaticFinal = getAttributStaticFinal(attribut);

                    // Parcours du dictionnaire en utilisant keySet et get
                    for (String cle : attributStaticFinal.keySet())
                    {
                        if( cle.equals("static") )
                        {
                            attributStatic = attributStaticFinal.get(cle);
                        }
                        else if(cle.equals("final"))
                        {
                            attributFinal = attributStaticFinal.get(cle);
                        }
                    }

                    String typeDeBase = getNomSimple(getBaseType(attribut.asType()));
                    typeDeBase = supprimeCaracteresInutiles(typeDeBase);    // Car il se peut qu'il reste des crochets ou des chevrons (pour récupérer seulement le type)
                    System.out.println("Attribut = "+ nomAttribut +" - Le type de base est : "+getNomSimple(typeDeBase));

                    if( estPrimitif(typeDeBase) )
                    {
                        typeAttribut = TraduisTypeEnUML_v2(attribut.asType());
                        System.out.println("-- type dans UML = "+typeAttribut);
                        champs.add(visibiliteAttribut+" "+attributStatic+" "+nomAttribut+" : "+typeAttribut+" "+attributFinal);
                    }
                }




        }

        for( String champ : champs )
        {
            res.append("\t"+champ+"\n");
        }

        return res;
    }


    // Nous avons opté pour une liste car lorrqu'on appelle les méthodes isPrimitive, les types avec des lettres majuscules ne sont pas comptés comme des types primitifs
    List<String> typesPrimitifs = Arrays.asList("boolean", "byte", "short", "int", "long", "float", "double", "char", "Integer", "String","Double", "Float", "Character", "Boolean", "Short", "Char", "Long", "Byte");
    public boolean estPrimitif(String typeDeBase)
    {
        boolean res = false;

            if( typesPrimitifs.contains(typeDeBase))
            {
                res = true;
            }


        return res;
    }


    public List<VariableElement> getAttributs()
    {
        List<VariableElement> res = new ArrayList<>();

        if(!classe.getKind().equals(ElementKind.ENUM))  // Si ce n'est pas une énumération
        {
            for ( Element attribut : classe.getEnclosedElements())
            {
                // Si le champ que l'on étudie est un champ --> ajout à la liste
                if (attribut.getKind().equals(ElementKind.FIELD))
                {
                    res.add( (VariableElement)attribut);    // Cast car attribut est de type Element et nous
                }
            }
        }
        else
        {
            for ( Element attribut : classe.getEnclosedElements())
            {
                // Si le champ est une constante d'énumération --> ajout à la liste
                if (attribut.getKind().equals(ElementKind.ENUM_CONSTANT))
                {
                    res.add((VariableElement) attribut);
                }
            }

        }

        return res;
    }

    public String getVisibiliteAttribut(Element attribut)
    {

        String res = "";

        Set<Modifier> modifiers = attribut.getModifiers();
        //System.out.println("Nombre de modifiers de "+attribut.getSimpleName()+" = "+modifiers.size());

        for( Modifier m : modifiers )
        {
            if( m.equals(Modifier.PRIVATE) )
            {
                res = "-";
            }
            else if(m.equals(Modifier.PUBLIC))
            {
                res = "+";
            }
            else if( m.equals(Modifier.PROTECTED) )
            {
                res = "#";
            }
        }

        // Si l'élément n'a pas de visibilitée --> nous mettons un tilde '~'
        if( res.equals("") )
        {
            res = "~";
        }

        return res;
    }

    public Map<String,String> getAttributStaticFinal(Element attribut)
    {
        Map<String,String> res = new HashMap<>();

        boolean attributStatic = false;
        boolean attributFinal = false;

        Set<Modifier> modifiers = attribut.getModifiers();

        // Parcourt des modifiers
        for( Modifier m : modifiers )
        {
            if( m == Modifier.STATIC )
            {
                attributStatic = true;
            }
            else if( m == Modifier.FINAL )
            {
                attributFinal = true;
            }
        }

        if( attributStatic )
        {
            res.put("static","{static}");
        }
        else
        {
            res.put("static","");
        }


        if( attributFinal )
        {
            res.put("final","{ReadOnly}");
        }
        else
        {
            res.put("final","");
        }

        return res;
    }

    public String getTypeAttribut(VariableElement attribut)
    {
        String res = "";

        TypeMirror typeAttribut = attribut.asType();
        String typeAttributNomSimple = getNomSimple(typeAttribut);  // Pour enlever les '.' et garder que ce qui nous intéresse (la dernière partie)

        // Si le type de l'attribut est un réel --> nous méttons Integer, sinon juste la 1ère lettre est en majuscule
        if( estInteger(typeAttributNomSimple ))
        {
            res = "Integer";
        }
        else if( estReel(typeAttributNomSimple) )
        {
            res = "Real";
        }
        else if( typeAttributNomSimple.equals("Character") || typeAttributNomSimple.equals("Char") )
        {
            res = "String";
        }
        else
        {
            res = premiereLettreEnMajuscule(typeAttributNomSimple);
        }
        return res;
    }


    public boolean estInteger(String typeAttributNomSimple )
    {
        boolean res = false;

        List<String> nomsTypesInteger = new ArrayList<>();
        nomsTypesInteger.add("int");
        nomsTypesInteger.add("Integer");
        nomsTypesInteger.add("byte");
        nomsTypesInteger.add("short");
        nomsTypesInteger.add("long");

        // Il est possible d'utiliser le type Double au lieu de double, java fait une différence entre les 2 donc nous faisons également la différence et nous ajoutons pour chaque type sa classe
        List<String> temp = new ArrayList<>();
        for( String nomType : nomsTypesInteger )
        {
            if( !nomType.equals("int") && !nomType.equals("Integer") )
            {
                String nomClasseDuType = premiereLettreEnMajuscule(nomType);
                temp.add(nomClasseDuType);
            }
        }
        for( String nomType : temp )
        {
            nomsTypesInteger.add(nomType);
        }

        // Vérification
        if( nomsTypesInteger.contains(typeAttributNomSimple) )
        {
            res = true;
        }

        return res;
    }

    public boolean estReel(String typeAttributNomSimple )
    {
        boolean res = false;

        List<String> nomsTypesReel = new ArrayList<>();
        nomsTypesReel.add("double");
        nomsTypesReel.add("float");

        // Il est possible d'utiliser le type Double au lieu de double, java fait une différence entre les 2 donc nous faisons également la différence et nous ajoutons pour chaque type sa classe
        List<String> temp = new ArrayList<>();
        for( String nomType : nomsTypesReel )
        {
            String nomClasseDuType = premiereLettreEnMajuscule(nomType);
            temp.add(nomClasseDuType);
        }
        for( String nomType : temp )
        {
            nomsTypesReel.add(nomType);
        }


        if( nomsTypesReel.contains(typeAttributNomSimple) )
        {
            res = true;
        }

        return res;
    }

    public String premiereLettreEnMajuscule(String chaine)
    {
        String res = chaine.substring(0,1).toUpperCase() + chaine.substring(1).toLowerCase();
        return res;
    }

    private TypeMirror getBaseType(TypeMirror type) {
        if (type instanceof DeclaredType) {
            DeclaredType declaredType = (DeclaredType) type;
            List<? extends TypeMirror> typeArguments = declaredType.getTypeArguments();

            if (!typeArguments.isEmpty()) {
                TypeMirror firstTypeArgument = typeArguments.get(0);
                if (estTypeMap(type))
                {
                    // Si le type est une Map, on récupère le type de base de la valeur
                    TypeMirror valueType = typeArguments.get(1);
                    return getBaseType(valueType);
                } else {
                    return getBaseType(firstTypeArgument);  // Appel récursif pour obtenir le type de base
                }
            }
        }

        return type;
    }

    public boolean estTypeMap( TypeMirror typeMirror )
    {
        boolean res = false;

        if (typeMirror.getKind().equals(TypeKind.DECLARED))
        {
            DeclaredType typeDeclare = (DeclaredType) typeMirror;
            String nomType = typeDeclare.asElement().toString();

            if( nomType.equals(Map.class.getName() ))      // Si le nom du type passé en paramètre est le même que celui de la classe Map
            {
                res = true;
            }
        }

        return res;



    }





}
