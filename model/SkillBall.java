package model;

public class SkillBall {
    private int x, y;
    private int speed; // Kecepatan gerak (misal: 5 pixel per frame)
    private int score;
    private ObjectType type;

    public SkillBall(int x, int y, int speed, int score, ObjectType type) {
        this.x = x;
        this.y = y;
        this.speed = speed;
        this.score = score;
        this.type = type;
    }

    public void move() {
        this.x += this.speed;
    }

    // --- Getter dan Setter ---
    public int getX() { return x; }
    public int getY() { return y; }
    public int getScore() { return score; }
    public ObjectType getType() { return type; }
}