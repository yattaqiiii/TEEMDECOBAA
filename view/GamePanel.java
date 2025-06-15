package view;

import model.*;
import viewmodel.GameViewModel;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class GamePanel extends JPanel implements Runnable {
    private final GameViewModel viewModel;
    private final GameView parentFrame;
    private GameState currentGameState;
    private final Map<ObjectType, Image> objectImages = new HashMap<>();
    private Image backgroundImage, playerImageFront, playerImageLeft, playerImageRight, currentImage, tanganImage, tanganStghImage, basketImage0, basketImage150, basketImage300;
    private Font customFont;
    private final Color FONT_OUTER_COLOR = new Color(0x7B, 0x40, 0x19);
    private final Color FONT_INNER_COLOR = new Color(0xFF, 0xEE, 0xA9);
    private JButton menuButton, resumeButton, quitInGameButton, backToMenuButton;

    public GamePanel(GameViewModel viewModel, GameView parentFrame) {
        this.viewModel = viewModel;
        this.parentFrame = parentFrame;
        this.currentGameState = GameState.PLAYING;
        setLayout(null);
        // --- PERUBAHAN: Set ukuran pilihan panel ---
        setPreferredSize(new Dimension(682, 512));

        loadAssets();
        createButtons();
        setFocusable(true);
        setupKeyBindings();
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (currentGameState == GameState.PLAYING) viewModel.castLasso(e.getX(), e.getY());
            }
        });
        new Thread(this).start();
    }

    private void loadAssets() {
        // --- PERUBAHAN: Memuat font Daydream.ttf ---
        try (InputStream is = getClass().getResourceAsStream("/assets/Daydream.ttf")) {
            customFont = (is != null) ? Font.createFont(Font.TRUETYPE_FONT, is) : new Font("Arial", Font.BOLD, 20);
        } catch (Exception e) {
            System.err.println("Gagal memuat font Daydream.ttf");
            customFont = new Font("Arial", Font.BOLD, 20);
        }
        backgroundImage = loadImage("/assets/background.gif");
        tanganImage = loadImage("/assets/tangan.png");
        tanganStghImage = loadImage("/assets/tanganstgh.png");
        playerImageFront = loadImage("/assets/userDepan.png");
        playerImageLeft = loadImage("/assets/userKiri.png");
        playerImageRight = loadImage("/assets/userKanan.png");
        currentImage = playerImageFront;
        basketImage0 = loadImage("/assets/kotak0.png");
        basketImage150 = loadImage("/assets/kotak150.png");
        basketImage300 = loadImage("/assets/kotak300.png");
        objectImages.put(ObjectType.AYAM, loadImage("/assets/ayam.png"));
        objectImages.put(ObjectType.BOM, loadImage("/assets/bom.png"));
        objectImages.put(ObjectType.COTTON, loadImage("/assets/cotton.png"));
        objectImages.put(ObjectType.SEMANGKA, loadImage("/assets/semangka.png"));
    }

    private Image loadImage(String path) {
        try { return new ImageIcon(Objects.requireNonNull(getClass().getResource(path))).getImage(); } catch (Exception e) { System.err.println("Gagal memuat aset: " + path); return null; }
    }

    private void createButtons() {
        menuButton = createImageButton("/assets/menu_button.png");
        menuButton.setBounds(682 - 125, 15, 120, 50);
        menuButton.addActionListener(e -> setGameState(GameState.PAUSED));
        add(menuButton);

        resumeButton = createImageButton("/assets/resume_button.png");
        resumeButton.setBounds(241, 280, 200, 50);
        resumeButton.addActionListener(e -> setGameState(GameState.PLAYING));
        add(resumeButton);

        quitInGameButton = createImageButton("/assets/quit_button.png");
        quitInGameButton.setBounds(241, 340, 200, 50);
        quitInGameButton.addActionListener(e -> parentFrame.showMenu(viewModel.getTotalScore()));
        add(quitInGameButton);

        backToMenuButton = createImageButton("/assets/back_button.png");
        backToMenuButton.setBounds(241, 340, 200, 50);
        backToMenuButton.addActionListener(e -> parentFrame.showMenu(viewModel.getTotalScore()));
        add(backToMenuButton);

        setComponentVisibility();
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
        } catch (Exception e) { System.err.println("Gagal memuat gambar tombol: " + imagePath); }
        return button;
    }

    private void setGameState(GameState state) { this.currentGameState = state; setComponentVisibility(); }

    private void setComponentVisibility() {
        menuButton.setVisible(currentGameState == GameState.PLAYING);
        resumeButton.setVisible(currentGameState == GameState.PAUSED);
        quitInGameButton.setVisible(currentGameState == GameState.PAUSED);
        backToMenuButton.setVisible(currentGameState == GameState.GAME_OVER);
    }

    @Override
    public void run() {
        while (true) {
            if (currentGameState == GameState.PLAYING) {
                viewModel.updateGame();
                if (viewModel.isGameOver()) setGameState(GameState.GAME_OVER);
            }
            repaint();
            try { Thread.sleep(16); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        drawGameWorld(g2d);
        if (currentGameState == GameState.PAUSED) drawOverlay(g2d, "Game Paused");
        else if (currentGameState == GameState.GAME_OVER) drawOverlay(g2d, "Game Over");
    }

    private void drawGameWorld(Graphics2D g2d) {
        if (backgroundImage != null) g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        Image currentBasketImage = basketImage0; int score = viewModel.getTotalScore();
        if (score >= 300) currentBasketImage = basketImage300; else if (score >= 150) currentBasketImage = basketImage150;
        if (currentBasketImage != null) g2d.drawImage(currentBasketImage, 501, 400, this);
        Lasso lasso = viewModel.getLasso();
        Image imageToDraw = currentImage;
        if (lasso.isActive()) imageToDraw = (lasso.getEndX() < viewModel.getPlayer().getX() + 32) ? playerImageLeft : playerImageRight;
        if (imageToDraw != null) g2d.drawImage(imageToDraw, viewModel.getPlayer().getX(), viewModel.getPlayer().getY(), this);
        if (lasso.isActive() && tanganStghImage != null) drawLasso(g2d, lasso, imageToDraw == playerImageLeft);
        for (SkillBall ball : viewModel.getSkillBalls()) { Image ballImage = objectImages.get(ball.getType()); if (ballImage != null) g2d.drawImage(ballImage, ball.getX(), ball.getY(), this); }
        drawUI(g2d);
    }

    // --- PERBAIKAN: Logika posisi keluar lasso ---
    private void drawLasso(Graphics2D g2d, Lasso lasso, boolean isFacingLeft) {
        // Tentukan titik awal lasso dari "bahu" player, bukan dari tengah
        int playerShoulderX = isFacingLeft ? viewModel.getPlayer().getX() : viewModel.getPlayer().getX() + 64; // Sisi kiri atau kanan player
        int playerShoulderY = viewModel.getPlayer().getY() + 32; // Tengah-tengah player secara vertikal

        int targetX, targetY;
        if (lasso.getTarget() != null) { targetX = lasso.getTarget().getX() + 20; targetY = lasso.getTarget().getY() + 20; }
        else { targetX = lasso.getEndX(); targetY = lasso.getEndY(); }
        double dx = targetX - playerShoulderX; double dy = targetY - playerShoulderY; double angle = Math.atan2(dy, dx);
        double distance = Math.sqrt(dx * dx + dy * dy); int segmentLength = tanganStghImage.getWidth(this); if (segmentLength <= 0) segmentLength = 1;
        AffineTransform oldTransform = g2d.getTransform();
        g2d.translate(playerShoulderX, playerShoulderY); g2d.rotate(angle);
        for (int i = 0; i < distance - segmentLength; i += segmentLength * 0.9) g2d.drawImage(tanganStghImage, i, -tanganStghImage.getHeight(this) / 2, this);
        if (tanganImage != null) g2d.drawImage(tanganImage, (int) distance - tanganImage.getWidth(this), -tanganImage.getHeight(this) / 2, this);
        g2d.setTransform(oldTransform);
    }

    private void drawUI(Graphics2D g2d) {
        drawTextWithOutline(g2d, String.format("Time: %02d:%02d", viewModel.getRemainingTime() / 60, viewModel.getRemainingTime() % 60), 15, 40, 24f);
        drawTextWithOutline(g2d, "Score: " + viewModel.getTotalScore(), 15, 70, 24f);
        Font popupFont = customFont.deriveFont(20f);
        for (ScorePopup popup : viewModel.getScorePopups()) {
            g2d.setFont(popupFont);
            g2d.setColor(popup.getColor());
            g2d.drawString(popup.getText(), popup.getX(), popup.getY());
        }
    }

    private void drawOverlay(Graphics2D g2d, String text) {
        g2d.setColor(new Color(0, 0, 0, 150)); g2d.fillRect(0, 0, getWidth(), getHeight());
        Font bigFont = customFont.deriveFont(50f); FontMetrics fmBig = g2d.getFontMetrics(bigFont);
        int msgWidth = fmBig.stringWidth(text);
        drawTextWithOutline(g2d, text, (getWidth() - msgWidth) / 2, getHeight() / 2 - 100, 50f);
        if (text.equals("Game Over")) {
            Font smallFont = customFont.deriveFont(20f); FontMetrics fmSmall = g2d.getFontMetrics(smallFont);
            int spaceMsgWidth = fmSmall.stringWidth("Press SPACE to return to Menu");
            drawTextWithOutline(g2d, "Press SPACE to return to Menu", (getWidth() - spaceMsgWidth) / 2, getHeight() / 2 + 50, 20f);
        }
    }

    private void drawTextWithOutline(Graphics2D g2d, String text, int x, int y, float size) {
        Font font = customFont.deriveFont(size); g2d.setFont(font); g2d.setColor(FONT_OUTER_COLOR); int offset = 2;
        g2d.drawString(text, x - offset, y); g2d.drawString(text, x + offset, y); g2d.drawString(text, x, y - offset); g2d.drawString(text, x, y + offset);
        g2d.setColor(FONT_INNER_COLOR); g2d.drawString(text, x, y);
    }

    private void setupKeyBindings() {
        InputMap inputMap = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW); ActionMap actionMap = getActionMap(); int moveDistance = 10;
        actionMap.put("moveUp", new AbstractAction() { @Override public void actionPerformed(ActionEvent e) { if(currentGameState == GameState.PLAYING) { currentImage = playerImageFront; viewModel.movePlayer(0, -moveDistance); } }});
        actionMap.put("moveDown", new AbstractAction() { @Override public void actionPerformed(ActionEvent e) { if(currentGameState == GameState.PLAYING) { currentImage = playerImageFront; viewModel.movePlayer(0, moveDistance); } }});
        actionMap.put("moveLeft", new AbstractAction() { @Override public void actionPerformed(ActionEvent e) { if(currentGameState == GameState.PLAYING) { currentImage = playerImageLeft; viewModel.movePlayer(-moveDistance, 0); } }});
        actionMap.put("moveRight", new AbstractAction() { @Override public void actionPerformed(ActionEvent e) { if(currentGameState == GameState.PLAYING) { currentImage = playerImageRight; viewModel.movePlayer(moveDistance, 0); } }});
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_W, 0), "moveUp"); inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "moveUp");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, 0), "moveDown"); inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "moveDown");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, 0), "moveLeft"); inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), "moveLeft");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_D, 0), "moveRight"); inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), "moveRight");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), "returnToMenu"); getActionMap().put("returnToMenu", new AbstractAction() { @Override public void actionPerformed(ActionEvent e) { if (currentGameState == GameState.GAME_OVER) parentFrame.showMenu(viewModel.getTotalScore()); } });
    }
}