package scanette;

import nz.ac.waikato.jdsl.graph.api.InspectableGraph;
import nz.ac.waikato.jdsl.graph.api.Vertex;
import nz.ac.waikato.modeljunit.Model;
import nz.ac.waikato.modeljunit.TestFailureException;
import nz.ac.waikato.modeljunit.Transition;
import nz.ac.waikato.modeljunit.coverage.CoverageMetric;

import java.io.*;
import java.util.*;

/**
 * Requirements coverage based on requirements.csv:
 * REQxxx,Description,STATE;event;STATE,STATE;event;STATE,...
 *
 * A requirement is covered if at least one of its listed transitions is observed.
 */
public class RequirementCoverage implements CoverageMetric {

    private Model model;

    private final Map<String, String> reqDescriptions = new LinkedHashMap<>();

    private final Map<String, Set<TransitionKey>> reqTransitions = new LinkedHashMap<>();

    private final Map<Object, Integer> reqCount = new LinkedHashMap<>();

    private static final String CSV_DELIM = ",";

     public RequirementCoverage(File requirementsCsv) throws IOException {
        load(requirementsCsv);
        clear();
    }

    private void load(File f) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;

                // split only first 2 commas: REQ, desc, transitions...
                String[] parts = splitFirstTwoCommas(line);
                if (parts.length < 2) continue;

                String reqId = parts[0].trim();
                String desc = parts[1].trim();
                String transitionsPart = (parts.length >= 3) ? parts[2].trim() : "";

                reqDescriptions.put(reqId, desc);

                Set<TransitionKey> keys = new LinkedHashSet<>();
                if (!transitionsPart.isEmpty()) {
                    // transitions are comma-separated, each is "SRC;EVENT;DST"
                    String[] trans = transitionsPart.split(CSV_DELIM);
                    for (String t : trans) {
                        TransitionKey key = TransitionKey.parse(t.trim());
                        if (key != null) keys.add(key);
                    }
                }
                reqTransitions.put(reqId, keys);
                reqCount.put(reqId, 0);
            }
        }
    }

    // Helper: "a,b,c,d" -> [a, b, "c,d"] with only first two commas
    private static String[] splitFirstTwoCommas(String s) {
        int c1 = s.indexOf(',');
        if (c1 < 0) return new String[]{s};
        int c2 = s.indexOf(',', c1 + 1);
        if (c2 < 0) return new String[]{s.substring(0, c1), s.substring(c1 + 1)};
        return new String[]{s.substring(0, c1), s.substring(c1 + 1, c2), s.substring(c2 + 1)};
    }

    @Override
    public String getName() {
        return "Requirements coverage (requirements.csv)";
    }

    @Override
    public String getDescription() {
        return "Covers a requirement if any listed (src;event;dst) transition is observed.";
    }

    @Override
    public void clear() {
        for (Object k : reqCount.keySet()) reqCount.put(k, 0);
    }

    @Override
    public int getCoverage() {
        int covered = 0;
        for (Object k : reqCount.keySet()) {
            Integer c = reqCount.get(k);
            if (c != null && c > 0) covered++;
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

    public Map<String, String> getRequirementDescriptions() {
        return reqDescriptions;
    }

    @Override
    public void setGraph(InspectableGraph inspectableGraph, Map<Object, Vertex> map) {
        // not used
    }

    @Override
    public Model getModel() {
        return model;
    }

    @Override
    public void setModel(Model model) {
        this.model = model;
    }

    @Override public void doneReset(String reason, boolean testing) {}
    @Override public void doneGuard(Object state, int action, boolean enabled, int value) {}
    @Override public void startAction(Object state, int action, String name) {}
    @Override public void failure(TestFailureException e) {}

    @Override
    public void doneTransition(int actionIndex, Transition t) {
        // Build the observed transition key
        TransitionKey observed = TransitionKey.from(t);
        if (observed == null) return;

        // Mark requirement covered if observed matches any of its transitions
        for (String reqId : reqTransitions.keySet()) {
            Set<TransitionKey> expected = reqTransitions.get(reqId);
            if (expected == null || expected.isEmpty()) continue;

            if (expected.contains(observed)) {
                // cover once (0->1). If you want multiple hits, replace by ++
                if (reqCount.get(reqId) == 0) reqCount.put(reqId, 1);
            }
        }
    }

    @Override
    public String toString() {
        return getCoverage() + "/" + getMaximum();
    }

    /** Transition signature: src;event;dst */
    private static final class TransitionKey {
        final String src;
        final String event;
        final String dst;

        private TransitionKey(String src, String event, String dst) {
            this.src = normalize(src);
            this.event = normalize(event);
            this.dst = normalize(dst);
        }

        static TransitionKey parse(String s) {
            if (s == null || s.isEmpty()) return null;
            String[] p = s.split(";", -1);
            if (p.length != 3) return null;
            return new TransitionKey(p[0], p[1], p[2]);
        }

        static TransitionKey from(Transition t) {
            try {
                // Most ModelJUnit versions have these getters
                Object start = t.getStartState();
                Object end = t.getEndState();
                Object act = t.getAction();
                return new TransitionKey(
                        String.valueOf(start),
                        String.valueOf(act),
                        String.valueOf(end)
                );
            } catch (Throwable ignored) {
                return null;
            }
        }

        private static String normalize(String s) {
            return s == null ? "" : s.trim();
        }

        @Override public boolean equals(Object o) {
            if (!(o instanceof TransitionKey)) return false;
            TransitionKey other = (TransitionKey) o;
            return src.equals(other.src) && event.equals(other.event) && dst.equals(other.dst);
        }

        @Override public int hashCode() {
            return Objects.hash(src, event, dst);
        }

        @Override public String toString() {
            return src + ";" + event + ";" + dst;
        }
    }
}
