package model;

import java.awt.Color;

public class ScorePopup {
    private String text;
    private int x, y;
    private int lifetime = 60; // Durasi tampil (60 frame ~ 1 detik)
    private Color color;

    public ScorePopup(int score, int x, int y) {
        this.text = (score > 0 ? "+" : "") + score;
        this.x = x;
        this.y = y;
        // Set warna hijau jika skor positif, merah jika negatif
        this.color = (score > 0) ? new Color(50, 205, 50) : Color.RED;
    }

    public void update() {
        y -= 1; // Bergerak ke atas
        lifetime--;
    }

    public boolean isExpired() {
        return lifetime <= 0;
    }

    public String getText() {
        return text;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Color getColor() {
        return color;
    }
}