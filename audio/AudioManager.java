package audio;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.net.URL;

// kelas audiomanager bertanggung jawab untuk memutar musik latar (bgm) dalam game.
// metode playbgm mengambil path ke file audio, memuatnya, dan memutarnya secara berulang.
public class AudioManager {

    public static void playBGM(String filePath) {
        try {
            // Dapatkan URL dari file audio di dalam folder assets
            URL audioUrl = AudioManager.class.getResource(filePath);
            if (audioUrl == null) {
                System.err.println("File audio tidak ditemukan di path: " + filePath);
                return;
            }

            // Dapatkan stream audio dari URL
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioUrl);

            // Dapatkan clip (objek untuk memutar audio)
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);

            // Atur agar musik berulang terus-menerus
            clip.loop(Clip.LOOP_CONTINUOUSLY);

            // Mulai putar musik
            clip.start();

        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("Error saat memutar audio: " + e.getMessage());
            e.printStackTrace();
        }
    }
}