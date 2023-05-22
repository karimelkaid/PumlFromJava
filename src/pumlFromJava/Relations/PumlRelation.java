package pumlFromJava.Relations;

import pumlFromJava.PumlTypeClasse;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;

public class PumlRelation
{
    Element classe;
    String typeClasse;
    PumlAgregation pumlAgregation;
    PumlSuperClass pumlSuperClass;
    PumlImplementation pumlImplementation;

    public PumlRelation(Element classe)
    {
        this.classe = classe;

        PumlTypeClasse pumlTypeClasse= new PumlTypeClasse(classe);
        this.typeClasse = pumlTypeClasse.getTypeClasse();
    }

    public StringBuilder ajouteAgregations(StringBuilder codePumlDeBase)
    {
        StringBuilder res;

        pumlAgregation = new PumlAgregation(classe);
        res = pumlAgregation.ajouteAgregations(codePumlDeBase);

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





}
