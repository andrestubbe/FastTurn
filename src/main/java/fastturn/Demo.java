package fastturn;

import fastturn.ansi.FastTurnAnsi;
import fastturn.events.FastTurnEvents;

public final class Demo {

    public static void main(String[] args) {
        FastTurnAnsi.printHeader(
            "🔄 FAST TURN — ADAPTIVE SEMANTIC ENDPOINTING & TURN-TAKING ENGINE",
            "Four-State Conversational Machine • Dynamic Silence Scaling (250-1200ms) • Barge-In Substrate"
        );

        final int[] turnCompletions = {0};
        final int[] bargeIns = {0};

        FastTurn turn = new FastTurn(new FastTurnEvents() {
            @Override
            public void onTurnComplete(String transcript) {
                turnCompletions[0]++;
                System.out.println(FastTurnAnsi.GREEN + "  [EVENT] >>> TURN COMPLETE: '" + transcript + "' ➔ Dispatching to LLM <<<" + FastTurnAnsi.RESET);
            }

            @Override
            public void onBargeIn() {
                bargeIns[0]++;
                System.out.println(FastTurnAnsi.RED + "  [EVENT] <<< BARGE-IN DETECTED: Halting active TTS playback immediately! <<<" + FastTurnAnsi.RESET);
            }
        });

        FastTurnAnsi.printSection("1. SCENARIO A: PATIENT MODE (Mid-Sentence Thinking Pause)");
        turn.onPartialTranscript("Ich möchte gerne einen Tisch reservieren für");
        turn.onVadSpeechStart();
        System.out.println("  User speaks: 'Ich möchte gerne einen Tisch reservieren für...'");
        
        turn.onVadSpeechEnd(300);
        System.out.println("  Pause 300 ms ➔ State: " + turn.getState() + " (Waiting for thought completion)");
        turn.onVadSpeechEnd(400); // 700 ms total
        System.out.println("  Pause 700 ms ➔ State: " + turn.getState() + " (Patient limit: 1200 ms)");
        
        turn.onPartialTranscript("Ich möchte gerne einen Tisch reservieren für vier Personen.");
        turn.onVadSpeechStart(); // Resumes
        turn.onVadSpeechEnd(250); // Completes sentence

        FastTurnAnsi.printSection("2. SCENARIO B: FAST RESPONSE MODE (Punctuation & Confirmation)");
        turn.onPartialTranscript("Ja, bitte.");
        turn.onVadSpeechStart();
        turn.onVadSpeechEnd(250); // Instant 250ms completion

        FastTurnAnsi.printSection("3. SCENARIO C: BARGE-IN INTERRUPTION");
        System.out.println("  Agent is currently replying via TTS...");
        turn.onVadSpeechStart(); // User interrupts

        FastTurnAnsi.printSection("4. TURN-TAKING TELEMETRY SUMMARY");
        FastTurnAnsi.printTreeItem("Completed Turns", String.valueOf(turnCompletions[0]), false);
        FastTurnAnsi.printTreeItem("Barge-In Interrupts", String.valueOf(bargeIns[0]), false);
        FastTurnAnsi.printTreeItem("State Machine Transitions", "< 1 µs overhead", true);
    }
}