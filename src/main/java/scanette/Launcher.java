package scanette;

import nz.ac.waikato.modeljunit.*;
import nz.ac.waikato.modeljunit.coverage.ActionCoverage;
import nz.ac.waikato.modeljunit.coverage.StateCoverage;
import nz.ac.waikato.modeljunit.coverage.TransitionCoverage;

import java.io.File;
import java.io.IOException;

public class Launcher {
    public static void main(String[] args) throws IOException {

        FsmModel model = new SCANETTE1();
        Tester tester = new GreedyTester(model);

        // Traces (optionnel)
        tester.addListener(new VerboseListener());
        tester.addCoverageMetric(new TransitionCoverage());
        tester.addCoverageMetric(new ActionCoverage());
        tester.addCoverageMetric(new StateCoverage() {
            @Override
            public String getName() {
                return "state coverage";
            }
        });
        // Export JUnit
        JUnitExporter exporter = new JUnitExporter();
        tester.addListener(exporter);

        // "Requirement" coverage basé sur produits.csv (version adaptée ci-dessus)
        RequirementCoverage reqCov = new RequirementCoverage(
                ResourceUtils.loadCsvFromResources("csv/coverage.csv")

        );
        tester.addCoverageMetric(reqCov);



        // Graphe
        tester.buildGraph().printGraphDot("./guetarni-seffar/target/graphScanette.dot");

        // Génération
        tester.generate(10000);

        // Export après génération
        exporter.exportToFile(
                new File("./guetarni-seffar/src/test/java/scanette/ScanetteGeneratedTests.java"),
                "scanette"
        );

        // Affichage si tu veux
        System.out.println(reqCov.getName() + " : " + reqCov);
        System.out.println(reqCov.getDetails());
    }
}
