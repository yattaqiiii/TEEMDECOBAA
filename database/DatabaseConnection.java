package database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.ScoreEntry;

public class DatabaseConnection {
    private static final String URL = "jdbc:sqlite:the_usual_suspect.db";

    public static Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(URL);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    public static void createNewTable() {
        String sql = "CREATE TABLE IF NOT EXISTS highscores (\n"
                + " id integer PRIMARY KEY AUTOINCREMENT,\n"
                + " name text NOT NULL,\n"
                + " score integer NOT NULL,\n"
                + " mode text NOT NULL\n"
                + ");";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void insertScore(String name, int score, String mode) {
        String sql = "INSERT INTO highscores(name, score, mode) VALUES(?,?,?)";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setInt(2, score);
            pstmt.setString(3, mode.toUpperCase());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static List<ScoreEntry> getScoresByMode(String mode) {
        String sql = "SELECT name, score FROM highscores WHERE mode = ? ORDER BY score DESC LIMIT 10";
        List<ScoreEntry> scores = new ArrayList<>();

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, mode.toUpperCase());
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                scores.add(new ScoreEntry(rs.getString("name"), rs.getInt("score")));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return scores;
    }
}