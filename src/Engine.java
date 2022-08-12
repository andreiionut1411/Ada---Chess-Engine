import java.util.Scanner;

public class Engine {
    public static final int maxDepth = 6;

    private static final MoveGen moveGen = new MoveGen();

    // THe method receives a bitboard, the move that it is supposed to make, the
    // type of the piece to indicate us which bitboard we are modifying, and a boolean
    // to let us know which player is moving, in order to clear any misunderstandings of
    // the position when dealing with promotions, en passant, and castling.
    // The method returns the updated bitboard based on the move.
    public static long makeMoveForPiece(long bitboard, String move, char type, boolean isWhite) {
        if (move.charAt(3) == 'P') {
            int start, end;

            if (isWhite) {
                start = 48 + (7 - (move.charAt(0) - '0'));
                end = 56 + (7 - (move.charAt(1) - '0'));
            } else {
                start = 8 + (7 - (move.charAt(0) - '0'));
                end = 7 - (move.charAt(1) - '0');
            }

            if (type == move.charAt(2)) {
                bitboard = bitboard | (1L << end);
            } else {
                bitboard = bitboard & ~(1L << end) & ~(1L << start);
            }

        } else if (move.charAt(3) == 'E') {
            int start, end, capturedPawn;

            if (isWhite) {
                start = 24 + (move.charAt(0) - '0');
                end = 16 + (move.charAt(1) - '0');
                capturedPawn = 24 + (move.charAt(1) - '0');
            } else {
                start = 32 + (move.charAt(0) - '0');
                end = 40 + (move.charAt(1) - '0');
                capturedPawn = 32 + (move.charAt(1) - '0');
            }

            if ((bitboard & (1L << (63 - start))) != 0) {
                bitboard &= ~(1L << (63 - start));
                bitboard |= (1L << (63 - end));
            } else if ((bitboard & (1L << (63 - capturedPawn))) != 0) {
                bitboard &= ~(1L << (63 - capturedPawn));
            }

        } else if (move.charAt(3) == 'C') {
            switch (move) {
                case "wk C":
                    if (type == 'K') bitboard = (1L << 1);
                    else if (type == 'R') {
                        bitboard &= ~(1L);
                        bitboard |= (1L << 2);
                    }
                    break;
                case "wq C":
                    if (type == 'K') bitboard = (1L << 5);
                    else if (type == 'R') {
                        bitboard &= ~(1L << MoveGen.initialRookPos[2]);
                        bitboard |= (1L << 4);
                    }
                    break;
                case "bk C":
                    if (type == 'k') bitboard = (1L << 57);
                    else if (type == 'r') {
                        bitboard &= ~(1L << MoveGen.initialRookPos[1]);
                        bitboard |= (1L << 58);
                    }
                    break;
                case "bq C":
                    if (type == 'k') bitboard = (1L << 61);
                    else if (type == 'r') {
                        bitboard &= ~(1L << MoveGen.initialRookPos[0]);
                        bitboard |= (1L << 60);
                    }
                    break;
            }

        } else {
            int start = (move.charAt(0) - '0') * 8 + (move.charAt(1) - '0');
            int end = (move.charAt(2) - '0') * 8 + (move.charAt(3) - '0');

            if (((bitboard >>> (63 - start)) & 1L) == 1L) {
                bitboard = bitboard & ~(1L << (63 - start)); // we empty the starting square
                bitboard = bitboard | (1L << (63 - end)); // we move the piece to the current position
            } else {
                bitboard = bitboard & ~(1L << (63 - end)); // in case of capture
            }
        }

        return bitboard;
    }

    // The method verifies if a pawn moved 2 squares, so there can be an en passant
    // opportunity next turn. If it wasn't a pawn push, then the method returns true,
    // otherwise it returns the mask for the file on which the en passant might occur.
    public static long verifyEnPassant(String move, long wp, long bp, boolean isWhite) {

        if (move.charAt(1) == move.charAt(3) && Math.abs(move.charAt(0) - move.charAt(2)) == 2) {
            int start = (move.charAt(0) - '0') * 8 + (move.charAt(1) - '0');

            if (isWhite) {

                if ((wp & (1L << (63 - start))) != 0) {
                    return MoveGen.fileMask[move.charAt(1) - '0'];
                } else return 0L;
            } else {
                if ((bp & (1L << (63 - start))) != 0) {
                    return MoveGen.fileMask[move.charAt(1) - '0'];
                } else return 0L;
            }
        }

        return 0L;
    }

    // The rook code is the following:
    // for the rook on a8: 0
    // for the rook on h8: 1
    // for the rook on a1: 2
    // for the rook on h1: 3
    // The method returns true if the castling can be done on a certain side, or not.
    public static boolean verifyCastling(boolean castling, long r, int rookCode, long k, int kingLoc) {
        if (castling) {
            return (r & (1L << rookCode)) != 0 && ((k & (1L << kingLoc)) != 0);
        }

        return false;
    }

