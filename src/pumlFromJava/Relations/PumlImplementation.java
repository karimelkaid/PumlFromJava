package pumlFromJava.Relations;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import java.util.ArrayList;
import java.util.List;

public class PumlImplementation
{
    Element classe;
    List<String> nomInterfaces;
    String typeClasse;      // Va nous permettre de savoir s'il faut mettre le mot clé "implements" ou "extends"

    public PumlImplementation(Element classe, String typeClasse)
    {
        this.classe = classe;
        this.nomInterfaces = new ArrayList<>();
        this.typeClasse = typeClasse;
    }

    public StringBuilder ajouteImplementations(StringBuilder codePumlDeBase)
    {
        StringBuilder res = new StringBuilder(codePumlDeBase);

        // Ajout des interfaces implémentées
        nomInterfaces = getInterfaces();
        if( nomInterfaces.size() > 0 )
        {
            if( typeClasse.equals("") )     // Pour les classes le mot clé est "implements"
            {
                res.append( " implements "+nomInterfaces.get(0) );
            }
            else     // Pour les interfaces le mot clé est "extends"
            {
                res.append( " extends "+nomInterfaces.get(0) );
            }

            if( (long) nomInterfaces.size() > 1 )
            {
                for(int i = 1; i< nomInterfaces.size(); i++)
                {
                    String nomInterface = nomInterfaces.get(i);
                    res.append(", "+nomInterface);
                }
            }
        }

        return res;
    }

    public List<String> getInterfaces()
    {
        List<String> res = new ArrayList<>();

        List<? extends TypeMirror> types;

        TypeElement classeType = (TypeElement)classe;
        types = classeType.getInterfaces();

        for( TypeMirror type : types )
        {
            res.add( ((DeclaredType)type).asElement().getSimpleName().toString() );
        }

        return res;
    }

}
