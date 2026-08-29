package fastturn;

import fastturn.events.FastTurnEvents;
import fastturn.heuristics.SemanticHeuristics;

public final class FastTurn {

    private final FastTurnEvents events;
    private final SemanticHeuristics heuristics = new SemanticHeuristics();
    private final SilenceTimer timer = new SilenceTimer();

    private TurnState state = TurnState.LISTENING;
    private String partialTranscript = "";

    public FastTurn(FastTurnEvents events) {
        this.events = events != null ? events : new FastTurnEvents() {
            @Override public void onTurnComplete(String t) {}
            @Override public void onBargeIn() {}
        };
    }

    /**
     * Invoked when FastVAD detects active speech.
     */
    public void onVadSpeechStart() {
        if (state == TurnState.AGENT_REPLYING) {
            events.onBargeIn();
        }
        this.state = TurnState.USER_SPEAKING;
        timer.reset();
    }

    /**
     * Invoked on silence ticks with elapsed delta milliseconds.
     */
    public void onVadSpeechEnd(int deltaMs) {
        if (state == TurnState.USER_SPEAKING || state == TurnState.COUNTING_SILENCE) {
            state = TurnState.COUNTING_SILENCE;
            timer.add(deltaMs);

            int limit = heuristics.computeAdaptiveSilenceLimit(partialTranscript);

            if (timer.getElapsedMs() >= limit) {
                state = TurnState.AGENT_REPLYING;
                events.onTurnComplete(partialTranscript);
                timer.reset();
            }
        }
    }

    /**
     * Updates streaming partial transcript from FastSTT.
     */
    public void onPartialTranscript(String text) {
        this.partialTranscript = text != null ? text : "";
    }

    public TurnState getState() { return state; }
    public int getSilenceMs() { return timer.getElapsedMs(); }
    public String getPartialTranscript() { return partialTranscript; }
}