package pumlFromJava.Relations;
import javax.lang.model.element.*;
import javax.lang.model.type.*;
import java.util.*;

public class PumlDependance
{
    private Element classe;
    private List<Element> classesPackage;
    public PumlDependance(Element classe, List<Element> classesPackage)
    {
        this.classe = classe;
        this.classesPackage = new ArrayList<>(classesPackage);
    }
    public String analyzeClass(StringBuilder codePumlDeBase)
    {
        Set<String> pumlCode = new HashSet<>();

        List<? extends Element> elementsEncapsules = classe.getEnclosedElements();
        for (Element elementEncapsule : elementsEncapsules)
        {
            if (elementEncapsule.getKind() == ElementKind.METHOD)
            {
                ExecutableElement methode = (ExecutableElement) elementEncapsule;
                List<? extends VariableElement> parametres = methode.getParameters();
                for (VariableElement parametre : parametres)
                {
                    TypeMirror parametreType = parametre.asType();
                    if (parametreType instanceof DeclaredType)
                    {
                        Element typeElement = ((DeclaredType) parametreType).asElement();
                        if (typeElement.getKind() == ElementKind.CLASS)
                        {
                            // Nous voulons mettre les dépendances uniquement entre lec classes du package
                            if( classesPackage.contains(typeElement) )
                            {
                                String dependence = getPumlDependence(classe, typeElement);
                                if (dependence != null)
                                {
                                    pumlCode.add(dependence);
                                }
                            }
                        }
                    }
                }
            }
        }

        StringBuilder pumlCodeBuilder = new StringBuilder();
        for (String dependence : pumlCode)
        {
            pumlCodeBuilder.append(dependence);
        }

        StringBuilder result = new StringBuilder(codePumlDeBase);
        result.append(pumlCodeBuilder.toString());

        System.out.println(result);
        return result.toString();
    }

    private static String getPumlDependence(Element classeSource, Element classeCible)
    {
        String nomClasseSource = classeSource.getSimpleName().toString();
        String nomClasseCible = classeCible.getSimpleName().toString();

        // VÃ©rifier si classeCible est bien une classe et n'est pas de type "String"
        if (classeCible.getKind() == ElementKind.CLASS && !nomClasseCible.equals("String")) {
            StringBuilder pumlDependence = new StringBuilder();
            pumlDependence.append(nomClasseSource).append(" ..> ").append(nomClasseCible).append(" : <<use>>").append("\n");
            return pumlDependence.toString();
        }

        return null; // Retourner null si classeCible n'est pas une classe ou si c'est de type "String"
    }
}
