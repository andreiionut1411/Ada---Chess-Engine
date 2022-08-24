import java.security.SecureRandom;

public class Transposition {

    private static final int numberOfEntries = 1 << 23; // we have 8 million entries in the transposition table
    private static final int mask = 0x7fffff;

    // We have a two-tier system replacement strategy to deal with collisions.
    public static Position[] table1 = new Position[numberOfEntries]; // depth preferred table
    public static Position[] table2 = new Position[numberOfEntries]; // always-replace entry

    // For the Zobrist array we have 2 colors, 6 pieces, and a whole board to generate keys for.
    private static long[][][] zobristArray = new long[2][6][64];
    private static long[] zobristEnPassant = new long[8];
    private static long[] zobristCastling = new long[4];
    private static long zobristBlack;

    // We initialise all the random numbers used for Zobrist hashing.
    public static void initialiseZobristHash() {
        SecureRandom random = new SecureRandom();

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 6; j++) {
                for (int k = 0; k < 64; k++) {
                    zobristArray[i][j][k] = random.nextLong();
                }
            }
        }

        for (int i = 0; i < 8; i++){
            zobristEnPassant[i] = random.nextLong();
        }

        for (int i = 0; i < 4; i++) {
            zobristCastling[i] = random.nextLong();
        }

        zobristBlack = random.nextLong();
    }

    // For a certain position of the board we calculate the Zobrist hash.
    public static long makeZobristHash(long wp, long wr, long wn, long wb, long wq, long wk,
                                       long bp, long br, long bn, long bb, long bq, long bk,
                                       long ep, boolean wck, boolean wcq, boolean bck,
                                       boolean bcq, boolean white) {

        long empty = ~(wp | wr | wn | wb | wq | wk | bp | br | bn | bb | bq | bk);
        long hash = 0L;

        for (int i = 0; i < 64; i++) {
            if ((empty & (1L << (63 - i))) != 0) {}
            else if ((wp & (1L << (63 - i))) != 0) hash = hash ^ zobristArray[0][0][i];
            else if ((bp & (1L << (63 - i))) != 0) hash = hash ^ zobristArray[1][0][i];
            else if ((wr & (1L << (63 - i))) != 0) hash = hash ^ zobristArray[0][1][i];
            else if ((br & (1L << (63 - i))) != 0) hash = hash ^ zobristArray[1][1][i];
            else if ((wn & (1L << (63 - i))) != 0) hash = hash ^ zobristArray[0][2][i];
            else if ((bn & (1L << (63 - i))) != 0) hash = hash ^ zobristArray[1][2][i];
            else if ((wb & (1L << (63 - i))) != 0) hash = hash ^ zobristArray[0][3][i];
            else if ((bb & (1L << (63 - i))) != 0) hash = hash ^ zobristArray[1][3][i];
            else if ((wq & (1L << (63 - i))) != 0) hash = hash ^ zobristArray[0][4][i];
            else if ((bq & (1L << (63 - i))) != 0) hash = hash ^ zobristArray[1][4][i];
            else if ((wk & (1L << (63 - i))) != 0) hash = hash ^ zobristArray[0][5][i];
            else if ((bk & (1L << (63 - i))) != 0) hash = hash ^ zobristArray[1][5][i];
        }

        for (int i = 0; i < 8; i++) {
            if (ep == 0L) break;
            else if ((ep & MoveGen.fileMask[i]) != 0) {
                hash = hash ^ zobristEnPassant[i];
                break;
            }
        }

        if (wck) hash = hash ^ zobristCastling[0];

        if (wcq) hash = hash ^ zobristCastling[1];

        if (bck) hash = hash ^ zobristCastling[2];

        if (bcq) hash = hash ^ zobristCastling[3];

        if (!white) hash = hash ^ zobristBlack;

        return hash;
    }

    // The method searches the transposition table to see if the position was already found.
    // It returns the information from the table if true, otherwise it returns null.
    public static Position getInformationFromTransposition(long hash) {
        int location = (int)((hash & mask) % numberOfEntries);

        if (table1[location] == null) return null;
        else {
            if (table1[location].hash == hash) return table1[location];

            // If table1 does not have a position, then table 2 certainly doesn't.
            if (table2[location] != null) {
                if (table2[location].hash == hash) return table2[location];
            }
        }

        return null;
    }

    // We add a position to the transposition table. We do not verify if the entry already exists,
    // because we add information to the transposition table only because we didn't find the entry
    // in the first place.
    public static void addPosition(long hash, String bestMove, int depth, int score) {
        int location = (int) ((hash & mask) % numberOfEntries);

        if (table1[location] == null) {
            table1[location] = new Position(hash, bestMove, depth, score);
            return;
        }
        else {
            if (table1[location].hash == hash && table1[location].depth == depth) return;

            // We add the entry to the depth preferred hash table.
            if (table1[location].depth < depth) {
                table1[location] = new Position(hash, bestMove, depth, score);
                return;
            }
        }

        if (table2[location] == null) {
            table2[location] = new Position(hash, bestMove, depth, score);
        } else {
            if (table2[location].hash != hash || table2[location].depth != depth) {
                table2[location] = new Position(hash, bestMove, depth, score);
            }
        }
    }
}
