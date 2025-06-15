package view;

import database.DatabaseConnection;
import model.Difficulty;
import model.Player;
import viewmodel.GameViewModel;
import javax.swing.*;
import java.awt.*;

public class GameView extends JFrame {
    private final CardLayout cardLayout;
    private final JPanel mainPanel;
    private final MainMenuPanel mainMenuPanel;
    private GamePanel gamePanel;
    private String currentUsername;
    private Difficulty currentDifficulty;

    public GameView() {
        setTitle("The Usual Suspect");
        // --- PERUBAHAN: Mengunci ukuran jendela game ---
        // Ukuran konten panel adalah 682x512.
        // JFrame perlu sedikit lebih tinggi untuk menampung title bar Windows.
        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.setPreferredSize(new Dimension(682, 512));
        setContentPane(contentPane);
        pack(); // pack() akan menyesuaikan ukuran frame dengan isinya

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false); // Ukuran tidak bisa diubah oleh user

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mainMenuPanel = new MainMenuPanel(this);
        mainPanel.add(mainMenuPanel, "MENU");

        getContentPane().add(mainPanel);
        setVisible(true);
    }

    public void startGame(String username, Difficulty difficulty) {
        this.currentUsername = username;
        this.currentDifficulty = difficulty;

        Player player = new Player(350, 350);
        // Mengirim ukuran yang benar ke ViewModel
        GameViewModel viewModel = new GameViewModel(player, 682, 512, difficulty);
        gamePanel = new GamePanel(viewModel, this);

        mainPanel.add(gamePanel, "GAME");
        cardLayout.show(mainPanel, "GAME");
        gamePanel.requestFocusInWindow();
    }

    public void showMenu(int finalScore) {
        if (currentUsername != null && !currentUsername.isBlank() && currentDifficulty != null) {
            DatabaseConnection.insertScore(currentUsername, finalScore, currentDifficulty.name());
        }
        if (gamePanel != null) {
            mainPanel.remove(gamePanel);
        }
        cardLayout.show(mainPanel, "MENU");
        mainMenuPanel.updateScoreTable();
    }
}