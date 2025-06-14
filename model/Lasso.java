package model;

public class Lasso {
    private int endX, endY;
    private boolean active;
    private SkillBall target;
    private long activationTime;

    public Lasso() { this.active = false; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public int getEndX() { return endX; }
    public void setEndX(int endX) { this.endX = endX; }
    public int getEndY() { return endY; }
    public void setEndY(int endY) { this.endY = endY; }
    public SkillBall getTarget() { return target; }
    public void setTarget(SkillBall target) { this.target = target; }
    public long getActivationTime() { return activationTime; }
    public void setActivationTime(long time) { this.activationTime = time; }
}