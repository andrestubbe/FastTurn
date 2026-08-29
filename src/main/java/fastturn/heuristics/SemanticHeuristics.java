package fastturn.heuristics;

import java.util.Locale;

public final class SemanticHeuristics {

    private static final String[] HESITATION_CONNECTORS = {
        "und", "oder", "weil", "aber", "denn", "falls", "wenn",
        "bei", "nach", "mit", "von", "zu", "im", "in", "auf",
        "äh", "ähm", "hmm", "uh", "um", "and", "or", "because", "but", "if"
    };

    /**
     * Dynamically calculates silence threshold in milliseconds based on semantic sentence completeness.
     */
    public int computeAdaptiveSilenceLimit(String partialTranscript) {
        if (partialTranscript == null || partialTranscript.trim().isEmpty()) {
            return 800; // Default conversation tempo
        }

        String text = partialTranscript.trim().toLowerCase(Locale.ROOT);

        // Fast Response Mode (250 ms)
        if (text.endsWith(".") || text.endsWith("!") || text.endsWith("?") ||
            text.equals("ja") || text.equals("nein") || text.equals("ok") || text.equals("yes") || text.equals("no")) {
            return 250;
        }

        // Patient Mode (1200 ms) for thought pauses & mid-sentence connectors
        for (String conn : HESITATION_CONNECTORS) {
            if (text.endsWith(" " + conn) || text.equals(conn)) {
                return 1200;
            }
        }

        return 800; // Standard adaptive pause limit
    }
}