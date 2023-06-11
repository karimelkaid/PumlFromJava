package pumlFromJava.Relations;

import pumlFromJava.ElementsClasse.PumlTypeClasse;

import javax.lang.model.element.Element;
import java.util.ArrayList;
import java.util.List;

public class PumlRelation
{
    Element classe;
    List<Element> lesClasses;
    String typeClasse;
    PumlAgregation pumlAgregation;
    PumlSuperClass pumlSuperClass;
    PumlImplementation pumlImplementation;
    PumlDependance pumlDependance;

    public PumlRelation(Element classe, List<Element> lesClasses)
    {
        this.classe = classe;

        PumlTypeClasse pumlTypeClasse= new PumlTypeClasse(classe);
        this.typeClasse = pumlTypeClasse.getTypeClasse();
        this.lesClasses = new ArrayList<>(lesClasses);
    }

    public StringBuilder ajouteAgregations(StringBuilder codePumlDeBase)
    {
        StringBuilder res;

        pumlAgregation = new PumlAgregation(classe, lesClasses);
        res = pumlAgregation.ajouteAgregations(codePumlDeBase);

        return res;
    }

    public StringBuilder ajouteAgregationsDCA(StringBuilder codePumlDeBase)
    {
        StringBuilder res = new StringBuilder("");

        pumlAgregation = new PumlAgregation(classe, lesClasses);
        res = pumlAgregation.ajouteAgregationsDCA(codePumlDeBase);

        return res;
    }



    public StringBuilder ajouteSuperClass(StringBuilder codePumlDeBase)
    {
        StringBuilder res = new StringBuilder(codePumlDeBase);

        // Seule les classes normales peuvent posséder une super classe
        if( typeClasse.equals("") )     // Si c'est une classe normale
        {
            pumlSuperClass = new PumlSuperClass(classe);
            res = pumlSuperClass.ajouteSuperClass(res);
        }

        return res;
    }

    public StringBuilder ajouteImplementations(StringBuilder codePumlDeBase)
    {
        StringBuilder res = new StringBuilder(codePumlDeBase);

        // Seule les classes normales ou les interfaces peuvent implémenter des interfaces
        if(typeClasse.equals("") || typeClasse.equals("interface"))
        {
            pumlImplementation = new PumlImplementation(classe, typeClasse);
            res = pumlImplementation.ajouteImplementations(codePumlDeBase);
        }

        return res;
    }

    public StringBuilder ajouteDependances(StringBuilder codePumlDeBase)
    {
        StringBuilder res = new StringBuilder(codePumlDeBase);

        // Seule les classes normales ou les interfaces peuvent implémenter des interfaces
        //if(typeClasse.equals("") || typeClasse.equals("interface"))
        //{
            pumlDependance = new PumlDependance(classe, lesClasses);
            res = new StringBuilder(pumlDependance.analyzeClass(res));
        //}

        return res;
    }

}
