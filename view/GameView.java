package view;

import javax.swing.*;

/**
 * View (Frame): Jendela utama aplikasi.
 * Kembali ke versi sederhana yang hanya menampung GamePanel.
 */
public class GameView extends JFrame {

    public GameView(GamePanel gamePanel) {
        setTitle("Collect The Skill Balls");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Langsung menambahkan panel game ke dalam frame
        add(gamePanel);

        setVisible(true);
    }
}