    // The method receives a move, and sees if it is a valid one or not.
    // It returns true if it is valid. If doMove is true, it also does the move,
    // if it is a valid move.
    public static boolean tryMove(String move, boolean doMove) {
        long wpt = Engine.makeMoveForPiece(MoveGen.wp, move, 'P', MoveGen.white);
        long wrt = Engine.makeMoveForPiece(MoveGen.wr, move, 'R', MoveGen.white);
        long wnt = Engine.makeMoveForPiece(MoveGen.wn, move, 'N', MoveGen.white);
        long wbt = Engine.makeMoveForPiece(MoveGen.wb, move, 'B', MoveGen.white);
        long wqt = Engine.makeMoveForPiece(MoveGen.wq, move, 'Q', MoveGen.white);
        long wkt = Engine.makeMoveForPiece(MoveGen.wk, move, 'K', MoveGen.white);
        long bpt = Engine.makeMoveForPiece(MoveGen.bp, move, 'p', MoveGen.white);
        long brt = Engine.makeMoveForPiece(MoveGen.br, move, 'r', MoveGen.white);
        long bnt = Engine.makeMoveForPiece(MoveGen.bn, move, 'n', MoveGen.white);
        long bbt = Engine.makeMoveForPiece(MoveGen.bb, move, 'b', MoveGen.white);
        long bqt = Engine.makeMoveForPiece(MoveGen.bq, move, 'q', MoveGen.white);
        long bkt = Engine.makeMoveForPiece(MoveGen.bk, move, 'k', MoveGen.white);
        long ept = Engine.verifyEnPassant(move, MoveGen.wp, MoveGen.bp, MoveGen.white);
        boolean wckt = Engine.verifyCastling(MoveGen.whiteCastleK, wrt, MoveGen.initialRookPos[3], wkt, 3);
        boolean wcqt = Engine.verifyCastling(MoveGen.whiteCastleQ, wrt, MoveGen.initialRookPos[2], wkt, 3);
        boolean bckt = Engine.verifyCastling(MoveGen.blackCastleK, brt, MoveGen.initialRookPos[1], bkt, 59);
        boolean bcqt = Engine.verifyCastling(MoveGen.blackCastleQ, brt, MoveGen.initialRookPos[0], bkt, 59);

        long notWhitePieces = ~(wpt | wrt | wnt | wbt | wqt | wkt);
        long notBlackPieces = ~(bpt | brt | bnt | bbt | bqt | bkt);
        MoveGen.occupied = wpt | wrt | wnt | wbt | wqt | wkt | bpt | brt | bnt | bbt | bqt | bkt;
        MoveGen.empty = ~MoveGen.occupied;

        // If the king is not in left in check, then it is a valid move.
        if (((wkt & moveGen.controlledSquares(bpt, brt, bnt, bbt, bqt, bkt, notBlackPieces, false)) == 0
                && MoveGen.white) ||
                ((bkt & moveGen.controlledSquares(wpt, wrt, wnt, wbt, wqt, wkt, notWhitePieces, true)) == 0
                        && !MoveGen.white)) {

            // If doMove is true, then we actually make the move and modify all the bitboards.
            if (doMove){
                makeMove(wpt, wrt, wnt, wbt, wqt, wkt, bpt, brt, bnt, bbt, bqt, bkt, ept, wckt, wcqt, bckt, bcqt);
            }

            return true;
        }

        return false;
    }

    // The method receives the bitboards after the move, and we update the bitboards from MoveGen.
    private static void makeMove(long wpt, long wrt, long wnt, long wbt, long wqt, long wkt,
                                 long bpt, long brt, long bnt, long bbt, long bqt, long bkt,
                                 long ept, boolean wckt, boolean wcqt, boolean bckt, boolean bcqt) {

        MoveGen.wp = wpt;
        MoveGen.wr = wrt;
        MoveGen.wn = wnt;
        MoveGen.wb = wbt;
        MoveGen.wq = wqt;
        MoveGen.wk = wkt;
        MoveGen.bp = bpt;
        MoveGen.br = brt;
        MoveGen.bn = bnt;
        MoveGen.bb = bbt;
        MoveGen.bq = bqt;
        MoveGen.bk = bkt;
        MoveGen.enPassant = ept;
        MoveGen.whiteCastleK = wckt;
        MoveGen.whiteCastleQ = wcqt;
        MoveGen.blackCastleK = bckt;
        MoveGen.blackCastleQ = bcqt;
        MoveGen.white = !MoveGen.white; // We change the player's turn.
    }

    // The method verifies if we are in the endgame or not.
    public static boolean verifyEndGame(long wq, long bq, boolean endGame) {
        if (!endGame) {
            return wq == 0 && bq == 0;
        }

        return true;
    }

