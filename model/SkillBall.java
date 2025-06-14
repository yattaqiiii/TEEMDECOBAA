package model;

public class SkillBall {
    private int x, y;
    private int speed;
    private int score;
    private ObjectType type;
    private BallState state; // <-- State baru

    public SkillBall(int x, int y, int speed, int score, ObjectType type) {
        this.x = x;
        this.y = y;
        this.speed = speed;
        this.score = score;
        this.type = type;
        this.state = BallState.MOVING; // Awalnya selalu bergerak bebas
    }

    public void move() {
        if (state == BallState.MOVING) {
            this.x += this.speed;
        }
    }

    // --- Getter dan Setter ---
    public int getX() { return x; }
    public void setX(int x) { this.x = x; }
    public int getY() { return y; }
    public void setY(int y) { this.y = y; }
    public int getScore() { return score; }
    public ObjectType getType() { return type; }
    public BallState getState() { return state; }
    public void setState(BallState state) { this.state = state; }
}