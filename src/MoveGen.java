public class MoveGen {

    // These will be the bitboards used to represent the data. Each variable represents the
    // bitboard for the pieces denoted by the second letter and the color indicated by the first,
    // where w is for white, and b is for black.
    public static long wp = 0L, wr = 0L, wn = 0L, wb = 0L, wq = 0L, wk = 0L;
    public static long bp = 0L, br = 0L, bn = 0L, bb = 0L, bq = 0L, bk = 0L;

    // These are some useful bitboards that we can use in order to generate moves faster.
    public static boolean white;
    public static boolean isEndGame;
    public static final long fileA = 0x8080808080808080L;
    public static final long fileH = 0x0101010101010101L;
    public static final long fileAB = 0xc0c0c0c0c0c0c0c0L;
    public static final long fileGH = 0x0303030303030303L;
    public static final long rank1 = 0x00000000000000ffL;
    public static final long rank8 = 0xff00000000000000L;
    public static final long rank4 = 0x00000000ff000000L;
    public static final long rank5 = 0x000000ff00000000L;
    public static final long knightMoves = 0x0000284400442800L;
    public static final long kingMoves = 0x0000003828380000L;
    public static final int[] initialRookPos = {63, 56, 7, 0};
    public static long whitePieces;
    public static long blackPieces;
    public static long empty;
    public static long occupied;
    public static long notWhitePieces;
    public static long notBlackPieces;
    public static long enPassant = 0L; // The column on which a pawn moved 2 squares on the last move.
    public static long[] fileMask =
            {0x8080808080808080L, 0x4040404040404040L,
            0x2020202020202020L, 0x1010101010101010L,
            0x0808080808080808L, 0x0404040404040404L,
            0x0202020202020202L, 0x0101010101010101L}; // we have ones only on a certain file.
    public static long[] rankMask =
            {0xff00000000000000L, 0x00ff000000000000L,
            0x0000ff0000000000L, 0x000000ff00000000L,
            0x00000000ff000000L, 0x0000000000ff0000L,
            0x000000000000ff00L, 0x00000000000000ffL};

    public static long[] diagonalMask =
            {0x80L, 0x8040L, 0x804020L, 0x80402010L, 0x8040201008L, 0x804020100804L,
            0x80402010080402L, 0x8040201008040201L, 0x4020100804020100L,
            0x2010080402010000L, 0x1008040201000000L, 0x0804020100000000L,
            0x0402010000000000L, 0x0201000000000000L, 0x0100000000000000L};

    public static long[] antiDiagonalMask =
            {0x8000000000000000L, 0x4080000000000000L, 0x2040800000000000L,
            0x1020408000000000L, 0x0810204080000000L, 0x0408102040800000L,
            0x0204081020408000L, 0x0102040810204080L, 0x01020408102040L,
            0x010204081020L, 0x0102040810L, 0x01020408L, 0x010204L, 0x0102L, 1L};

    // If they are true, it means that a castle on that side of the board is still valid.
    public static boolean whiteCastleK, whiteCastleQ, blackCastleK, blackCastleQ;

    // The knight moves are calculated as if the knight was in the middle of the
    // board at the position d4 (denoted by the x), and the bitboard is the next one:
    // 00000000
    // 00000000
    // 00101000
    // 01000100
    // 000x0000
    // 01000100
    // 00101000
    // 00000000
    //
    // The king moves' bitboard is calculated as if the king is on the position dr,
    // symbolised by 'x', like in the next board:
    // 00000000
    // 00000000
    // 00000000
    // 00111000
    // 001x1000
    // 00111000
    // 00000000
    // 00000000

    // With capital letters we symbolise the white pieces, and with the others we represent
    // the black pieces.
    // P/p = pawn
    // R/r = rook
    // N/n = knight (as in algebraic notation, not to be confused with the king)
    // B/b = bishop
    // Q/q = queen
    // K/k = king
    private final char[][] board = {
            {'r', 'n', 'b', 'q', 'k', 'b', 'n', 'r'},
            {'p', 'p', 'p', 'p', 'p', 'p', 'p', 'p'},
            {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
            {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
            {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
            {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
            {'P', 'P', 'P', 'P', 'P', 'P', 'P', 'P'},
            {'R', 'N', 'B', 'Q', 'K', 'B', 'N', 'R'}};

    // The method initializes all the bitboards based on the board.
    public void initialiseBoard() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                wp = wp << 1;
                wr = wr << 1;
                wn = wn << 1;
                wb = wb << 1;
                wq = wq << 1;
                wk = wk << 1;
                bp = bp << 1;
                br = br << 1;
                bn = bn << 1;
                bb = bb << 1;
                bq = bq << 1;
                bk = bk << 1;

                switch (board[i][j]) {
                    case 'P' -> wp += 1;
                    case 'R' -> wr += 1;
                    case 'N' -> wn += 1;
                    case 'B' -> wb += 1;
                    case 'Q' -> wq += 1;
                    case 'K' -> wk += 1;
                    case 'p' -> bp += 1;
                    case 'r' -> br += 1;
                    case 'n' -> bn += 1;
                    case 'b' -> bb += 1;
                    case 'q' -> bq += 1;
                    case 'k' -> bk += 1;
                }
            }
        }

        white = true;
        whiteCastleK = true;
        whiteCastleQ = true;
        blackCastleK = true;
        blackCastleQ = true;
        enPassant = 0L;
        isEndGame = false;
    }

    // The method receives a FEN string and based on it, we initialize the board.
    public void initialiseFen(String fenString) {
        int index = 0;
        int fenIndex = 0;

        while (fenString.charAt(fenIndex) != ' ') {
            switch (fenString.charAt(fenIndex++)) {
                case 'P' -> wp |= (1L << (63 - index));
                case 'R' -> wr |= (1L << (63 - index));
                case 'N' -> wn |= (1L << (63 - index));
                case 'B' -> wb |= (1L << (63 - index));
                case 'Q' -> wq |= (1L << (63 - index));
                case 'K' -> wk |= (1L << (63 - index));
                case 'p' -> bp |= (1L << (63 - index));
                case 'r' -> br |= (1L << (63 - index));
                case 'n' -> bn |= (1L << (63 - index));
                case 'b' -> bb |= (1L << (63 - index));
                case 'q' -> bq |= (1L << (63 - index));
                case 'k' -> bk |= (1L << (63 - index));
                case '2' -> index++;
                case '3' -> index += 2;
                case '4' -> index += 3;
                case '5' -> index += 4;
                case '6' -> index += 5;
                case '7' -> index += 6;
                case '8' -> index += 7;
                case '/' -> index--;
            }

            index++;
        }

        white = fenString.charAt(++fenIndex) == 'w';

        fenIndex++;

        while (fenString.charAt(++fenIndex) != ' ') {
            if (fenString.charAt(fenIndex) == '-') {
                whiteCastleK = false;
                whiteCastleQ = false;
                blackCastleK = false;
                blackCastleQ = false;
            } else if (fenString.charAt(fenIndex) == 'K') {
                whiteCastleK = true;
            } else if (fenString.charAt(fenIndex) == 'Q') {
                whiteCastleQ = true;
            } else if (fenString.charAt(fenIndex) == 'k') {
                blackCastleK = true;
            } else if (fenString.charAt(fenIndex) == 'q') {
                blackCastleQ = true;
            }
        }

        fenIndex++;

        if (fenString.charAt(fenIndex) == '-') {
            enPassant = 0L;
        } else {
            enPassant = fileMask[fenString.charAt(fenIndex) - 'a'];
        }

        isEndGame = Engine.verifyEndGame(wq, bq, false);
    }

    // The method prints a simplified view of the board.
    public static void visualizeBoard(long wp, long wr, long wn, long wb, long wq, long wk,
                                      long bp, long br, long bn, long bb, long bq, long bk) {

        for (int i = 0; i < 64; i++) {
            if ((wp & (1L << (63 - i))) != 0) System.out.print("P, ");
            else if ((wr & (1L << (63 - i))) != 0) System.out.print("R, ");
            else if ((wn & (1L << (63 - i))) != 0) System.out.print("N, ");
            else if ((wb & (1L << (63 - i))) != 0) System.out.print("B, ");
            else if ((wq & (1L << (63 - i))) != 0) System.out.print("Q, ");
            else if ((wk & (1L << (63 - i))) != 0) System.out.print("K, ");
            else if ((bp & (1L << (63 - i))) != 0) System.out.print("p, ");
            else if ((br & (1L << (63 - i))) != 0) System.out.print("r, ");
            else if ((bn & (1L << (63 - i))) != 0) System.out.print("n, ");
            else if ((bb & (1L << (63 - i))) != 0) System.out.print("b, ");
            else if ((bq & (1L << (63 - i))) != 0) System.out.print("q, ");
            else if ((bk & (1L << (63 - i))) != 0) System.out.print("k, ");
            else System.out.print(" , ");

            if (i % 8 == 7){
                System.out.println();
            }
        }
    }

    // The method prints a board of ones and zeros to represent the bitboard.
    public static void visualizeBitboard(long bitBoard) {
        for (int i = 0; i < 64; i++) {
            if ((bitBoard & (1L << (63 - i))) != 0) System.out.print("1 ");
            else System.out.print("0 ");

            if (i % 8 == 7) System.out.println();
        }
    }

    // The moves will follow the next notation: srcY, srcX, destY, destX
    // srcX, srcY, destX, destY are indices in the board array.
    // For promotions, we will use the notation: srcX, destX, piecePromoted, 'P'
    // For en passant we will use the notation: srcX, destX, " E"
    // For calculating the moves of sliding pieces we use the formula:
    // (((occupied & mask) - 2 * (position of Piece)) ^ reverse (reverse(occupied & mask) - 2 * reverse(position))) & mask.
    // Mask is a mask that indicates us in which direction the piece should move.
    // The first part of the expression, without the mask, gives us how a rook can move to the left,
    // so we reverse it and apply the same procedure as to the right.
    public String possibleMoves(long wp, long wr, long wn, long wb, long wq, long wk,
                                long bp, long br, long bn, long bb, long bq, long bk,
                                long enPassant, boolean white,
                                boolean whiteCastleK, boolean whiteCastleQ,
                                boolean blackCastleK, boolean blackCastleQ){

        // We can't capture the kings, so we don't add them to the bitboards.
        whitePieces = wp | wr | wn | wb | wq;
        blackPieces = bp | br | bn | bb | bq;
        notWhitePieces = ~(wp | wr | wn | wb | wq | wk);
        notBlackPieces = ~(bp | br | bn | bb | bq | bk);
        occupied = wp | wr | wn | wb | wq | wk | bp | br | bn | bb | bq | bk;
        empty = ~occupied;
        String moves;

        if (white) {
            long blackControlled = controlledSquares(bp, br, bn, bb, bq, bk, notBlackPieces, false);
            moves = String.valueOf(possibleBishopMoves(notWhitePieces, wb)) +
                    possibleKnightMoves(notWhitePieces, wn) +
                    possibleRookMoves(notWhitePieces, wr) +
                    possibleQueenMoves(notWhitePieces, wq) +
                    possibleWPMoves(wp, bp, enPassant, empty) +
                    possibleKingMoves(notWhitePieces, wk) +
                    verifyCastleWhite(whiteCastleK, whiteCastleQ, blackControlled, wk);
        } else {
            long whiteControlled = controlledSquares(wp, wr, wn, wb, wq, wk, notWhitePieces, true);
            moves = String.valueOf(possibleBishopMoves(notBlackPieces, bb)) +
                    possibleKnightMoves(notBlackPieces, bn) +
                    possibleRookMoves(notBlackPieces, br) +
                    possibleQueenMoves(notBlackPieces, bq) +
                    possibleBPMoves(bp, wp, enPassant, empty) +
                    possibleKingMoves(notBlackPieces, bk) +
                    verifyCastleBlack(blackCastleK, blackCastleQ, whiteControlled, bk);
        }

        return moves;
    }

    // The method adds all the possible moves that lead to promotion from the given bitboard.
    private void auxPromotionMoves(StringBuilder moves, long pawnMoves, int direction, boolean white){
        long possibleBit = pawnMoves & -pawnMoves;

        while (possibleBit != 0) {
            int index = Long.numberOfLeadingZeros(possibleBit);
            moves.append(index % 8 + direction).append(index % 8);

            if (white) moves.append("QP");
            else moves.append("qP");

            moves.append(index % 8 + direction).append(index % 8);

            if (white) moves.append("NP");
            else moves.append("nP");

            moves.append(index % 8 + direction).append(index % 8);

            if (white) moves.append("BP");
            else moves.append("bP");

            moves.append(index % 8 + direction).append(index % 8);

            if (white) moves.append("RP");
            else moves.append("rP");

            pawnMoves = pawnMoves & ~possibleBit;
            possibleBit = pawnMoves & -pawnMoves;
        }
    }

    // The method adds all the moves that don't lead to promotion. directionY and directionX
    // indicate in which direction is the initial pawn.
    private void auxNonPromotionMoves(StringBuilder moves, long pawnMoves, int directionY, int directionX) {
        // This gives us one of the active bits of pawnMoves
        long possibleBit = pawnMoves & -pawnMoves;

        while (possibleBit != 0) {
            int index = Long.numberOfLeadingZeros(possibleBit);
            moves.append(index / 8 + directionY).append(index % 8 + directionX).append(index / 8).append(index % 8);
            pawnMoves = pawnMoves & ~possibleBit;
            possibleBit = pawnMoves & -pawnMoves;
        }
    }

    // For en passant we will have the notation srcX, srcY, " E"
    // We know already know the ranks on which en passant can occur, so we
    // don't calculate them. We use " E" so that we still have 4 characters
    // per move, and we know that it was en passant.
    private void verifyEnPassant(StringBuilder moves, long rank,
                                 long player, long opponent, long enPassant){
        if (enPassant != 0L) {
            long pawnMoves = (player >>> 1L) & opponent & rank & enPassant & ~fileA;
            long possibleBit = pawnMoves & -pawnMoves;

            if (possibleBit != 0) {
                int index = Long.numberOfLeadingZeros(possibleBit);
                moves.append(index % 8 - 1).append(index % 8).append(" E");
            }

            pawnMoves = (player << 1L) & opponent & rank & enPassant & ~fileH;
            possibleBit = pawnMoves & -pawnMoves;

            if (possibleBit != 0) {
                int index = Long.numberOfLeadingZeros(possibleBit);

                moves.append(index % 8 + 1).append(index % 8).append(" E");
            }
        }
    }

    // The method returns a string with all the possible moves that the white pawns can make.
    private StringBuilder possibleWPMoves(long wp, long bp, long enPassant, long empty) {
        StringBuilder moves = new StringBuilder();

        // captures to the right
        long pawnMoves = (wp << 7) & blackPieces & ~rank8 & ~fileA;
        auxNonPromotionMoves(moves, pawnMoves, 1, -1);

        // captures to the left
        pawnMoves = (wp << 9) & blackPieces & ~rank8 & ~fileH;
        auxNonPromotionMoves(moves, pawnMoves, 1, 1);

        // move 1 up
        pawnMoves = (wp << 8) & empty & ~rank8;
        auxNonPromotionMoves(moves, pawnMoves, 1, 0);

        // move 2 up
        pawnMoves = (wp << 16) & empty & (empty << 8) & rank4;
        auxNonPromotionMoves(moves, pawnMoves, 2, 0);

        // captures to the right with promotion
        // For promotion we use the notation srcX, destX, pieceCode, 'P'
        // The 'P' is for us to know that we made a promotion, and the piece code
        // tells us which piece we promote into. We don't need the initial ranks because
        // the promotion can only happen from rank7 to rank8.
        pawnMoves = (wp << 7) & blackPieces & rank8 & ~fileA;
        auxPromotionMoves(moves, pawnMoves, -1, true);

        // capture to the left with promotion
        pawnMoves = (wp << 9) & blackPieces & rank8 & ~fileH;
        auxPromotionMoves(moves, pawnMoves, 1, true);

        // move 1 up with promotion
        pawnMoves = (wp << 8) & empty & rank8;
        auxPromotionMoves(moves, pawnMoves, 0, true);

        // we verify en passant
        verifyEnPassant(moves, rank5, wp, bp, enPassant);

        return moves;
    }

    private StringBuilder possibleBPMoves(long bp, long wp, long enPassant, long empty) {
        StringBuilder moves = new StringBuilder();

        // captures to the right
        long pawnMoves = (bp >>> 9) & whitePieces & ~rank1 & ~fileA;
        auxNonPromotionMoves(moves, pawnMoves,-1, -1);

        // captures to the left
        pawnMoves = (bp >>> 7) & whitePieces & ~rank1 & ~fileH;
        auxNonPromotionMoves(moves, pawnMoves, -1, 1);

        // move 1 up
        pawnMoves = (bp >>> 8) & empty & ~rank1;
        auxNonPromotionMoves(moves, pawnMoves, -1, 0);

        // move 2 up
        pawnMoves = (bp >>> 16) & empty & (empty >>> 8) & rank5;
        auxNonPromotionMoves(moves, pawnMoves, -2, 0);

        // captures to the right with promotion
        // For promotion we use the notation srcX, destX, pieceCode, 'P'
        // The 'P' is for us to know that we made a promotion, and the piece code
        // tells us which piece we promote into. We don't need the initial ranks because
        // the promotion can only happen from rank7 to rank8.
        pawnMoves = (bp >>> 9) & whitePieces & rank1 & ~fileA;
        auxPromotionMoves(moves, pawnMoves, -1, false);

        // capture to the left with promotion
        pawnMoves = (bp >>> 7) & whitePieces & rank1 & ~fileH;
        auxPromotionMoves(moves, pawnMoves, 1, false);

        // move 1 up with promotion
        pawnMoves = (bp >>> 8) & empty & rank1;
        auxPromotionMoves(moves, pawnMoves, 0, false);

        // we verify en passant
        verifyEnPassant(moves, rank4, bp, wp, enPassant);

        return moves;
    }

    // The method receives a starting position of a piece that can move horizontal and
    // vertical (a rook or a queen), and returns a bitboard with all the positions it can move.
    private long lineMoves(int position) {
        long binaryPos = 1L << (63 - position);
        long horizMoves = ((occupied & rankMask[position / 8])- 2 * binaryPos) ^
                Long.reverse(Long.reverse(occupied) - 2 * Long.reverse(binaryPos));

        long verticalMoves = ((occupied & fileMask[position % 8]) - 2 * binaryPos) ^
                Long.reverse(Long.reverse(occupied & fileMask[position % 8]) - 2 * Long.reverse(binaryPos));

        return (horizMoves & rankMask[position / 8]) | (verticalMoves & fileMask[position % 8]);
    }

    // The method receives a starting position of the piece that can move diagonally (a bishop
    // or a queen), and returns a bitboard containing all the moves that it can make.
    private long diagonalMoves(int position) {
        long binaryPos = 1L << (63 - position);
        long diagMask = diagonalMask[position % 8 - position / 8 + 7];
        long antiDiagMask = antiDiagonalMask[position % 8 + position / 8];
        long diagMoves = ((occupied & diagMask) - 2 * binaryPos) ^
                Long.reverse(Long.reverse(occupied & diagMask) - 2 * Long.reverse(binaryPos));
        long antiDiagMoves = ((occupied & antiDiagMask) - 2 * binaryPos) ^
                Long.reverse(Long.reverse(occupied & antiDiagMask) - 2 * Long.reverse(binaryPos));

        return (diagMoves & diagMask) | (antiDiagMoves & antiDiagMask);
    }

    private void auxCalculateMoves(StringBuilder moves, int position, long possibleMoves) {
        long move = possibleMoves & -possibleMoves;

        while (move != 0) {
            int index = Long.numberOfLeadingZeros(move);
            moves.append(position / 8).append(position % 8).append(index / 8).append(index % 8);
            possibleMoves = possibleMoves & ~move;
            move = possibleMoves & -possibleMoves;
        }
    }

    // The method returns the bitboard that represents the possible moves that
    // a knight or a king can make. It is obtained by shifting the existing
    // bitboard we get as a parameter. We also take into account the wrap around.
    // So, if a knight is on the right side of the board, then a possible move
    // can't be on the first two files, and vice versa if it is on the left
    // side of the board. A king can move less than a knight so if it is on the
    // edge of the board it can't get to the B file by shifting him to the right,
    // but for code reusability, we maintain the same process.
    // The argument protectedPieces is true if we want to see also the pieces
    // that are protected, not only the possible moves.
    private long bitboardMoves(int position, long notWhitePieces, long bitboard,
                               boolean protectedPieces) {
        int initialPosition = 35; // The position of the knight/king in their bitboard.
        long moves = bitboard;

        if (position < initialPosition) {
            moves = moves << (initialPosition - position);
        } else {
            moves = moves >>> (position - initialPosition);
        }

        // We verify for wrap around and that we don't capture our own pieces.
        if (position % 8 < 4) {
            moves = moves & ~fileGH;
        } else {
            moves = moves & ~fileAB;
        }

        if (!protectedPieces) {
            moves = moves & notWhitePieces;
        }

        return moves;
    }

    private StringBuilder possibleKnightMoves(long notWhitePieces, long wn) {
        long knightLocation = wn & -wn;
        StringBuilder moves = new StringBuilder();

        while (knightLocation != 0) {
            int position = Long.numberOfLeadingZeros(knightLocation);
            long bitboard = bitboardMoves(position, notWhitePieces, knightMoves, false);

            auxCalculateMoves(moves, position, bitboard);

            wn = wn & ~knightLocation;
            knightLocation = wn & -wn;
        }

        return moves;
    }

    // The method returns all the moves that the bishops can make.
    private StringBuilder possibleBishopMoves(long enemyPieces, long b) {
        long bishopLocation = b & -b;
        StringBuilder moves = new StringBuilder();

        while (bishopLocation != 0) {
            int position = Long.numberOfLeadingZeros(bishopLocation);
            long bishopMoves = diagonalMoves(position) & enemyPieces;

            auxCalculateMoves(moves, position, bishopMoves);

            b = b & ~bishopLocation;
            bishopLocation = b & -b;
        }

        return moves;
    }

    // The method returns all the moves that the rooks can make.
    private StringBuilder possibleRookMoves(long enemyPieces, long r) {
        long rookLocation = r & -r;
        StringBuilder moves = new StringBuilder();

        while (rookLocation != 0) {
            int position = Long.numberOfLeadingZeros(rookLocation);
            long rookMoves = lineMoves(position) & enemyPieces;

            auxCalculateMoves(moves, position, rookMoves);

            r = r & ~rookLocation;
            rookLocation = r & -r;
        }

        return moves;
    }

    private StringBuilder possibleQueenMoves(long enemyPieces, long q) {
        long queenLocation = q & -q;
        StringBuilder moves = new StringBuilder();

        while (queenLocation != 0) {
            int position = Long.numberOfLeadingZeros(queenLocation);
            long queenMoves = (lineMoves(position) | diagonalMoves(position)) & enemyPieces;

            auxCalculateMoves(moves, position, queenMoves);

            q = q & ~queenLocation;
            queenLocation = q & -q;
        }

        return moves;
    }

    private StringBuilder possibleKingMoves(long enemyPieces, long k) {
        StringBuilder moves = new StringBuilder();
        int kingLocation = Long.numberOfLeadingZeros(k);
        long bitboardKing = (bitboardMoves(kingLocation, enemyPieces, kingMoves, false));

        auxCalculateMoves(moves, kingLocation, bitboardKing);

        return moves;
    }

    private long controlledByWP(long wp) {
        long bitboard = (wp << 7) & ~fileA;
        bitboard = bitboard | ((wp << 9) & ~fileH);

        return bitboard;
    }

    private long controlledByBP(long bp) {
        long bitboard = (bp >>> 7) & ~fileH;
        bitboard = bitboard | ((bp >>> 9) & ~fileA);

        return bitboard;
    }

    // The method returns a bitboard that contains all the squares controlled by one side.
    // If the variable white is true, then we look at the white controlled squares, otherwise
    // we look at black.
    public long controlledSquares(long p, long r, long n, long b, long q, long k,
                                   long enemyPieces, boolean white) {
        long bitboard;
        long location;

        // pawn controlled squares
        if (white) {
            bitboard = controlledByWP(p);
        } else {
            bitboard = controlledByBP(p);
        }

        // rook and queen (straight) controlled squares
        long wrq = r | q;
        location = wrq & -wrq;
        while (location != 0) {
            int position = Long.numberOfLeadingZeros(location);
            bitboard = bitboard | lineMoves(position);

            wrq = wrq & ~location;
            location = wrq & -wrq;
        }

        // bishop and queen (diagonally) controlled squares
        long wbq = b | q;
        location = wbq & -wbq;
        while (location != 0) {
            int position = Long.numberOfLeadingZeros(location);
            bitboard = bitboard | diagonalMoves(position);

            wbq = wbq & ~location;
            location = wbq & -wbq;
        }

        // knight controlled squares
        location = n & -n;
        while (location != 0) {
            int position = Long.numberOfLeadingZeros(location);
            bitboard = bitboard | (bitboardMoves(position, enemyPieces, knightMoves, true));

            n = n & ~location;
            location = n & -n;
        }

        // king controlled squares
        location = Long.numberOfLeadingZeros(k);
        bitboard = bitboard | (bitboardMoves((int) location, enemyPieces, kingMoves, true));

        return bitboard;
    }

    // For castling we have the notation: color, side, " C"
    // The letter C is to indicate that we castle, and the side is
    // either k for the short castle, or q for the long castle.
    private StringBuilder verifyCastleWhite(boolean whiteCastleK, boolean whiteCastleQ,
                                            long blackControlled, long wk) {
        StringBuilder moves = new StringBuilder();

        // To be able to castle, the king must not be in check, nor the squares
        // it passes through, and there must be no piece between the king and the rook.
        if (whiteCastleK) {
            if (((1L << 1) & empty) != 0 && ((1L << 2) & empty) != 0) {
                if ((wk & blackControlled) == 0 && ((wk >>> 1) & blackControlled) == 0 &&
                        ((wk >>> 2) & blackControlled) == 0) {
                    moves.append("wk C");
                }
            }
        }

        if (whiteCastleQ) {
            if (((1L << 4) & empty) != 0 && ((1L << 5) & empty) != 0 && ((1L << 6) & empty) != 0) {
                if ((wk & blackControlled) == 0 && ((wk << 1) & blackControlled) == 0 &&
                        ((wk << 2) & blackControlled) == 0) {
                    moves.append("wq C");
                }
            }
        }

        return moves;
    }

    // The method verifies if the conditions for castling are met or not. If they
    // are, then the move code for the castling will be returned.
    private StringBuilder verifyCastleBlack(boolean blackCastleK, boolean blackCastleQ,
                                            long whiteControlled, long bk) {
        StringBuilder moves = new StringBuilder();

        if (blackCastleK) {
            if (((1L << 57) & empty) != 0 && ((1L << 58) & empty) != 0) {
                if ((bk & whiteControlled) == 0 && ((bk >>> 1) & whiteControlled) == 0 &&
                        (((bk >>> 2) & whiteControlled) == 0)) {
                    moves.append("bk C");
                }
            }
        }

        if (blackCastleQ) {
            if (((1L << 60) & empty) != 0 && ((1L << 61) & empty) != 0 && ((1L << 62) & empty) != 0) {
                if ((bk & whiteControlled) == 0 && ((bk << 1) & whiteControlled) == 0 &&
                        ((bk << 2) & whiteControlled) == 0) {
                    moves.append("bq C");
                }
            }
        }

        return moves;
    }
}
