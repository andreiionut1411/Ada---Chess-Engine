import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class GraphicalInterface extends JPanel implements MouseListener, MouseMotionListener {

    private final Image[] pieces;
    private final MoveGen moveGen;
    private int srcX, srcY; // The initial position of the piece that we will move.
    private int mouseX, mouseY;
    private int startPosition;
    private int endingOfGameCode; // For checkmate, it's 0, and for stalemate it's 1
    private boolean gameEnded;
    private final Color white = new Color(236, 220, 206);
    private final Color black = new Color(165, 122, 79);
    private final Color possibleWhiteMoveColor = new Color(228, 67, 67);
    private final Color possibleBlackMoveColor = new Color(155, 42, 42);

    public GraphicalInterface(MoveGen moveGen) {
        pieces = preparePieces();
        startPosition = -1;
        srcX = -1;
        srcY = -1;
        mouseX = -1;
        mouseY = -1;
        gameEnded = false;
        this.moveGen = moveGen;
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
    }

    private Image[] preparePieces() {
        Image[] images = new Image[12]; // We have 12 types of pieces on the board.
        try {
            BufferedImage bigImage = ImageIO.read(this.getClass().getResource("pieces.png"));
            int index = 0;

            // We cut the big image in each individual piece.
            for (int y = 0; y < 400; y += 200) {
                for (int x = 0; x < 1200; x += 200) {
                    Image im = bigImage.getSubimage(x, y, 200, 200).getScaledInstance(64, 64, BufferedImage.SCALE_SMOOTH);
                    images[index++] = im;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return images;
    }

    private void paintPiecesWhite(Graphics g) {
        long mask = 1L << 63;
        int j, k;

        for (int i = 0; i < 64; i++) {
            j = -1;
            k = -1;

            if (srcX != i % 8 || srcY != i / 8){
                if ((MoveGen.wk & (mask >>> i)) != 0) {j = 0; k = 0;}
                else if ((MoveGen.bk & (mask >>> i)) != 0) {j = 1; k = 0;}
                else if ((MoveGen.wq & (mask >>> i)) != 0) {j = 0; k = 1;}
                else if ((MoveGen.bq & (mask >>> i)) != 0) {j = 1; k = 1;}
                else if ((MoveGen.wb & (mask >>> i)) != 0) {j = 0; k = 2;}
                else if ((MoveGen.bb & (mask >>> i)) != 0) {j = 1; k = 2;}
                else if ((MoveGen.wn & (mask >>> i)) != 0) {j = 0; k = 3;}
                else if ((MoveGen.bn & (mask >>> i)) != 0) {j = 1; k = 3;}
                else if ((MoveGen.wr & (mask >>> i)) != 0) {j = 0; k = 4;}
                else if ((MoveGen.br & (mask >>> i)) != 0) {j = 1; k = 4;}
                else if ((MoveGen.wp & (mask >>> i)) != 0) {j = 0; k = 5;}
                else if ((MoveGen.bp & (mask >>> i)) != 0) {j = 1; k = 5;}

                if (j != -1) {
                    g.drawImage(pieces[j * 6 + k], (i % 8) * 64, (i / 8) * 64, 64, 64, this);
                }
            }
        }

        if (srcX != -1) {
            // We subtract 32 so the mouse is in the center of the image of the piece.
            g.drawImage(getPiece(srcX, srcY), mouseX - 32, mouseY - 32, 64, 64, this);
        }
    }

    private void paintPiecesBlack(Graphics g) {
        long mask = 1L << 63;
        int j, k;

        for (int i = 0; i < 64; i++) {
            j = -1;
            k = -1;

            if (srcX != (63 - i) % 8 || srcY != (63 - i) / 8){
                if ((MoveGen.wk & (mask >>> (63 - i))) != 0) {j = 0; k = 0;}
                else if ((MoveGen.bk & (mask >>> (63 - i))) != 0) {j = 1; k = 0;}
                else if ((MoveGen.wq & (mask >>> (63 - i))) != 0) {j = 0; k = 1;}
                else if ((MoveGen.bq & (mask >>> (63 - i))) != 0) {j = 1; k = 1;}
                else if ((MoveGen.wb & (mask >>> (63 - i))) != 0) {j = 0; k = 2;}
                else if ((MoveGen.bb & (mask >>> (63 - i))) != 0) {j = 1; k = 2;}
                else if ((MoveGen.wn & (mask >>> (63 - i))) != 0) {j = 0; k = 3;}
                else if ((MoveGen.bn & (mask >>> (63 - i))) != 0) {j = 1; k = 3;}
                else if ((MoveGen.wr & (mask >>> (63 - i))) != 0) {j = 0; k = 4;}
                else if ((MoveGen.br & (mask >>> (63 - i))) != 0) {j = 1; k = 4;}
                else if ((MoveGen.wp & (mask >>> (63 - i))) != 0) {j = 0; k = 5;}
                else if ((MoveGen.bp & (mask >>> (63 - i))) != 0) {j = 1; k = 5;}

                if (j != -1) {
                    g.drawImage(pieces[j * 6 + k], (i % 8) * 64, (i / 8) * 64, 64, 64, this);
                }
            }
        }

        if (srcX != -1) {
            // We subtract 32 so the mouse is in the center of the image of the piece.
            g.drawImage(getPiece(srcX, srcY), mouseX - 32, mouseY - 32, 64, 64, this);
        }
    }

    private void paintPieces(Graphics g) {
        if (Chess.playerIsWhite) paintPiecesWhite(g);
        else paintPiecesBlack(g);
    }

    private Color oppositeColor (Color color) {
        if (color == black) {
            return white;
        }

        return black;
    }

    // The method returns true if the current move did not lead to a checkmate
    // or a stalemate, so the game can continue.
    private boolean verifyNotEndOfGame() {
        String moves = moveGen.possibleMoves(MoveGen.wp, MoveGen.wr, MoveGen.wn,
                MoveGen.wb, MoveGen.wq, MoveGen.wk, MoveGen.bp, MoveGen.br, MoveGen.bn, MoveGen.bb,
                MoveGen.bq, MoveGen.bk, MoveGen.enPassant, MoveGen.white, MoveGen.whiteCastleK,
                MoveGen.whiteCastleQ, MoveGen.blackCastleK, MoveGen.blackCastleQ);

        for (int i = 0; i < moves.length(); i += 4) {
            String move = moves.substring(i, i + 4);

            if (Engine.tryMove(move, false)) return true;
        }

        MoveGen.occupied = MoveGen.wp | MoveGen.wr | MoveGen.wn | MoveGen.wb | MoveGen.wq | MoveGen.wk |
                MoveGen.bp | MoveGen.br | MoveGen.bn | MoveGen.bb | MoveGen.bq | MoveGen.bk;

        if (((MoveGen.wk & moveGen.controlledSquares(MoveGen.bp, MoveGen.br,
                MoveGen.bn, MoveGen.bb, MoveGen.bq, MoveGen.bk,
                MoveGen.notBlackPieces, false)) == 0 && MoveGen.white) ||
                ((MoveGen.bk & moveGen.controlledSquares(MoveGen.wp, MoveGen.wr,
                        MoveGen.wn, MoveGen.wb, MoveGen.wq, MoveGen.wk,
                        MoveGen.notWhitePieces, true)) == 0 && !MoveGen.white)) {

            endingOfGameCode = 1;
        } else {
            endingOfGameCode = 0;
        }

            return false;
    }

    private void presentCheckmateScreen(int code) {
        gameEnded = true;
        if (code == 0) {
            JOptionPane.showMessageDialog(this, "Checkmate");
        } else {
            JOptionPane.showMessageDialog(this, "Stalemate");
        }
    }

    // The method receives the square where the mouse clicked and returns the
    // image of the piece that was clicked.
    private Image getPiece(int x, int y) {
        int index = y * 8 + x;
        long bitboard = 1L << (63 - index);

        if ((bitboard & MoveGen.wk) != 0) return pieces[0];
        else if ((bitboard & MoveGen.wq) != 0) return pieces[1];
        else if ((bitboard & MoveGen.wb) != 0) return pieces[2];
        else if ((bitboard & MoveGen.wn) != 0) return pieces[3];
        else if ((bitboard & MoveGen.wr) != 0) return pieces[4];
        else if ((bitboard & MoveGen.wp) != 0) return pieces[5];
        else if ((bitboard & MoveGen.bk) != 0) return pieces[6];
        else if ((bitboard & MoveGen.bq) != 0) return pieces[7];
        else if ((bitboard & MoveGen.bb) != 0) return pieces[8];
        else if ((bitboard & MoveGen.bn) != 0) return pieces[9];
        else if ((bitboard & MoveGen.br) != 0) return pieces[10];
        else if ((bitboard & MoveGen.bp) != 0) return pieces[11];
        else return null;
    }

    // The method receives a promotion type move and returns the starting position of the pawn.
    private int startPositionOfPromotion(String move) {
        int start = move.charAt(0) - '0';

        if (MoveGen.white) {
            return 10 + start;
        }

        return 60 + start;
    }

    // The method receives a promotion type move and returns the end position of the pawn.
    private int endPositionOfPromotion(String move) {
        int end = move.charAt(1) - '0';

        if (MoveGen.white) {
            return end;
        }

        return 70 + end;
    }

    // The method receives an en passant type move and returns the starting position of the pawn.
    private int startPositionOfEnPassant(String move) {
        int start = move.charAt(0) - '0';

        if (MoveGen.white) {
            return 30 + start;
        }

        return 40 + start;
    }

    // The method receives an en passant type move and returns the end position of the pawn.
    private int endPositionOfEnPassant(String move) {
        int end = move.charAt(1) - '0';

        if (MoveGen.white) {
            return 20 + end;
        }

        return 50 + end;
    }

    // The method receives a castling type move and returns the starting position of the king.
    private int startPositionOfCastle(String move) {
        if (move.charAt(0) == 'w') {
            return 74;
        }

        return 4;
    }

    // The method receives a castling type move and returns the end position of the king.
    private int endPositionOfCastle(String move) {
        return switch (move.substring(0, 2)) {
            case "wk" -> 76;
            case "wq" -> 72;
            case "bk" -> 6;
            default -> 2;
        };

    }

    // The method receives the starting position of a piece, in the form of
    // pieceY * 10 + pieceX, in order to be easier to parse. Then, it returns
    // a list of all the possible positions it can move to(in the same notation).
    // This method is useful in order for the player to know which moves he has at
    // his disposal.
    private ArrayList<Integer> getPossibleMovesForPiece(int start) {
        ArrayList<Integer> possibilities = new ArrayList<>();
        String moves = moveGen.possibleMoves(MoveGen.wp, MoveGen.wr, MoveGen.wn,
                MoveGen.wb, MoveGen.wq, MoveGen.wk, MoveGen.bp, MoveGen.br, MoveGen.bn, MoveGen.bb,
                MoveGen.bq, MoveGen.bk, MoveGen.enPassant, MoveGen.white, MoveGen.whiteCastleK,
                MoveGen.whiteCastleQ, MoveGen.blackCastleK, MoveGen.blackCastleQ);

        for (int i = 0; i < moves.length(); i += 4) {
            String move = moves.substring(i, i + 4);

            if (move.charAt(3) == 'P') {
                if (start == startPositionOfPromotion(move) && Engine.tryMove(move, false)) {
                    possibilities.add(endPositionOfPromotion(move));
                }
            } else if (move.charAt(3) == 'E') {
                if (start == startPositionOfEnPassant(move) && Engine.tryMove(move, false)) {
                    possibilities.add(endPositionOfEnPassant(move));
                }
            } else if (move.charAt(3) == 'C') {
                if (start == startPositionOfCastle(move) && Engine.tryMove(move, false)) {
                    possibilities.add(endPositionOfCastle(move));
                }
            } else {
                if (Integer.parseInt(move.substring(0, 2)) == start && Engine.tryMove(move, false)) {
                    possibilities.add(Integer.parseInt(move.substring(2, 4)));
                }
            }
        }

        return possibilities;
    }

    // The method prompts the player the options he has to promote his pawn.
    private int promptPromotionPiece() {
        ImageIcon[] pieceImages = new ImageIcon[4];

        if (MoveGen.white) {
            for (int i = 0; i < 4; i++) {
                pieceImages[i] = new ImageIcon(pieces[i + 1]);
            }
        } else {
            for (int i = 0; i < 4; i++) {
                pieceImages[i] = new ImageIcon(pieces[i + 7]);
            }
        }

        Object[] pieces = {pieceImages[0], pieceImages[1], pieceImages[2], pieceImages[3]};

        return JOptionPane.showOptionDialog(null, "Choose the piece to promote into", null,
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, pieces, pieces[0]);
    }

    // The player chooses an option to promote his pawn, and we return the final move.
    private String moveBasedOnCode(String move, int code) {
        String finalMove = move.substring(0, 2);

        if (code == 0 && MoveGen.white) {
            finalMove += "Q";
        } else if (code == 0) {
            finalMove += "q";
        } else if (code == 1 && MoveGen.white) {
            finalMove += "R";
        } else if (code == 1) {
            finalMove += "r";
        } else if (code == 2 && MoveGen.white) {
            finalMove += "N";
        } else if (code == 2) {
            finalMove += "n";
        } else if (code == 3 && MoveGen.white) {
            finalMove += "B";
        } else {
            finalMove += "b";
        }

        return finalMove + "P";
    }

    private boolean makePlayerMove(int start) {
        String moves = moveGen.possibleMoves(MoveGen.wp, MoveGen.wr, MoveGen.wn,
                MoveGen.wb, MoveGen.wq, MoveGen.wk, MoveGen.bp, MoveGen.br, MoveGen.bn, MoveGen.bb,
                MoveGen.bq, MoveGen.bk, MoveGen.enPassant, MoveGen.white, MoveGen.whiteCastleK,
                MoveGen.whiteCastleQ, MoveGen.blackCastleK, MoveGen.blackCastleQ);
        int end = mouseY / 64 * 10 + mouseX / 64;

        // If we see the board from black's perspective, we also flip the moves. The start is already flipped.
        if (!Chess.playerIsWhite) {
            int x = end % 10;
            int y = end / 10;

            x = 7 - x;
            y = 7 - y;
            end = y * 10 + x;
        }

        for (int i = 0; i < moves.length(); i += 4) {
            String move = moves.substring(i, i + 4);

            // For promotion, we verify if we can promote the pawn, and if we can, we
            // then prompt the player with the 4 piece options he can promote to, in order to
            // choose. After the choice is made, we make the move. It is not the most
            // efficient way, but we don't need to be as fast when the player moves as
            // we are when we generate moves, so it is ok.
            if (move.charAt(3) == 'P') {
                if (start == startPositionOfPromotion(move) && end == endPositionOfPromotion(move) &&
                Engine.tryMove(move, false)) {
                    int code = promptPromotionPiece();
                    return Engine.tryMove(moveBasedOnCode(move, code), true);
                }
            } else if (move.charAt(3) == 'E') {
                if (start == startPositionOfEnPassant(move) && end == endPositionOfEnPassant(move) &&
                Engine.tryMove(move, true)) {
                    return true;
                }
            } else if (move.charAt(3) == 'C') {
                if (start == startPositionOfCastle(move) && end == endPositionOfCastle(move) &&
                Engine.tryMove(move, true)) {
                    return true;
                }
            } else {
                if (start == Integer.parseInt(move.substring(0, 2)) && end == Integer.parseInt(move.substring(2, 4)) &&
                Engine.tryMove(move, true)) {
                    return true;
                }
            }
        }
        return false;
    }

    // The method verifies if there is a piece that is clicked, and if it is, it
    // highlights the positions to where it might move.
    private void paintPossibleMoves(Graphics g) {
        if (startPosition != -1) {
            ArrayList<Integer> moves = getPossibleMovesForPiece(startPosition);

            for (Integer move: moves) {
                int y = move / 10;
                int x = move % 10;

                if (!Chess.playerIsWhite) {
                    y = 7 - y;
                    x = 7 - x;
                }

                if ((x + y) % 2 == 0) {
                    g.setColor(possibleWhiteMoveColor);
                } else {
                    g.setColor(possibleBlackMoveColor);
                }
                g.fillRect(x * 64, y * 64, 64, 64);
            }
        }
    }

    public void paintComponent(Graphics g){
        Color crtColor = white;

        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                g.setColor(crtColor);
                g.fillRect(x * 64, y * 64, 64, 64);
                crtColor = oppositeColor(crtColor);
            }
            crtColor = oppositeColor(crtColor);
        }

        paintPossibleMoves(g);

        paintPieces(g);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (!gameEnded) {
            startPosition = (e.getY() / 64) * 10 + (e.getX() / 64);
            srcX = e.getX() / 64;
            srcY = e.getY() / 64;

            if (!Chess.playerIsWhite) {
                srcX = 7 - srcX;
                srcY = 7 - srcY;
                startPosition = srcY * 10 + srcX;
            }

            mouseX = e.getX();
            mouseY = e.getY();
            repaint();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (!gameEnded) {
            boolean playerMoved = makePlayerMove(startPosition);
            startPosition = -1;
            srcX = -1;
            srcY = -1;
            mouseX = -1;
            mouseY = -1;
            paintComponent(this.getGraphics());

            if (!verifyNotEndOfGame()) {
                presentCheckmateScreen(endingOfGameCode);
            }

            if (playerMoved) {
                makeEngineMove();
            }
        }
    }

    public void makeEngineMove() {
        MoveGen.isEndGame = Engine.verifyEndGame(MoveGen.wq, MoveGen.bq, MoveGen.isEndGame);
        Engine.counter = 0;
        Engine.emptyKillerMoves();

        String move = Engine.alphaBeta(Engine.maxDepth, Integer.MIN_VALUE / 2, Integer.MAX_VALUE / 2, "",
                MoveGen.wp, MoveGen.wr, MoveGen.wn, MoveGen.wb, MoveGen.wq, MoveGen.wk,
                MoveGen.bp, MoveGen.br, MoveGen.bn, MoveGen.bb, MoveGen.bq, MoveGen.bk, MoveGen.enPassant,
                MoveGen.whiteCastleK, MoveGen.whiteCastleQ, MoveGen.blackCastleK, MoveGen.blackCastleQ,
                MoveGen.white, MoveGen.isEndGame, Engine.ply);

        Engine.tryMove(move.substring(0, 4), true);

        // If we are in the endgame, there are fewer pieces, so less to calculate. This means, that
        // we can go a little deeper.
        if (Engine.counter < 500000 && Engine.maxDepth < 9 && Engine.ply > 30 && MoveGen.isEndGame) {
            Engine.maxDepth++;
        }

        repaint();
        if (!verifyNotEndOfGame()) {
            presentCheckmateScreen(endingOfGameCode);
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (!gameEnded) {
            mouseX = e.getX();
            mouseY = e.getY();
            repaint();
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }
}
