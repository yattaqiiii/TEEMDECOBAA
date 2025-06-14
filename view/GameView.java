package view;

import database.DatabaseConnection;
import model.Difficulty;
import model.Player;
import viewmodel.GameViewModel;

import javax.swing.*;
import java.awt.*;

public class GameView extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private MainMenuPanel mainMenuPanel;
    private GamePanel gamePanel;
    private String currentUsername;
    private Difficulty currentDifficulty;

    public GameView() {
        setTitle("The Usual Suspect");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mainMenuPanel = new MainMenuPanel(this);
        mainPanel.add(mainMenuPanel, "MENU");

        add(mainPanel);
        setVisible(true);
    }

    public void startGame(String username, Difficulty difficulty) {
        this.currentUsername = username;
        this.currentDifficulty = difficulty;

        Player player = new Player(350, 450); // Posisi awal disesuaikan
        GameViewModel viewModel = new GameViewModel(player, 800, 600, difficulty);
        gamePanel = new GamePanel(viewModel, this);

        mainPanel.add(gamePanel, "GAME");
        cardLayout.show(mainPanel, "GAME");
        gamePanel.requestFocusInWindow();
    }

    public void showMenu(int finalScore) {
        // Simpan skor sebelum kembali ke menu
        DatabaseConnection.insertScore(currentUsername, finalScore, currentDifficulty.name());

        // Hapus panel game lama dan kembali ke menu
        mainPanel.remove(gamePanel);
        cardLayout.show(mainPanel, "MENU");
        mainMenuPanel.updateScoreTable();
    }
}