package database;

import model.ScoreEntry;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseConnection {
    private static final String URL = "jdbc:sqlite:the_usual_suspect.db";

    private static Connection connect() {
        Connection conn = null;
        try { conn = DriverManager.getConnection(URL); } catch (SQLException e) { System.out.println(e.getMessage()); }
        return conn;
    }

    public static void createNewTable() {
        String sql = "CREATE TABLE IF NOT EXISTS highscores (id integer PRIMARY KEY, name text NOT NULL, score integer NOT NULL, mode text NOT NULL)";
        try (Connection conn = connect(); Statement stmt = conn.createStatement()) { stmt.execute(sql); } catch (SQLException e) { System.out.println(e.getMessage()); }
    }

    public static void populateWithDummyData() {
        String checkSql = "SELECT COUNT(*) FROM highscores";
        try (Connection conn = connect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(checkSql)) {
            if (rs.getInt(1) == 0) {
                System.out.println("Tabel kosong, mengisi dengan 10 data dummy...");
                insertScore(conn, "NalarJalan", 1000, "EASY"); insertScore(conn, "UseYourLogic", 800, "EASY");
                insertScore(conn, "NoJudgement", 700, "MEDIUM"); insertScore(conn, "ProGamer", 1200, "HARD");
                insertScore(conn, "Newbie", 150, "EASY"); insertScore(conn, "RacerX", 950, "MEDIUM");
                insertScore(conn, "Master", 1500, "HARD"); insertScore(conn, "PlayerOne", 500, "EASY");
                insertScore(conn, "Challenger", 1100, "HARD"); insertScore(conn, "Casual", 650, "MEDIUM");
            }
        } catch (SQLException e) { System.out.println("Gagal mempopulasi data dummy: " + e.getMessage()); }
    }

    private static void insertScore(Connection conn, String name, int score, String mode) throws SQLException {
        String sql = "INSERT INTO highscores(name, score, mode) VALUES(?,?,?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name); pstmt.setInt(2, score); pstmt.setString(3, mode.toUpperCase()); pstmt.executeUpdate();
        }
    }

    public static void insertScore(String name, int score, String mode) {
        String sql = "INSERT INTO highscores(name, score, mode) VALUES(?,?,?)";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name); pstmt.setInt(2, score); pstmt.setString(3, mode.toUpperCase());
            pstmt.executeUpdate();
        } catch (SQLException e) { System.out.println(e.getMessage()); }
    }

    public static List<ScoreEntry> getScoresByMode(String mode) {
        String sql = "SELECT name, score FROM highscores WHERE mode = ? ORDER BY score DESC LIMIT 10";
        List<ScoreEntry> scores = new ArrayList<>();
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, mode.toUpperCase());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) { scores.add(new ScoreEntry(rs.getString("name"), rs.getInt("score"))); }
        } catch (SQLException e) { System.out.println(e.getMessage()); }
        return scores;
    }
}