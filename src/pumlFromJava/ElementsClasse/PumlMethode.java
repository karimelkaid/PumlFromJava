package pumlFromJava.ElementsClasse;

import javax.lang.model.element.*;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.util.*;

public class  PumlMethode
{

    Element classe;
    String nomClasse;
    List<ExecutableElement> methodes;

    public PumlMethode(Element classe)
    {
        this.classe = classe;
        this.nomClasse = classe.getSimpleName().toString();
        this.methodes = new ArrayList<>();
    }

    public String TraduisTypeEnUML(TypeMirror typeMirror)
    {
        String res = "";

        String typeMethodeNomSimple = getNomSimple(typeMirror);

        if (typeMirror.getKind() == TypeKind.VOID) {
            res = "void";
        }
        else if( estInteger(typeMethodeNomSimple ))
        {
            res = "Integer";
        }
        else if( estReel(typeMethodeNomSimple) )
        {
            res = "Real";
        }
        else if( typeMethodeNomSimple.equals("Character") || typeMethodeNomSimple.equals("Char") || typeMethodeNomSimple.equals("char") )
        {
            res = "String";
        }
        else if (typeMirror.getKind().equals(TypeKind.ARRAY))
        {
            ArrayType arrayType = (ArrayType) typeMirror;
            TypeMirror typeElementDansTableau = arrayType.getComponentType();
            String nomTypeElementDansTableau = getNomSimple(typeElementDansTableau);
            res = nomTypeElementDansTableau + "[]";
        }
        else if (estCollection(typeMirror))
        {
            DeclaredType declaredType = (DeclaredType) typeMirror;
            List<? extends TypeMirror> typeArguments = declaredType.getTypeArguments();
            if (!typeArguments.isEmpty())
            {
                TypeMirror typeElement = typeArguments.get(0);
                String nomTypeElement = getNomSimple(typeElement);
                res = nomTypeElement + "[*]";
            }
        }
        else
        {
            res = premiereLettreEnMajuscule(getNomSimple(typeMirror));
        }

        return res;
    }

