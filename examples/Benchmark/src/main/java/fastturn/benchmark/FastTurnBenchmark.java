package fastturn.benchmark;

import fastturn.FastTurn;
import fastturn.events.FastTurnEvents;
import fastturn.heuristics.SemanticHeuristics;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Warmup(iterations = 2, time = 1)
@Measurement(iterations = 3, time = 1)
@Fork(1)
public class FastTurnBenchmark {

    private FastTurn turn;
    private SemanticHeuristics heuristics;
    private String connectorText;
    private String completedText;

    @Setup
    public void setup() {
        turn = new FastTurn(new FastTurnEvents() {
            @Override public void onTurnComplete(String t) {}
            @Override public void onBargeIn() {}
        });

        heuristics = new SemanticHeuristics();
        connectorText = "Ich möchte gerne einen Tisch reservieren für";
        completedText = "Ja, bitte.";
    }

    @Benchmark
    public int benchmarkSemanticHeuristicsConnector() {
        return heuristics.computeAdaptiveSilenceLimit(connectorText);
    }

    @Benchmark
    public int benchmarkSemanticHeuristicsComplete() {
        return heuristics.computeAdaptiveSilenceLimit(completedText);
    }

    @Benchmark
    public void benchmarkFullTurnCycle() {
        turn.onPartialTranscript(completedText);
        turn.onVadSpeechStart();
        turn.onVadSpeechEnd(250);
    }
}