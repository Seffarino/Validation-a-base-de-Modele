import nz.ac.waikato.modeljunit.Model;
import nz.ac.waikato.modeljunit.ModelListener;
import nz.ac.waikato.modeljunit.TestFailureException;
import nz.ac.waikato.modeljunit.Transition;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class JUnitExporter implements ModelListener {
    private List<String> tests = new ArrayList<>();
    private String test = new String();
    @Override
    public String getName() {
        return "";
    }

    @Override
    public Model getModel() {
        return null;
    }

    @Override
    public void setModel(Model model) {

    }

    @Override
    public void doneReset(String s, boolean b) {
        tests.add(test);
        test = "";

    }

    @Override
    public void doneGuard(Object o, int i, boolean b, int i1) {

    }

    @Override
    public void startAction(Object o, int i, String s) {

        test += "       adapter." + s + "()" + "\n";
    }

    @Override
    public void doneTransition(int i, Transition transition) {
    }

    @Override
    public void failure(TestFailureException e) {

    }

    public void exportTest(String className, File outputFile) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(outputFile))) {
            writer.println("import org.junit.Test;");
            writer.println("public class "+ className + " {");
            writer.println("    DABAdapter adapter = new DABAdapter();");
            int i = 0;
            for (String element : tests){
                writer.println("    @Test");
                writer.println("    public void test"+tests.indexOf(element)+" {");
                writer.println(element);
                writer.println("    }");
            }






            writer.println("}");

        }

    }
}
