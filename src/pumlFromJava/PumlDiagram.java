package pumlFromJava;

import com.sun.source.util.DocTrees;
import jdk.javadoc.doclet.DocletEnvironment;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static java.util.Arrays.asList;

public class PumlDiagram
{
    private String repertoireDestination;
    private String nomFichierACree;
    private DocletEnvironment environment;

    public PumlDiagram( String repertoireDestination, String nomFichierACree, DocletEnvironment environment )
    {
        if( repertoireDestination != null )
        {
            this.repertoireDestination = repertoireDestination;
        }
        else
        {
            this.repertoireDestination = ".";
        }

        this.nomFichierACree = nomFichierACree;
        this.environment = environment;
    }

    public void generePuml()
    {
        List<Element> Classes = recupClasses(environment);

        try
        {
            // Création du fichier puml au bon emplacement
            String filePath = repertoireDestination +"/"+ nomFichierACree;
            FileWriter fw = new FileWriter(filePath);

            // Remplissage du code PUML à mettre plus tard dans le fichier PUML
            String chPuml = ecrisCodePuml(Classes);

            // Écriture dans le fichier PUML et fermeture du flux
            fw.write(chPuml);
            fw.flush();
            fw.close();
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }

    }

    public List<Element> recupClasses(DocletEnvironment environment)
    {
        List<Element> res = new ArrayList<>();

        // Récupération du/des élément(s) spécifié(s)
        Set<? extends Element> specifiedElements = environment.getSpecifiedElements();

        // Parcourt du/des élément(s) spécifié(s)
        for (Element element : specifiedElements)
        {
            // Si l'utilisateur n'a pas spécifié de nom pour le PUML à créer, alors on lui attribue le premier élément sélectionné
            if(nomFichierACree == null)
            {
                nomFichierACree = element.getSimpleName().toString() + ".puml";
            }

            // Parcourt des classes du package
            for (Element classe : element.getEnclosedElements())
            {
                res.add(classe);    // Ajout du nom de la classe actuelle à la liste
            }
        }
        return res;
    }

    public String ecrisCodePuml(List<Element> classes)
    {
        StringBuilder res = new StringBuilder("@startuml\n\n");

        res = ajoutePackage(res,classes);
        for( Element classe : classes )
        {
            res = ajouteTypeElement(res, classe);
            res = ajouteChamps(res, classe);
            res.append("}\n");

            res = ajouteAgregations(res, classe);
            res.append("\n\n");
        }
        res.append("}\n@enduml\n");

        return res.toString();
    }

    public String getStereotype(Element classe)
    {
        String res;

        if( classe.getKind().equals(ElementKind.ENUM) )
        {
            res = "enum";
        }
        else if( classe.getKind().equals(ElementKind.INTERFACE ))
        {
            res = "interface";
        }
        else
        {
            res = "";
        }

        return res;
    }

    public List<String> getPrimitiveFieldsNamesAndConstEnum(Element classe)
    {
        List<String> variableNames = new ArrayList<>();

        if(classe.getKind().equals(ElementKind.ENUM))
        {
            for ( Element variable_local : classe.getEnclosedElements())
            {
                // Si le champ que l'on étudie est un champ ET est de type primitif --> ajout à la liste
                if (variable_local.getKind().equals(ElementKind.ENUM_CONSTANT))
                {
                    variableNames.add(variable_local.getSimpleName().toString());
                }
            }
        }
        else
        {
            for ( Element variable_local : classe.getEnclosedElements())
            {
                // Si le champ que l'on étudie est un champ ET est de type primitif --> ajout à la liste
                if (variable_local.getKind().equals(ElementKind.FIELD) && variable_local.asType().getKind().isPrimitive())
                {
                    variableNames.add(variable_local.getSimpleName().toString());
                }
            }
        }


        return variableNames;
    }

