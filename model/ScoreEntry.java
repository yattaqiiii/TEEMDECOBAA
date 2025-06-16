package model;

// record scoreentry adalah struktur data sederhana untuk menyimpan informasi satu entri skor.
// Mengubah record untuk menyertakan username, score, dan count.
public record ScoreEntry(String username, int score, int count) {
}