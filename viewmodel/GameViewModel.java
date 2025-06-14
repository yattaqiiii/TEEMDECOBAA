package viewmodel;

import model.*;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

public class GameViewModel {
    private final Player player;
    private final List<SkillBall> skillBalls = new CopyOnWriteArrayList<>();
    private final List<ScorePopup> scorePopups = new CopyOnWriteArrayList<>();
    private final Lasso lasso = new Lasso();
    private int totalScore = 0;
    private final int gameWidth, gameHeight;
    private static final int PLAYER_WIDTH = 64, PLAYER_HEIGHT = 64;
    private static final int OBJECT_WIDTH = 40, OBJECT_HEIGHT = 40;
    private static final int BASKET_X = 501, BASKET_Y = 400;
    private final Random random = new Random();
    private final long startTime = System.currentTimeMillis();
    private boolean isGameOver = false;
    private static final long GAME_DURATION_SECONDS = 120;
    private final int speedGrowthFactor;

    public GameViewModel(Player player, int gameWidth, int gameHeight, Difficulty difficulty) {
        this.player = player;
        this.gameWidth = gameWidth;
        this.gameHeight = gameHeight;
        this.speedGrowthFactor = switch (difficulty) {
            case EASY -> 30;
            case MEDIUM -> 20;
            case HARD -> 10;
        };
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
        lasso.setActivationTime(System.currentTimeMillis());

        for (SkillBall ball : skillBalls) {
            if (ball.getState() == BallState.MOVING && targetX >= ball.getX() && targetX <= ball.getX() + OBJECT_WIDTH &&
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
            double dx = BASKET_X - target.getX();
            double dy = BASKET_Y - target.getY();
            double distance = Math.sqrt(dx * dx + dy * dy);
            int moveSpeed = 15;
            if (distance < moveSpeed) {
                totalScore += target.getScore();
                scorePopups.add(new ScorePopup(target.getScore(), BASKET_X, BASKET_Y));
                skillBalls.remove(target);
                lasso.setActive(false);
            } else {
                target.setX(target.getX() + (int) (dx / distance * moveSpeed));
                target.setY(target.getY() + (int) (dy / distance * moveSpeed));
            }
        } else {
            long elapsedTime = System.currentTimeMillis() - lasso.getActivationTime();
            if (elapsedTime > 300) {
                lasso.setActive(false);
            }
        }
    }

    private void updateScorePopups() { scorePopups.forEach(ScorePopup::update); scorePopups.removeIf(ScorePopup::isExpired); }
    private void spawnNewBall() { if (random.nextInt(100) < 4) { long elapsedSeconds = (System.currentTimeMillis() - startTime) / 1000; int currentSpeed = 5 + (int) (elapsedSeconds / speedGrowthFactor); ObjectType type = ObjectType.values()[random.nextInt(ObjectType.values().length)]; int score = switch (type) { case SEMANGKA -> 30; case COTTON -> 20; case AYAM -> 10; case BOM -> -10; }; int y = random.nextInt(gameHeight / 3); int x = random.nextBoolean() ? -OBJECT_WIDTH : gameWidth; int speed = (x == gameWidth) ? -currentSpeed : currentSpeed; skillBalls.add(new SkillBall(x, y, speed, score, type)); } }
    public void movePlayer(int dx, int dy) { if (isGameOver) return; int newX = player.getX() + dx; int newY = player.getY() + dy; int topBoundary = (gameHeight * 2) / 3; newX = Math.max(0, newX); newX = Math.min(gameWidth - PLAYER_WIDTH, newX); newY = Math.max(topBoundary, newY); newY = Math.min(gameHeight - PLAYER_HEIGHT, newY); player.setX(newX); player.setY(newY); }
    private void moveBalls() { skillBalls.forEach(SkillBall::move); }
    private void removeOffscreenBalls() { skillBalls.removeIf(ball -> ball.getState() == BallState.MOVING && (ball.getX() < -OBJECT_WIDTH || ball.getX() > gameWidth)); }
    private void checkGameOver() { if (getRemainingTime() <= 0) isGameOver = true; }
    public List<SkillBall> getSkillBalls() { return skillBalls; }
    public List<ScorePopup> getScorePopups() { return scorePopups; }
    public Lasso getLasso() { return lasso; }
    public int getTotalScore() { return totalScore; }
    public boolean isGameOver() { return isGameOver; }
    public long getRemainingTime() { long elapsedMillis = System.currentTimeMillis() - startTime; return Math.max(0, GAME_DURATION_SECONDS - (elapsedMillis / 1000)); }
    public Player getPlayer() { return player; }
}