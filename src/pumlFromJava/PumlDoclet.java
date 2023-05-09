package pumlFromJava;

import jdk.javadoc.doclet.Doclet;
import jdk.javadoc.doclet.DocletEnvironment;
import jdk.javadoc.doclet.Reporter;

import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import java.io.FileWriter;
import java.util.*;


/**
 * Doclets : https://openjdk.org/groups/compiler/processing-code.html
 *
 * Doclets provide code that can be executed by the JDK javadoc tool.
 * Although the tool is primarily designed to support the ability to generate
 * API documentation from element declarations and documentation comments,
 * it is not limited to that purpose, and can run any user-supplied doclet,
 * which can use the Language Model API and Compiler Tree API to analyze the packages,
 * classes and files specified on the command line.
 */

/**
 * A minimal doclet that just prints out the names of the
 * selected elements.
 */
public class PumlDoclet implements Doclet {
    @Override
    public void init(Locale locale, Reporter reporter) {  }

    @Override
    public String getName() {
        // For this doclet, the name of the doclet is just the
        // simple name of the class. The name may be used in
        // messages related to this doclet, such as in command-line
        // help when doclet-specific options are provided.
        return getClass().getSimpleName();
    }

    private String nomFichierACree;
    private String repertoireDestination;
    @Override
    public Set<? extends Option> getSupportedOptions() {
        // This doclet does not support any options.

        Option[] options = {
                // Ajout de l'option -d
                new Option() {
                    private final List<String> someOption = List.of(
                            "-d"
                    );

                    @Override
                    public int getArgumentCount() {
                        return 1;
                    }

                    @Override
                    public String getDescription() {
                        return "le répertoire où sera créé le fichier .puml";
                    }

                    @Override
                    public Option.Kind getKind() {
                        return Option.Kind.STANDARD;
                    }

                    @Override
                    public List<String> getNames() {
                        return someOption;
                    }

                    @Override
                    public String getParameters() {
                        return "répertoire";
                    }

                    @Override
                    public boolean process(String opt, List<String> arguments)
                    {
                        repertoireDestination = arguments.get(0);
                        return true;
                    }
                },

                // Ajout de l'option -out
                new Option() {
                    private final List<String> someOption = List.of(
                            "-out"
                    );

                    @Override
                    public int getArgumentCount() {
                        return 1;
                    }

                    @Override
                    public String getDescription() {
                        return "le nom du fichier .puml à créer";
                    }

                    @Override
                    public Option.Kind getKind() {
                        return Option.Kind.STANDARD;
                    }

                    @Override
                    public List<String> getNames() {
                        return someOption;
                    }

                    @Override
                    public String getParameters() {
                        return "<String>";
                    }

                    @Override
                    public boolean process(String opt, List<String> arguments)
                    {
                        nomFichierACree = arguments.get(0)+".puml";
                        return true;
                    }
                }
        };

        return Set.of(options);
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        // This doclet supports all source versions.
        // More sophisticated doclets may use a more
        // specific version, to ensure that they do not
        // encounter more recent language features that
        // they may not be able to handle.
        return SourceVersion.latest();
    }

    @Override
    public boolean run(DocletEnvironment environment) {
        // This method is called to perform the work of the doclet.
        // In this case, it just prints out the names of the
        // elements specified on the command line.
        /*System.out.println(this.getName());
        System.out.println(environment.getSpecifiedElements());
        System.out.println(environment.getIncludedElements());
        for (Element element : environment.getSpecifiedElements())
        {
            dumpElement(element);
        }*/

        if( repertoireDestination == null )
        {
            repertoireDestination = ".";
        }

        List<String> nomClasses = recupNomsClasses(environment);
        try
        {
            // Création du fichier puml au bon emplacement
            String filePath = repertoireDestination +"/"+ nomFichierACree;
            FileWriter fw = new FileWriter(filePath);

            // Remplissage du code PUML à mettre plus tard dans le fichier PUML
            String chPuml = ecrisCodePuml(nomClasses);

            // Écriture dans le fichier PUML et fermeture du flux
            fw.write(chPuml.toString());
            fw.flush();
            fw.close();
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }

        return true;
    }

    private void dumpElement(Element element)
    {
        System.out.print("---- ");
        System.out.println("element: " + element);
        System.out.println("kind: " + element.getKind());
        System.out.println("simpleName: " + element.getSimpleName());
        System.out.println("enclosingElement: " + element.getEnclosingElement());
        System.out.println("enclosedElement: " + element.getEnclosedElements());
        System.out.println("modifiers: " + element.getModifiers());
        System.out.println();
    }

    public List<String> recupNomsClasses(DocletEnvironment Xenvironment)
    {
        List<String> res = new ArrayList<>();

        // Récupération du/des élément(s) spécifié(s)
        Set<? extends Element> specifiedElements = Xenvironment.getSpecifiedElements();

        // Parcourt du/des élément(s) spécifié(s)
        for (Element element : specifiedElements)
        {
            // Si l'utilisateur n'a pas spécifié de nom pour le PUML à créer, alors on lui attribue le premier élément sélectionné
            if(nomFichierACree == null)
            {
                nomFichierACree = element.getSimpleName().toString() + ".puml";
            }

            // Parcourt des classes du package
            for (Element enclosedElement : element.getEnclosedElements())
            {
                res.add(enclosedElement.getSimpleName().toString());    // Ajout du nom de la classe actuelle à la liste
            }
        }
        return res;
    }

    public String ecrisCodePuml(List<String> XnomClasses)
    {
        StringBuilder res = new StringBuilder("@startuml\n\n");

        for( String nomClasse : XnomClasses )
        {
            res.append( "class "+nomClasse+"\n" );
        }
        res.append("\n@enduml\n");

        return res.toString();
    }


}
