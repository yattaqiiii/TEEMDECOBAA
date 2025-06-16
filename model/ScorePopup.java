package model;

import java.awt.Color;

// kelas scorepopup merepresentasikan teks skor yang muncul sementara di layar ketika pemain berhasil menangkap bola.
// variabel text adalah teks skor yang ditampilkan (misalnya "+10" atau "-5").
// variabel x dan y adalah posisi popup skor di layar.
// variabel lifetime menentukan berapa lama popup akan ditampilkan sebelum menghilang.
// variabel color menentukan warna teks skor (hijau untuk positif, merah untuk negatif).
// variabel alpha mengatur transparansi popup, membuatnya memudar seiring waktu.
public class ScorePopup {
    private final String text;
    private int x, y;
    private int lifetime = 60;
    private final Color color;
    private int alpha = 255;

    public ScorePopup(int score, int x, int y) {
        this.text = (score >= 0 ? "+" : "") + score;
        this.x = x;
        this.y = y;
        this.color = (score >= 0) ? new Color(75, 255, 75) : Color.RED;
    }

    public void update() {
        y -= 1;
        lifetime--;
        if (lifetime < 20) {
            alpha = Math.max(0, alpha - 13);
        }
    }

    public boolean isExpired() { return lifetime <= 0; }
    public String getText() { return text; }
    public int getX() { return x; }
    public int getY() { return y; }
    public Color getColor() { return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha); }
}