    public StringBuilder ajouteTypeElement(StringBuilder codePumlDeBase, Element classe)
    {
        StringBuilder res = new StringBuilder(codePumlDeBase);

        String stereotype = getStereotype(classe);      // Récupération du stéréotype (s'il y en a un)

        if( stereotype.equals("") )     // S'il n'y a aucun stéréotype --> c'est une classe normale
        {
            res.append( "class "+classe.getSimpleName().toString() );

            //  Récupération de la super classe
            String nomClasse = getSuperClassName(classe);
            if( nomClasse != null && !nomClasse.equals("Object") )    // Si la classe possède une super classe ET ce n'est pas la classe Object--> ajout de l'héritage
            {
                res.append(" extends "+nomClasse);
            }

            // Ajout des interfaces implémentées
            List<String> nomInterfaces = getInterfaces(classe);
            if( nomInterfaces.size() != 0 )
            {
                res.append( " implements "+nomInterfaces.get(0) );

                if( (long) nomInterfaces.size() > 1 )
                {
                    for(int i = 1; i< nomInterfaces.size(); i++)
                    {
                        String nomInterface = nomInterfaces.get(i);
                        res.append(", "+nomInterface);
                    }
                }
            }

            res.append("{\n");

        }
        else if(stereotype.equals("interface"))   // Sinon, l'élément est soit une énumération, soit une interface
        {
            res.append(stereotype+" "+classe.getSimpleName().toString() + " <<"+stereotype+">>{\n");
        }
        else    // Si c'est une enumeration
        {
            res.append(stereotype+" "+classe.getSimpleName().toString() + " <<"+stereotype+">>{\n");
        }

        //VariableElement v;
        return res;
    }

    public StringBuilder ajouteChamps(StringBuilder codePumlDeBase, Element classe)
    {
        StringBuilder res = new StringBuilder(codePumlDeBase);

        List<String> champs = getPrimitiveFieldsNamesAndConstEnum(classe);      // Récupération des champs (primitifs et constantes d'une énumération)

        for( String champ : champs )
        {
            res.append("\t"+champ+"\n");
        }

        return res;
    }

    public String getSuperClassName(Element classe)
    {
        TypeElement classeType = (TypeElement)classe;   // Récupération du type de la super classe
        TypeMirror superClassType = classeType.getSuperclass();

        String superSimpleClassName = ((DeclaredType) superClassType).asElement().getSimpleName().toString();

        return superSimpleClassName;
    }

    public List<String> getInterfaces(Element classe)
    {
        List<String> res = new ArrayList<>();
        List<? extends TypeMirror> types;

        TypeElement classeType = (TypeElement)classe;
        types = classeType.getInterfaces();

        for( TypeMirror type : types )
        {
            res.add( ((DeclaredType)type).asElement().getSimpleName().toString() );
        }

        return res;
    }

    public StringBuilder ajouteAgregations(StringBuilder codePumlDeBase, Element classe)
    {
        StringBuilder res = new StringBuilder(codePumlDeBase);

        List<String> agregationsExistantes = new ArrayList<>();   // Nous stockons toutes les agrégations dans cette liste afin de ne pas avoir de répétitions (par exemple : Boisson -- Substantif et Substantif -- Boisson)
        List<String> typeNonVoulu = Arrays.asList("String", "Set", "List");     // Les String, Set et List ne sont pas considérés comme des types primitifs, mais nous ne les voulons pas dans l'UML

        // Parcourt des champs de la classe
        for( Element champOuConstructeurOuMethode : classe.getEnclosedElements() )
        {
            // Si l'élément est un champ ET ne possède pas un type primitif
            if( champOuConstructeurOuMethode.getKind().equals(ElementKind.FIELD)  )
            {
                TypeMirror typeChamp = champOuConstructeurOuMethode.asType();
                if( !typeChamp.getKind().isPrimitive() )
                {
                    // Nous récupérons le type
                    String typeChampString = ((DeclaredType)typeChamp).asElement().getSimpleName().toString();
                    if( !typeNonVoulu.contains(typeChampString)  )
                    {
                        String liaison = classe.getSimpleName()+" -- "+ typeChampString;

                        // Nous ajoutons la liaison uniquement si celle-ci n'est pas présente (sinon il y aura des doublons)
                        if( !liaisonExistante(agregationsExistantes, liaison) )
                        {
                            res.append(  liaison + "\n" );      // Ajout de la liaison dans le code
                            agregationsExistantes.add(liaison);   // Ajout de la liaison à la liste pour ne pas avoir de doublon(s)
                        }
                    }
                }
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

    public StringBuilder ajoutePackage(StringBuilder codePumlDeBase, List<Element> classes)
    {
        String nomPackage = classes.get(0).getEnclosingElement().getSimpleName().toString();    // Récupération du package (étant donné que nous étudions uniquement

        StringBuilder res = new StringBuilder(codePumlDeBase);
        res.append("package "+nomPackage+"{\n\t");
        return res;
    }

}
