import javafx.scene.media.AudioClip;
import java.net.URL;
import java.util.Random;

/**
 * Handles loading and playing sound effects.
 * 
 * @author Mark Tarnavskyi
 */
public class SoundManager {
    private static Random random;
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
    private static AudioClip hitSound;
    private static AudioClip levelComplete;
    private static AudioClip eating;
    private static AudioClip swallow;
    private static AudioClip drums;
    private static AudioClip sunPicked;


    public static void init() {
        random = new Random();
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
        hitSound = loadSound("/sounds/hit.wav");
        levelComplete = loadSound("/sounds/level_complete.mp3");
        eating = loadSound("/sounds/eating.wav");
        swallow = loadSound("/sounds/swallow.wav");
        drums = loadSound("/sounds/drum_threat.wav");
        sunPicked = loadSound("/sounds/sun_pick_up.wav");
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

    public static void playDrums(){
        if (!drums.isPlaying()){
            drums.play(0.75);
        } 
    }
    
    public static void playSunPicked(){
        sunPicked.play(0.8);
    }
    
    public static void playEating(){
        if (!eating.isPlaying()){
            eating.play(0.5);
        }
    }
    
    public static void playSwallow() {
        swallow.play(0.5);
    }
    
    public static void playShowel() {
        showelSound.play(0.2);
    }

    public static void playPlant() {
        plantSound.play(0.2);
    }
    
    public static void playHitSound(){
        hitSound.play(0.1);
    }
    
    public static void playZombie() {
        if (random.nextInt(2) == 0) {
            if (!zombieS1.isPlaying()){
                zombieS1.play(0.4);
            }
            else if (!zombieS2.isPlaying()){
                zombieS2.play(0.6);
            }
        } else {
            if (!zombieS2.isPlaying()){
                zombieS2.play(0.5);
            }
            else if (!zombieS1.isPlaying()){
                zombieS1.play(0.45);
            }
        }
    }

    public static void playZombie2() {
        if (!zombieS2.isPlaying()){
            zombieS2.play();
        }
    }
    
    public static void playMenuBtn() {
        menuBtn.play();
    }
    
    public static void playMenuTheme() {
        if (!(menuTheme.isPlaying())) {
            menuTheme.play(0.5);
        }
    }
    
    public static void stopMenuTheme() {
        menuTheme.stop();
    }

    public static boolean isLevelThemePlaying(){
        return levelTheme.isPlaying();
    }
    
    public static void playLevelTheme() {
        if (!(levelTheme.isPlaying())) {
            levelTheme.play(0.75);
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
    
    public static void playLVLComplete() {
        levelComplete.play(0.5);
    }

}