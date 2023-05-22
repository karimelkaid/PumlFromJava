package pumlFromJava.Relations;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PumlAgregation
{
    Element classe;
    List<String> agregationsExistantes;        // Nous stockons toutes les agrégations dans cette liste afin de ne pas avoir de répétitions (par exemple : Boisson -- Substantif et Substantif -- Boisson)
    List<String> typeNonVoulu;

    public PumlAgregation(Element classe)
    {
        this.classe = classe;
        this.agregationsExistantes = new ArrayList<>();
        this.typeNonVoulu = Arrays.asList("String", "Set", "List");     // Les String, Set et List ne sont pas considérés comme des types primitifs, mais nous ne les voulons pas dans l'UML
    }

    public StringBuilder ajouteAgregations(StringBuilder codePumlDeBase)
    {
        StringBuilder res = new StringBuilder(codePumlDeBase);

        // Parcourt des champs de la classe
        for( Element champOuConstructeurOuMethode : classe.getEnclosedElements() )
        {
            // Si l'élément est un champ, ne possède pas un type primitif et la nouvelle liaison n'existe pas --> ajout de l'agrégation
            if( champOuConstructeurOuMethode.getKind().equals(ElementKind.FIELD)  )
            {
                TypeMirror typeChamp = champOuConstructeurOuMethode.asType();   // Récupération du type
                if( !typeChamp.getKind().isPrimitive() )
                {
                    // Nous récupérons le type
                    String typeChampString = ((DeclaredType)typeChamp).asElement().getSimpleName().toString();
                    if( !typeNonVoulu.contains(typeChampString)  )
                    {
                        String liaison = classe.getSimpleName()+" -- "+ typeChampString;

                        // Nous ajoutons la liaison uniquement si celle-ci n'est pas présente (sinon il y aura des doublons)
                        if( !liaisonExistante(agregationsExistantes, liaison) )
                        {
                            res.append(  liaison + "\n" );      // Ajout de la liaison dans le code
                            agregationsExistantes.add(liaison);   // Ajout de la liaison à la liste pour ne pas avoir de doublon(s)
                        }
                    }
                }
            }
        }

        return res;
    }

    public boolean liaisonExistante(List<String> agregationsExistantes, String liaison)
    {
        boolean res = false;

        String liaisonInverse = inverseLiaison(liaison);
        if( agregationsExistantes.contains(liaison) || agregationsExistantes.contains(liaisonInverse) )
        {
            res = true;
        }

        return res;
    }

    public String inverseLiaison(String liaison)
    {
        String[] nomsClasses = liaison.split(" -- ");       // Récupération des noms de classes dans un tableau
        StringBuilder laisonInverse = new StringBuilder();

        laisonInverse.append(nomsClasses[1] + " -- " + nomsClasses[0]);     // Inversement

        return laisonInverse.toString();
    }


}
