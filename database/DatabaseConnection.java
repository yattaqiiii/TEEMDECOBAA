package database;

import model.ScoreEntry;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

// kelas ini menangani semua interaksi dengan database sqlite.
// ini termasuk membuat koneksi, membuat tabel jika belum ada, mengisi data awal (dummy data), memasukkan skor baru, dan mengambil daftar skor tertinggi berdasarkan mode kesulitan.
// variabel url adalah string koneksi ke file database sqlite.
public class DatabaseConnection {
    private static final String URL = "jdbc:sqlite:the_usual_suspect.db";

    private static Connection connect() {
        Connection conn = null;
        try { conn = DriverManager.getConnection(URL); } catch (SQLException e) { System.out.println(e.getMessage()); }
        return conn;
    }

    public static void createNewTable() {
        // Mengubah skema tabel highscores
        String sql = "CREATE TABLE IF NOT EXISTS highscores (id integer PRIMARY KEY, username text NOT NULL, score integer NOT NULL, count integer NOT NULL, mode text NOT NULL)";
        try (Connection conn = connect(); Statement stmt = conn.createStatement()) { stmt.execute(sql); } catch (SQLException e) { System.out.println(e.getMessage()); }
    }

    public static void createThasilTable() {
        String sql = "CREATE TABLE IF NOT EXISTS thasil (id integer PRIMARY KEY, username text NOT NULL, score integer NOT NULL, count integer NOT NULL, mode text NOT NULL)";
        try (Connection conn = connect(); Statement stmt = conn.createStatement()) { stmt.execute(sql); } catch (SQLException e) { System.out.println(e.getMessage()); }
    }

    public static void populateWithDummyData() {
        String checkSql = "SELECT COUNT(*) FROM highscores";
        try (Connection conn = connect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(checkSql)) {
            if (rs.getInt(1) == 0) {
                System.out.println("Tabel kosong, mengisi dengan 10 data dummy...");
                // Menyesuaikan data dummy dengan skema baru
                insertScore(conn, "NalarJalan", 1000, 10, "EASY"); insertScore(conn, "UseYourLogic", 800, 8, "EASY");
                insertScore(conn, "NoJudgement", 700, 7, "MEDIUM"); insertScore(conn, "ProGamer", 1200, 12, "HARD");
                insertScore(conn, "Newbie", 150, 5, "EASY"); insertScore(conn, "RacerX", 950, 9, "MEDIUM");
                insertScore(conn, "Master", 1500, 15, "HARD"); insertScore(conn, "PlayerOne", 500, 5, "EASY");
                insertScore(conn, "Challenger", 1100, 11, "HARD"); insertScore(conn, "Casual", 650, 6, "MEDIUM");
            }
        } catch (SQLException e) { System.out.println("Gagal mempopulasi data dummy: " + e.getMessage()); }
    }

    // Menyesuaikan parameter metode insertScore
    private static void insertScore(Connection conn, String username, int score, int count, String mode) throws SQLException {
        String sql = "INSERT INTO highscores(username, score, count, mode) VALUES(?,?,?,?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setInt(2, score);
            pstmt.setInt(3, count);
            pstmt.setString(4, mode.toUpperCase());
            pstmt.executeUpdate();
        }
    }

    // Menyesuaikan parameter metode insertScore
    public static void insertScore(String username, int score, int count, String mode) {
        String sql = "INSERT INTO highscores(username, score, count, mode) VALUES(?,?,?,?)";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setInt(2, score);
            pstmt.setInt(3, count);
            pstmt.setString(4, mode.toUpperCase());
            pstmt.executeUpdate();
        } catch (SQLException e) { System.out.println(e.getMessage()); }
    }

    public static List<ScoreEntry> getScoresByMode(String mode) {
        // Mengambil username, score, dan count
        String sql = "SELECT username, score, count FROM highscores WHERE mode = ? ORDER BY score DESC LIMIT 10";
        List<ScoreEntry> scores = new ArrayList<>();
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, mode.toUpperCase());
            ResultSet rs = pstmt.executeQuery();
            // Menyesuaikan pembuatan ScoreEntry
            while (rs.next()) { scores.add(new ScoreEntry(rs.getString("username"), rs.getInt("score"), rs.getInt("count"))); }
        } catch (SQLException e) { System.out.println(e.getMessage()); }
        return scores;
    }
}