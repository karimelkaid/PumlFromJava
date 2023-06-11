package pumlFromJava.ElementsClasse;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;

public class PumlTypeClasse
{
    Element classe;
    String typeClasse;
    public PumlTypeClasse(Element classe)
    {
        this.classe = classe;
    }

    public StringBuilder ajouteTypeClasse(StringBuilder codePumlDeBase)
    {
        StringBuilder res = new StringBuilder(codePumlDeBase);

        typeClasse = getTypeClasse();      // Récupération du type de la classe

        if( typeClasse.equals("") )     // Si c'est une classe normale
        {
            res.append( "class "+classe.getSimpleName().toString() );
        }
        else if(typeClasse.equals("interface") || typeClasse.equals("enum"))   // Sinon si l'élément est soit une énumération, soit une interface
        {
            res.append(typeClasse+" "+classe.getSimpleName().toString() + " <<"+typeClasse+">>");
        }

        return res;
    }

    public String getTypeClasse()
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
}
