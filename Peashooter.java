import java.util.List;
import java.util.ArrayList;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 *  Represents a Peashooter plant that shoots a single bullet at zombies
 *  in the same row within a range of 5 columns.
 */
public class Peashooter extends Plants
{
    private static final int COOLDOWN = 300;
    
    /**
     * Constructor for objects of class Peashooter
     */
    public Peashooter(int row, int col)
    {
        super(100, 100, row, col);

    }
    
    /**
     * Called every game tick
     * Only shoots if a zombie is in the range of 5 coloums
     */
    @Override
    public void act(List<Entity> entities, List<Bullet> bullets, Game game){
        if (!alive) { return; }
        
        timer ++;
        for (Entity entity : entities) {
            if (entity instanceof Zombies){
                Zombies zombie = (Zombies) entity;
            
                if (timer >= COOLDOWN && zombie.getRow() == this.getRow() && zombie.getCol() >= this.getCol() 
                && zombie.getCol() <= this.getCol() + 5 && zombie.isAlive())
                {
                    bullets.addAll(shoot());
                    timer = 0;
                }
        }
        }
    }
    
    /**
     * creates and returns a Bullet at the Peashooter`s location
     */
    public List<Bullet> shoot(){
        List<Bullet> bullets = new ArrayList<>();
        bullets.add(new Bullet(this.getX(), this.getY()));
        return bullets;
    }
    
    
    public void draw(GraphicsContext gc){
        //change this to improve the visual design of the plant
        gc.setFill(Color.LIGHTGREEN);
        gc.fillOval(x - 25, y - 30, 50, 60);
        gc.setFill(Color.DARKGREEN);
        gc.fillOval(x - 8, y - 8, 16, 16);
    }
}