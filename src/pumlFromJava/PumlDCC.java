package pumlFromJava;

import jdk.javadoc.doclet.DocletEnvironment;
import pumlFromJava.ElementsClasse.PumlAttribut;
import pumlFromJava.ElementsClasse.PumlConstructeur;
import pumlFromJava.ElementsClasse.PumlMethod;
import pumlFromJava.ElementsClasse.PumlTypeClasse;
import pumlFromJava.Relations.PumlRelation;

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
            PumlMethod pumlMethod = new PumlMethod(classe);
            PumlConstructeur pumlConstructeur = new PumlConstructeur(classe);
            PumlRelation pumlRelation = new PumlRelation(classe);

            res = pumlTypeClasse.ajouteTypeClasse(res);
            //res = pumlRelation.ajouteSuperClass(res);
            //res = pumlRelation.ajouteImplementations(res);
            res.append("{\n");
            res = pumlChamps.ajouteChampsDCC(res);
            res = pumlConstructeur.ajouteConstructeurs(res);
            res = pumlMethod.ajouteMethodes(res);
            res.append("}\n");

            //res = pumlRelation.ajouteAgregations(res);    Pas d'agrégation dans le DCC

            res.append("\n\n");
        }
        res.append("}\n@enduml\n");

        return res.toString();
    }


}
