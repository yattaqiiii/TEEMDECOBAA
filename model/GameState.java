package model;

// enum gamestate mendefinisikan berbagai status atau kondisi permainan.
// main_menu: saat menu utama ditampilkan.
// playing: saat permainan sedang berlangsung aktif.
// paused: saat permainan dijeda oleh pemain.
// game_over: saat permainan telah berakhir (misalnya waktu habis).
public enum GameState {
    MAIN_MENU,
    PLAYING,
    PAUSED,
    GAME_OVER
}