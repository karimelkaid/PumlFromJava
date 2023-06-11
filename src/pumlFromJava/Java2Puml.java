package pumlFromJava;


import java.util.spi.ToolProvider;

/*
    - Ne pas présenter le code dès le début
    - Voir le doc word
*/

public class Java2Puml
{

    public static void main(String[] args)
    {
        String[] arguments = {"-private","-sourcepath", "./src","-doclet" ,"pumlFromJava.PumlDoclet","-docletpath" ,"","test", "-d", "uml"  } ;

        ToolProvider toolProvider = ToolProvider.findFirst("javadoc").get();
        System.out.println(toolProvider.name());

        toolProvider.run(System.out, System.err, arguments);
    }
}
