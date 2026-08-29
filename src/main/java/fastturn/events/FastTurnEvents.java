package fastturn.events;

public interface FastTurnEvents {
    /**
     * Fired when the user has completed their conversational turn.
     * Triggers LLM inference and subsequent FastTTS streaming.
     */
    void onTurnComplete(String finalTranscript);

    /**
     * Fired when the user begins speaking while the agent is replying.
     * Triggers immediate FastTTS audio playback cancellation.
     */
    void onBargeIn();
}