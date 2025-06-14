package main;

import database.DatabaseConnection;
import view.GameView;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        DatabaseConnection.createNewTable();
        DatabaseConnection.populateWithDummyData();
        SwingUtilities.invokeLater(GameView::new);
    }
}