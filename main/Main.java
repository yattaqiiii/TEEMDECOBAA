package main;

import audio.AudioManager;
import database.DatabaseConnection;
import view.GameView;
import javax.swing.SwingUtilities;

// kelas utama aplikasi.
// kelas ini bertanggung jawab untuk inisialisasi database, memulai musik latar, dan menjalankan antarmuka pengguna (ui) game.
public class Main {
    public static void main(String[] args) {
        // Persiapkan database
        DatabaseConnection.createNewTable();
        DatabaseConnection.createThasilTable(); // Pastikan baris ini ada
        DatabaseConnection.populateWithDummyData();

        // Mulai putar BGM
        // Pastikan nama file sesuai dengan file .wav Anda
        AudioManager.playBGM("/assets/beautifulcriminal.wav");

        // Jalankan UI
        SwingUtilities.invokeLater(GameView::new);
    }
}