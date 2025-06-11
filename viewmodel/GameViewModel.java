package viewmodel;

import model.Player;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class GameViewModel {
    private Player player;
    private final PropertyChangeSupport support = new PropertyChangeSupport(this);

    public GameViewModel(Player player) {
        this.player = player;
    }

    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        support.addPropertyChangeListener(pcl);
    }

    public void removePropertyChangeListener(PropertyChangeListener pcl) {
        support.removePropertyChangeListener(pcl);
    }

    public int getPlayerX() {
        return player.getX();
    }

    public int getPlayerY() {
        return player.getY();
    }

    public String getPlayerDirection() {
        return player.getDirection();
    }

    // Metode pergerakan yang juga mengubah arah karakter
    public void movePlayer(int dx, int dy, String direction) {
        int oldX = player.getX();
        int oldY = player.getY();
        player.setX(oldX + dx);
        player.setY(oldY + dy);
        player.setDirection(direction);

        support.firePropertyChange("playerMoved", null, null);
    }
}