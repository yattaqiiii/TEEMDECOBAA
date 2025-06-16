package model;

// enum ballstate mendefinisikan status atau kondisi dari sebuah skillball (bola objek).
// moving: bola sedang bergerak bebas di area permainan.
// caught: bola telah berhasil ditangkap oleh lasso pemain.
public enum BallState {
    MOVING,  // Bergerak bebas
    CAUGHT   // Tertangkap oleh lasso
}