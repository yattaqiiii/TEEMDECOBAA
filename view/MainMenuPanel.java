package view;

import database.DatabaseConnection;
import model.Difficulty;
import model.ScoreEntry;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;

public class MainMenuPanel extends JPanel {
    private GameView parentFrame;
    private JTable scoreTable;
    private DefaultTableModel tableModel;
    private JRadioButton easyButton, mediumButton, hardButton;
    private Font arcadeFont, titleFont;

    // --- PERBAIKAN 1: Tambahkan variabel untuk gambar background ---
    private Image backgroundImage;

    public MainMenuPanel(GameView parentFrame) {
        this.parentFrame = parentFrame;
        loadAssets(); // Panggil metode untuk memuat font DAN background
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // Title
        JLabel titleLabel = new JLabel("The Usual Suspect");
        titleLabel.setFont(titleFont);
        titleLabel.setForeground(new Color(0xFFEEA9)); // Sesuaikan warna agar terbaca di background

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; gbc.insets = new Insets(20, 20, 20, 20);
        add(titleLabel, gbc);

        // Kolom Kiri (Input & Mode)
        JPanel leftPanel = new JPanel(new GridBagLayout());
        leftPanel.setOpaque(false); // Buat panel transparan
        GridBagConstraints leftGbc = new GridBagConstraints();

        JTextField nameField = new JTextField(15);
        JButton startButton = new JButton("Start");
        JButton quitButton = new JButton("Quit");

        easyButton = new JRadioButton("Easy", true);
        mediumButton = new JRadioButton("Medium");
        hardButton = new JRadioButton("Hard");
        ButtonGroup difficultyGroup = new ButtonGroup();
        difficultyGroup.add(easyButton);
        difficultyGroup.add(mediumButton);
        difficultyGroup.add(hardButton);

        // Terapkan font dan warna
        Font labelFont = arcadeFont.deriveFont(18f);
        Color labelColor = new Color(0xFFEEA9);
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(labelFont);
        usernameLabel.setForeground(labelColor);
        JLabel modeLabel = new JLabel("Mode:");
        modeLabel.setFont(labelFont);
        modeLabel.setForeground(labelColor);

        nameField.setFont(arcadeFont.deriveFont(18f));
        startButton.setFont(arcadeFont.deriveFont(20f));
        quitButton.setFont(arcadeFont.deriveFont(20f));
        easyButton.setFont(arcadeFont.deriveFont(18f));
        easyButton.setOpaque(false); easyButton.setForeground(labelColor);
        mediumButton.setFont(arcadeFont.deriveFont(18f));
        mediumButton.setOpaque(false); mediumButton.setForeground(labelColor);
        hardButton.setFont(arcadeFont.deriveFont(18f));
        hardButton.setOpaque(false); hardButton.setForeground(labelColor);

        leftGbc.gridx = 0; leftGbc.gridy = 0; leftGbc.anchor = GridBagConstraints.WEST;
        leftPanel.add(usernameLabel, leftGbc);
        leftGbc.gridy++; leftGbc.fill = GridBagConstraints.HORIZONTAL;
        leftPanel.add(nameField, leftGbc);
        leftGbc.gridy++; leftGbc.insets = new Insets(10, 0, 10, 0);
        leftPanel.add(startButton, leftGbc);
        leftGbc.gridy++; leftGbc.insets = new Insets(20, 0, 0, 0);
        leftPanel.add(modeLabel, leftGbc);
        leftGbc.gridy++; leftGbc.insets = new Insets(5, 0, 0, 0);
        leftPanel.add(easyButton, leftGbc);
        leftGbc.gridy++;
        leftPanel.add(mediumButton, leftGbc);
        leftGbc.gridy++;
        leftPanel.add(hardButton, leftGbc);
        leftGbc.gridy++; leftGbc.weighty = 1.0; leftGbc.anchor = GridBagConstraints.SOUTH;
        leftPanel.add(quitButton, leftGbc);
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1; gbc.weightx = 0.3; gbc.fill = GridBagConstraints.VERTICAL;
        add(leftPanel, gbc);

        // Kolom Kanan (Tabel Skor)
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setOpaque(false); // Buat panel transparan
        JLabel topScoresLabel = new JLabel("Top Scores:");
        topScoresLabel.setFont(labelFont);
        topScoresLabel.setForeground(labelColor);
        rightPanel.add(topScoresLabel, BorderLayout.NORTH);
        String[] columnNames = {"Name", "Score"};
        tableModel = new DefaultTableModel(columnNames, 0);
        scoreTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(scoreTable);
        scrollPane.getViewport().setOpaque(false); // Buat area viewport transparan
        scrollPane.setOpaque(false); // Buat scrollpane transparan
        rightPanel.add(scrollPane, BorderLayout.CENTER);

        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 0.7; gbc.fill = GridBagConstraints.BOTH;
        add(rightPanel, gbc);

        // Listeners...
        startButton.addActionListener(e -> { String username = nameField.getText(); if (username.isBlank()) { JOptionPane.showMessageDialog(this, "Please enter a username.", "Warning", JOptionPane.WARNING_MESSAGE); return; } parentFrame.startGame(username, getSelectedDifficulty()); });
        quitButton.addActionListener(e -> System.exit(0));
        easyButton.addActionListener(e -> updateScoreTable()); mediumButton.addActionListener(e -> updateScoreTable()); hardButton.addActionListener(e -> updateScoreTable());
        updateScoreTable();
    }

    // --- PERBAIKAN 2: Override paintComponent untuk menggambar background ---
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }

    private void loadAssets() {
        // Muat Background
        try {
            backgroundImage = new ImageIcon(Objects.requireNonNull(getClass().getResource("/assets/background.gif"))).getImage();
        } catch (Exception e) {
            System.err.println("Gagal memuat background.gif: " + e.getMessage());
        }

        // Muat Font
        try (InputStream is = getClass().getResourceAsStream("/assets/ArcadeClassic.ttf")) {
            if (is != null) {
                arcadeFont = Font.createFont(Font.TRUETYPE_FONT, is);
                titleFont = arcadeFont.deriveFont(48f);
            } else {
                throw new Exception("File font tidak ditemukan!");
            }
        } catch (Exception e) {
            System.err.println("Gagal memuat ArcadeClassic.ttf: " + e.getMessage());
            arcadeFont = new Font("Arial", Font.BOLD, 14);
            titleFont = new Font("Arial", Font.BOLD, 30);
        }
    }

    private Difficulty getSelectedDifficulty() {
        if (easyButton.isSelected()) return Difficulty.EASY;
        else if (mediumButton.isSelected()) return Difficulty.MEDIUM;
        else return Difficulty.HARD;
    }

    public void updateScoreTable() {
        tableModel.setRowCount(0);
        String selectedMode = getSelectedDifficulty().name();
        List<ScoreEntry> scores = DatabaseConnection.getScoresByMode(selectedMode);
        for (ScoreEntry score : scores) {
            tableModel.addRow(new Object[]{score.name(), score.score()});
        }
    }
}