package scanette;

import nz.ac.waikato.jdsl.graph.api.InspectableGraph;
import nz.ac.waikato.jdsl.graph.api.Vertex;
import nz.ac.waikato.modeljunit.Model;
import nz.ac.waikato.modeljunit.TestFailureException;
import nz.ac.waikato.modeljunit.Transition;
import nz.ac.waikato.modeljunit.coverage.CoverageMetric;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * "Requirement" coverage derived from produits.csv.
 *
 * Each line (EAN, prix, nom) is considered a requirement: the product exists in DB
 * and should be scannable at least once during the test generation.
 *
 * Covered if we observe at least one transition with action "AjouterArticleOK".
 *
 * NOTE: this metric does NOT distinguish which EAN was scanned, because Transition
 * only exposes the action name, not the parameters. For per-EAN coverage, you'd
 * need to log the scanned EAN in the adapter/model.
 */
public class RequirementCoverage implements CoverageMetric {

    private Model model;

    private final Map<String, String> reqDescriptions = new HashMap<>();
    private final Map<Object, Integer> reqCount = new HashMap<>();

    private static final String DELIMITER = ",";

    // Action that indicates "scan of an existing product succeeded"
    private static final String COVERING_ACTION = "AjouterArticleOK";

    public RequirementCoverage(File produitsCsv) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(produitsCsv))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;

                String[] values = line.split(DELIMITER, -1);
                if (values.length < 3) continue;

                String ean = values[0].trim();
                String prix = values[1].trim();
                String nom = values[2].trim();

                // Define a "requirement id" per product
                String reqId = "PROD_" + ean;
                String desc = "Produit en base: EAN=" + ean + ", prix=" + prix + ", nom=" + nom;

                reqDescriptions.put(reqId, desc);
                reqCount.put(reqId, 0);
            }
        }
    }

    @Override
    public String getDescription() {
        return "Measures 'product requirements' coverage derived from produits.csv";
    }

    @Override
    public void clear() {
        for (Object k : reqCount.keySet()) {
            reqCount.put(k, 0);
        }
    }

    @Override
    public int getCoverage() {
        int covered = 0;
        for (String k : reqDescriptions.keySet()) {
            Integer c = reqCount.get(k);
            covered += (c != null && c > 0) ? 1 : 0;
        }
        return covered;
    }

    @Override
    public int getMaximum() {
        return reqCount.size();
    }

    @Override
    public float getPercentage() {
        int max = getMaximum();
        return max == 0 ? 0.0f : (1.0f * getCoverage() / max);
    }

    @Override
    public Map<Object, Integer> getDetails() {
        return reqCount;
    }

    @Override
    public void setGraph(InspectableGraph inspectableGraph, Map<Object, Vertex> map) {
        // not used
    }

    @Override
    public String getName() {
        return "Product coverage (from produits.csv)";
    }

    @Override
    public Model getModel() {
        return model;
    }

    @Override
    public void setModel(Model model) {
        this.model = model;
    }

    @Override
    public void doneReset(String reason, boolean testing) {
        // nothing
    }

    @Override
    public void doneGuard(Object o, int i, boolean b, int i1) {
        // nothing
    }

    @Override
    public void startAction(Object o, int i, String s) {
        // nothing
    }

    @Override
    public void doneTransition(int action, Transition t) {
        // When we see AjouterArticleOK at least once, we mark ALL products as "covered once".
        // This is the best we can do using only Transition info (no EAN parameter).
        // If you want true per-EAN coverage, we need adapter/model instrumentation.
        String act = String.valueOf(t.getAction());
        if (COVERING_ACTION.equals(act)) {
            for (Object reqId : reqCount.keySet()) {
                if (reqCount.get(reqId) == 0) {
                    reqCount.put(reqId, 1);
                }
            }
        }
    }

    @Override
    public void failure(TestFailureException e) {
        // nothing
    }

    @Override
    public String toString() {
        return getCoverage() + "/" + getMaximum();
    }

    public Map<String, String> getRequirementDescriptions() {
        return reqDescriptions;
    }
}
