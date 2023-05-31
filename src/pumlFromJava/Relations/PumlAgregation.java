package pumlFromJava.Relations;

import jdk.javadoc.doclet.DocletEnvironment;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class PumlAgregation
{
    Element classe;
    List<String> agregationsExistantes;        // Nous stockons toutes les agrégations dans cette liste afin de ne pas avoir de répétitions (par exemple : Boisson -- Substantif et Substantif -- Boisson)
    List<String> typeNonVoulu;
    List<String> typeVoulu;


    public PumlAgregation(Element classe, List<Element> lesClasses)
    {
        this.classe = classe;
        this.agregationsExistantes = new ArrayList<>();
        this.typeNonVoulu = Arrays.asList("String", "String[]",  "Set", "List");     // Les String, Set et List ne sont pas considérés comme des types primitifs, mais nous ne les voulons pas dans l'UML
        this.typeVoulu = new ArrayList<>();

        for( Element classe_ : lesClasses )
        {
            typeVoulu.add( classe_.getSimpleName().toString() );
        }
    }

    public String getTypeSimplifie(TypeMirror typeMirror)
    {
        String res = "";

        if (typeMirror.getKind() == TypeKind.VOID) {
            res = "void";
        }
        else if (typeMirror.getKind().isPrimitive())
        {
            // Si le type de l'attribut est un réel --> nous mettons Integer, sinon juste la 1ère lettre est en majuscule
            if( estReel(typeMirror.toString()) )
            {
                res = "Integer";
            }
            else
            {
                res = premiereLettreEnMajuscule(typeMirror.toString());
            }

        }

        else if (typeMirror.getKind().equals(TypeKind.ARRAY))
        {
            ArrayType arrayType = (ArrayType) typeMirror;
            TypeMirror typeElementDansTableau = arrayType.getComponentType();
            String nomTypeElementDansTableau = getNomSimple(typeElementDansTableau);
            res = nomTypeElementDansTableau + "[]";
        }
        else if (estListe(typeMirror))
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
            res = getNomSimple(typeMirror);
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
    public String getNomSimplifie_v2(String typeEnString)
    {
        String res = "";

        // Extraire le nom simple du type (par exemple : "String")
        int lastIndex = typeEnString.lastIndexOf('.');
        if (lastIndex != -1)
        {
            res = typeEnString.substring(lastIndex + 1);
        }

        return res;

    }



    public boolean estListe(TypeMirror typeMirror) {

        boolean res = false;

        if (typeMirror.getKind().equals(TypeKind.DECLARED))
        {
            DeclaredType typeDeclare = (DeclaredType) typeMirror;
            String nomType = typeDeclare.asElement().toString();

            if( nomType.equals(List.class.getName() ))      // Si le nom du type passé en paramètre est le même que celui de la classe List
            {
                res = true;
            }
        }

        return res;
    }


    public boolean estReel( String type )
    {
        boolean res = false;

        if( type.equals("int") || type.equals("double") || type.equals("byte") || type.equals("double") || type.equals("short") || type.equals("char") || type.equals("long") || type.equals("float"))
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


    public StringBuilder ajouteAgregations(StringBuilder codePumlDeBase)
    {
        StringBuilder res = new StringBuilder(codePumlDeBase);

        // Parcourt des champs de la classe
        for( Element champOuConstructeurOuMethode : classe.getEnclosedElements() )
        {
            // Si l'élément est un champ, ne possède pas un type primitif et la nouvelle liaison n'existe pas --> ajout de l'agrégation
            if( champOuConstructeurOuMethode.getKind().equals(ElementKind.FIELD)  )
            {
                TypeMirror typeChamp = champOuConstructeurOuMethode.asType();   // Récupération du type
                if( !typeChamp.getKind().isPrimitive()  )
                {
                    String multiplicite = "";
                    // Nous récupérons le type
                    String typeChampString = getTypeSimplifie(typeChamp);
                    if( !typeChampString.equals("String") )
                    //if( !typeNonVoulu.contains(typeChampString)  )
                    {

                        if( typeVoulu.contains(typeChampString) )
                        {

                            multiplicite = "1";
                        }
                        else if( estTableau(champOuConstructeurOuMethode) )
                        {
                            // Si je veux ne pas avoir à utiliser la fonction getNomSimplifie_v2 à chaque fois --> dans la liste typeVoulu qui contient les classes du package je mets les classes cache sans utiliser la fonction getNomSimplifie_v2
                            /*String typeDansTab = getNomSimplifie_v2(((ArrayType)typeChamp).getComponentType().toString());
                            if( typeVoulu.contains( typeDansTab ))
                            {
                                int tailleTab = Array.getLength(  champOuConstructeurOuMethode);
                                multiplicite = "0.."+tailleTab;
                            }*/

                            Element tableauElement = champOuConstructeurOuMethode;
                            if (tableauElement instanceof VariableElement)
                            {
                                VariableElement variableElement = (VariableElement) tableauElement;
                                TypeMirror typeAttribut = variableElement.asType();
                                if (typeAttribut instanceof ArrayType)
                                {
                                    ArrayType arrayType = (ArrayType) typeAttribut;
                                    TypeMirror componentType = arrayType.getComponentType();
                                    // Maintenant, vous pouvez utiliser le type de tableau et le composant
                                    // pour effectuer des opérations supplémentaires
                                    // ...
                                    if( typeVoulu.contains( getNomSimplifie_v2(componentType.toString()) ))
                                    {
                                        // Vérifiez si le champ est initialisé et est un tableau
                                        Object fieldValue = new Array[]{}; // Remplacez null par la valeur réelle du champ


                                        if (fieldValue.getClass().isArray())
                                        {
                                            if (componentType instanceof DeclaredType)
                                            {
                                                DeclaredType declaredComponentType = (DeclaredType) componentType;
                                                String componentTypeName = declaredComponentType.toString();

                                                // Utilisez le nom du type de composant pour effectuer des opérations supplémentaires
                                                int tailleTab = Array.getLength(fieldValue);
                                                // ...
                                            }
                                        }

                                    }


                                }
                            }

                        }
                        else if( estListe(champOuConstructeurOuMethode) )
                        {
                            String typeDansCollection = getTypeDansListe(champOuConstructeurOuMethode);

                            if( typeVoulu.contains(typeDansCollection) )
                            {
                                multiplicite = "*";
                            }

                        }
                        else    // Si c'est une Map
                        {
                            /*List<String> typesDansCollection = getTypeDansMap(champOuConstructeurOuMethode);

                            // Si la clé ou la valeur de la map est une des classes du package
                            for( String typeDansCollection : typesDansCollection )
                            {
                                if( typeVoulu.contains(typeDansCollection) )
                                {
                                    multiplicite = "*";
                                }
                            }*/

                        }

                        System.out.println("multiplicité de l'attribut '"+champOuConstructeurOuMethode.getSimpleName().toString()+"' : "+multiplicite);


                        String liaison = classe.getSimpleName()+" -- "+ typeChampString;

                        // Nous ajoutons la liaison uniquement si celle-ci n'est pas présente (sinon il y aura des doublons)
                        if( !liaisonExistante(agregationsExistantes, liaison) )
                        {
                            res.append(  liaison + "\n" );      // Ajout de la liaison dans le code
                            agregationsExistantes.add(liaison);   // Ajout de la liaison à la liste pour ne pas avoir de doublon(s)
                        }
                    }
                    /*else    // Sinon si c'est un type à ne pas relier avec la classe actuelle (Pour traiter les rôles)
                    {
                        // nomRôle, visibilité et multiplicité de la classe actuelle
                        String nomRole = champOuConstructeurOuMethode.getSimpleName().toString();
                        String visibilite = getVisibiliteAttribut(champOuConstructeurOuMethode);
                        String multiplicite = getMultiplicite(champOuConstructeurOuMethode);
                    }*/

                }
            }
        }

        return res;
    }

    public String getTypeDansListe(Element attribut)
    {
        String res = "";

        TypeMirror typeAttribut = attribut.asType();
        String typeEnString = typeAttribut.toString();

        int posPremierChevron = typeEnString.indexOf('<');
        int posDeuxiemeChevron = typeEnString.lastIndexOf('>');
        for(int i=posPremierChevron+1; i<posDeuxiemeChevron; i++)
        {
            //res.append( typeEnString.charAt(i) );
            res += typeEnString.charAt(i);
        }

        // Arrivé ici, nous avons le type entre les 2 chevrons mais il est possible que le résultat soit de type nomPackage.nomClasse
        res = getNomSimplifie_v2(res.toString());   // J'utilise le v2 pck il prend un String en paramètre et c'est ce dont j'ai besoin

        return res.toString();
    }

    public boolean liaisonExistante(List<String> agregationsExistantes, String liaison)
    {
        boolean res = false;

        String liaisonInverse = inverseLiaison(liaison);
        if( agregationsExistantes.contains(liaison) || agregationsExistantes.contains(liaisonInverse) )
        {
            res = true;
        }

        return res;
    }

    public String inverseLiaison(String liaison)
    {
        String[] nomsClasses = liaison.split(" -- ");       // Récupération des noms de classes dans un tableau
        StringBuilder laisonInverse = new StringBuilder();

        laisonInverse.append(nomsClasses[1] + " -- " + nomsClasses[0]);     // Inversement

        return laisonInverse.toString();
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
                res = "~";
            }
        }

        return res;
    }

    public String getMultiplicite(Element attribut)
    {
        String res = "";

        /*- Si c cache un atttribut de type Classe --> nomRôle = nomAttribut, visibilité = visibilité de l'attribut; multiplicité = 1 --> c juste la multiplicité qui change
            - Si c une List --> nomRôle = nomAttribut, visibilité = visibilité de l'attribut; multiplicité = *
            - Si c un tableau --> nomRôle = nomAttribut, visibilité = visibilité de l'attribut; multiplicité = 0..tailleTab
        */

        TypeMirror typeAttribut = attribut.asType();
        //String
        if( estListe(attribut) )
        {
            res = "*";
        }
        else if( estTableau(attribut) )
        {
            int tailleTableau = Array.getLength(attribut);
            res = "0.."+tailleTableau;
        }
        else
        {
            res = "1";
        }

        return res;
    }

    public boolean estTableau(Element attribut)
    {
        boolean res = false;

        TypeMirror typeAttribut = attribut.asType();
        String typeCollection = getNomSimple(typeAttribut);
        if( typeAttribut.getKind().equals(TypeKind.ARRAY))
        {
            res = true;
        }
        return res;
    }

    public boolean estListe(Element attribut)
    {
        boolean res = false;

        TypeMirror typeAttribut = attribut.asType();

        if (typeAttribut.getKind().equals(TypeKind.DECLARED))
        {
            DeclaredType typeDeclare = (DeclaredType) typeAttribut;
            String nomType = typeDeclare.asElement().toString();

            if( nomType.equals(List.class.getName() ))      // Si le nom du type passé en paramètre est le même que celui de la classe List
            {
                res = true;
            }
        }

        return res;

    }

    public String getCollection( TypeMirror type )
    {
        StringBuilder res = new StringBuilder("");

        String typeEnString = type.toString();
        int posPremierChevron = typeEnString.indexOf("<");

        for(int i=0; i<posPremierChevron; i++)
        {
            res.append( typeEnString.charAt(i) );
        }

        return res.toString();
    }



}
