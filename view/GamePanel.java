package view;

import model.Lasso;
import model.ObjectType;
import model.SkillBall;
import viewmodel.GameViewModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.KeyEvent;
import java.awt.event.ActionEvent;
import java.awt.geom.AffineTransform;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class GamePanel extends JPanel implements Runnable {

    private GameViewModel viewModel;
    private Thread gameThread;
    private Map<ObjectType, Image> objectImages;

    // --- Aset Visual ---
    private Image backgroundImage;
    private Image playerImageFront, playerImageLeft, playerImageRight, currentImage;
    private Image lassoImage;
    private Image basketImage0, basketImage150, basketImage300;
    private Font arcadeFont;
    private final Color FONT_OUTER_COLOR = new Color(0x7B, 0x40, 0x19);
    private final Color FONT_INNER_COLOR = new Color(0xFF, 0xEE, 0xA9);

    public GamePanel(GameViewModel viewModel) {
        this.viewModel = viewModel;
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
        objectImages = new HashMap<>();
        try {
            // Muat Font Kustom
            InputStream is = getClass().getResourceAsStream("/assets/ArcadeClassic.ttf");
            if (is != null) {
                arcadeFont = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(24f);
            } else {
                arcadeFont = new Font("Arial", Font.BOLD, 20); // Fallback font
                System.err.println("Font ArcadeClassic.ttf tidak ditemukan!");
            }

            // Muat Gambar
            backgroundImage = loadImage("/assets/background.gif");
            lassoImage = loadImage("/assets/tangan.png");
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

        } catch (Exception e) {
            System.err.println("Gagal memuat aset: " + e.getMessage());
        }
    }

    private Image loadImage(String path) { try { return new ImageIcon(getClass().getResource(path)).getImage(); } catch (Exception e) { System.err.println("Gagal memuat aset gambar di path: " + path); return null; } }

    @Override
    public void run() { while (true) { if (!viewModel.isGameOver()) viewModel.updateGame(); repaint(); try { Thread.sleep(16); } catch (InterruptedException e) { e.printStackTrace(); } } }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        if (backgroundImage != null) g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);

        int score = viewModel.getTotalScore();
        Image currentBasketImage = basketImage0;
        if (score >= 300) currentBasketImage = basketImage300;
        else if (score >= 150) currentBasketImage = basketImage150;
        if (currentBasketImage != null) g2d.drawImage(currentBasketImage, 381, 400, this);

        // --- PERUBAHAN: Logika gambar player ---
        Lasso lasso = viewModel.getLasso();
        Image imageToDraw = currentImage;
        if (lasso.isActive() && lasso.getTarget() != null) {
            imageToDraw = playerImageRight; // Gunakan gambar kanan saat menangkap
        }
        if (imageToDraw != null) g2d.drawImage(imageToDraw, viewModel.getPlayerX(), viewModel.getPlayerY(), this);

        if (lasso.isActive() && lassoImage != null) {
            // ... logika gambar lasso tetap sama ...
            int playerCenterX = viewModel.getPlayerX() + 32; int playerCenterY = viewModel.getPlayerY() + 32; int targetX = lasso.getEndX(); int targetY = lasso.getEndY(); double dx = targetX - playerCenterX; double dy = targetY - playerCenterY; double angle = Math.atan2(dy, dx); double distance = Math.sqrt(dx * dx + dy * dy); int segmentLength = lassoImage.getWidth(this); if(segmentLength <= 0) segmentLength = 1; AffineTransform oldTransform = g2d.getTransform(); g2d.translate(playerCenterX, playerCenterY); g2d.rotate(angle); for (int i = 0; i < distance; i += segmentLength) { g2d.drawImage(lassoImage, i, -lassoImage.getHeight(this) / 2, this); } g2d.setTransform(oldTransform);
        }

        for (SkillBall ball : viewModel.getSkillBalls()) {
            Image ballImage = objectImages.get(ball.getType());
            if (ballImage != null) g2d.drawImage(ballImage, ball.getX(), ball.getY(), this);
        }

        // --- PERUBAHAN: Tampilan Skor dan Timer ---
        g2d.setFont(arcadeFont);
        long remainingTime = viewModel.getRemainingTime();
        String timeStr = String.format("Time= %02d.%02d", remainingTime / 60, remainingTime % 60);
        String scoreStr = "Score= " + viewModel.getTotalScore();

        // Gambar Teks dengan Outline
        drawTextWithOutline(g2d, timeStr, 15, 40);
        drawTextWithOutline(g2d, scoreStr, 15, 70);

        if (viewModel.isGameOver()) { /* ... logika game over tetap sama ... */ g2d.setColor(new Color(0, 0, 0, 150)); g2d.fillRect(0, 0, getWidth(), getHeight()); g2d.setColor(Color.RED); g2d.setFont(new Font("Arial", Font.BOLD, 50)); FontMetrics fm = g2d.getFontMetrics(); int msgWidth = fm.stringWidth("GAME OVER"); g2d.drawString("GAME OVER", (getWidth() - msgWidth) / 2, getHeight() / 2); }
    }

    private void drawTextWithOutline(Graphics2D g2d, String text, int x, int y) {
        // Gambar outline (warna luar)
        g2d.setColor(FONT_OUTER_COLOR);
        g2d.drawString(text, x - 1, y - 1);
        g2d.drawString(text, x - 1, y + 1);
        g2d.drawString(text, x + 1, y - 1);
        g2d.drawString(text, x + 1, y + 1);
        // Gambar fill (warna dalam)
        g2d.setColor(FONT_INNER_COLOR);
        g2d.drawString(text, x, y);
    }

    // --- PERUBAHAN: Logika key-binding untuk ganti sprite ---
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
    }
}