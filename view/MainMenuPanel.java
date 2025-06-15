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

    // --- PERBAIKAN: Kata kunci 'final' dihapus dari sini ---
    private JRadioButton easyButton;
    private JRadioButton mediumButton;
    private JRadioButton hardButton;

    private Font arcadeFont, titleFont;
    private Image backgroundImage;
    private final Color FONT_COLOR = new Color(0xFFEEA9);
    private final Color FONT_SHADOW_COLOR = new Color(0x7B4019);

    public MainMenuPanel(GameView parentFrame) {
        this.parentFrame = parentFrame;
        loadAssets();
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        JLabel titleLabel = new JLabel("The Usual Suspect");
        titleLabel.setFont(titleFont);
        titleLabel.setForeground(FONT_COLOR);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; gbc.insets = new Insets(20, 20, 20, 20);
        add(titleLabel, gbc);

        JPanel leftPanel = createLeftPanel();
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1; gbc.weightx = 0.4;
        gbc.fill = GridBagConstraints.VERTICAL; gbc.anchor = GridBagConstraints.CENTER;
        add(leftPanel, gbc);

        JTable scoreTable;
        JPanel rightPanel = new JPanel(new BorderLayout(0, 10));
        rightPanel.setOpaque(false);
        rightPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        JLabel topScoresLabel = new JLabel("Top Scores");
        setupLabel(topScoresLabel, 22f);
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

        JButton startButton = createImageButton("/assets/start_button.png");
        JButton quitButton = createImageButton("/assets/quit_button.png");

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
        setupLabel(usernameLabel, 18f);
        JLabel modeLabel = new JLabel("Mode:");
        setupLabel(modeLabel, 18f);

        leftGbc.fill = GridBagConstraints.HORIZONTAL;
        leftGbc.insets = new Insets(0, 0, 5, 0);
        leftGbc.gridx = 0;
        leftGbc.gridy = 0;
        leftPanel.add(usernameLabel, leftGbc);
        leftGbc.gridy++;
        leftPanel.add(nameField, leftGbc);
        leftGbc.gridy++;
        leftGbc.insets = new Insets(10, 0, 10, 0);
        leftPanel.add(startButton, leftGbc);
        leftGbc.gridy++;
        leftGbc.insets = new Insets(20, 0, 5, 0);
        leftPanel.add(modeLabel, leftGbc);
        leftGbc.gridy++;
        leftGbc.insets = new Insets(0, 0, 0, 0);
        leftPanel.add(easyButton, leftGbc);
        leftGbc.gridy++;
        leftPanel.add(mediumButton, leftGbc);
        leftGbc.gridy++;
        leftPanel.add(hardButton, leftGbc);
        leftGbc.gridy++;
        leftGbc.weighty = 1.0;
        leftGbc.anchor = GridBagConstraints.SOUTH;
        leftPanel.add(quitButton, leftGbc);

        startButton.addActionListener(e -> {
            String username = nameField.getText();
            if (username.isBlank()) {
                JOptionPane.showMessageDialog(this, "Please enter a username.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            parentFrame.startGame(username, getSelectedDifficulty());
        });
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
        // Menggambar outline pada judul secara manual
        for (Component comp : getComponents()) {
            if (comp instanceof JLabel && "The Usual Suspect".equals(((JLabel) comp).getText())) {
                JLabel label = (JLabel) comp;
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setFont(label.getFont());
                int ascent = g.getFontMetrics(label.getFont()).getAscent();
                int x = label.getX();
                int y = label.getY() + ascent;
                int offset = 2;
                g2d.setColor(FONT_SHADOW_COLOR);
                g2d.drawString(label.getText(), x + offset, y + offset);
                g2d.setColor(label.getForeground());
                g2d.drawString(label.getText(), x, y);
                g2d.dispose();
            }
        }
    }

    private void loadAssets() {
        try { backgroundImage = new ImageIcon(Objects.requireNonNull(getClass().getResource("/assets/background.gif"))).getImage(); } catch (Exception e) { System.err.println("Gagal memuat background.gif: " + e.getMessage()); }
        try (InputStream is = getClass().getResourceAsStream(gi"/assets/joystixmonospace.otf")) {
            joystixFont = (is != null) ? Font.createFont(Font.TRUETYPE_FONT, is) : new Font("Monospaced", Font.BOLD, 14);
            titleFont = joystixFont.deriveFont(48f);
        } catch (Exception e) {
            System.err.println("Gagal memuat joystix monospace.otf: " + e.getMessage());
            joystixFont = new Font("Monospaced", Font.BOLD, 14);
            titleFont = new Font("Arial", Font.BOLD, 30);
        }
    }

    private void setupRadioButton(JRadioButton button) {
        button.setFont(joystixFont.deriveFont(20f));
        button.setOpaque(false);
        button.setForeground(FONT_COLOR);
        button.setFocusPainted(false);
    }

    private void setupLabel(JLabel label, float size) {
        label.setFont(joystixFont.deriveFont(size));
        label.setForeground(FONT_COLOR);
    }

    private JButton createImageButton(String imagePath) {
        JButton button = new JButton();
        try {
            ImageIcon icon = new ImageIcon(Objects.requireNonNull(getClass().getResource(imagePath)));
            button.setIcon(icon);
            button.setText("");
            button.setBorder(BorderFactory.createEmptyBorder());
            button.setContentAreaFilled(false);
            button.setFocusPainted(false);
        } catch (Exception e) {
            System.err.println("Gagal memuat gambar tombol: " + imagePath);
        }
        return button;
    }

    private Difficulty getSelectedDifficulty() {
        if (mediumButton.isSelected()) return Difficulty.MEDIUM;
        if (hardButton.isSelected()) return Difficulty.HARD;
        return Difficulty.EASY;
    }

    public void updateScoreTable() {
        tableModel.setRowCount(0);
        List<ScoreEntry> scores = DatabaseConnection.getScoresByMode(getSelectedDifficulty().name());
        for (ScoreEntry score : scores) {
            tableModel.addRow(new Object[]{score.name(), score.score()});
        }
    }
}