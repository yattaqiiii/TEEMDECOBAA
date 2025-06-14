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
    private final GameView parentFrame;
    private final DefaultTableModel tableModel;

    // --- PERBAIKAN: Kata kunci 'final' dihapus dari deklarasi variabel ini ---
    private JRadioButton easyButton;
    private JRadioButton mediumButton;
    private JRadioButton hardButton;

    private Font arcadeFont, titleFont;
    private Image backgroundImage;

    public MainMenuPanel(GameView parentFrame) {
        this.parentFrame = parentFrame;
        loadAssets();
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // Title
        JLabel titleLabel = new JLabel("The Usual Suspect");
        titleLabel.setFont(titleFont);
        titleLabel.setForeground(new Color(0xFFEEA9));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; gbc.insets = new Insets(20, 20, 20, 20);
        add(titleLabel, gbc);

        // Kolom Kiri
        JPanel leftPanel = createLeftPanel();
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1; gbc.weightx = 0.4;
        gbc.fill = GridBagConstraints.VERTICAL; gbc.anchor = GridBagConstraints.CENTER;
        add(leftPanel, gbc);

        // Kolom Kanan
        JTable scoreTable;
        JPanel rightPanel = new JPanel(new BorderLayout(0, 10));
        rightPanel.setOpaque(false);
        rightPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        JLabel topScoresLabel = new JLabel("Top Scores");
        setupLabel(topScoresLabel);
        topScoresLabel.setHorizontalAlignment(SwingConstants.CENTER);
        rightPanel.add(topScoresLabel, BorderLayout.NORTH);
        String[] columnNames = {"Name", "Score"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        scoreTable = new JTable(tableModel);
        scoreTable.setFont(arcadeFont.deriveFont(16f));
        scoreTable.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(scoreTable);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scoreTable.setOpaque(false);
        rightPanel.add(scrollPane, BorderLayout.CENTER);
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 0.6; gbc.fill = GridBagConstraints.BOTH;
        add(rightPanel, gbc);

        updateScoreTable();
    }

    private JPanel createLeftPanel() {
        JPanel leftPanel = new JPanel(new GridBagLayout());
        leftPanel.setOpaque(false);
        GridBagConstraints leftGbc = new GridBagConstraints();

        JTextField nameField = new JTextField(15);
        nameField.setFont(arcadeFont.deriveFont(18f));
        nameField.setHorizontalAlignment(JTextField.CENTER);

        JButton startButton = createImageButton("/assets/start_button.png", "/assets/start_button_pressed.png");
        JButton quitButton = createImageButton("/assets/quit_button.png", "/assets/quit_button_pressed.png");

        easyButton = new JRadioButton("Easy", true);
        mediumButton = new JRadioButton("Medium");
        hardButton = new JRadioButton("Hard");
        setupRadioButton(easyButton);
        setupRadioButton(mediumButton);
        setupRadioButton(hardButton);

        ButtonGroup difficultyGroup = new ButtonGroup();
        difficultyGroup.add(easyButton);
        difficultyGroup.add(mediumButton);
        difficultyGroup.add(hardButton);

        JLabel usernameLabel = new JLabel("Username:");
        setupLabel(usernameLabel);
        JLabel modeLabel = new JLabel("Mode:");
        setupLabel(modeLabel);

        leftGbc.gridx = 0; leftGbc.gridy = 0; leftGbc.anchor = GridBagConstraints.WEST; leftGbc.insets = new Insets(0, 0, 5, 0);
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

        startButton.addActionListener(e -> { String username = nameField.getText(); if (username.isBlank()) { JOptionPane.showMessageDialog(this, "Please enter a username.", "Warning", JOptionPane.WARNING_MESSAGE); return; } parentFrame.startGame(username, getSelectedDifficulty()); });
        quitButton.addActionListener(e -> System.exit(0));
        easyButton.addActionListener(e -> updateScoreTable());
        mediumButton.addActionListener(e -> updateScoreTable());
        hardButton.addActionListener(e -> updateScoreTable());

        return leftPanel;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }

    private void loadAssets() {
        try { backgroundImage = new ImageIcon(Objects.requireNonNull(getClass().getResource("/assets/background.gif"))).getImage(); } catch (Exception e) { System.err.println("Gagal memuat background.gif: " + e.getMessage()); }
        try (InputStream is = getClass().getResourceAsStream("/assets/ArcadeClassic.ttf")) {
            if (is != null) {
                arcadeFont = Font.createFont(Font.TRUETYPE_FONT, is);
                titleFont = arcadeFont.deriveFont(48f);
            } else { throw new Exception("File font tidak ditemukan!"); }
        } catch (Exception e) {
            System.err.println("Gagal memuat ArcadeClassic.ttf: " + e.getMessage());
            arcadeFont = new Font("Arial", Font.BOLD, 14);
            titleFont = new Font("Arial", Font.BOLD, 30);
        }
    }

    private void setupRadioButton(JRadioButton button) {
        button.setFont(arcadeFont.deriveFont(20f));
        button.setOpaque(false);
        button.setForeground(new Color(0xFFEEA9));
        button.setFocusPainted(false);
    }

    private void setupLabel(JLabel label) {
        label.setFont(arcadeFont.deriveFont(22f));
        label.setForeground(new Color(0xFFEEA9));
    }

    private JButton createImageButton(String imagePath, String pressedImagePath) {
        JButton button = new JButton();
        try {
            ImageIcon icon = new ImageIcon(Objects.requireNonNull(getClass().getResource(imagePath)));
            button.setIcon(icon);
            button.setText("");
            if (pressedImagePath != null) {
                ImageIcon pressedIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource(pressedImagePath)));
                button.setPressedIcon(pressedIcon);
            }
            button.setBorder(BorderFactory.createEmptyBorder());
            button.setContentAreaFilled(false);
            button.setFocusPainted(false);
        } catch (Exception e) {
            System.err.println("Gagal memuat gambar tombol: " + imagePath);
        }
        return button;
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