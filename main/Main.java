package main;

import model.Player;
import view.GamePanel;
import view.GameView;
import viewmodel.GameViewModel;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // 1. Buat Model
            Player player = new Player(341, 400); // Posisi awal pemain

            // --- PERBAIKAN DI SINI ---
            // Sekarang kita kirim ukuran layar (800, 600) ke ViewModel
            // sesuai dengan konstruktor yang baru.
            GameViewModel viewModel = new GameViewModel(player, 682, 512);

            // 3. Buat View (Panel) dan berikan ViewModel kepadanya
            GamePanel gamePanel = new GamePanel(viewModel);

            // 4. Buat View (Frame) dan masukkan Panel ke dalamnya
            new GameView(gamePanel);
        });
    }
}