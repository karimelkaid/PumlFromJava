package pumlFromJava.ElementsClasse;

import javax.lang.model.element.*;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.util.List;
import java.util.Set;

public class PumlMethod {

    Element classe;
    String nomClasse;

    public PumlMethod(Element classe)
    {
        this.classe = classe;
        this.nomClasse = classe.getSimpleName().toString();
    }
    public String getNomSimple(TypeMirror typeChamp)
    {
        String res = "";

        if (typeChamp.getKind() == TypeKind.VOID) {
            res = "void";
        }
        else if (typeChamp.getKind().isPrimitive())
        {
            //res = getPrimitiveTypeName(typeChamp.getKind());
            //res = typeChamp.toString();
            // Si le type de l'attribut est un réel --> nous méttons Integer, sinon juste la 1ère lettre est en majuscule
            if( estReel(typeChamp.toString()) )
            {
                res = "Integer";
            }
            else
            {
                res = premiereLettreEnMajuscule(typeChamp.toString());
            }

        }
        else if (typeChamp.getKind().equals(TypeKind.ARRAY))
        {
            ArrayType typeTableau = (ArrayType) typeChamp;
            TypeMirror typeElementDansTableau = typeTableau.getComponentType();
            res = getNomSimple(typeElementDansTableau) + "[]";
        }
        else
        {
            res = ((DeclaredType) typeChamp).asElement().getSimpleName().toString();
        }

        return res;
    }

    public boolean estReel( String type )
    {
        boolean res = false;

        if( type.equals("int") || type.equals("double") || type.equals("byte") || type.equals("double") || type.equals("short") || type.equals("char") || type.equals("long") || type.equals("float"))
        {
            res = true;
        }

        return res;
    }

    public String premiereLettreEnMajuscule(String chaine)
    {
        String res = chaine.substring(0,1).toUpperCase() + chaine.substring(1).toLowerCase();
        return res;
    }



    public StringBuilder ajouteMethodes(StringBuilder codePumlDeBase)
    {
        StringBuilder codePumlMethode = new StringBuilder(codePumlDeBase);
        for (Element e : classe.getEnclosedElements())
        {
            // Si l'élément est une méthode ou un constructeur
            if (e.getKind() == ElementKind.METHOD)
            {
                ExecutableElement methodeOuConstructeur = (ExecutableElement) e;    // Cast pour accéder aux méthodes de la classe ExecutableElement

                String visibilite = getVisibilite(methodeOuConstructeur);
                codePumlMethode.append(visibilite + " ");

                String nomMethode = methodeOuConstructeur.getSimpleName().toString();
                codePumlMethode.append(nomMethode);


                String parametres = getParametres(methodeOuConstructeur);
                codePumlMethode.append(parametres);

                String typeDeRetour = getNomSimple(methodeOuConstructeur.getReturnType());


                codePumlMethode.append(" : ").append(typeDeRetour);


                /*if (isOverrideMethod(methodeOuConstructeur)) {
                    codePumlMethode.append(" {redefined + ").append(getRedefiningMethod(methodeOuConstructeur)).append("}");
                }*/

                codePumlMethode.append("\n");


            }
        }

        System.out.println(codePumlMethode);
        return codePumlMethode;
    }



    public String getVisibilite(ExecutableElement methode) {
        String res = "";

        Set<Modifier> modifiers = methode.getModifiers();

        for( Modifier m : methode.getModifiers() )
        {
            if( m.equals(Modifier.PRIVATE) )
            {
                res = "-";
            }
            else if(m.equals(Modifier.PUBLIC))
            {
                res = "+";
            }
            else if( m.equals(Modifier.PROTECTED) )
            {
                res = "~";
            }
        }

        return res;
    }

    public String getParametres(ExecutableElement method)
    {
        StringBuilder parameters = new StringBuilder();
        parameters.append("(");
        for (VariableElement parameter : method.getParameters())
        {
            String parameterType = getNomSimple(parameter.asType());
            String parameterName = parameter.getSimpleName().toString();
            parameters.append(parameterName).append(":").append(parameterType).append(", ");
        }

        if (method.getParameters().size() > 0) {
            parameters.setLength(parameters.length() - 2); // Supprimer la virgule et l'espace en trop
        }

        parameters.append(")");
        return parameters.toString();
    }

    public boolean isOverrideMethod(ExecutableElement method)
    {
        Element enclosingClass = method.getEnclosingElement().getEnclosingElement();
        if (enclosingClass instanceof TypeElement)
        {
            TypeElement classElement = (TypeElement) enclosingClass;
            List<? extends TypeMirror> interfaces = classElement.getInterfaces();
            for (TypeMirror interfaceType : interfaces)
            {
                TypeElement interfaceElement = (TypeElement) ((DeclaredType) interfaceType).asElement();
                for (Element enclosedElement : interfaceElement.getEnclosedElements())
                {
                    if (enclosedElement.getKind() == ElementKind.METHOD)
                    {
                        ExecutableElement interfaceMethod = (ExecutableElement) enclosedElement;
                        if (methodMatch(interfaceMethod, method))
                        {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    public boolean methodMatch(ExecutableElement method1, ExecutableElement method2) {
        return method1.getSimpleName().toString().equals(method2.getSimpleName().toString())
                && method1.getParameters().size() == method2.getParameters().size()
                && method1.getReturnType().equals(method2.getReturnType());
    }

    public String getRedefiningMethod(ExecutableElement method)
    {
        StringBuilder signature = new StringBuilder();
        signature.append(method.getSimpleName().toString());
        signature.append("(");
        for (VariableElement parameter : method.getParameters())
        {
            String parameterType = getNomSimple(parameter.asType());
            String parameterName = parameter.getSimpleName().toString();
            signature.append(parameterName).append(":").append(parameterType).append(", ");
        }

        if (method.getParameters().size() > 0) {
            signature.setLength(signature.length() - 2); // Supprimer la virgule et l'espace en trop
        }

        signature.append(")");
        return signature.toString();
    }
}
