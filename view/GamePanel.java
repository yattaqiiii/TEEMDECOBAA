package view;

import viewmodel.GameViewModel;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.io.File;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;

public class GamePanel extends JPanel implements PropertyChangeListener {

    private GameViewModel viewModel;
    private Map<String, Image> playerImages;

    public GamePanel(GameViewModel viewModel) {
        this.viewModel = viewModel;
        this.viewModel.addPropertyChangeListener(this);

        // Map untuk menyimpan gambar-gambar karakter
        playerImages = new HashMap<>();

        // Muat gambar karakter untuk berbagai arah
        loadPlayerImages();

        setFocusable(true);
        setupKeyBindings();
        setBackground(Color.LIGHT_GRAY);
    }

    private void loadPlayerImages() {
        try {
            System.out.println("Working Directory: " + System.getProperty("user.dir"));

            // Try with different resource paths
            java.net.URL frontUrl = getClass().getResource("/assets/userDepan.png");
            System.out.println("Front URL with /assets/: " + frontUrl);

            if (frontUrl == null) {
                // Try without leading slash
                frontUrl = getClass().getResource("assets/userDepan.png");
                System.out.println("Front URL with assets/ (no slash): " + frontUrl);
            }

            // Same for other images
            java.net.URL leftUrl = getClass().getResource("/assets/userKiri.png");
            java.net.URL rightUrl = getClass().getResource("/assets/userKanan.png");

            if (frontUrl != null) {
                playerImages.put("front", new ImageIcon(frontUrl).getImage());
                System.out.println("Front image loaded successfully");
            }
            if (leftUrl != null) {
                playerImages.put("left", new ImageIcon(leftUrl).getImage());
                System.out.println("Left image loaded successfully");
            }
            if (rightUrl != null) {
                playerImages.put("right", new ImageIcon(rightUrl).getImage());
                System.out.println("Right image loaded successfully");
            }

            if (playerImages.isEmpty()) {
                System.err.println("No images loaded! Trying file system approach...");
                loadImagesFromFileSystem();
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error loading images: " + e.getMessage());
            loadImagesFromFileSystem();
        }
    }

    private void loadImagesFromFileSystem() {
        try {
            // Try multiple potential paths
            String[] paths = {
                    "src/main/resources/assets/",
                    "src/assets/",
                    "assets/",
                    "resources/assets/"
            };

            for (String path : paths) {
                File dir = new File(path);
                System.out.println("Checking directory: " + dir.getAbsolutePath() + " exists: " + dir.exists());

                if (dir.exists()) {
                    File frontFile = new File(path + "userDepan.png");
                    File leftFile = new File(path + "userKiri.png");
                    File rightFile = new File(path + "userKanan.png");

                    System.out.println("Front file exists: " + frontFile.exists());

                    if (frontFile.exists()) {
                        playerImages.put("front", new ImageIcon(frontFile.getAbsolutePath()).getImage());
                        playerImages.put("left", new ImageIcon(leftFile.getAbsolutePath()).getImage());
                        playerImages.put("right", new ImageIcon(rightFile.getAbsolutePath()).getImage());
                        System.out.println("Images loaded from: " + path);
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupKeyBindings() {
        InputMap inputMap = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = getActionMap();

        int moveDistance = 10;

        // Atas - menggunakan gambar depan
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_W, 0), "moveUp");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "moveUp");
        actionMap.put("moveUp", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewModel.movePlayer(0, -moveDistance, "front");
            }
        });

        // Bawah - menggunakan gambar depan
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, 0), "moveDown");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "moveDown");
        actionMap.put("moveDown", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewModel.movePlayer(0, moveDistance, "front");
            }
        });

        // Kiri - menggunakan gambar kiri
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, 0), "moveLeft");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), "moveLeft");
        actionMap.put("moveLeft", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewModel.movePlayer(-moveDistance, 0, "left");
            }
        });

        // Kanan - menggunakan gambar kanan
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_D, 0), "moveRight");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), "moveRight");
        actionMap.put("moveRight", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewModel.movePlayer(moveDistance, 0, "right");
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Ambil arah player saat ini dan tampilkan gambar yang sesuai
        String direction = viewModel.getPlayerDirection();
        Image currentImage = playerImages.get(direction);

        if (currentImage != null) {
            g.drawImage(currentImage, viewModel.getPlayerX(), viewModel.getPlayerY(), this);
            int imgWidth = currentImage.getWidth(this);
            int imgHeight = currentImage.getHeight(this);
            System.out.println("Drawing image: " + direction +
                    " | Width: " + imgWidth +
                    " | Height: " + imgHeight +
                    " | at X: " + viewModel.getPlayerX() +
                    " | Y: " + viewModel.getPlayerY());
        } else {
            // Gambar placeholder jika gambar tidak ditemukan
            g.setColor(Color.RED);
            g.fillRect(viewModel.getPlayerX(), viewModel.getPlayerY(), 50, 50);
            g.setColor(Color.WHITE);
            g.drawString("No Img", viewModel.getPlayerX() + 10, viewModel.getPlayerY() + 30);
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("playerMoved".equals(evt.getPropertyName())) {
            repaint();
        }
    }
}