import java.util.List;
import javafx.scene.canvas.GraphicsContext;
/**
 * Write a description of class Sunflower here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class Sunflower extends Plants
{
    private int sunAmount;
    private static final int COOLDOWN = 300;
    
    /**
     * Constructor for objects of class Sunflower
     */
    public Sunflower(int row, int col)
    {
        super(100, 50, row, col); // 100 -> hp, 50 -> cost
        this.cooldown = COOLDOWN;
        this.sunAmount =25;
    }

    @Override
    public void act(List<Entity> entities, List<Bullet> bullets, Game game){
        if (!alive) return;
        
        timer++;
        if (timer >= cooldown) {
            game.addSun(sunAmount);
            timer = 0;
        }
    }
    
    public void draw(GraphicsContext gc){
    // TODO: draw plant sprite
    }
}