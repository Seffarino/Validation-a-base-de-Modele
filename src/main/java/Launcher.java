import nz.ac.waikato.modeljunit.FsmModel;
import nz.ac.waikato.modeljunit.RandomTester;
import nz.ac.waikato.modeljunit.Tester;
import nz.ac.waikato.modeljunit.VerboseListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Launcher {
    public static void main(String[] args) throws IOException {
        FsmModel model = new SCANETTE1();
        Tester tester = new RandomTester(model);
        JUnitExporter testMaker = new JUnitExporter();
        tester.addListener(testMaker);
        tester.buildGraph().printGraphDot("./graphDAB1.dot");
        tester.generate(20);
        File outfile = new File("src/test/java/ScanetteTest.java");
        testMaker.exportTest("src/test",outfile);

    }
}
