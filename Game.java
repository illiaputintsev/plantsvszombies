import java.util.List;
import java.util.ArrayList;

/**
 * Write a description of class Game here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class Game extends Board
{
    List<Zombies> Zombie;
    List<Plants> Plant;
    List<Bullet> bullets;
    int sun;
    boolean gameRunning;
    int time;

    /**
     * Constructor for objects of class Game
     */
    public Game()
    {
        bullets = new ArrayList<>();
    }

    public void startGame(){
        
    }
    
    public void update(){
        for (Plants plant: Plant){
            bullets.addAll(plant.shoot());
        }
    }
    
    public void genFallingSun(){
        // generates the sun that falls from the sky
    }
    
    private void checkCollision(){
        
    }
}