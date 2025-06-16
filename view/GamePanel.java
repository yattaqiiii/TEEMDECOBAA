package view;

import model.*;
import viewmodel.GameViewModel;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
        // Mengirim total skor dan total bola yang ditangkap ke showMenu
        quitInGameButton.addActionListener(e -> parentFrame.showMenu(viewModel.getTotalScore(), viewModel.getTotalBallsCaught()));
        add(quitInGameButton);

        backToMenuButton = createImageButton("/assets/back_button.png");
        backToMenuButton.setBounds(241, 340, 200, 50);
        // Mengirim total skor dan total bola yang ditangkap ke showMenu
        backToMenuButton.addActionListener(e -> parentFrame.showMenu(viewModel.getTotalScore(), viewModel.getTotalBallsCaught()));
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
        else if (currentGameState == GameState.PLAYING) {
            // Tidak ada overlay khusus untuk PLAYING, hanya gambar game world
        }
        else if (currentGameState == GameState.GAME_OVER) drawOverlay(g2d, "Game Over");
    }

    private void drawGameWorld(Graphics2D g2d) {
        // gambar background
        if (backgroundImage != null) g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);

        // tentukan gambar keranjang berdasarkan skor pemain
        Image currentBasketImage = basketImage0;
        int score = viewModel.getTotalScore();
        if (score >= 300) currentBasketImage = basketImage300;
        else if (score >= 150) currentBasketImage = basketImage150;
        if (currentBasketImage != null) g2d.drawImage(currentBasketImage, 501, 400, this); // gambar keranjang pada posisi tetap

        // dapatkan data lasso dan gambar pemain
        Lasso lasso = viewModel.getLasso();
        Image imageToDraw = currentImage; // gambar pemain default (menghadap depan)
        // jika lasso aktif, ubah gambar pemain menghadap kiri/kanan sesuai arah lasso
        if (lasso.isActive()) imageToDraw = (lasso.getEndX() < viewModel.getPlayer().getX() + 32) ? playerImageLeft : playerImageRight;
        if (imageToDraw != null) g2d.drawImage(imageToDraw, viewModel.getPlayer().getX(), viewModel.getPlayer().getY(), this); // gambar pemain

        // jika lasso aktif dan gambar tangan tersedia, gambar lasso
        if (lasso.isActive() && tanganStghImage != null) drawLasso(g2d, lasso, imageToDraw == playerImageLeft);

        // gambar semua bola skill (skillball) yang ada di permainan
        for (SkillBall ball : viewModel.getSkillBalls()) {
            Image ballImage = objectImages.get(ball.getType()); // dapatkan gambar sesuai tipe bola
            if (ballImage != null) g2d.drawImage(ballImage, ball.getX(), ball.getY(), this);
        }
        // gambar elemen ui (skor, waktu)
        drawUI(g2d);
    }

    private void drawLasso(Graphics2D g2d, Lasso lasso, boolean isFacingLeft) {
        // Tentukan titik awal lasso dari "bahu" player, bukan dari tengah
        int playerShoulderX = isFacingLeft ? viewModel.getPlayer().getX() : viewModel.getPlayer().getX() + 64; // Sisi kiri atau kanan player berdasarkan arah hadap
        int playerShoulderY = viewModel.getPlayer().getY() + 32; // Tengah-tengah player secara vertikal (posisi bahu y)

        int targetX, targetY;
        // jika ada target (bola tertangkap), arahkan lasso ke tengah bola
        if (lasso.getTarget() != null) {
            targetX = lasso.getTarget().getX() + 20; // 20 adalah setengah perkiraan lebar/tinggi objek bola
            targetY = lasso.getTarget().getY() + 20; // 20 adalah setengah perkiraan lebar/tinggi objek bola
        }
        // jika tidak ada target (lasso dilempar ke titik kosong), arahkan lasso ke posisi klik mouse
        else {
            targetX = lasso.getEndX();
            targetY = lasso.getEndY();
        }
        // hitung sudut dan jarak antara bahu pemain dan target lasso
        double dx = targetX - playerShoulderX; // perbedaan x
        double dy = targetY - playerShoulderY; // perbedaan y
        double angle = Math.atan2(dy, dx); // sudut dalam radian, dari sumbu x positif ke vektor (dx, dy)
        double distance = Math.sqrt(dx * dx + dy * dy); // jarak euclidean antara bahu dan target
        int segmentLength = tanganStghImage.getWidth(this); // panjang satu segmen gambar tangan (bagian tengah lasso)
        if (segmentLength <= 0) segmentLength = 1; // hindari pembagian dengan nol atau nilai negatif jika gambar gagal dimuat

        AffineTransform oldTransform = g2d.getTransform(); // simpan transformasi grafis (rotasi, translasi) saat ini
        g2d.translate(playerShoulderX, playerShoulderY); // pindahkan titik origin (0,0) sistem koordinat grafis ke bahu pemain
        g2d.rotate(angle); // rotasi sistem koordinat grafis sesuai sudut ke target

        // gambar segmen-segmen tangan (tanganStghImage) sepanjang jarak ke target
        // loop ini menggambar bagian tengah lasso yang terdiri dari banyak gambar 'tanganStghImage'
        for (int i = 0; i < distance - segmentLength; i += segmentLength * 0.9) { // 0.9 untuk sedikit tumpang tindih antar segmen agar terlihat menyambung
            g2d.drawImage(tanganStghImage, i, -tanganStghImage.getHeight(this) / 2, this); // gambar segmen pada posisi i (sepanjang sumbu x baru setelah rotasi), y disesuaikan agar tengah gambar tangan sejajar
        }
        // gambar ujung tangan (tanganImage) di akhir lasso (dekat target)
        if (tanganImage != null) {
            g2d.drawImage(tanganImage, (int) distance - tanganImage.getWidth(this), -tanganImage.getHeight(this) / 2, this);
        }
        g2d.setTransform(oldTransform); // kembalikan transformasi grafis ke kondisi semula sebelum menggambar lasso
    }

    private void drawUI(Graphics2D g2d) {
        g2d.setFont(customFont.deriveFont(20f));
        FontMetrics fm = g2d.getFontMetrics();

        String scoreText = "Score: " + viewModel.getTotalScore();
        String timeText = "Time: " + viewModel.getRemainingTime() + "s";
        String ballsCaughtText = "Ball: " + viewModel.getTotalBallsCaught(); // Teks untuk jumlah bola

        // Gambar teks skor
        drawTextWithOutline(g2d, scoreText, 20, 40, fm);
        // Gambar teks waktu
        drawTextWithOutline(g2d, timeText, 20, 70, fm);
        // Gambar teks jumlah bola yang ditangkap
        drawTextWithOutline(g2d, ballsCaughtText, 20, 100, fm);
    }

    private void drawTextWithOutline(Graphics2D g2d, String text, int x, int y, FontMetrics fm) {
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getAscent();

        // Gambar bayangan teks (outline) dengan offset
        int offset = 2;
        g2d.setColor(FONT_OUTER_COLOR);
        g2d.drawString(text, x - offset, y - offset);
        g2d.drawString(text, x + offset, y - offset);
        g2d.drawString(text, x - offset, y + offset);
        g2d.drawString(text, x + offset, y + offset);

        // Gambar teks utama di atasnya
        g2d.setColor(FONT_INNER_COLOR);
        g2d.drawString(text, x, y);
    }

    private void drawOverlay(Graphics2D g2d, String message) {
        g2d.setColor(new Color(0, 0, 0, 170)); // Sedikit lebih gelap untuk kontras
        g2d.fillRect(0, 0, getWidth(), getHeight());

        Font overlayFont = customFont.deriveFont(40f); // Perbesar font menjadi 60f
        FontMetrics fmOverlay = g2d.getFontMetrics(overlayFont);
        int msgWidth = fmOverlay.stringWidth(message);
        int msgAscent = fmOverlay.getAscent();
        int msgDescent = fmOverlay.getDescent(); // Untuk perhitungan tinggi total yang lebih akurat

        // Hitung posisi Y agar teks benar-benar di tengah vertikal
        int yPosition = (getHeight() - (msgAscent + msgDescent)) / 2 + msgAscent;

        // Gunakan FontMetrics dari overlayFont untuk drawTextWithOutline
        // Simpan dan set font baru untuk drawTextWithOutline
        Font originalFont = g2d.getFont();
        g2d.setFont(overlayFont);
        drawTextWithOutline(g2d, message, (getWidth() - msgWidth) / 2, yPosition, fmOverlay);
        g2d.setFont(originalFont); // Kembalikan font asli

        if (message.equals("Game Over")) {
            Font smallFont = customFont.deriveFont(20f);
            FontMetrics fmSmall = g2d.getFontMetrics(smallFont);
            String spaceMsg = "Press SPACE to return to Menu";
            int spaceMsgWidth = fmSmall.stringWidth(spaceMsg);
            // Posisikan pesan "Press SPACE" sedikit di bawah pesan "Game Over"
            drawTextWithOutline(g2d, spaceMsg, (getWidth() - spaceMsgWidth) / 2, yPosition + fmOverlay.getDescent() + fmSmall.getAscent() + 10, fmSmall);
        }
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
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), "returnToMenu"); getActionMap().put("returnToMenu", new AbstractAction() { @Override public void actionPerformed(ActionEvent e) { if (currentGameState == GameState.GAME_OVER) parentFrame.showMenu(viewModel.getTotalScore(), viewModel.getTotalBallsCaught()); } });
    }
}