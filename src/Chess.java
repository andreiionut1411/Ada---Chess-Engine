import javax.swing.*;

public class Chess {
    private static final int fromLeft = 14; // These values represent the offset added by the borders of the frame.
    private static final int fromTop = 37;
    public static boolean playerIsWhite;

    // The method prompts the player to ask which color he wants to be.
    private static void askPlayerForColor() {
        String[] options = {"White", "Black", "Random"};
        int code = JOptionPane.showOptionDialog(null, "What color do you want to play as?", "Welcome to ADA 1.0",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

        if (code == 0) {
            playerIsWhite = true;
        } else if (code == 1) {
            playerIsWhite = false;
        } else {
            double rand = Math.random();

            if ((int)(rand * 1000) % 2 == 0) {
                playerIsWhite = true;
            } else {
                playerIsWhite = false;
            }
        }
    }

    public static void main(String[] args) {
        MoveGen moveGen = new MoveGen();
        moveGen.initialiseFen("4kr2/1q3n1p/p3b1p1/2p2p2/1pNnpQ1P/6P1/PPP1BP2/1KBR4 w - - 3 35");
        askPlayerForColor();

        JFrame frame = new JFrame("Ada 1.0");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        GraphicalInterface gui = new GraphicalInterface(moveGen);
        frame.add(gui);
        frame.setSize(512 + fromLeft, 512 + fromTop);
        frame.setVisible(true);

        if (!playerIsWhite) {
            gui.makeEngineMove();
        }
    }
}