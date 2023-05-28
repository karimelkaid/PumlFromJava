package pumlFromJava.ElementsClasse;

import javax.lang.model.element.*;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.util.Set;

public class PumlConstructeur{

    Element classe;
    String nomClasse;

    public PumlConstructeur(Element classe)
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

    /*private String getPrimitiveTypeName(TypeKind kind) {
        String typeName = "";

        if (kind == TypeKind.BOOLEAN) {
            typeName = "boolean";
        } else if (kind == TypeKind.BYTE) {
            typeName = "byte";
        } else if (kind == TypeKind.SHORT) {
            typeName = "short";
        } else if (kind == TypeKind.INT) {
            typeName = "integer";
        } else if (kind == TypeKind.LONG) {
            typeName = "long";
        } else if (kind == TypeKind.CHAR) {
            typeName = "char";
        } else if (kind == TypeKind.FLOAT) {
            typeName = "float";
        } else if (kind == TypeKind.DOUBLE) {
            typeName = "double";
        }

        return typeName;
    }*/

    public StringBuilder ajouteConstructeurs(StringBuilder codePumlDeBase)
    {
        StringBuilder codePumlConstructeur = new StringBuilder(codePumlDeBase);
        for (Element e : classe.getEnclosedElements())
        {
            // Si l'élément est un constructeur
            if (e.getKind() == ElementKind.CONSTRUCTOR)
            {
                ExecutableElement methodeOuConstructeur = (ExecutableElement) e;    // Cast pour accéder aux méthodes de la classe ExecutableElement

                /*String v = getMethodSignature((ExecutableElement) e);
                codePumlMethode.append(v);*/

                String visibilite = getVisibilite(methodeOuConstructeur);
                codePumlConstructeur.append(visibilite + " ");

                codePumlConstructeur.append("<<Create>> ").append(nomClasse);


                String parametres = getParametres(methodeOuConstructeur);
                codePumlConstructeur.append(parametres);

                codePumlConstructeur.append("\n");


            }
        }

        System.out.println(codePumlConstructeur);
        return codePumlConstructeur;
    }


    public String getVisibilite(ExecutableElement methode) {
        String res = "";

        Set<Modifier> modifiers = methode.getModifiers();
        //System.out.println("Nombre de modifiers de "+attribut.getSimpleName()+" = "+modifiers.size());

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

}