    // The method returns the best move for the current position, as well as the evaluation for
    // said position. The return value has the first 5 letters representing the move, using our
    // notation, and the score is what comes after. The argument player is either a 0, or a 1.
    // It is a 0 when it is the maximizing player's turn, and a 1 for the minimizing player's turn.
    // We use the minimax algorithm with alpha-beta pruning.
    public static String alphaBeta(int depth, int alpha, int beta, String move,
                            long wp, long wr, long wn, long wb, long wq, long wk,
                            long bp, long br, long bn, long bb, long bq, long bk,
                            long ep, boolean wck, boolean wcq, boolean bck, boolean bcq,
                            boolean white, boolean endGame) {

        String returnString;
        String bestMove;
        boolean legalMoves = false;

        if (depth == 0) {
            int eval = Evaluation.eval(wp, wr, wn, wb, wq, wk, bp, br, bn, bb, bq, bk, endGame);

            if (white) return move + eval;
            else return move + (-eval);
        }

        String moves = moveGen.possibleMoves(wp, wr, wn, wb, wq, wk, bp, br, bn, bb, bq, bk, ep, white,
                wck, wcq, bck, bcq);

        bestMove = moves.substring(0, 4);
        int score = Integer.MIN_VALUE;

        for (int i = 0; i < moves.length(); i += 4) {
            String crtMove = moves.substring(i, i + 4);
            long wpt = Engine.makeMoveForPiece(wp, crtMove, 'P', white);
            long wrt = Engine.makeMoveForPiece(wr, crtMove, 'R', white);
            long wnt = Engine.makeMoveForPiece(wn, crtMove, 'N', white);
            long wbt = Engine.makeMoveForPiece(wb, crtMove, 'B', white);
            long wqt = Engine.makeMoveForPiece(wq, crtMove, 'Q', white);
            long wkt = Engine.makeMoveForPiece(wk, crtMove, 'K', white);
            long bpt = Engine.makeMoveForPiece(bp, crtMove, 'p', white);
            long brt = Engine.makeMoveForPiece(br, crtMove, 'r', white);
            long bnt = Engine.makeMoveForPiece(bn, crtMove, 'n', white);
            long bbt = Engine.makeMoveForPiece(bb, crtMove, 'b', white);
            long bqt = Engine.makeMoveForPiece(bq, crtMove, 'q', white);
            long bkt = Engine.makeMoveForPiece(bk, crtMove, 'k', white);
            long ept = Engine.verifyEnPassant(crtMove, wp, bp, white);
            boolean wckt = Engine.verifyCastling(wck, wrt, MoveGen.initialRookPos[3], wkt, 3);
            boolean wcqt = Engine.verifyCastling(wcq, wrt, MoveGen.initialRookPos[2], wkt, 3);
            boolean bckt = Engine.verifyCastling(bck, brt, MoveGen.initialRookPos[1], bkt, 59);
            boolean bcqt = Engine.verifyCastling(bcq, brt, MoveGen.initialRookPos[0], bkt, 59);

            long notWhitePieces = ~(wpt | wrt | wnt | wbt | wqt | wkt);
            long notBlackPieces = ~(bpt | brt | bnt | bbt | bqt | bkt);
            MoveGen.occupied = wpt | wrt | wnt | wbt | wqt | wkt | bpt | brt | bnt | bbt | bqt | bkt;
            MoveGen.empty = ~MoveGen.occupied;
            boolean endGamet = verifyEndGame(wqt, bqt, endGame);
            if ((white && (wkt & moveGen.controlledSquares(bpt, brt, bnt, bbt, bqt, bkt, notBlackPieces, false)) == 0) ||
                    (!white && (bkt & moveGen.controlledSquares(wpt, wrt, wnt, wbt, wqt, wkt, notWhitePieces, true)) == 0)) {

                legalMoves = true;
                returnString = alphaBeta(depth - 1, -beta, -alpha, crtMove, wpt, wrt, wnt, wbt, wqt, wkt,
                        bpt, brt, bnt, bbt, bqt, bkt, ept, wckt, wcqt, bckt, bcqt, !white, endGamet);

                int cur = -Integer.parseInt(returnString.substring(4));

                if (cur > score) {
                    score = cur;
                    bestMove = crtMove;
                }

                if (score > alpha) {
                    alpha = score;
                }

                if (alpha >= beta) {
                    return bestMove + alpha;
                }
            }
        }

        long notWhitePieces = ~(wp | wr | wn | wb | wq | wk);
        long notBlackPieces = ~(bp | br | bn | bb | bq | bk);
        if (!legalMoves) {
            if ((white && (wk & moveGen.controlledSquares(bp, br, bn, bb, bq, bk, notBlackPieces, false)) == 0) ||
                    (!white && (bk & moveGen.controlledSquares(wp, wr, wn, wb, wq, wk, notWhitePieces, true)) == 0)) {
                return move + 0;
            }
            return move + (Integer.MIN_VALUE / 3 - depth * 300);
        }
        return bestMove + score;
    }
}
