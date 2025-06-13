package view;

import model.ObjectType;
import model.SkillBall;
import viewmodel.GameViewModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;

public class GamePanel extends JPanel implements PropertyChangeListener, Runnable {

    private GameViewModel viewModel;
    private Image backgroundImage;
    private Thread gameThread;
    private Map<ObjectType, Image> objectImages;

    // --- PERUBAHAN: Variabel untuk semua gambar player ---
    private Image playerImageFront;
    private Image playerImageLeft;
    private Image playerImageRight;
    private Image currentImage; // Gambar player yang aktif

    public GamePanel(GameViewModel viewModel) {
        this.viewModel = viewModel;
        this.viewModel.addPropertyChangeListener(this);

        loadAllImages();

        setFocusable(true);
        setupKeyBindings();

        gameThread = new Thread(this);
        gameThread.start();
    }

    private void loadAllImages() {
        objectImages = new HashMap<>();
        try {
            backgroundImage = new ImageIcon(getClass().getResource("/assets/background.gif")).getImage();

            // --- PERUBAHAN: Muat semua gambar player ---
            playerImageFront = loadImage("/assets/userDepan.png");
            playerImageLeft = loadImage("/assets/userKiri.png");
            playerImageRight = loadImage("/assets/userKanan.png");
            currentImage = playerImageFront; // Set gambar awal

            objectImages.put(ObjectType.AYAM, loadImage("/assets/ayam.png"));
            objectImages.put(ObjectType.BOM, loadImage("/assets/bom.png"));
            objectImages.put(ObjectType.COTTON, loadImage("/assets/cotton.png"));
            objectImages.put(ObjectType.SEMANGKA, loadImage("/assets/semangka.png"));

        } catch (Exception e) {
            System.err.println("Gagal memuat gambar: " + e.getMessage());
        }
    }

    private Image loadImage(String path) {
        try {
            return new ImageIcon(getClass().getResource(path)).getImage();
        } catch (Exception e) {
            System.err.println("Gagal memuat aset gambar di path: " + path);
            return null;
        }
    }

    @Override
    public void run() {
        while (true) {
            if (!viewModel.isGameOver()) {
                viewModel.updateGame();
            }
            repaint();
            try {
                Thread.sleep(16);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        if (backgroundImage != null) {
            g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }

        // Gambar player dengan gambar yang aktif saat ini
        if (currentImage != null) {
            g2d.drawImage(currentImage, viewModel.getPlayerX(), viewModel.getPlayerY(), this);
        }

        for (SkillBall ball : viewModel.getSkillBalls()) {
            Image ballImage = objectImages.get(ball.getType());
            if (ballImage != null) {
                g2d.drawImage(ballImage, ball.getX(), ball.getY(), this);
            } else {
                g2d.setColor(Color.MAGENTA);
                g2d.fillRect(ball.getX(), ball.getY(), 40, 40);
            }

            // --- PERUBAHAN: Jangan tampilkan skor untuk BOM ---
            if (ball.getType() != ObjectType.BOM) {
                g2d.setColor(Color.WHITE);
                g2d.drawString(String.valueOf(ball.getScore()), ball.getX() + 10, ball.getY() + 25);
            }
        }

        g2d.setFont(new Font("Arial", Font.BOLD, 20));
        g2d.setColor(Color.YELLOW);
        long remainingTime = viewModel.getRemainingTime();
        long minutes = remainingTime / 60;
        long seconds = remainingTime % 60;
        g2d.drawString(String.format("Time: %02d:%02d", minutes, seconds), getWidth() - 120, 30);

        if (viewModel.isGameOver()) {
            g2d.setColor(new Color(0, 0, 0, 150));
            g2d.fillRect(0, 0, getWidth(), getHeight());
            g2d.setColor(Color.RED);
            g2d.setFont(new Font("Arial", Font.BOLD, 50));
            FontMetrics fm = g2d.getFontMetrics();
            int msgWidth = fm.stringWidth("GAME OVER");
            g2d.drawString("GAME OVER", (getWidth() - msgWidth) / 2, getHeight() / 2);
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) { }

    private void setupKeyBindings() {
        InputMap inputMap = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = getActionMap();
        int moveDistance = 10;

        // --- PERUBAHAN: Ganti gambar player saat tombol ditekan ---
        actionMap.put("moveUp", new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) {
                currentImage = playerImageFront;
                viewModel.movePlayer(0, -moveDistance);
            }
        });
        actionMap.put("moveDown", new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) {
                currentImage = playerImageFront;
                viewModel.movePlayer(0, moveDistance);
            }
        });
        actionMap.put("moveLeft", new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) {
                currentImage = playerImageLeft;
                viewModel.movePlayer(-moveDistance, 0);
            }
        });
        actionMap.put("moveRight", new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) {
                currentImage = playerImageRight;
                viewModel.movePlayer(moveDistance, 0);
            }
        });

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_W, 0), "moveUp");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "moveUp");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, 0), "moveDown");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "moveDown");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, 0), "moveLeft");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), "moveLeft");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_D, 0), "moveRight");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), "moveRight");
    }
}