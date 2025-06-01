package soundEffects;

import javax.sound.sampled.*;
import java.net.URL;

public class SoundPlayer {

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

            //ustawianie glosnosci
            FloatControl volume = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            volume.setValue(-25.0f);//zakres -80.0f do 0.0f

            //granie dzwieku
            clip.start();

        } catch (Exception e) {
            System.out.println("Error playing sound");
        }


    }
}
