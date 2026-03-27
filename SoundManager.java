import javafx.scene.media.AudioClip;
import java.net.URL;

/**
 * Handles loading and playing sound effects.
 * 
 * @author Mark Tarnavskyi
 */
public class SoundManager {
    private static AudioClip showelSound;
    private static AudioClip plantSound;
    private static AudioClip menuBtn;
    private static AudioClip zombieS1;
    private static AudioClip zombieS2;
    private static AudioClip menuTheme;
    private static AudioClip levelTheme;
    private static AudioClip peaShoot1;
    private static AudioClip peaShoot2;
    private static AudioClip gameOver;

    public static void init() {
        showelSound = loadSound("/sounds/showelOut.wav");
        plantSound = loadSound("/sounds/plantingSound.wav");
        menuBtn = loadSound("/sounds/menuButton.wav");
        menuTheme = loadSound("/sounds/theme_menu.wav");
        levelTheme = loadSound("/sounds/theme_level.wav"); 
        zombieS1 = loadSound("/sounds/zombieRoam1.wav");
        zombieS2 = loadSound("/sounds/zombieRoam2.wav");
        peaShoot1 = loadSound("/sounds/peashoot1.wav");
        peaShoot2 = loadSound("/sounds/peashoot2.wav");
        gameOver = loadSound("/sounds/gameover.wav");
    }

    private static AudioClip loadSound(String path) {
        try {
            URL resource = SoundManager.class.getResource(path);
            if (resource != null) {
                return new AudioClip(resource.toString());
            } else {
                System.out.println("Could not find sound file: " + path);
            }
        } catch (Exception e) {
            System.out.println("Error loading sound: " + path);
        }
        return null;
    }

    public static void playShowel() {
        showelSound.play(0.2);
    }

    public static void playPlant() {
        plantSound.play(0.2);
    }
    
    public static void playZombie1() {
        zombieS1.play();
    }

    public static void playZombie2() {
        zombieS2.play();
    }
    
    public static void playMenuBtn() {
        menuBtn.play();
    }
    
    public static void playMenuTheme() {
        if (!(menuTheme.isPlaying())) {
            menuTheme.play();
        }
    }
    
    public static void stopMenuTheme() {
        menuTheme.stop();
    }
    
    public static void playLevelTheme() {
        if (!(levelTheme.isPlaying())) {
            levelTheme.play();
        }
    }
    
    public static void stopLevelTheme() {
        levelTheme.stop();
    }
    
    public static void playGameOver() {
        gameOver.play();
    }
    
    public static void stopGameOver() {
        gameOver.stop();
    }
    
    public static void playShoot1() {
        peaShoot1.play();
    }
    
    public static void playShoot2() {
        peaShoot2.play();
    }

}