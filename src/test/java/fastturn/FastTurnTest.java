package fastturn;

import fastturn.events.FastTurnEvents;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class FastTurnTest {

    @Test
    public void testAdaptiveSilenceLimit() {
        int[] turns = {0};
        FastTurn turn = new FastTurn(new FastTurnEvents() {
            @Override public void onTurnComplete(String t) { turns[0]++; }
            @Override public void onBargeIn() {}
        });

        // 1. Thinking pause with connector 'und' -> limit is 1200ms
        turn.onPartialTranscript("Ich brauche ein Ticket nach Hamburg und");
        turn.onVadSpeechStart();
        turn.onVadSpeechEnd(500);
        assertEquals(TurnState.COUNTING_SILENCE, turn.getState());
        assertEquals(0, turns[0]);

        turn.onVadSpeechEnd(700); // 1200ms reached
        assertEquals(TurnState.AGENT_REPLYING, turn.getState());
        assertEquals(1, turns[0]);
    }

    @Test
    public void testBargeInInterruption() {
        int[] bargeInCount = {0};
        FastTurn turn = new FastTurn(new FastTurnEvents() {
            @Override public void onTurnComplete(String t) {}
            @Override public void onBargeIn() { bargeInCount[0]++; }
        });

        turn.onPartialTranscript("Fertig.");
        turn.onVadSpeechStart();
        turn.onVadSpeechEnd(250);
        assertEquals(TurnState.AGENT_REPLYING, turn.getState());

        // User speaks while agent replying
        turn.onVadSpeechStart();
        assertEquals(1, bargeInCount[0]);
        assertEquals(TurnState.USER_SPEAKING, turn.getState());
    }
}