    public String TraduisTypeEnUML_v2(TypeMirror typeMirror, String typeDeBase)
    {
        String res = "";

        if (typeMirror.getKind().equals(TypeKind.ARRAY))
        {
            // Nous récupérons uniquement le type dedans SANS les crochets
            int posPremierCrochet = typeDeBase.indexOf('[');
            String nomTypeDansTableau = typeDeBase.substring(0,posPremierCrochet);

            // Traduction de typeDeBase en UML
            if( estInteger(nomTypeDansTableau ))
            {
                nomTypeDansTableau = "Integer";
            }
            else if( estReel(typeDeBase) )
            {
                nomTypeDansTableau = "Real";
            }
            else if( nomTypeDansTableau.equals("Character") || nomTypeDansTableau.equals("Char") || nomTypeDansTableau.equals("char") )
            {
                nomTypeDansTableau = "String";
            }


            res = nomTypeDansTableau+"[*]";
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

    public String TraduisTypeEnUML_v2(TypeMirror typeMirror)
    {
        String res = "";

        String typeDeBase = getNomSimple( getBaseType(typeMirror) );

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

    public boolean estCollection(TypeMirror typeMirror)
    {
        boolean res = false;

        // Les Collections sont dans le package java.util
        if (typeMirror.getKind().equals(TypeKind.DECLARED))
        {
            DeclaredType typeDeclare = (DeclaredType) typeMirror;
            String nomType = typeDeclare.asElement().toString();

            String nomPackageDeLaCollection = "";
            int posFinDuNomPackageDeLaCollection = nomType.lastIndexOf('.');

            // Si le point a été trouvé, nous récupérons tout ce qu'il y a avant
            if( posFinDuNomPackageDeLaCollection != -1)
            {
                nomPackageDeLaCollection = nomType.substring(0,posFinDuNomPackageDeLaCollection);
            }

            if( nomPackageDeLaCollection.equals("java.util") )
            {
                res = true;
            }
        }

        return res;
    }


    public String getNomSimple(TypeMirror typeMirror) {
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



    public StringBuilder ajouteMethodes(StringBuilder codePumlDeBase)
    {
        StringBuilder codePumlMethode = new StringBuilder(codePumlDeBase);

        methodes = getMethodes();

        for (ExecutableElement methode : methodes)
        {
            String visibilite = getVisibilite(methode);
            codePumlMethode.append(visibilite + " ");

            String static_ = getStatic(methode);
            codePumlMethode.append(static_+" ");

            String nomMethode = methode.getSimpleName().toString();
            codePumlMethode.append(nomMethode);

            //if( nomMethode.equals("aListOfDoubleListFunction") )
            //{
                String parametres = getParametres(methode);
                codePumlMethode.append(parametres);


                /*String typeDeBase = getNomSimple(getBaseType(methode.asType()));

                // Suppression des caractères inutiles
                typeDeBase = supprimeCaracteresInutiles(typeDeBase);

                String typeDeRetour = TraduisTypeEnUML_v2(methode.getReturnType(), typeDeBase);*/
            String typeDeRetour = TraduisTypeEnUML_v2(methode.getReturnType());

                System.out.println("Methode "+nomMethode+" - type de retour = "+typeDeRetour);

                // Nous ajoutons le type de retour uniquement s'il n'est pas void
                if( !typeDeRetour.equals("void") )
                {
                    codePumlMethode.append(" : ").append(typeDeRetour);
                }

                codePumlMethode.append("\n");
            //}




        }

        return codePumlMethode;
    }

    public String supprimeCaracteresInutiles(String typeDeBase)
    {
        String res = typeDeBase;

        boolean caracetere_a_enlever = true;
        while( caracetere_a_enlever )
        {
            caracetere_a_enlever = false;

            if( res.contains("[") )
            {
                int posPremierCrochet = typeDeBase.indexOf('[');
                res = typeDeBase.substring(0,posPremierCrochet);
            }
            else if( res.contains("<") || res.contains(">") )
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

            if( res.contains("<") || res.contains(">") || res.contains("[") || res.contains("]") )
            {
                caracetere_a_enlever = true;
            }
        }


        return res;
    }


    public String getVisibilite(ExecutableElement methode) {
        String res = "";

        Set<Modifier> modifiers = methode.getModifiers();

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

    public String getStatic(ExecutableElement methode)
    {
        String res = "";

        Set<Modifier> modifiers = methode.getModifiers();

        // Parcourt des modifiers
        for( Modifier m : modifiers )
        {
            if( m == Modifier.STATIC )
            {
                res = "{static}";
            }
        }

        return res;
    }


    public String getParametres(ExecutableElement methode)
    {
        StringBuilder parametres = new StringBuilder();
        parametres.append("(");
        for (VariableElement parameter : methode.getParameters())
        {

            String typeParametre = TraduisTypeEnUML_v2(parameter.asType());
            String nomParametre = parameter.getSimpleName().toString();
            parametres.append(nomParametre).append(":").append(typeParametre).append(", ");



            /*String nomParametre = parameter.getSimpleName().toString();
            String typeProfondParametre = TraduisTypeEnUML(parameter.asType());
            parametres.append(nomParametre).append(":").append(typeProfondParametre).append(", ");*/


        }

        if (methode.getParameters().size() > 0) {
            parametres.setLength(parametres.length() - 2); // Supprimer la virgule et l'espace en trop
        }

        parametres.append(")");
        return parametres.toString();
    }

    public List<ExecutableElement> getMethodes()
    {
        List<ExecutableElement> res = new ArrayList<>();

        for( Element e : classe.getEnclosedElements() )
        {
            if( e.getKind().equals(ElementKind.METHOD) )
            {
                res.add( (ExecutableElement)e );
            }
        }

        return res;
    }

}
