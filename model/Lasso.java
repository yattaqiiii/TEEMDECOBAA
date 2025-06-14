package model;

public class Lasso {
    private int startX, startY, endX, endY;
    private boolean active;
    private SkillBall target; // Bola yang menjadi target

    public Lasso() {
        this.active = false;
        this.target = null;
    }

    // --- Getter dan Setter ---
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public int getStartX() { return startX; }
    public void setStartX(int startX) { this.startX = startX; }
    public int getStartY() { return startY; }
    public void setStartY(int startY) { this.startY = startY; }
    public int getEndX() { return endX; }
    public void setEndX(int endX) { this.endX = endX; }
    public int getEndY() { return endY; }
    public void setEndY(int endY) { this.endY = endY; }
    public SkillBall getTarget() { return target; }
    public void setTarget(SkillBall target) { this.target = target; }
}