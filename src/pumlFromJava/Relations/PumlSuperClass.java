package pumlFromJava.Relations;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

public class PumlSuperClass
{
    Element classe;
    String nomSuperClasse;

    public PumlSuperClass(Element classe)
    {
        this.classe = classe;
        this.nomSuperClasse = "";        //  Récupération de la super classe
    }

    public StringBuilder ajouteSuperClass(StringBuilder codePumlDeBase)
    {
        StringBuilder res = codePumlDeBase;

        this.nomSuperClasse = getSuperClassName();
        if( nomSuperClasse != null && !nomSuperClasse.equals("Object") )    // Si la classe possède une super classe ET ce n'est pas la classe Object--> ajout de l'héritage
        {
            res.append(" extends "+nomSuperClasse);
        }
        return res;
    }

    public String getSuperClassName()
    {
        TypeElement classeType = (TypeElement)classe;
        TypeMirror superClassType = classeType.getSuperclass();     // Récupération du type de la super classe

        String superClassName = ((DeclaredType) superClassType).asElement().getSimpleName().toString();

        return superClassName;
    }



}
