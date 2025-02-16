package chess.gui.utils;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.InputStream;

public class SoundUtils {

    public static void playSound(final String url) {
        try {
            Clip clip = AudioSystem.getClip();
            InputStream io = SoundUtils.class.getClassLoader().getResourceAsStream(url);
            AudioInputStream inputStream = AudioSystem.getAudioInputStream(io);
            clip.open(inputStream);
            clip.start();
        } catch (Exception e) {
            System.err.println(e);
        }
    }
}
