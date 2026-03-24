import java.util.List;
import javafx.scene.canvas.GraphicsContext;
/**
 * Write a description of class Wallnut here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class Walnut extends Plants
{
    
    /**
     * Constructor for objects of class Wallnut
     */
    public Walnut(int row, int col)
    {
        super(200, 50, row, col);
    }
    
    @Override
    public void act(List<Entity> entities, List<Bullet> bullets, Game game) {
        if (!alive) return;
    }
    
    public void draw(GraphicsContext gc){
    // TODO: draw plant sprite
    }
}