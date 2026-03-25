import java.util.List;
import java.util.ArrayList;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Write a description of class Repeater here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class Repeater extends Plants
{
    private static final int COOLDOWN = 300;
    private static final int BURST_DELAY = 10;
    private int shootPhase = 0;

    /**
     * Constructor for objects of class Repeater
     */
    public Repeater(int row, int col)
    {
        super(100, 200, row, col);
    }
    
    /**
     * Called every game tick
     * Only shoots if a zombie is in the range of 5 coloums
     */
    @Override
    public void act(List<Entity> entities, List<Bullet> bullets, Game game){
        if (!alive) { return; }
        
        timer ++;
        
        boolean zombieInRange = false;
        for (Entity entity : entities) {
            if (entity instanceof Zombies) {
                Zombies zombie = (Zombies) entity;
                if (zombie.isAlive()
                    && zombie.getRow() == this.getRow()
                    && zombie.getCol() >= this.getCol()
                    && zombie.getCol() <= this.getCol() + 5)
                {
                    zombieInRange = true;
                    break;
                }
            }
        }
 
        if (!zombieInRange) { return; }
 
        if (shootPhase == 0 && timer >= COOLDOWN) {
            bullets.addAll(shoot());
            shootPhase = 1;
        } else if (shootPhase == 1 && timer >= COOLDOWN + BURST_DELAY) {
            bullets.addAll(shoot());
            timer = 0;
            shootPhase = 0;
        }
    }
    
    
    /**
     * creates and returns a Bullet at the Repeater`s location
     */
    public List<Bullet> shoot(){
        List<Bullet> bullets = new ArrayList<>();
        bullets.add(new Bullet(this.getX(), this.getY()));
        return bullets;
    }
    
    public void draw(GraphicsContext gc){
        //change this to improve the visual design of the plant
        gc.setFill(Color.DARKGREEN);
        gc.fillOval(x - 25, y - 30, 50, 60);
        gc.setFill(Color.LIME);
        gc.fillOval(x - 14, y - 10, 12, 12);
        gc.fillOval(x + 2, y - 10, 12, 12);
    }
}