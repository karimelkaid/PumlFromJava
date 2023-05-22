package pumlFromJava;

import jdk.javadoc.doclet.DocletEnvironment;

import javax.lang.model.element.Element;
import java.util.List;
import java.util.Set;

public class PumlPackage
{
    private DocletEnvironment environnement;
    public PumlPackage(DocletEnvironment environnement)
    {
        this.environnement = environnement;
    }

    public StringBuilder ajoutePackage(StringBuilder codePumlDeBase)
    {
        Set<? extends Element> monPackage = environnement.getSpecifiedElements();     // Le package est spécifié dans la commande javadoc donc nous pouvons le récupérer comme ceci
        String nomPackage = monPackage.toString();

        StringBuilder res = new StringBuilder(codePumlDeBase);
        res.append("package "+nomPackage+"{\n\t");
        return res;
    }

}
