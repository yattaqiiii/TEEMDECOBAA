package viewmodel;

import model.Difficulty;
import model.ScoreEntry;
import database.DatabaseConnection;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

public class MenuViewModel {
    private final PropertyChangeSupport support = new PropertyChangeSupport(this);

    private String username = "";
    private Difficulty selectedDifficulty = Difficulty.EASY;
    private List<ScoreEntry> highScores = new ArrayList<>();
    private String validationError = null;

    // Listener for GameView to know when to start the game
    public interface StartGameListener {
        void onStartGameRequested(String username, Difficulty difficulty);
    }
    private StartGameListener startGameListener;

    public MenuViewModel() {
        // Load initial scores
        loadHighScores();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        String oldUsername = this.username;
        this.username = username;
        support.firePropertyChange("username", oldUsername, username);
    }

    public Difficulty getSelectedDifficulty() {
        return selectedDifficulty;
    }

    public void setSelectedDifficulty(Difficulty difficulty) {
        if (this.selectedDifficulty != difficulty) {
            Difficulty oldDifficulty = this.selectedDifficulty;
            this.selectedDifficulty = difficulty;
            support.firePropertyChange("selectedDifficulty", oldDifficulty, difficulty);
            loadHighScores(); // Reload scores when difficulty changes
        }
    }

    public List<ScoreEntry> getHighScores() {
        return Collections.unmodifiableList(highScores);
    }

    private void loadHighScores() {
        List<ScoreEntry> oldHighScores = new ArrayList<>(this.highScores);
        this.highScores = DatabaseConnection.getScoresByMode(selectedDifficulty.name());
        support.firePropertyChange("highScores", oldHighScores, Collections.unmodifiableList(this.highScores));
    }

    // Call this when GameView needs to refresh scores (e.g., after a game)
    public void refreshHighScores() {
        loadHighScores();
    }

    public String getValidationError() {
        return validationError;
    }

    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        support.addPropertyChangeListener(pcl);
    }

    public void removePropertyChangeListener(PropertyChangeListener pcl) {
        support.removePropertyChangeListener(pcl);
    }

    // Method to be called by View when start button is pressed
    public void requestStartGame() {
        if (username == null || username.trim().isEmpty()) {
            String oldError = this.validationError;
            this.validationError = "Username cannot be empty.";
            support.firePropertyChange("validationError", oldError, this.validationError);
            this.validationError = null; // Clear error after firing
            return;
        }
        if (startGameListener != null) {
            startGameListener.onStartGameRequested(username, selectedDifficulty);
        }
    }

    public void setStartGameListener(StartGameListener listener) {
        this.startGameListener = listener;
    }
}

