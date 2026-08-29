package fastturn;

public final class SilenceTimer {
    private int elapsedMs = 0;

    public void reset() {
        this.elapsedMs = 0;
    }

    public void add(int deltaMs) {
        this.elapsedMs += deltaMs;
    }

    public int getElapsedMs() {
        return elapsedMs;
    }
}