package viewmodel;

import model.*;

import java.awt.Color;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

public class GameViewModel {
    // ... (Semua variabel Anda yang lain tetap sama) ...
    private Player player;
    private List<SkillBall> skillBalls;
    private Lasso lasso;
    private List<ScorePopup> scorePopups;
    private int totalScore;
    private int gameWidth, gameHeight;
    private static final int PLAYER_WIDTH = 64, PLAYER_HEIGHT = 64;
    private static final int OBJECT_WIDTH = 40, OBJECT_HEIGHT = 40;
    private static final int BASKET_X = 501, BASKET_Y = 400;
    private final PropertyChangeSupport support = new PropertyChangeSupport(this);
    private Random random;
    private long startTime;
    private boolean isGameOver;
    private static final long GAME_DURATION_SECONDS = 120;
    private long lassoActivationTime;
    private int speedGrowthFactor;

    public GameViewModel(Player player, int gameWidth, int gameHeight, Difficulty difficulty) {
        this.player = player;
        this.gameWidth = gameWidth;
        this.gameHeight = gameHeight;
        this.skillBalls = new CopyOnWriteArrayList<>();
        this.scorePopups = new CopyOnWriteArrayList<>();
        this.lasso = new Lasso();
        this.random = new Random();
        this.startTime = System.currentTimeMillis();
        this.isGameOver = false;
        this.totalScore = 0;

        switch (difficulty) {
            case EASY: this.speedGrowthFactor = 30; break;
            case MEDIUM: this.speedGrowthFactor = 20; break;
            case HARD: this.speedGrowthFactor = 10; break;
        }
    }

    public void updateGame() {
        if (isGameOver) return;
        checkGameOver();
        updateLasso();
        spawnNewBall();
        moveBalls();
        removeOffscreenBalls();
        updateScorePopups();
    }

    public void castLasso(int targetX, int targetY) {
        if (isGameOver || lasso.isActive()) return;

        lasso.setActive(true);
        lasso.setTarget(null);
        lasso.setEndX(targetX);
        lasso.setEndY(targetY);
        this.lassoActivationTime = System.currentTimeMillis();

        for (SkillBall ball : skillBalls) {
            if (ball.getState() == BallState.MOVING &&
                    targetX >= ball.getX() && targetX <= ball.getX() + OBJECT_WIDTH &&
                    targetY >= ball.getY() && targetY <= ball.getY() + OBJECT_HEIGHT) {

                ball.setState(BallState.CAUGHT);
                lasso.setTarget(ball);
                break;
            }
        }
    }

    private void updateLasso() {
        if (!lasso.isActive()) return;

        SkillBall target = lasso.getTarget();
        if (target != null) {
            int currentX = target.getX();
            int currentY = target.getY();
            double dx = BASKET_X - currentX;
            double dy = BASKET_Y - currentY;
            double distance = Math.sqrt(dx * dx + dy * dy);
            int moveSpeed = 15;

            if (distance < moveSpeed) {
                totalScore += target.getScore();
                scorePopups.add(new ScorePopup(target.getScore(), BASKET_X, BASKET_Y));
                skillBalls.remove(target);
                lasso.setActive(false);
            } else {
                target.setX(currentX + (int)(dx / distance * moveSpeed));
                target.setY(currentY + (int)(dy / distance * moveSpeed));
            }
        } else {
            long elapsedTime = System.currentTimeMillis() - lassoActivationTime;
            if (elapsedTime > 300) {
                lasso.setActive(false);
            }
        }
    }

    private void updateScorePopups() {
        for (ScorePopup popup : scorePopups) {
            popup.update();
        }
        scorePopups.removeIf(ScorePopup::isExpired);
    }

    private void spawnNewBall() {
        if (random.nextInt(100) < 4) {
            long elapsedSeconds = (System.currentTimeMillis() - startTime) / 1000;
            int currentSpeed = 5 + (int)(elapsedSeconds / speedGrowthFactor);
            ObjectType type = ObjectType.values()[random.nextInt(ObjectType.values().length)];
            int score;
            switch (type) {
                case SEMANGKA: score = 30; break;
                case COTTON: score = 20; break;
                case AYAM: score = 10; break;
                case BOM: score = -10; break;
                default: score = 0;
            }
            int y = random.nextInt(gameHeight / 3);
            int x = random.nextBoolean() ? -OBJECT_WIDTH : gameWidth;
            int speed = (x == gameWidth) ? -currentSpeed : currentSpeed;
            skillBalls.add(new SkillBall(x, y, speed, score, type));
        }
    }

    private void moveBalls() { for (SkillBall ball : skillBalls) ball.move(); }
    private void removeOffscreenBalls() { skillBalls.removeIf(ball -> ball.getState() == BallState.MOVING && (ball.getX() < -OBJECT_WIDTH || ball.getX() > gameWidth)); }
    public List<SkillBall> getSkillBalls() { return skillBalls; }
    public List<ScorePopup> getScorePopups() { return scorePopups; }
    public Lasso getLasso() { return lasso; }
    public int getTotalScore() { return totalScore; }
    public boolean isGameOver() { return isGameOver; }
    public long getRemainingTime() { long elapsedMillis = System.currentTimeMillis() - startTime; return Math.max(0, GAME_DURATION_SECONDS - (elapsedMillis / 1000)); }
    private void checkGameOver() { if (getRemainingTime() <= 0) isGameOver = true; }
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
    }
}

// KELAS ScorePopup DIHAPUS DARI SINI