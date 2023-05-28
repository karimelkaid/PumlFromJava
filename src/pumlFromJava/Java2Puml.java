package pumlFromJava;


import java.util.spi.ToolProvider;


/*
    - Modifier le DCA (et DCC du coup), rajouter Doclet.Option, DocTree, Option.Kind, etc... --> faut faire tout ce qu'on aura besoin dans ce projet même si c'est pas dans cette semaine --> FAIT
    - Dans le DCA et DCC, jsp comment relier la classe DocTrees avec le reste
    - Dans le DS :
        - Modifier MyDoclet.class par Java2Puml.class
        - Ajouter un objet DocletEnvironnement pck on en a besoin quand j'appelle run(environnement)
        - Le point d'entrée c'est Java2Puml après faut faire le DS de Java2Puml et ENSUITE quand on a appelé le run on recopie ce qu'il y a déjà
    - Ajouter le package western dans la semaine 2

    - À demander :
        - Est ce qu'on doit gérer le cas où on a un package contenant DES packages ? (par exemple dans western on a un package Scénario 1 etc...) Perso j'ai géré uniquement le cas où y a UN SEUL package
*/
public class Java2Puml
{

    public static void main(String[] args)
    {
        String[] arguments = {"-private","-sourcepath", "./src","-doclet" ,"pumlFromJava.PumlDoclet","-docletpath" ,"","western", "-d", "uml" } ;
        //String[] arguments = {"-private","-sourcepath", "./src","-doclet" ,"pumlFromJava.PumlDoclet","-docletpath" ,"","pumlFromJava.ElementsClasse", "-d", "uml" } ;

        ToolProvider toolProvider = ToolProvider.findFirst("javadoc").get();
        System.out.println(toolProvider.name());

        toolProvider.run(System.out, System.err, arguments);
    }
}
