package model;

// kelas lasso merepresentasikan lasso (tali) yang digunakan pemain untuk menangkap objek.
// variabel endx dan endy adalah koordinat target atau ujung dari lasso ketika dilempar.
// variabel active menandakan apakah lasso sedang aktif (dilempar) atau tidak.
// variabel target menyimpan referensi ke skillball yang berhasil ditangkap oleh lasso.
// variabel activationtime mencatat waktu kapan lasso mulai diaktifkan, berguna untuk mengatur durasi lasso jika tidak mengenai target.
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