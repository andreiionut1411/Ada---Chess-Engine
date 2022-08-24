public class Position {
    long hash;
    String bestMove;
    int depth;
    int score;

    public Position(long hash, String bestMove, int depth, int score) {
        this.hash = hash;
        this.bestMove = bestMove;
        this.depth = depth;
        this.score = score;
    }
}
