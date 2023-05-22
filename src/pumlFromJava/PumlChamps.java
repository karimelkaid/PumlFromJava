package pumlFromJava;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import java.util.ArrayList;
import java.util.List;

public class PumlChamps
{
    private Element classe;
    private List<String> champs;
    public PumlChamps(Element classe)
    {
        this.classe = classe;
        this.champs = new ArrayList<>();
    }

    public StringBuilder ajouteChamps(StringBuilder codePumlDeBase)
    {
        StringBuilder res = new StringBuilder(codePumlDeBase);

        champs = getPrimitiveFieldsNamesAndConstEnum();      // Récupération des champs (primitifs et constantes d'une énumération)

        for( String champ : champs )
        {
            res.append("\t"+champ+"\n");
        }

        return res;
    }

    public List<String> getPrimitiveFieldsNamesAndConstEnum()
    {
        List<String> variableNames = new ArrayList<>();

        if(classe.getKind().equals(ElementKind.ENUM))   // Si la classe est une énumération
        {
            for ( Element variable_local : classe.getEnclosedElements())
            {
                // Si le champ que l'on étudie est un champ ET est de type primitif --> ajout à la liste
                if (variable_local.getKind().equals(ElementKind.ENUM_CONSTANT))
                {
                    variableNames.add(variable_local.getSimpleName().toString());
                }
            }
        }
        else
        {
            for ( Element variable_local : classe.getEnclosedElements())
            {
                // Si le champ que l'on étudie est un champ ET est de type primitif --> ajout à la liste
                if (variable_local.getKind().equals(ElementKind.FIELD) && variable_local.asType().getKind().isPrimitive())
                {
                    variableNames.add(variable_local.getSimpleName().toString());
                }
            }
        }


        return variableNames;
    }


}
