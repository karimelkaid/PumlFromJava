package pumlFromJava;

import com.sun.source.util.DocTrees;
import jdk.javadoc.doclet.DocletEnvironment;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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

        for( Element classe : classes )
        {
            res = ajouteTypeElement(res, classe);
            res = ajouteChamps(res, classe);
            res.append("}\n\n");

        }
        res.append("\n@enduml\n");

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
            res.append( "class "+classe.getSimpleName().toString()+"{\n" );
        }
        else    // Sinon, l'élément est soit une énumération, soit une interface
        {
            res.append(stereotype+" "+classe.getSimpleName().toString() + " <<"+stereotype+">>{\n");
        }

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



}
