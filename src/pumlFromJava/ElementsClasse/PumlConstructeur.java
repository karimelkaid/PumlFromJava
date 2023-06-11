package pumlFromJava.ElementsClasse;

import javax.lang.model.element.*;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class PumlConstructeur{

    Element classe;
    String nomClasse;
    List<ExecutableElement> constructeurs;

    public PumlConstructeur(Element classe)
    {
        this.classe = classe;
        this.nomClasse = classe.getSimpleName().toString();
        this.constructeurs = new ArrayList<>();
    }
    public String TraduisTypeEnUML(TypeMirror typeMirror)
    {
        String res = "";

        String typeMethodeNomSimple = getNomSimple(typeMirror);

        if (typeMirror.getKind() == TypeKind.VOID) {
            res = "void";
        }
        else if( estInteger(typeMethodeNomSimple ))
        {
            res = "Integer";
        }
        else if( estReel(typeMethodeNomSimple) )
        {
            res = "Real";
        }
        else if( typeMethodeNomSimple.equals("Character") || typeMethodeNomSimple.equals("Char") || typeMethodeNomSimple.equals("char") )
        {
            res = "String";
        }
        else if (typeMirror.getKind().equals(TypeKind.ARRAY))
        {
            ArrayType arrayType = (ArrayType) typeMirror;
            TypeMirror typeElementDansTableau = arrayType.getComponentType();
            String nomTypeElementDansTableau = getNomSimple(typeElementDansTableau);
            res = nomTypeElementDansTableau + "[]";
        }
        else if (estListe(typeMirror))
        {
            DeclaredType declaredType = (DeclaredType) typeMirror;
            List<? extends TypeMirror> typeArguments = declaredType.getTypeArguments();
            if (!typeArguments.isEmpty())
            {
                TypeMirror typeElement = typeArguments.get(0);
                String nomTypeElement = getNomSimple(typeElement);
                res = nomTypeElement + "[*]";
            }
        }
        else
        {
            res = premiereLettreEnMajuscule(getNomSimple(typeMirror));
        }

        return res;
    }

    public String getNomSimple(TypeMirror typeMirror) {
        // Nous n'utilisons pas la classe DeclaredType car le getSimpleName() ne fonctionne pas pour es type comme "? extens Nommable"

        // Obtenir le nom complet du type (par exemple "java.lang.String")
        String nomType = typeMirror.toString();

        // Extraire le nom simple du type (par exemple : "String")
        int lastIndex = nomType.lastIndexOf('.');
        if (lastIndex != -1)
        {
            nomType = nomType.substring(lastIndex + 1);
        }

        return nomType;
    }



    public boolean estListe(TypeMirror typeMirror) {

        boolean res = false;

        if (typeMirror.getKind().equals(TypeKind.DECLARED))
        {
            DeclaredType typeDeclare = (DeclaredType) typeMirror;
            String nomType = typeDeclare.asElement().toString();

            if( nomType.equals(List.class.getName() ))      // Si le nom du type passé en paramètre est le même que celui de la classe List
            {
                res = true;
            }
        }

        return res;
    }


    public boolean estInteger(String typeAttributNomSimple )
    {
        boolean res = false;

        List<String> nomsTypesInteger = new ArrayList<>();
        nomsTypesInteger.add("int");
        nomsTypesInteger.add("byte");
        nomsTypesInteger.add("char");
        nomsTypesInteger.add("short");
        nomsTypesInteger.add("long");

        // Il est possible d'utiliser le type Double au lieu de double, java fait une différence entre les 2 donc nous faisons également la différence et nous ajoutons pour chaque type sa classe
        List<String> temp = new ArrayList<>();
        for( String nomType : nomsTypesInteger )
        {
            String nomClasseDuType = premiereLettreEnMajuscule(nomType);
            temp.add(nomClasseDuType);
        }
        for( String nomType : temp )
        {
            nomsTypesInteger.add(nomType);
        }

        // Vérification
        if( nomsTypesInteger.contains(typeAttributNomSimple) )
        {
            res = true;
        }

        return res;
    }

    public boolean estReel(String typeAttributNomSimple )
    {
        boolean res = false;

        List<String> nomsTypesReel = new ArrayList<>();
        nomsTypesReel.add("double");
        nomsTypesReel.add("float");

        // Il est possible d'utiliser le type Double au lieu de double, java fait une différence entre les 2 donc nous faisons également la différence et nous ajoutons pour chaque type sa classe
        List<String> temp = new ArrayList<>();
        for( String nomType : nomsTypesReel )
        {
            String nomClasseDuType = premiereLettreEnMajuscule(nomType);
            temp.add(nomClasseDuType);
        }
        for( String nomType : temp )
        {
            nomsTypesReel.add(nomType);
        }


        if( nomsTypesReel.contains(typeAttributNomSimple) )
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

    public StringBuilder ajouteConstructeurs(StringBuilder codePumlDeBase)
    {
        StringBuilder codePumlConstructeur = new StringBuilder(codePumlDeBase);
        constructeurs = getConstructeurs();
        for (ExecutableElement constructeur : constructeurs)
        {

            String visibilite = getVisibilite(constructeur);
            codePumlConstructeur.append(visibilite + " ");

            codePumlConstructeur.append("<<Create>> ").append(nomClasse);


            String parametres = getParametres(constructeur);
            codePumlConstructeur.append(parametres);

            codePumlConstructeur.append("\n");



        }

        return codePumlConstructeur;
    }

    public List<ExecutableElement> getConstructeurs()
    {
        List<ExecutableElement> res = new ArrayList<>();

        for (Element e : classe.getEnclosedElements())
        {
            // Si l'élément est un constructeur
            if (e.getKind() == ElementKind.CONSTRUCTOR)
            {
                res.add( (ExecutableElement) e);    // Casr pour avoir accès aux méthodes de la classe ExecutableElement
            }
        }

        return res;
    }


    public String getVisibilite(ExecutableElement methode) {
        String res = "";

        Set<Modifier> modifiers = methode.getModifiers();

        for( Modifier m : modifiers )
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

        // Si l'élément n'a pas de visibilitée --> nous mettons un tilde '~'
        if( res.equals("") )
        {
            res = "~";
        }


        return res;
    }

    public String getParametres(ExecutableElement methode)
    {
        StringBuilder parametres = new StringBuilder("");
        parametres.append("(");
        for (VariableElement parameter : methode.getParameters())
        {
            String typeParametre = TraduisTypeEnUML(parameter.asType());
            String nomParametre = parameter.getSimpleName().toString();
            parametres.append(nomParametre).append(":").append(typeParametre).append(", ");
        }

        if (methode.getParameters().size() > 0) {
            parametres.setLength(parametres.length() - 2); // Supprimer la virgule et l'espace en trop
        }

        parametres.append(")");
        return parametres.toString();
    }

}
