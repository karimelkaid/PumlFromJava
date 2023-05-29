package pumlFromJava.TypesDeDiagrammes;

import jdk.javadoc.doclet.DocletEnvironment;
import pumlFromJava.ElementsClasse.PumlAttribut;
import pumlFromJava.ElementsClasse.PumlConstructeur;
import pumlFromJava.ElementsClasse.PumlMethode;
import pumlFromJava.ElementsClasse.PumlTypeClasse;
import pumlFromJava.ElementsClasse.PumlPackage;

import javax.lang.model.element.Element;
import java.util.ArrayList;
import java.util.List;

public class PumlDCC
{
    private DocletEnvironment environment;
    private List<Element> Classes;

    public PumlDCC(DocletEnvironment environment, List<Element> Classes)
    {
        this.environment = environment;
        this.Classes = new ArrayList<>(Classes);
    }

    public String genereDCC()
    {
        String codeDCC = ecrisCodeDCC(Classes);
        return codeDCC;
    }

    public String ecrisCodeDCC(List<Element> classes)
    {
        StringBuilder res = new StringBuilder("@startuml\n\n");

        PumlPackage pumlPackage = new PumlPackage(environment);
        res = pumlPackage.ajoutePackage(res);

        for( Element classe : classes )
        {
            PumlTypeClasse pumlTypeClasse = new PumlTypeClasse(classe);
            PumlAttribut pumlChamps = new PumlAttribut(classe);
            PumlMethode pumlMethode = new PumlMethode(classe);
            PumlConstructeur pumlConstructeur = new PumlConstructeur(classe);

            res = pumlTypeClasse.ajouteTypeClasse(res);
            res.append("{\n");
            res = pumlChamps.ajouteChampsDCC(res);

            // Nous ajoutons les constructeurs et méthodes uniquement si la classe n'est pas une énumération
            if( !pumlTypeClasse.getTypeClasse().equals("enum") )
            {
                res = pumlConstructeur.ajouteConstructeurs(res);
                res = pumlMethode.ajouteMethodes(res);
            }
            res.append("}\n");

            res.append("\n\n");
        }
        res.append("}\n@enduml\n");

        return res.toString();
    }



}
