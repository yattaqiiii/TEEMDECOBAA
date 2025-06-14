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

public class GamePanel extends JPanel implements Runnable {

    private GameViewModel viewModel;
    private GameView parentFrame;
    private Thread gameThread;
    private Map<ObjectType, Image> objectImages;

    // Aset Visual
    private Image backgroundImage;
    private Image playerImageFront, playerImageLeft, playerImageRight, currentImage;
    private Image tanganImage, tanganStghImage;
    private Image basketImage0, basketImage150, basketImage300;
    private Font arcadeFont;
    private final Color FONT_OUTER_COLOR = new Color(0x7B, 0x40, 0x19);
    private final Color FONT_INNER_COLOR = new Color(0xFF, 0xEE, 0xA9);

    public GamePanel(GameViewModel viewModel, GameView parentFrame) {
        this.viewModel = viewModel;
        this.parentFrame = parentFrame;
        loadAssets();
        setFocusable(true);
        setupKeyBindings();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                viewModel.castLasso(e.getX(), e.getY());
            }
        });

        gameThread = new Thread(this);
        gameThread.start();
    }

    private void loadAssets() {
        // --- PERBAIKAN: Logika pemuatan font dibuat lebih aman ---
        try (InputStream is = getClass().getResourceAsStream("/assets/ArcadeClassic.ttf")) {
            if (is != null) {
                // Jika file font ditemukan, gunakan
                arcadeFont = Font.createFont(Font.TRUETYPE_FONT, is);
            } else {
                // Jika tidak ditemukan, beri pesan error dan gunakan font cadangan
                System.err.println("Font ArcadeClassic.ttf tidak ditemukan! Menggunakan font Arial.");
                arcadeFont = new Font("Arial", Font.BOLD, 20);
            }
        } catch (Exception e) {
            System.err.println("Gagal memuat font: " + e.getMessage());
            arcadeFont = new Font("Arial", Font.BOLD, 20); // Gunakan font cadangan jika ada error
        }

        objectImages = new HashMap<>();
        // ... (sisa kode pemuatan gambar tidak berubah) ...
        try { backgroundImage = loadImage("/assets/background.gif"); tanganImage = loadImage("/assets/tangan.png"); tanganStghImage = loadImage("/assets/tanganstgh.png"); playerImageFront = loadImage("/assets/userDepan.png"); playerImageLeft = loadImage("/assets/userKiri.png"); playerImageRight = loadImage("/assets/userKanan.png"); currentImage = playerImageFront; basketImage0 = loadImage("/assets/kotak0.png"); basketImage150 = loadImage("/assets/kotak150.png"); basketImage300 = loadImage("/assets/kotak300.png"); objectImages.put(ObjectType.AYAM, loadImage("/assets/ayam.png")); objectImages.put(ObjectType.BOM, loadImage("/assets/bom.png")); objectImages.put(ObjectType.COTTON, loadImage("/assets/cotton.png")); objectImages.put(ObjectType.SEMANGKA, loadImage("/assets/semangka.png")); } catch (Exception e) { System.err.println("Gagal memuat salah satu gambar utama: " + e.getMessage()); }
    }

    private Image loadImage(String path) { try { return new ImageIcon(getClass().getResource(path)).getImage(); } catch (Exception e) { System.err.println("Gagal memuat aset gambar di path: " + path); return null; } }

    @Override
    public void run() { while (true) { if (!viewModel.isGameOver()) viewModel.updateGame(); repaint(); try { Thread.sleep(16); } catch (InterruptedException e) { e.printStackTrace(); } } }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (backgroundImage != null) g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);

        int score = viewModel.getTotalScore();
        Image currentBasketImage = basketImage0;
        if (score >= 300) currentBasketImage = basketImage300; else if (score >= 150) currentBasketImage = basketImage150;
        if (currentBasketImage != null) g2d.drawImage(currentBasketImage, 501, 400, this);

        Lasso lasso = viewModel.getLasso();
        Image imageToDraw = currentImage;
        if (lasso.isActive()) {
            if (lasso.getEndX() < viewModel.getPlayerX()) imageToDraw = playerImageLeft;
            else imageToDraw = playerImageRight;
        }
        if (imageToDraw != null) g2d.drawImage(imageToDraw, viewModel.getPlayerX(), viewModel.getPlayerY(), this);

        if (lasso.isActive() && tanganStghImage != null) {
            int playerCenterX = viewModel.getPlayerX() + 32; int playerCenterY = viewModel.getPlayerY() + 32;
            int targetX, targetY;
            if (lasso.getTarget() != null) { targetX = lasso.getTarget().getX() + 20; targetY = lasso.getTarget().getY() + 20; }
            else { targetX = lasso.getEndX(); targetY = lasso.getEndY(); }
            double dx = targetX - playerCenterX; double dy = targetY - playerCenterY; double angle = Math.atan2(dy, dx);
            double distance = Math.sqrt(dx * dx + dy * dy); int segmentLength = tanganStghImage.getWidth(this); if(segmentLength <= 0) segmentLength = 1;
            AffineTransform oldTransform = g2d.getTransform(); g2d.translate(playerCenterX, playerCenterY); g2d.rotate(angle);
            for (int i = 0; i < distance; i += segmentLength) g2d.drawImage(tanganStghImage, i, -tanganStghImage.getHeight(this) / 2, this);
            if (tanganImage != null) g2d.drawImage(tanganImage, (int)distance - tanganImage.getWidth(this)/2, -tanganImage.getHeight(this)/2, this);
            g2d.setTransform(oldTransform);
        }

        for (SkillBall ball : viewModel.getSkillBalls()) { Image ballImage = objectImages.get(ball.getType()); if (ballImage != null) g2d.drawImage(ballImage, ball.getX(), ball.getY(), this); }

        // Tampilan Skor, Timer, dan Animasi Skor
        drawUI(g2d);

        if (viewModel.isGameOver()) { /* ... (logika game over) ... */ g2d.setColor(new Color(0, 0, 0, 150)); g2d.fillRect(0, 0, getWidth(), getHeight()); g2d.setColor(Color.RED); g2d.setFont(arcadeFont.deriveFont(50f)); FontMetrics fm = g2d.getFontMetrics(); int msgWidth = fm.stringWidth("GAME OVER"); g2d.drawString("GAME OVER", (getWidth() - msgWidth) / 2, getHeight() / 2); g2d.setFont(arcadeFont.deriveFont(20f)); int spaceMsgWidth = fm.stringWidth("Press SPACE to return to Menu"); g2d.drawString("Press SPACE to return to Menu", (getWidth() - spaceMsgWidth) / 2 + 50, getHeight() / 2 + 40); }
    }

    private void drawUI(Graphics2D g2d) {
        long remainingTime = viewModel.getRemainingTime();
        String timeStr = String.format("Time: %02d:%02d", remainingTime / 60, remainingTime % 60);
        String scoreStr = "Score: " + viewModel.getTotalScore();

        drawTextWithOutline(g2d, timeStr, 15, 40, 24f);
        drawTextWithOutline(g2d, scoreStr, 15, 70, 24f);

        for (ScorePopup popup : viewModel.getScorePopups()) {
            g2d.setFont(arcadeFont.deriveFont(20f));
            g2d.setColor(popup.getColor());
            g2d.drawString(popup.getText(), popup.getX(), popup.getY());
        }
    }

    private void drawTextWithOutline(Graphics2D g2d, String text, int x, int y, float size) {
        g2d.setFont(arcadeFont.deriveFont(size));
        g2d.setColor(FONT_OUTER_COLOR);
        g2d.drawString(text, x - 2, y - 2); g2d.drawString(text, x + 2, y - 2);
        g2d.drawString(text, x - 2, y + 2); g2d.drawString(text, x + 2, y + 2);
        g2d.setColor(FONT_INNER_COLOR);
        g2d.drawString(text, x, y);
    }

    private void setupKeyBindings() {
        InputMap inputMap = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = getActionMap();
        int moveDistance = 10;

        actionMap.put("moveUp", new AbstractAction() { @Override public void actionPerformed(ActionEvent e) { currentImage = playerImageFront; viewModel.movePlayer(0, -moveDistance); }});
        actionMap.put("moveDown", new AbstractAction() { @Override public void actionPerformed(ActionEvent e) { currentImage = playerImageFront; viewModel.movePlayer(0, moveDistance); }});
        actionMap.put("moveLeft", new AbstractAction() { @Override public void actionPerformed(ActionEvent e) { currentImage = playerImageLeft; viewModel.movePlayer(-moveDistance, 0); }});
        actionMap.put("moveRight", new AbstractAction() { @Override public void actionPerformed(ActionEvent e) { currentImage = playerImageRight; viewModel.movePlayer(moveDistance, 0); }});

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_W, 0), "moveUp"); inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "moveUp");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, 0), "moveDown"); inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "moveDown");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, 0), "moveLeft"); inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), "moveLeft");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_D, 0), "moveRight"); inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), "moveRight");

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), "returnToMenu");
        getActionMap().put("returnToMenu", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (viewModel.isGameOver()) {
                    parentFrame.showMenu(viewModel.getTotalScore());
                }
            }
        });
    }
}