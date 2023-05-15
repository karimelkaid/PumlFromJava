package pumlFromJava;


import java.util.spi.ToolProvider;


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
