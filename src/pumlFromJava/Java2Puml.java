package pumlFromJava;


import java.util.spi.ToolProvider;


/*
    - Modifier le DCA (et DCC du coup), rajouter Doclet.Option, Tree, Option.Kind, etc... au max
    - Dans le DS :
        - Modifier MyDoclet.class par Java2Puml.class
        - Ajouter un objet DocletEnvironnement pck on en a besoin quand j'appelle run(environnement)
        - Le point d'entrée c'est Java2Puml après faut faire le DS de Java2Puml et ENSUITE quand on a appelé le run on recopie ce qu'il y a déjà
    - Dans le code :
        - Il faut ajouter une classe PumlDiagram (voir dans la dernière question) et faire une partie du code dedans

*/
public class Java2Puml
{

    public static void main(String[] args)
    {
        String[] arguments = {"-private","-sourcepath", "./src","-doclet" ,"pumlFromJava.PumlDoclet","-docletpath" ,"","western", "-d", "uml" } ;

        ToolProvider toolProvider = ToolProvider.findFirst("javadoc").get();
        System.out.println(toolProvider.name());

        toolProvider.run(System.out, System.err, arguments);
    }
}
