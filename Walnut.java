import java.util.List;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

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
        super(1000, 50, row, col);
    }
    
    @Override
    public void act(List<Entity> entities, List<Bullet> bullets, Game game) {
        if (!alive) return;
    }
    
    public void draw(GraphicsContext gc){
        //change this to improve the visual design of the plant
        gc.setFill(Color.SADDLEBROWN);
        gc.fillRoundRect(x - 25, y - 35, 50, 70, 18, 18);
        gc.setFill(Color.PERU);
        gc.fillRoundRect(x - 16, y - 22, 32, 44, 10, 10);
    }
}