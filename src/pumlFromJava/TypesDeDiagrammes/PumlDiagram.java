package pumlFromJava.TypesDeDiagrammes;

import jdk.javadoc.doclet.DocletEnvironment;

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
        List<Element> Classes = getClasses(environment);

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

            // Écriture dans le fichier PUML et fermeture du flux
            fw.write(code);
            fw.flush();
            fw.close();
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }


    }

    // Récupère les classes du package dans une liste
    public List<Element> getClasses(DocletEnvironment environment)
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

}
