package pumlFromJava.TypesDeDiagrammes;

import jdk.javadoc.doclet.DocletEnvironment;
import pumlFromJava.ElementsClasse.PumlAttribut;
import pumlFromJava.ElementsClasse.PumlTypeClasse;
import pumlFromJava.ElementsClasse.PumlPackage;
import pumlFromJava.Relations.PumlRelation;

import javax.lang.model.element.Element;
import java.util.ArrayList;
import java.util.List;

public class PumlDCA
{
    private DocletEnvironment environment;
    private List<Element> Classes;

    public PumlDCA(DocletEnvironment environment, List<Element> Classes)
    {
        this.environment = environment;
        this.Classes = new ArrayList<>(Classes);
    }

    public String genereDCA()
    {
        String codeDCA = ecrisCodeDCA(Classes);
        return codeDCA;
    }

    public String ecrisCodeDCA(List<Element> classes)
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
