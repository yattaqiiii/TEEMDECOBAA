package main;

import database.DatabaseConnection;
import view.GameView;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // Persiapkan database saat aplikasi pertama kali jalan
        DatabaseConnection.createNewTable();

        // --- PANGGIL METODE DATA DUMMY DI SINI ---
        DatabaseConnection.populateWithDummyData();

        SwingUtilities.invokeLater(() -> {
            // Cukup buat GameView, ia akan menampilkan menu utama
            new GameView();
        });
    }
}