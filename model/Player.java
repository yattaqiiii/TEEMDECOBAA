package model;

// kelas player merepresentasikan pemain dalam game.
// variabel x dan y menyimpan koordinat posisi pemain di layar.
// variabel direction menyimpan arah hadap pemain saat ini (misalnya "front", "left", "right").
public class Player {
    private int x;
    private int y;
    private String direction; // menyimpan arah karakter

    public Player(int startX, int startY) {
        this.x = startX;
        this.y = startY;
        this.direction = "front"; // Default: menghadap depan
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }
}