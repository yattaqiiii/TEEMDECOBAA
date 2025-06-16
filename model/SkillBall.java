package model;

// kelas skillball merepresentasikan objek bola (skillball) dalam game yang bisa ditangkap pemain.
// variabel x dan y menyimpan posisi bola.
// variabel speed menentukan kecepatan gerak bola.
// variabel score adalah nilai yang didapat jika bola ini ditangkap.
// variabel type menentukan jenis objek (misalnya ayam, bom) yang mempengaruhi skor atau perilaku lain.
// variabel state menandakan kondisi bola, apakah sedang bergerak (moving) atau sudah tertangkap (caught).
public class SkillBall {
    private int x, y, speed, score;
    private final ObjectType type;
    private BallState state;

    public SkillBall(int x, int y, int speed, int score, ObjectType type) {
        this.x = x; this.y = y; this.speed = speed; this.score = score;
        this.type = type; this.state = BallState.MOVING;
    }
    public void move() { if (state == BallState.MOVING) this.x += this.speed; }
    public int getX() { return x; }
    public void setX(int x) { this.x = x; }
    public int getY() { return y; }
    public void setY(int y) { this.y = y; }
    public int getScore() { return score; }
    public ObjectType getType() { return type; }
    public BallState getState() { return state; }
    public void setState(BallState state) { this.state = state; }
}