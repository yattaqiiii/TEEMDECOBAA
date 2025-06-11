package view;

import javax.swing.*;

/**
 * View (Frame): Jendela utama aplikasi.
 * Tugasnya hanya menampung panel utama game.
 */
public class GameView extends JFrame {

    public GameView(GamePanel gamePanel) {
        setTitle("Collect The Skill Balls");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Menambahkan panel game ke dalam frame
        add(gamePanel);

        setVisible(true);
    }
}