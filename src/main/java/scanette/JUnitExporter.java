package scanette;

import nz.ac.waikato.modeljunit.AbstractListener;
import nz.ac.waikato.modeljunit.Model;
import nz.ac.waikato.modeljunit.Transition;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class JUnitExporter extends AbstractListener {

    private final String testClassName;
    private final String adapterImplClass;
    private final String adapterFieldName;

    private final List<List<String>> testCases = new ArrayList<>();
    private List<String> currentTest = new ArrayList<>();

    /**
     * Constructeur par défaut pour la Scanette
     */
    public JUnitExporter() {
        this("ScanetteGeneratedTests", "ScanetteAdapter", "adapter");
    }

    public JUnitExporter(String testClassName,
                         String adapterImplClass,
                         String adapterFieldName) {
        this.testClassName = testClassName;
        this.adapterImplClass = adapterImplClass;
        this.adapterFieldName = adapterFieldName;
    }

    @Override
    public String getName() {
        return "ScanetteJUnitExporter";
    }

    /**
     * À chaque reset → on clôt un test
     */
    @Override
    public void doneReset(String reason, boolean testing) {
        if (!currentTest.isEmpty()) {
            testCases.add(currentTest);
            currentTest = new ArrayList<>();
        }
    }

    /**
     * À chaque transition → on enregistre l’action
     */
    @Override
    public void doneTransition(int action, Transition tr) {
        Model model = getModel();
        if (model == null) return;

        String actionName = model.getActionName(action);
        if (actionName != null && !actionName.isEmpty()) {
            currentTest.add(actionName);
        }
    }

    /**
     * Pour la Scanette : mapping direct
     */
    private String mapActionToAdapterCall(String actionName) {
        // Les noms des actions correspondent directement aux méthodes
        return actionName;
    }

    /**
     * Export JUnit Maven-compatible
     */
    public void exportToFile(File outputFile, String packageName) throws IOException {

        if (!currentTest.isEmpty()) {
            testCases.add(currentTest);
            currentTest = new ArrayList<>();
        }

        File parent = outputFile.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }

        try (PrintWriter out = new PrintWriter(new FileWriter(outputFile))) {

            // Package
            if (packageName != null && !packageName.isEmpty()) {
                out.println("package " + packageName + ";");
                out.println();
            }

            // Imports
            out.println("import org.junit.Test;");
            out.println("import org.junit.Before;");
            out.println("import org.junit.After;");
            out.println();

            // Classe
            out.println("public class " + testClassName + " {");
            out.println();
            out.println("    private " + adapterImplClass + " " + adapterFieldName
                    + " = new " + adapterImplClass + "();");
            out.println();

            // Setup / teardown
            out.println("    @Before");
            out.println("    public void setUp() {");
            out.println("        " + adapterFieldName + ".setup();");
            out.println("    }");
            out.println();
            out.println("    @After");
            out.println("    public void tearDown() {");
            out.println("        " + adapterFieldName + ".teardown();");
            out.println("    }");
            out.println();

            // Tests
            for (int i = 0; i < testCases.size(); i++) {
                out.println("    @Test");
                out.println("    public void test" + i + "() {");

                for (String actionName : testCases.get(i)) {
                    out.println("        " + adapterFieldName + "."
                            + mapActionToAdapterCall(actionName) + "();");
                }

                out.println("    }");
                out.println();
            }

            out.println("}");
        }
    }
}
