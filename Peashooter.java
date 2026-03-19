import java.util.List;

/**
 *
 */
public class Peashooter extends Plants
{
    private static final int COOLDOWN = 300;
    private int timer;
    
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
    public void act(List<Entity> entities){
        if (!alive) { return; }
        
        timer ++;
        for (Entity entity : entities) {
            if (entity instanceof Zombies){
                Zombies zombie = (Zombies) entity;
                zombie.getRow();
                zombie.getCol();
            
                if (timer >= COOLDOWN && zombie.getRow() == this.getRow() && zombie.getCol() > this.getCol() 
                && zombie.getCol() <= this.getCol() + 5 && zombie.isAlive())
                {
                    shoot();
                }
        }
        }
    }
    
    /**
     * creates and returns a Bullet at the Peashooter`s location
     */
    public Bullet shoot(){
        return new Bullet(this.getCol(), this.getRow());
    }
}