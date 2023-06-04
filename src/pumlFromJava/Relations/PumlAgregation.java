package pumlFromJava.Relations;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
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
    List<String> classesPackage;


    public PumlAgregation(Element classe, List<Element> lesClasses)
    {
        this.classe = classe;
        this.agregationsExistantes = new ArrayList<>();
        this.typeNonVoulu = Arrays.asList("String", "String[]",  "Set", "List");     // Les String, Set et List ne sont pas considérés comme des types primitifs, mais nous ne les voulons pas dans l'UML
        this.classesPackage = new ArrayList<>();

        for( Element classe_ : lesClasses )
        {
            classesPackage.add( classe_.getSimpleName().toString() );
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


    // Faire une classe liaison avec classeSrc, classeDst, une classe "Role" qui contient un nom, une visibilité et une multiplicité
    public StringBuilder ajouteAgregations(StringBuilder codePumlDeBase)
    {
        StringBuilder res = new StringBuilder(codePumlDeBase);

        List<String> agregationsExistantes = new ArrayList<>();   // Nous stockons toutes les agrégations dans cette liste afin de ne pas avoir de répétitions (par exemple : Boisson -- Substantif et Substantif -- Boisson)
        List<String> typeNonVoulu = Arrays.asList("String");     // Les String ne sont pas considérés comme des types primitifs, mais nous ne les voulons pas dans l'UML

        // Parcourt des champs de la classe
        for( Element champOuConstructeurOuMethode : classe.getEnclosedElements() )
        {
            // Si l'élément est un champ ET ne possède pas un type primitif
            if( champOuConstructeurOuMethode.getKind().equals(ElementKind.FIELD)  )
            {
                TypeMirror typeChamp = champOuConstructeurOuMethode.asType();
                if( !typeChamp.getKind().isPrimitive() && !typeChamp.equals("String"))
                {
                    String typeChampSimple = "";

                    String typeContenuDansTab = "";

                    String typeContenuDansCollectionSimple = "";

                    String[] typesDansMap = new String[2];
                    String typeCle = "";
                    String typeValeur = "";

                    // Si c'est un tableau --> nous récupérons ce qu'il y a dedans, nous faisons car sinon nous devrions ajouter dans la liste "typeNonVoulu" tous les tableaux possible (HorsLaLoi[] etc...), hors cela n'est pas optimisé et le programme ne fonctionnerait pas pour tous les packages
                    if( estTableau(champOuConstructeurOuMethode) )
                    {
                        ArrayType arrayType = (ArrayType) typeChamp;    // Cast pour accéder aux méthodes de la classse ArrayType
                        typeContenuDansTab = getNomSimple(arrayType.getComponentType());
                    }
                    else if( estCollection(champOuConstructeurOuMethode) )
                    {
                        String nomCollection = getNomSimplifie_v2(getCollection(typeChamp));
                        if( nomCollection.equals("Map") )
                        {
                            typesDansMap = getTypesDansMap(champOuConstructeurOuMethode);
                            typeCle = typesDansMap[0];
                            typeValeur = typesDansMap[1];
                        }
                        else    // Si c'est une List, Set etc... (toutes les Collections contenant un seul type d'élément)
                        {
                            typeContenuDansCollectionSimple = getTypeDansCollectionSimple(champOuConstructeurOuMethode);
                        }
                    }
                    else    // Si le type n'est ni un tableau ni une Collection
                    {
                        typeChampSimple = ((DeclaredType)typeChamp).asElement().getSimpleName().toString();
                    }


                    if( !typeChampSimple.equals("") && !typeChampSimple.equals("String") )
                    {
                        /*PumlLiaison pumlLiaison = new PumlLiaison(classe, typeChampSimple);
                        String liaison = pumlLiaison.getLiaison();*/

                        String nomRole = champOuConstructeurOuMethode.getSimpleName().toString();
                        String visibiliteRole = getVisibiliteAttribut(champOuConstructeurOuMethode);
                        String multiplicite = "1";
                        String role = "\""+multiplicite+"\\n"+visibiliteRole+nomRole+"\"";


                        String liaison = classe.getSimpleName()+" -- "+ role + " "+ typeChampSimple + " : >";

                        // Nous ajoutons la liaison uniquement si celle-ci n'est pas présente (sinon il y aura des doublons)
                        if( !liaisonExistante(agregationsExistantes, liaison) )
                        {
                            res.append(  liaison + "\n" );      // Ajout de la liaison dans le code
                            agregationsExistantes.add(liaison);   // Ajout de la liaison à la liste pour ne pas avoir de doublon(s)
                        }
                    }
                    else if( !typeContenuDansTab.equals("") && !typeContenuDansTab.equals("String") )
                    {
                        /*String nomRole = champOuConstructeurOuMethode.getSimpleName().toString();
                        String visibiliteRole = getVisibiliteAttribut(champOuConstructeurOuMethode);
                        String multiplicite = "0..";


                        String role = "\""+multiplicite+"\\n"+visibiliteRole+nomRole;*/

                        String liaison = classe.getSimpleName()+" -- "+ typeContenuDansTab + " : >";

                        // Nous ajoutons la liaison uniquement si celle-ci n'est pas présente (sinon il y aura des doublons)
                        if( !liaisonExistante(agregationsExistantes, liaison) )
                        {
                            res.append(  liaison + "\n" );      // Ajout de la liaison dans le code
                            agregationsExistantes.add(liaison);   // Ajout de la liaison à la liste pour ne pas avoir de doublon(s)
                        }
                    }
                    else if( !typeContenuDansCollectionSimple.equals("") && !typeContenuDansCollectionSimple.equals("String") )
                    {
                        String nomRole = champOuConstructeurOuMethode.getSimpleName().toString();
                        String visibiliteRole = getVisibiliteAttribut(champOuConstructeurOuMethode);
                        String multiplicite = "*";
                        String role = "\""+multiplicite+"\\n"+visibiliteRole+nomRole+"\"";


                        String liaison = classe.getSimpleName()+" -- "+ role + " "+ typeContenuDansCollectionSimple + " : >";

                        // Nous ajoutons la liaison uniquement si celle-ci n'est pas présente (sinon il y aura des doublons)
                        if( !liaisonExistante(agregationsExistantes, liaison) )
                        {
                            res.append(  liaison + "\n" );      // Ajout de la liaison dans le code
                            agregationsExistantes.add(liaison);   // Ajout de la liaison à la liste pour ne pas avoir de doublon(s)
                        }

                    }
                    // Sinon si la chaine typeCle n'est pas vide (donc le type de l'attribut est une Map)
                    else if( !typeCle.equals("") )
                    {
                        // Il peut y avoir 2 liaisons car dans une Collection Map il y a 2 types
                        String liaison1 = "";
                        String liaison2 = "";

                        String nomRole = champOuConstructeurOuMethode.getSimpleName().toString();
                        String visibiliteRole = getVisibiliteAttribut(champOuConstructeurOuMethode);
                        String multiplicite = "*";
                        String role = "\""+multiplicite+"\\n"+visibiliteRole+nomRole+"\"";

                        if( !typeCle.equals("String") )
                        {
                            liaison1 = classe.getSimpleName()+" -- "+ role + " "+ typeCle + " : >";

                            // Nous ajoutons la liaison uniquement si celle-ci n'est pas présente (sinon il y aura des doublons)
                            if( !liaisonExistante(agregationsExistantes, liaison1) )
                            {
                                res.append(  liaison1 + "\n" );      // Ajout de la liaison dans le code
                                agregationsExistantes.add(liaison1);   // Ajout de la liaison à la liste pour ne pas avoir de doublon(s)
                            }
                        }

                        if( !typeCle.equals("String") )
                        {
                            liaison2 = classe.getSimpleName()+" -- "+role+" "+ typeValeur + " : >";

                            // Nous ajoutons la liaison uniquement si celle-ci n'est pas présente (sinon il y aura des doublons)
                            if( !liaisonExistante(agregationsExistantes, liaison2) )
                            {
                                res.append(  liaison2 + "\n" );      // Ajout de la liaison dans le code
                                agregationsExistantes.add(liaison2);   // Ajout de la liaison à la liste pour ne pas avoir de doublon(s)
                            }

                        }



                    }
                }
            }
        }

        return res;

    }

    // Toutes les collections qui ne contiennent que un type (Comme une List ou un Set)
    public String getTypeDansCollectionSimple(Element attribut)
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

    public String[] getTypesDansMap(Element attribut)
    {
        String[] res = new String[2];
        int posTab = 0;

        String typesDansMap = "";

        TypeMirror typeAttribut = attribut.asType();
        String typeEnString = typeAttribut.toString();

        int posPremierChevron = typeEnString.indexOf('<');
        int posDeuxiemeChevron = typeEnString.lastIndexOf('>');
        for(int i=posPremierChevron+1; i<=posDeuxiemeChevron; i++)
        {
            if( typeEnString.charAt(i) == ',' || i == posDeuxiemeChevron)     // Sauvegrde du mot si l'on croise une virgule ou alors on est au dernier caractère
            {
                // il est possible que le résultat soit de type nomPackage.nomClasse donc nous utilisons la méthode getNomSimplifie_v2 pour récupérer uniquement le nom de la classe
                typesDansMap = getNomSimplifie_v2(typesDansMap);   // J'utilise le v2 pck il prend un String en paramètre et c'est ce dont j'ai besoin

                res[posTab] = typesDansMap;
                posTab++;
                typesDansMap = "";
            }
            else
            {
                //res.append( typeEnString.charAt(i) );
                typesDansMap += typeEnString.charAt(i);
            }
        }




        return res;
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

    public boolean estCollection(Element attribut)
    {
        boolean res = false;

        boolean trouve = false;

        // Récupération du type de l'attribut et s'il y a un chevron '<' --> c'est une Collection
        TypeMirror typeAttribut = attribut.asType();
        String typeAttributEnString = typeAttribut.toString();
        for(int i=0; i<typeAttributEnString.length() && trouve == false; i++)
        {
            if( typeAttributEnString.charAt(i) == '<' )
            {
                trouve = true;
                res = true;
            }
        }

        return res;
    }



}
