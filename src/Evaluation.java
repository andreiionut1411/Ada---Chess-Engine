public class Evaluation {
    private static final MoveGen moveGen = new MoveGen();
    private static final int[] mg_value = { 82, 337, 365, 477, 1025,  0};
    private static final int[] eg_value = { 94, 281, 297, 512,  936,  0};

    private static final int[] mg_pawn = {
             0,   0,   0,   0,   0,    0,  0,   0,
             98, 134,  61,  95,  68, 126, 34, -11,
             -6,   7,  26,  31,  65,  56, 25, -20,
            -14,  13,   6,  21,  23,  12, 17, -23,
            -27,  -2,  -5,  12,  17,   6, 10, -25,
            -26,  -4,  -4, -10,   3,   3, 33, -12,
            -35,  -1, -20, -23, -15,  24, 38, -22,
              0,   0,   0,   0,   0,   0,  0,   0,
    };

    private static final int[] eg_pawn = {
            0,   0,   0,   0,   0,   0,   0,   0,
            178, 173, 158, 134, 147, 132, 165, 187,
            94, 100,  85,  67,  56,  53,  82,  84,
            32,  24,  13,   5,  -2,   4,  17,  17,
            13,   9,  -3,  -7,  -7,  -8,   3,  -1,
            4,    7,  -6,   1,   0,  -5,  -1,  -8,
            13,   8,   8,  10,  13,   0,   2,  -7,
            0,    0,   0,   0,   0,   0,   0,   0
    };

    private static final int[] mg_knight = {
            -167, -89, -34, -49,  61, -97, -15, -107,
            -73,  -41,  72,  36,  23,  62,   7,  -17,
            -47,   60,  37,  65,  84, 129,  73,   44,
            -9,    17,  19,  53,  37,  69,  18,   22,
            -13,    4,  16,  13,  28,  19,  21,   -8,
            -23,   -9,  12,  10,  19,  17,  25,  -16,
            -29,  -53, -12,  -3,  -1,  18, -14,  -19,
            -105, -21, -58, -33, -17, -28, -19,  -23
    };

    private static final int[] eg_knight = {
            -58, -38, -13, -28, -31, -27, -63, -99,
            -25,  -8, -25,  -2,  -9, -25, -24, -52,
            -24, -20,  10,   9,  -1,  -9, -19, -41,
            -17,   3,  22,  22,  22,  11,   8, -18,
            -18,  -6,  16,  25,  16,  17,   4, -18,
            -23,  -3,  -1,  15,  10,  -3, -20, -22,
            -42, -20, -10,  -5,  -2, -20, -23, -44,
            -29, -51, -23, -15, -22, -18, -50, -64
    };

    private static final int[] mg_bishop = {
            -29,   4, -82, -37, -25, -42,   7,  -8,
            -26,  16, -18, -13,  30,  59,  18, -47,
            -16,  37,  43,  40,  35,  50,  37,  -2,
            -4,    5,  19,  50,  37,  37,   7,  -2,
            -6,   13,  13,  26,  34,  12,  10,   4,
             0,   15,  15,  15,  14,  27,  18,  10,
             4,   15,  16,   0,   7,  21,  33,   1,
            -33,  -3, -14, -21, -13, -12, -39, -21
    };

    private static final int[] eg_bishop = {
            -14, -21, -11,  -8, -7,  -9, -17, -24,
             -8,  -4,   7, -12, -3, -13,  -4, -14,
              2,  -8,   0,  -1, -2,   6,   0,   4,
             -3,   9,  12,   9, 14,  10,   3,   2,
             -6,   3,  13,  19,  7,  10,  -3,  -9,
            -12,  -3,   8,  10, 13,   3,  -7, -15,
            -14, -18,  -7,  -1,  4,  -9, -15, -27,
            -23,  -9, -23,  -5, -9, -16,  -5, -17,
    };

    private static final int[] mg_rook = {
             32,  42,  32,  51, 63,  9,  31,  43,
             27,  32,  58,  62, 80, 67,  26,  44,
             -5,  19,  26,  36, 17, 45,  61,  16,
            -24, -11,   7,  26, 24, 35,  -8, -20,
            -36, -26, -12,  -1,  9, -7,   6, -23,
            -45, -25, -16, -17,  3,  0,  -5, -33,
            -44, -16, -20,  -9, -1, 11,  -6, -71,
            -19, -13,   1,  17, 16,  7, -37, -26,
    };

    private static final int[] eg_rook = {
            13, 10, 18, 15, 12,  12,   8,   5,
            11, 13, 13, 11, -3,   3,   8,   3,
             7,  7,  7,  5,  4,  -3,  -5,  -3,
             4,  3, 13,  1,  2,   1,  -1,   2,
             3,  5,  8,  4, -5,  -6,  -8, -11,
            -4,  0, -5, -1, -7, -12,  -8, -16,
            -6, -6,  0,  2, -9,  -9, -11,  -3,
            -9,  2,  3, -1, -5, -13,   4, -20,
    };

    private static final int[] mg_queen = {
            -28,   0,  29,  12,  59,  44,  43,  45,
            -24, -39,  -5,   1, -16,  57,  28,  54,
            -13, -17,   7,   8,  29,  56,  47,  57,
            -27, -27, -16, -16,  -1,  17,  -2,   1,
             -9, -26,  -9, -10,  -2,  -4,   3,  -3,
            -14,   2, -11,  -2,  -5,   2,  14,   5,
            -35,  -8,  11,   2,   8,  15,  -3,   1,
             -1, -18,  -9,  10, -15, -25, -31, -50,
    };

    private static final int[] eg_queen = {
            -9,  22,  22,  27,  27,  19,  10,  20,
            -17,  20,  32,  41,  58,  25,  30,   0,
            -20,   6,   9,  49,  47,  35,  19,   9,
              3,  22,  24,  45,  57,  40,  57,  36,
            -18,  28,  19,  47,  31,  34,  39,  23,
            -16, -27,  15,   6,   9,  17,  10,   5,
            -22, -23, -30, -16, -16, -23, -36, -32,
            -33, -28, -22, -43,  -5, -32, -20, -41,
    };

    private static final int[] mg_king = {
            -65,  23,  16, -15, -56, -34,   2,  13,
             29,  -1, -20,  -7,  -8,  -4, -38, -29,
             -9,  24,   2, -16, -20,   6,  22, -22,
            -17, -20, -12, -27, -30, -25, -14, -36,
            -49,  -1, -27, -39, -46, -44, -33, -51,
            -14, -14, -22, -46, -44, -30, -15, -27,
              1,   7,  -8, -64, -43, -16,   9,   8,
            -15,  36,  12, -54,   8, -28,  24,  14,
    };

    private static final int[] eg_king = {
            -74, -35, -18, -18, -11,  15,   4, -17,
            -12,  17,  14,  17,  17,  38,  23,  11,
             10,  17,  23,  15,  20,  45,  44,  13,
             -8,  22,  24,  27,  26,  33,  26,   3,
            -18,  -4,  21,  24,  27,  23,   9, -11,
            -19,  -3,  11,  21,  23,  16,   7,  -9,
            -27, -11,   4,  13,  14,   4,  -5, -17,
            -53, -34, -21, -11, -28, -14, -24, -43
    };

    // We use a lazy evaluation, that first calculates the material and general position of pieces,
    // and for moves where we are close to alpha and beta, we evaluate more in depth.
    public static int eval(long wp, long wr, long wn, long wb, long wq, long wk,
                           long bp, long br, long bn, long bb, long bq, long bk, boolean endGame,
                           int alpha, int beta, int numOfPseudoMoves, boolean isWhite, int ply) {

        int evaluation = 0;
        evaluation += evaluateMaterialAndPosition(wp, wr, wn, wb, wq, wk, bp, br, bn, bb, bq, bk, endGame);

        // If we are winning by 2 pawns, we don't need to go deeper into analysis.
        if ((evaluation <= alpha - 270 || evaluation >= beta + 270) && !endGame) {
            return evaluation;
        }

        return evaluation + stageTwoEval(wp, wr, wn, wb, wq, wk, bp, br, bn, bb, bq, bk, numOfPseudoMoves, isWhite,
                endGame, ply);
    }

    // The method calculates the material points and the points based on the position of the pieces.
    private static int evaluateMaterialAndPosition(long wp, long wr, long wn, long wb, long wq, long wk,
                                        long bp, long br, long bn, long bb, long bq, long bk, boolean endGame) {

        MoveGen.empty = ~(wp | wr | wn | wb | wq | wk | bp | br | bb | bn | bq | bk);
        int evaluation = 0;
        for (int i = 0; i < 64; i ++) {
            if ((MoveGen.empty & (1L << (63 - i))) == 0) {
                if ((wp & (1L << (63 - i))) != 0) {
                    if (!endGame) evaluation += mg_pawn[i] + mg_value[0];
                    else evaluation += eg_pawn[i] + eg_value[0];
                } else if ((bp & (1L << (63 - i))) != 0) {
                    if (!endGame) evaluation -= (mg_pawn[i ^ 56] + mg_value[0]);
                    else evaluation -= (eg_pawn[i ^ 56] + eg_value[0]);
                } else if ((wn & (1L << (63 - i))) != 0) {
                    if (!endGame) evaluation += mg_knight[i] + mg_value[1];
                    else evaluation += eg_knight[i] + eg_value[1];
                } else if ((bn & (1L << (63 - i))) != 0) {
                  if (!endGame) evaluation -= (mg_knight[i ^ 56] + mg_value[1]);
                  else evaluation -= (eg_knight[i ^ 56] + eg_value[1]);
                } else if ((wb & (1L << (63 - i))) != 0) {
                    if (!endGame) evaluation += mg_bishop[i] + mg_value[2];
                    else evaluation += eg_bishop[i] + eg_value[2];
                } else if ((bb & (1L << (63 - i))) != 0) {
                    if (!endGame) evaluation -= (mg_bishop[i ^ 56] + mg_value[2]);
                    else evaluation -= (eg_bishop[i ^ 56] + eg_value[2]);
                } else if ((wr & (1L << (63 - i))) != 0) {
                    if (!endGame) evaluation += mg_rook[i] + mg_value[3];
                    else evaluation += eg_rook[i] + eg_value[3];
                } else if ((br & (1L << (63 - i))) != 0) {
                    if (!endGame) evaluation -= (mg_rook[i ^ 56] + mg_value[3]);
                    else evaluation -= (eg_rook[i ^ 56] + eg_value[3]);
                } else if ((wq & (1L << (63 - i))) != 0) {
                    if (!endGame) evaluation += mg_queen[i] + mg_value[4];
                    else evaluation += eg_queen[i] + eg_value[4];
                } else if ((bq & (1L << (63 - i))) != 0) {
                    if (!endGame) evaluation -= (mg_queen[i ^ 56] + mg_value[4]);
                    else evaluation -= (eg_queen[i ^ 56] + eg_value[4]);
                } else if ((wk & (1L << (63 - i))) != 0) {
                    if (!endGame) evaluation += mg_king[i] + mg_value[5];
                    else evaluation += eg_king[i] + eg_value[5];
                } else if ((bk & (1L << (63 - i))) != 0){
                    if (!endGame) evaluation -= (mg_king[i ^ 56] + mg_value[5]);
                    else evaluation -= (eg_king[i ^ 56] + eg_value[5]);
                }
            }
        }

        return evaluation;
    }

    // In the more in-depth evaluation, we evaluate mobility, pawn structure and space.
    private static int stageTwoEval(long wp, long wr, long wn, long wb, long wq, long wk,
                                    long bp, long br, long bn, long bb, long bq, long bk,
                                    int numOfPseudoMoves, boolean isWhite, boolean endGame, int ply) {

        long occupied = wp | wr | wn | wb | wq | wk | bp | br | bb | bn | bq | bk;
        long notWhitePieces = ~(wp | wr | wn | wb | wq | wk);
        long notBlackPieces = ~(bp | br | bn | bb | bq | bk);
        int numberOfPieces = -1;
        int evaluation = 0;

        if (endGame) {
            numberOfPieces = numberOfPieces(occupied);

            // In the endgame, the king needs to aid the player in trying to give checkmate.
            if (numberOfPieces <= 15) {
                evaluation += ((evaluateKingPosition(wk, bk, numberOfPieces)) -
                        evaluateKingPosition(bk, wk, numberOfPieces));
            }
        }

        evaluation = evaluation + evaluatePawnStructureW(wp, bp, occupied, endGame, ply, numberOfPieces) -
                evaluatePawnStructureB(wp, bp, occupied, endGame, ply, numberOfPieces);

        evaluation += (evaluateCenterControl(wp, wr, wn, wb, wq, wk, notWhitePieces, true, endGame) -
                       evaluateCenterControl(bp, br, bn, bb, bq, bk, notBlackPieces, false, endGame));

        String opponentMoves = moveGen.possibleMoves(wp, wr, wn, wb, wq, wk, bp, br, bn, bb, bq, bk, 0L, !isWhite,
                false, false, false, false);

        if (isWhite) {
            evaluation += (numOfPseudoMoves - opponentMoves.length() / 4) * 4;
        } else {
            evaluation += (opponentMoves.length() / 4 - numOfPseudoMoves) * 4;
        }

        return evaluation;
    }

    // The method returns the bonus for the pawn structure for white.
    private static int evaluatePawnStructureW(long wp, long bp, long occupied, boolean endGame, int ply, int number) {
        int evaluation = 0;
        long bitboard = (wp & (wp << 7) & ~MoveGen.fileA) | (wp & (wp << 9) & ~MoveGen.fileH);
        long location = bitboard & -bitboard;

        // pawn phalanx
        while (location != 0) {
            int position = Long.numberOfTrailingZeros(location);
            evaluation += 5;

            bitboard = bitboard & ~(1L << position);
            location = bitboard & -bitboard;
        }

        // blocked pawns
        bitboard = ((wp << 8) & occupied);
        location = bitboard & -bitboard;

        while (location != 0) {
            int position = Long.numberOfTrailingZeros(location);
            evaluation -= 4;

            bitboard = bitboard & ~(1L << position);
            location = bitboard & -bitboard;
        }

        // doubled pawns.
        for (int i = 0; i < 8; i++) {
            bitboard = wp & MoveGen.fileMask[i];
            location = bitboard & -bitboard;
            int counter = 0;

            while (location != 0) {
                int position = Long.numberOfTrailingZeros(location);
                counter++;

                bitboard = bitboard & ~(1L << position);
                location = bitboard & -bitboard;
            }

            if (counter > 1) evaluation -= (counter - 1) * 10; // tripled pawns are even worse.
        }

        if (endGame || ply > 30) {

            if (number == -1){
                evaluation += (evaluatePassedPawnW(wp, bp)) * 25;
            } else {
                evaluation += (evaluatePassedPawnW(wp, bp)) * (16 - number) * 5;
            }
        }

        return evaluation;
    }

    // The method returns the bonus for the pawn structure for black.
    private static int evaluatePawnStructureB(long wp, long bp, long occupied, boolean endGame, int ply, int number) {
        int evaluation = 0;
        long bitboard = (bp & (bp >>> 7) & ~MoveGen.fileH) | (bp & (bp >>> 9) & ~MoveGen.fileA);
        long location = bitboard & -bitboard;

        // pawn phalanx
        while (location != 0) {
            int position = Long.numberOfTrailingZeros(location);
            evaluation += 6;

            bitboard = bitboard & ~(1L << position);
            location = bitboard & -bitboard;
        }

        // blocked pawns
        bitboard = ((bp >>> 8) & occupied);
        location = bitboard & -bitboard;

        while (location != 0) {
            int position = Long.numberOfTrailingZeros(location);
            evaluation -= 4;

            bitboard = bitboard & ~(1L << position);
            location = bitboard & -bitboard;
        }

        // doubled pawns.
        for (int i = 0; i < 8; i++) {
            bitboard = bp & MoveGen.fileMask[i];
            location = bitboard & -bitboard;
            int counter = 0;

            while (location != 0) {
                int position = Long.numberOfTrailingZeros(location);
                counter++;

                bitboard = bitboard & ~(1L << position);
                location = bitboard & -bitboard;
            }

            if (counter > 1) evaluation -= (counter - 1) * 10; // tripled pawns are even worse.
        }

        if (endGame || ply > 30) {
            if (number == -1){
                evaluation += (evaluatePassedPawnB(wp, bp)) * 25;
            } else {
                evaluation += (evaluatePassedPawnB(wp, bp)) * (16 - number) * 5;
            }
        }

        return evaluation;
    }

    // The method counts the number of passed pawns.
    private static int evaluatePassedPawnW(long wp, long bp) {
        long location = wp & -wp;
        int counter = 0;

        while (location != 0) {
            int position = Long.numberOfLeadingZeros(location);
            int x = position % 8;
            int y = position / 8;

            if ((bp & (MoveGen.fileMask[x]) & MoveGen.firstRanks[y - 1]) != 0) {
                wp = wp & ~location;
                location = wp & -wp;
                continue;
            }

            if (x > 0) {
                if ((bp & (MoveGen.fileMask[x - 1]) & MoveGen.firstRanks[y - 1]) != 0){
                    wp = wp & ~location;
                    location = wp & -wp;
                    continue;
                }
            }

            if (x < 7) {
                if ((bp & (MoveGen.fileMask[x + 1]) & MoveGen.firstRanks[y - 1]) != 0) {
                    wp = wp & ~location;
                    location = wp & -wp;
                    continue;
                }
            }

            wp = wp & ~location;
            location = wp & -wp;
            counter++;
        }

        return counter;
    }

    private static int evaluatePassedPawnB(long wp, long bp) {
        long location = bp & -bp;
        int counter = 0;

        while (location != 0) {
            int position = Long.numberOfLeadingZeros(location);
            int x = position % 8;
            int y = position / 8;

            if ((wp & (MoveGen.fileMask[x]) & MoveGen.lastRanks[y + 1]) != 0) {
                bp = bp & ~location;
                location = bp & -bp;
                continue;
            }

            if (x > 0) {
                if ((wp & (MoveGen.fileMask[x - 1]) & MoveGen.lastRanks[y + 1]) != 0){
                    bp = bp & ~location;
                    location = bp & -bp;
                    continue;
                }
            }

            if (x < 7) {
                if ((wp & (MoveGen.fileMask[x + 1]) & MoveGen.lastRanks[y + 1]) != 0) {
                    bp = bp & ~location;
                    location = bp & -bp;
                    continue;
                }
            }

            bp = bp & ~location;
            location = bp & -bp;
            counter++;
        }

        return counter;
    }

    private static int evaluateCenterControl(long p, long r, long n, long b, long q, long k,
                                             long playerPieces, boolean white, boolean endGame) {
        long controlledByPlayer = moveGen.controlledSquares(p, r, n, b, q, k, playerPieces, white);
        controlledByPlayer = MoveGen.center & controlledByPlayer; // we get ones where we can put our pieces
        int eval = 0;

        for (int i = 2; i < 6; i++) {
            for (int j = 2; j < 6; j++) {
                if ((controlledByPlayer & (1L << (63 - (i * 8 + j)))) != 0) {
                    if (!endGame) eval += 10;
                }
            }
        }

        return eval;
    }

    // The method calculates the number of pieces still in play, in order to get an endgame score.
    private static int numberOfPieces(long occupied) {
        int counter = 0;
        for (int i = 0; i < 64; i++) {
            if ((occupied & (1L << i)) != 0){
                counter++;
            }
        }

        return counter;
    }

    // In the endgame, we want the king to help out with delivering checkmate.
    // The king should be close to the enemy king, and away from the margins,
    // because on the edge of the board it is easier to get checkmated.
    private static int evaluateKingPosition(long king, long opponentKing, int number){
        king = king & -king;
        int kingLoc = Long.numberOfLeadingZeros(king);
        opponentKing = opponentKing & -opponentKing;
        int oppKingLoc = Long.numberOfLeadingZeros(opponentKing);

        int evaluation = 0;

        int x = kingLoc % 8;
        int y = kingLoc / 8;
        int oppX = oppKingLoc % 8;
        int oppY = oppKingLoc / 8;

        if (oppX > 3) {
            if (oppY > 3)  evaluation += (oppX - 4) + (oppY - 4);
            else evaluation += (oppX - 4) + (3 - oppY);
        } else {
            if (oppY > 3) evaluation += (3 - oppX) + (oppY - 4);
            else evaluation += (3 - oppX) + (3 - oppY);
        }

        evaluation += (14 - (Math.abs(x - oppX) + Math.abs(y - oppY)));

        return evaluation * (16 - number) * 3;
    }
}
