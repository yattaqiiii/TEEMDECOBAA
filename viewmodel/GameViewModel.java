package viewmodel;

import model.Player;
import model.SkillBall;
import model.ObjectType;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameViewModel {
    private Player player;
    private final PropertyChangeSupport support = new PropertyChangeSupport(this);
    private int gameWidth;
    private int gameHeight;
    private static final int PLAYER_WIDTH = 64;
    private static final int PLAYER_HEIGHT = 64;

    private List<SkillBall> skillBalls;
    private Random random;
    private static final int OBJECT_SIZE = 40;

    private long startTime;
    private boolean isGameOver;
    private static final long GAME_DURATION_SECONDS = 120;

    public GameViewModel(Player player, int gameWidth, int gameHeight) {
        this.player = player;
        this.gameWidth = gameWidth;
        this.gameHeight = gameHeight;
        this.skillBalls = new ArrayList<>();
        this.random = new Random();
        this.startTime = System.currentTimeMillis();
        this.isGameOver = false;
    }

    public void updateGame() {
        if (isGameOver) {
            return;
        }
        checkGameOver();
        spawnNewBall();
        moveBalls();
        removeOffscreenBalls();
    }

    private void checkGameOver() {
        if (getRemainingTime() <= 0) {
            isGameOver = true;
        }
    }

    private void spawnNewBall() {
        if (random.nextInt(100) < 2) {
            long elapsedSeconds = (System.currentTimeMillis() - startTime) / 1000;
            int currentSpeed = 5 + (int)(elapsedSeconds / 15);

            ObjectType type = ObjectType.values()[random.nextInt(ObjectType.values().length)];

            // --- PERUBAHAN: SKOR DISESUAIKAN DENGAN TIPE OBJEK ---
            int score;
            switch (type) {
                case SEMANGKA:
                    score = 30;
                    break;
                case COTTON:
                    score = 20;
                    break;
                case AYAM:
                    score = 10;
                    break;
                case BOM:
                    score = -10;
                    break;
                default:
                    score = 0;
            }

            int y = random.nextInt(gameHeight / 3);
            int x;
            if (random.nextBoolean()) {
                x = -OBJECT_SIZE;
                skillBalls.add(new SkillBall(x, y, currentSpeed, score, type));
            } else {
                x = gameWidth;
                skillBalls.add(new SkillBall(x, y, -currentSpeed, score, type));
            }
        }
    }

    private void moveBalls() {
        for (SkillBall ball : skillBalls) {
            ball.move();
        }
    }

    private void removeOffscreenBalls() {
        skillBalls.removeIf(ball -> ball.getX() < -OBJECT_SIZE || ball.getX() > gameWidth);
    }

    public List<SkillBall> getSkillBalls() {
        return skillBalls;
    }

    public boolean isGameOver() {
        return isGameOver;
    }

    public long getRemainingTime() {
        long elapsedMillis = System.currentTimeMillis() - startTime;
        long remainingSeconds = GAME_DURATION_SECONDS - (elapsedMillis / 1000);
        return Math.max(0, remainingSeconds);
    }

    public void addPropertyChangeListener(PropertyChangeListener pcl) { support.addPropertyChangeListener(pcl); }
    public void removePropertyChangeListener(PropertyChangeListener pcl) { support.removePropertyChangeListener(pcl); }
    public int getPlayerX() { return player.getX(); }
    public int getPlayerY() { return player.getY(); }
    public void movePlayer(int dx, int dy) {
        if(isGameOver) return;
        int newX = player.getX() + dx;
        int newY = player.getY() + dy;
        int topBoundary = (gameHeight * 2) / 3;
        newX = Math.max(0, newX);
        newX = Math.min(gameWidth - PLAYER_WIDTH, newX);
        newY = Math.max(topBoundary, newY);
        newY = Math.min(gameHeight - PLAYER_HEIGHT, newY);
        player.setX(newX);
        player.setY(newY);
        support.firePropertyChange("playerMoved", null, null);
    }
}