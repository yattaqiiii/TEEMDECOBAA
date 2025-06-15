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
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false); // Ukuran tidak bisa diubah oleh user

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mainMenuPanel = new MainMenuPanel(this);
        mainPanel.add(mainMenuPanel, "MENU");

        add(mainPanel);

        // --- PERUBAHAN: Gunakan pack() untuk ukuran otomatis yang presisi ---
        pack();

        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void startGame(String username, Difficulty difficulty) {
        this.currentUsername = username;
        this.currentDifficulty = difficulty;

        Player player = new Player(317, 350); // Posisi awal player di tengah

        // Mengirim ukuran yang benar (682x512) ke ViewModel
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
            gamePanel = null; // Hapus referensi agar bisa di-garbage collected
        }
        cardLayout.show(mainPanel, "MENU");
        mainMenuPanel.updateScoreTable();
    }
}