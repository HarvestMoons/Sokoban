package lab10.Entity;

import java.io.File;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class Music {
    //音乐播放器
    public static void playBackgroundMusic() {
        String musicFile = "src/music/1.8b bgm.mp3";
        Media sound = new Media(new File(musicFile).toURI().toString());
        MediaPlayer mediaPlayer = new MediaPlayer(sound);
        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE); // 使音乐循环播放
        mediaPlayer.play();
    }

}
