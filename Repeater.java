import java.util.List;
import java.util.ArrayList;
/**
 * Write a description of class Repeater here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class Repeater extends Plants
{
    private static final int COOLDOWN = 300;

    /**
     * Constructor for objects of class Repeater
     */
    public Repeater(int row, int col)
    {
        super(100, 100, row, col);
    }
    
    /**
     * Called every game tick
     * Only shoots if a zombie is in the range of 5 coloums
     */
    @Override
    public void act(List<Entity> entities){
        if (!alive) { return; }
        
        timer ++;
        for (Entity entity : entities) {
            if (entity instanceof Zombies){
                Zombies zombie = (Zombies) entity;
            
                if (timer >= COOLDOWN && zombie.getRow() == this.getRow() && zombie.getCol() > this.getCol() 
                && zombie.getCol() <= this.getCol() + 5 && zombie.isAlive())
                {
                    shoot();
                }
                if (timer >= COOLDOWN + 10 && zombie.getRow() == this.getRow() && zombie.getCol() > this.getCol() 
                && zombie.getCol() <= this.getCol() + 5 && zombie.isAlive())
                {
                    shoot();
                    timer = 0;
                }
        }
        }
    }
    
    /**
     * creates and returns a Bullet at the Repeater`s location
     */
    public List<Bullet> shoot(){
        List<Bullet> bullets = new ArrayList<>();
        bullets.add(new Bullet(this.getCol(), this.getRow()));
        return bullets;
    }
}