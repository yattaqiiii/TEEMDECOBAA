package viewmodel;

import model.BallState;
import model.Difficulty;
import model.Lasso;
import model.ObjectType;
import model.Player;
import model.ScorePopup;
import model.SkillBall;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

// kelas gameviewmodel bertindak sebagai perantara antara tampilan (view) dan logika inti permainan (model).
// kelas ini mengelola status permainan, termasuk pemain, bola-bola objek, lasso, skor, dan logika pembaruan game.
// variabel player merepresentasikan objek pemain.
// variabel skillballs adalah daftar bola objek yang aktif dalam permainan.
// variabel scorepopups adalah daftar popup skor yang ditampilkan.
// variabel lasso merepresentasikan objek lasso.
// variabel totalscore menyimpan skor total pemain.
// variabel gamewidth dan gameheight menentukan ukuran area permainan.
// variabel player_width dan player_height adalah dimensi pemain untuk deteksi tabrakan.
// variabel object_width dan object_height adalah dimensi bola objek.
// variabel basket_x dan basket_y adalah posisi keranjang target.
// variabel random digunakan untuk menghasilkan nilai acak (misalnya untuk spawn bola).
// variabel starttime mencatat waktu dimulainya permainan.
// variabel isgameover menandakan apakah permainan sudah berakhir.
// variabel game_duration_seconds menentukan total durasi permainan dalam detik.
// variabel speedgrowthfactor mempengaruhi seberapa cepat kecepatan bola meningkat seiring waktu, tergantung tingkat kesulitan.
public class GameViewModel {
    private final Player player;
    private final List<SkillBall> skillBalls = new CopyOnWriteArrayList<>();
    private final List<ScorePopup> scorePopups = new CopyOnWriteArrayList<>();
    private final Lasso lasso = new Lasso();
    private int totalScore = 0;
    private int totalBallsCaught = 0; // Tambahkan variabel ini
    private final int gameWidth, gameHeight;
    private static final int PLAYER_WIDTH = 64, PLAYER_HEIGHT = 64;
    private static final int OBJECT_WIDTH = 40, OBJECT_HEIGHT = 40;
    private static final int BASKET_X = 501, BASKET_Y = 400;
    private final Random random = new Random();
    private final long startTime = System.currentTimeMillis();
    private boolean isGameOver = false;
    private static final long GAME_DURATION_SECONDS = 120;
    private final int speedGrowthFactor;

    public GameViewModel(Player player, int gameWidth, int gameHeight, Difficulty difficulty) {
        this.player = player;
        this.gameWidth = gameWidth;
        this.gameHeight = gameHeight;
        // tentukan faktor pertumbuhan kecepatan bola berdasarkan tingkat kesulitan
        this.speedGrowthFactor = switch (difficulty) {
            case EASY -> 30;    // kecepatan bertambah setiap 30 detik
            case MEDIUM -> 20;  // kecepatan bertambah setiap 20 detik
            case HARD -> 10;    // kecepatan bertambah setiap 10 detik
        };
    }

    // metode utama untuk memperbarui logika permainan setiap frame/tick
    public void updateGame() {
        if (isGameOver) return; // jika game sudah berakhir, tidak ada yang perlu diperbarui
        checkGameOver();        // periksa apakah kondisi game over terpenuhi (misalnya waktu habis)
        updateLasso();          // perbarui status dan posisi lasso
        spawnNewBall();         // coba munculkan bola baru secara acak
        moveBalls();            // gerakkan semua bola yang aktif
        removeOffscreenBalls(); // hapus bola yang sudah keluar layar
        updateScorePopups();    // perbarui animasi dan masa hidup popup skor
    }

    // metode untuk melempar lasso ketika pemain mengklik mouse
    public void castLasso(int targetX, int targetY) {
        if (isGameOver || lasso.isActive()) return; // tidak bisa melempar jika game over atau lasso sedang aktif
        lasso.setActive(true); // aktifkan lasso
        lasso.setTarget(null); // reset target sebelumnya (jika ada)
        lasso.setEndX(targetX); // atur koordinat x target lasso (posisi klik mouse)
        lasso.setEndY(targetY); // atur koordinat y target lasso (posisi klik mouse)
        lasso.setActivationTime(System.currentTimeMillis()); // catat waktu aktivasi lasso

        // periksa apakah ada bola yang tertangkap oleh lemparan lasso
        for (SkillBall ball : skillBalls) {
            // periksa apakah bola sedang bergerak dan koordinat klik berada dalam area bola
            if (ball.getState() == BallState.MOVING && targetX >= ball.getX() && targetX <= ball.getX() + OBJECT_WIDTH &&
                    targetY >= ball.getY() && targetY <= ball.getY() + OBJECT_HEIGHT) {
                ball.setState(BallState.CAUGHT); // ubah status bola menjadi tertangkap
                lasso.setTarget(ball);          // set bola ini sebagai target lasso
                break; // hentikan pencarian karena satu lasso hanya menangkap satu bola
            }
        }
    }

