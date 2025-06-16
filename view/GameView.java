package view;

import database.DatabaseConnection;
import model.Difficulty;
import model.Player;
import viewmodel.GameViewModel;
import javax.swing.JFrame; // Diubah dari javax.swing.*
import javax.swing.JPanel; // Diubah dari javax.swing.*
import java.awt.CardLayout; // Diubah dari java.awt.*;

// kelas gameview merupakan frame utama untuk aplikasi game.
// kelas ini mengatur panel-panel yang akan ditampilkan, seperti menu utama dan panel game.
// variabel cardlayout digunakan untuk mengganti antar panel.
// variabel mainpanel adalah panel utama yang memegang panel lainnya menggunakan cardlayout.
// variabel mainmenupanel merepresentasikan panel menu utama.
// variabel gamepanel merepresentasikan panel tempat permainan berlangsung.
// variabel currentusername menyimpan nama pengguna yang sedang bermain.
// variabel currentdifficulty menyimpan tingkat kesulitan yang dipilih.
public class GameView extends JFrame {
    private final CardLayout cardLayout;
    private final JPanel mainPanel;
    private final MainMenuPanel mainMenuPanel;
    private GamePanel gamePanel;
    private String currentUsername;
    private Difficulty currentDifficulty;
    private int currentTotalBallsCaught; // Tambahkan variabel ini

    public GameView() {
        setTitle("The Usual Suspect");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false); // Ukuran tidak bisa diubah oleh user

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mainMenuPanel = new MainMenuPanel(this);
        mainPanel.add(mainMenuPanel, "MENU");

        add(mainPanel);

        pack();

        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void startGame(String username, Difficulty difficulty) {
        this.currentUsername = username;
        this.currentDifficulty = difficulty;
        this.currentTotalBallsCaught = 0; // Inisialisasi saat game dimulai

        Player player = new Player(317, 350); // Posisi awal player di tengah

        // Mengirim ukuran yang benar (682x512) ke ViewModel
        GameViewModel viewModel = new GameViewModel(player, 682, 512, difficulty);
        gamePanel = new GamePanel(viewModel, this);

        mainPanel.add(gamePanel, "GAME");
        cardLayout.show(mainPanel, "GAME");
        gamePanel.requestFocusInWindow();
    }

    // Modifikasi showMenu untuk menerima total bola yang ditangkap
    public void showMenu(int finalScore, int totalBallsCaught) {
        if (currentUsername != null && !currentUsername.isBlank() && currentDifficulty != null) {
            // Panggil insertScore dengan parameter yang benar
            DatabaseConnection.insertScore(currentUsername, finalScore, totalBallsCaught, currentDifficulty.name());
        }
        if (gamePanel != null) {
            mainPanel.remove(gamePanel);
            gamePanel = null; // Hapus referensi agar bisa di-garbage collected
        }
        cardLayout.show(mainPanel, "MENU");
        mainMenuPanel.updateScoreTable();
    }
}