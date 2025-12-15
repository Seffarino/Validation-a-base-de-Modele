package scanette;

import nz.ac.waikato.modeljunit.FsmModel;
import nz.ac.waikato.modeljunit.RandomTester;
import nz.ac.waikato.modeljunit.Tester;
import nz.ac.waikato.modeljunit.VerboseListener;
import nz.ac.waikato.modeljunit.coverage.ActionCoverage;
import nz.ac.waikato.modeljunit.coverage.StateCoverage;
import nz.ac.waikato.modeljunit.coverage.TransitionCoverage;

import java.io.File;
import java.io.IOException;

public class Launcher {
    public static void main(String[] args) throws IOException {

        FsmModel model = new SCANETTE1();
        Tester tester = new RandomTester(model);

        // Traces (optionnel)
        tester.addListener(new VerboseListener());

        // Export JUnit
        JUnitExporter exporter = new JUnitExporter();
        tester.addListener(exporter);

        // "Requirement" coverage basé sur produits.csv (version adaptée ci-dessus)
        RequirementCoverage reqCov = new RequirementCoverage(
                new File("src/main/resources/csv/produits.csv")
        );
        tester.addListener(reqCov);

        // Couverture structurelle
        tester.addCoverageMetric(new TransitionCoverage());
        tester.addCoverageMetric(new ActionCoverage());
        tester.addCoverageMetric(new StateCoverage());

        // Graphe
        tester.buildGraph().printGraphDot("./graphScanette.dot");

        // Génération
        tester.generate(20);

        // Export après génération
        exporter.exportToFile(
                new File("src/test/java/scanette/ScanetteGeneratedTests.java"),
                "scanette"
        );

        // Affichage si tu veux
        System.out.println(reqCov.getName() + " : " + reqCov);
        System.out.println(reqCov.getDetails());
    }
}