    // metode untuk memperbarui logika lasso, terutama jika ada bola yang tertangkap
    private void updateLasso() {
        if (!lasso.isActive()) return; // jika lasso tidak aktif, tidak ada yang perlu diperbarui

        SkillBall target = lasso.getTarget(); // dapatkan bola yang menjadi target lasso
        if (target != null) { // jika ada bola yang tertangkap
            // hitung vektor arah dari bola ke keranjang
            double dx = BASKET_X - target.getX();
            double dy = BASKET_Y - target.getY();
            double distance = Math.sqrt(dx * dx + dy * dy); // jarak ke keranjang
            int moveSpeed = 15; // kecepatan bola ditarik ke keranjang

            if (distance < moveSpeed) { // jika bola sudah sangat dekat dengan keranjang
                totalScore += target.getScore(); // tambahkan skor bola ke total skor
                if (target.getScore() > 0) { // Hanya hitung bola yang memberi skor positif
                    totalBallsCaught++; // Tambah jumlah bola yang ditangkap
                }
                scorePopups.add(new ScorePopup(target.getScore(), BASKET_X, BASKET_Y)); // tampilkan popup skor di keranjang
                skillBalls.remove(target); // hapus bola dari daftar bola aktif
                lasso.setActive(false);    // nonaktifkan lasso
            } else {
                // gerakkan bola menuju keranjang
                target.setX(target.getX() + (int) (dx / distance * moveSpeed));
                target.setY(target.getY() + (int) (dy / distance * moveSpeed));
            }
        } else { // jika lasso aktif tapi tidak ada target (lemparan kosong)
            long elapsedTime = System.currentTimeMillis() - lasso.getActivationTime();
            if (elapsedTime > 300) { // setelah 300 milidetik, nonaktifkan lasso
                lasso.setActive(false);
            }
        }
    }

    // perbarui posisi dan masa hidup setiap popup skor
    private void updateScorePopups() { scorePopups.forEach(ScorePopup::update); scorePopups.removeIf(ScorePopup::isExpired); }

    // metode untuk memunculkan bola baru secara acak
    private void spawnNewBall() {
        // kemungkinan 4% untuk memunculkan bola baru setiap pembaruan game
        if (random.nextInt(100) < 4) {
            long elapsedSeconds = (System.currentTimeMillis() - startTime) / 1000; // waktu game berjalan dalam detik
            int currentSpeed = 5 + (int) (elapsedSeconds / speedGrowthFactor); // kecepatan awal bola, meningkat seiring waktu

            ObjectType type = ObjectType.values()[random.nextInt(ObjectType.values().length)]; // pilih jenis bola secara acak
            int score = switch (type) { // tentukan skor berdasarkan jenis bola
                case SEMANGKA -> 30;
                case COTTON -> 20;
                case AYAM -> 10;
                case BOM -> -10;
            };
            int y = random.nextInt(gameHeight / 3); // posisi y acak di sepertiga atas layar
            // posisi x acak, bisa dari kiri (-OBJECT_WIDTH) atau kanan (gameWidth)
            int x = random.nextBoolean() ? -OBJECT_WIDTH : gameWidth;
            int speed = (x == gameWidth) ? -currentSpeed : currentSpeed; // arah kecepatan sesuai sisi munculnya bola

            skillBalls.add(new SkillBall(x, y, speed, score, type)); // tambahkan bola baru ke daftar
        }
    }

    // metode untuk menggerakkan pemain dan memastikan tetap dalam batas area permainan
    public void movePlayer(int dx, int dy) {
        if (isGameOver) return; // pemain tidak bisa bergerak jika game over

        int newX = player.getX() + dx; // hitung posisi x baru
        int newY = player.getY() + dy; // hitung posisi y baru

        // batasi pergerakan pemain hanya di sepertiga bawah layar untuk sumbu y
        int topBoundary = (gameHeight * 2) / 3;

        // pastikan pemain tidak keluar dari batas kiri/kanan layar
        newX = Math.max(0, newX); // batas kiri
        newX = Math.min(gameWidth - PLAYER_WIDTH, newX); // batas kanan (dikurangi lebar pemain)
        // pastikan pemain tidak keluar dari batas atas/bawah area geraknya
        newY = Math.max(topBoundary, newY); // batas atas area gerak pemain
        newY = Math.min(gameHeight - PLAYER_HEIGHT, newY); // batas bawah layar (dikurangi tinggi pemain)

        player.setX(newX); // terapkan posisi x baru
        player.setY(newY); // terapkan posisi y baru
    }

    // gerakkan semua bola yang ada di daftar skillballs
    private void moveBalls() { skillBalls.forEach(SkillBall::move); }

    // hapus bola yang sudah bergerak keluar dari layar permainan
    private void removeOffscreenBalls() { skillBalls.removeIf(ball -> ball.getState() == BallState.MOVING && (ball.getX() < -OBJECT_WIDTH || ball.getX() > gameWidth)); }

    // periksa apakah waktu permainan sudah habis
    private void checkGameOver() { if (getRemainingTime() <= 0) isGameOver = true; }

    // getter untuk daftar bola skill
    public List<SkillBall> getSkillBalls() { return skillBalls; }
    // getter untuk daftar popup skor
    public List<ScorePopup> getScorePopups() { return scorePopups; }
    // getter untuk objek lasso
    public Lasso getLasso() { return lasso; }
    // getter untuk skor total
    public int getTotalScore() { return totalScore; }
    // getter untuk jumlah bola yang ditangkap
    public int getTotalBallsCaught() { return totalBallsCaught; } // Tambahkan getter ini
    // getter untuk status game over
    public boolean isGameOver() { return isGameOver; }
    // getter untuk sisa waktu permainan dalam detik
    public long getRemainingTime() { long elapsedMillis = System.currentTimeMillis() - startTime; return Math.max(0, GAME_DURATION_SECONDS - (elapsedMillis / 1000)); }
    // getter untuk objek pemain
    public Player getPlayer() { return player; }
}