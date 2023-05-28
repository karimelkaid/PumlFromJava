package pumlFromJava;

import jdk.javadoc.doclet.DocletEnvironment;
import pumlFromJava.ElementsClasse.PumlAttribut;
import pumlFromJava.ElementsClasse.PumlTypeClasse;
import pumlFromJava.Relations.PumlRelation;

import javax.lang.model.element.Element;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class PumlDiagram
{
    private String repertoireDestination;
    private String nomFichierACree;
    private DocletEnvironment environment;
    private boolean dca;

    private PumlDCC pumlDCC;
    private PumlDCA pumlDCA;

    public PumlDiagram( String repertoireDestination, String nomFichierACree, DocletEnvironment environment, boolean dca )
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
        this.dca = dca;
    }

    public void generePuml()
    {
        List<Element> Classes = recupClasses(environment);

        String code = "";
        if( dca )
        {
            pumlDCA = new PumlDCA(environment,Classes);
            code = pumlDCA.genereDCA();
        }
        else
        {
            pumlDCC = new PumlDCC(environment, Classes);
            code = pumlDCC.genereDCC();
        }

        try
        {
            // Création du fichier puml au bon emplacement
            String filePath = repertoireDestination +"/"+ nomFichierACree;
            FileWriter fw = new FileWriter(filePath);

            // Remplissage du code PUML à mettre plus tard dans le fichier PUML
            //String chPuml = ecrisCodePuml(Classes);

            // Écriture dans le fichier PUML et fermeture du flux
            fw.write(code);
            fw.flush();
            fw.close();
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }

        /*List<Element> Classes = recupClasses(environment);

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
        }*/

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

        PumlPackage pumlPackage = new PumlPackage(environment);
        res = pumlPackage.ajoutePackage(res);

        for( Element classe : classes )
        {
            PumlTypeClasse pumlTypeClasse = new PumlTypeClasse(classe);
            PumlAttribut pumlChamps = new PumlAttribut(classe);
            PumlRelation pumlRelation = new PumlRelation(classe);

            res = pumlTypeClasse.ajouteTypeClasse(res);
            res = pumlRelation.ajouteSuperClass(res);
            res = pumlRelation.ajouteImplementations(res);
            res.append("{\n");
            res = pumlChamps.ajouteChampsDCA(res);
            res.append("}\n");

            res = pumlRelation.ajouteAgregations(res);

            res.append("\n\n");
        }
        res.append("}\n@enduml\n");

        return res.toString();
    }
}
