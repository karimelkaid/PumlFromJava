package pumlFromJava.ElementsClasse;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import java.util.*;

public class PumlAttribut
{
    private Element classe;
    private List<String> champs;
    private List<VariableElement> attributs;


    public PumlAttribut(Element classe)
    {
        this.classe = classe;
        this.champs = new ArrayList<>();
        this.attributs = new ArrayList<>();
    }

    // Nous faisons la distinction entre le DCA et DCC car dans le DCA il suffit de mettre le nom de l'attribut alors que dans le DCC, il faut la visibilité et le type de l'attribut en plus
    public StringBuilder ajouteChampsDCA(StringBuilder codePumlDeBase)
    {
        StringBuilder res = new StringBuilder(codePumlDeBase);

        champs = getPrimitiveFieldsNamesAndConstEnum();      // Récupération des champs (primitifs et constantes d'une énumération)

        for( String champ : champs )
        {
            res.append("\t"+champ+"\n");
        }

        return res;
    }


    public StringBuilder ajouteChampsDCC(StringBuilder codePumlDeBase)
    {
        StringBuilder res = new StringBuilder(codePumlDeBase);

        attributs = getAttributs();

        for( VariableElement attribut : attributs )
        {
            String nomAttribut = attribut.getSimpleName().toString();

            // Si l'on a une contante d'énumération --> ajout du nom seul, sinon ajout de toutes les autres informations (visibilité etc...)
            if( attribut.getKind().equals(ElementKind.ENUM_CONSTANT) )
            {
                champs.add(nomAttribut);
            }
            else
            {
                String visibiliteAttribut = getVisibiliteAttribut(attribut);
                String attributStatic = "";
                String attributFinal = "";
                String typeAttribut = "";

                // Dictionnaire où nous stockerons les modifiers static et final s'ils existent
                Map<String,String> attributStaticFinal = getAttributStaticFinal(attribut);

                // Parcours du dictionnaire en utilisant keySet et get
                for (String cle : attributStaticFinal.keySet())
                {
                    if( cle.equals("static") )
                    {
                        attributStatic = attributStaticFinal.get(cle);
                    }
                    else if(cle.equals("final"))
                    {
                        attributFinal = attributStaticFinal.get(cle);
                    }
                    //System.out.println("Clé : " + cle + ", Valeur : " + valeur);
                }

                typeAttribut = getTypeAttribut(attribut);

                champs.add(visibiliteAttribut+" "+attributStatic+" "+nomAttribut+" : "+typeAttribut+" "+attributFinal);
            }

        }

        for( String champ : champs )
        {
            res.append("\t"+champ+"\n");

            System.out.println("oe : "+champ);
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
                // Si le champ est une constante d'énumération --> ajout à la liste
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

    public List<VariableElement> getAttributs()
    {
        List<VariableElement> res = new ArrayList<>();

        if(!classe.getKind().equals(ElementKind.ENUM))  // Si ce n'est pas une énumération
        {
            for ( Element attribut : classe.getEnclosedElements())
            {
                // Si le champ que l'on étudie est un champ ET est de type primitif --> ajout à la liste
                if (attribut.getKind().equals(ElementKind.FIELD) && attribut.asType().getKind().isPrimitive())
                {
                    res.add( (VariableElement)attribut);    // Cast car attribut est de type Element et nous
                }
            }
        }
        else
        {
            for ( Element attribut : classe.getEnclosedElements())
            {
                // Si le champ est une constante d'énumération --> ajout à la liste
                if (attribut.getKind().equals(ElementKind.ENUM_CONSTANT))
                {
                    res.add((VariableElement) attribut);
                }
            }

        }

        return res;
    }

    public List<String> getPrimitiveFieldsNamesAndConstEnum_V2()
    {
        List<String> variableNames = new ArrayList<>();

        if(classe.getKind().equals(ElementKind.ENUM))   // Si la classe est une énumération
        {
            for ( Element variable_local : classe.getEnclosedElements())
            {
                // Si le champ est une constante d'énumération --> ajout à la liste
                if (variable_local.getKind().equals(ElementKind.ENUM_CONSTANT))
                {
                    //variableNames.add(variable_local.getSimpleName().toString());
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

    public String getVisibiliteAttribut(Element attribut)
    {

        String res = "";

        Set<Modifier> modifiers = attribut.getModifiers();
        //System.out.println("Nombre de modifiers de "+attribut.getSimpleName()+" = "+modifiers.size());

        for( Modifier m : attribut.getModifiers() )
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

    public Map<String,String> getAttributStaticFinal(Element attribut)
    {
        Map<String,String> res = new HashMap<>();

        boolean attributStatic = false;
        boolean attributFinal = false;

        Set<Modifier> modifiers = attribut.getModifiers();

        // Parcourt des modifiers
        for( Modifier m : modifiers )
        {
            if( m == Modifier.STATIC )
            {
                attributStatic = true;
            }
            else if( m == Modifier.FINAL )
            {
                attributFinal = true;
            }
        }

        if( attributStatic )
        {
            res.put("static","{static}");
        }
        else
        {
            res.put("static","");
        }


        if( attributFinal )
        {
            res.put("final","{ReadOnly}");
        }
        else
        {
            res.put("final","");
        }

        return res;
    }

    public String getTypeAttribut(VariableElement attribut)
    {
        String res = "";

        TypeMirror typeAttribut = attribut.asType();

        // Si le type de l'attribut est un réel --> nous méttons Integer, sinon juste la 1ère lettre est en majuscule
        if( estReel(typeAttribut.toString()) )
        {
            res = "Integer";
        }
        else
        {
            res = premiereLettreEnMajuscule(typeAttribut.toString());
        }
        //res = typeAttribut.toString();
        //res = premiereLettreEnMajuscule(res);


        //System.out.println("Type de l'atrribut : "+res);
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




}
