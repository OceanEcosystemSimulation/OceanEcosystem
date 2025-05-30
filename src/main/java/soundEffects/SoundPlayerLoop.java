package soundEffects;

import javax.sound.sampled.*;
import java.io.File;
import java.net.URL;

public class SoundPlayerLoop {

    public static void playSound(String path) {

        try{
            //szuka pliku wewnątrz folderu resources
            URL soundURL = SoundPlayer.class.getClassLoader().getResource(path);


            if (soundURL == null) {
                System.out.println("Couldn't find sound file: " + path);
                return;
            }

            //wczytuje dzwiek z pliku
            AudioInputStream audio = AudioSystem.getAudioInputStream(soundURL);

            //obiekt ktory moze odtworzyc dzwiek
            Clip clip = AudioSystem.getClip();

            //laduje dane z pliku do obiektu clip
            clip.open(audio);

            //zapetla dzwiek
            clip.loop(Clip.LOOP_CONTINUOUSLY);

            //granie dzwieku
            clip.start();

        } catch (Exception e) {
            System.out.println("Error playing sound");
        }


    }
}
