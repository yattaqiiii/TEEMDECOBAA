package main;

import model.Player;
import view.GamePanel;
import view.GameView;
import viewmodel.GameViewModel;

import javax.swing.*;

/**
 * Kelas utama untuk menjalankan aplikasi.
 * Bertugas untuk menginisialisasi dan menghubungkan Model, View, dan ViewModel.
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // 1. Buat Model
            // Karakter muncul di tengah sesuai spesifikasi tugas.
            // (Asumsi layar 800x600, tengahnya sekitar 350, 250)
            Player player = new Player(350, 250);

            // 2. Buat ViewModel dan berikan Model kepadanya
            GameViewModel viewModel = new GameViewModel(player);

            // 3. Buat View (Panel) dan berikan ViewModel kepadanya
            GamePanel gamePanel = new GamePanel(viewModel);

            // 4. Buat View (Frame) dan masukkan Panel ke dalamnya
            new GameView(gamePanel);
        });
    }
}