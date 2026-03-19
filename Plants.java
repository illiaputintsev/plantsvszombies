import java.util.List;
/**
 * Write a description of class Plants here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public abstract class Plants extends Entity
{
    protected int cooldown;
    protected int cost;
    protected int timer;
    
    /**
     * Constructor for objects of class Plants
     */
    public Plants(int hp, int cost, int row, int col)
    {
        super(hp, row, col, true);
        this.cost = cost;
        this.cooldown = 0;
        this.timer = 0;
    }
    
    protected void shoot(){
        //shoots zombies
    }
    
    abstract public void act(List<Plants> newPlant);
    
    protected void projectile(){
        
    }
    
    public int getCost() { return cost; }
    public int getCooldown() { return cooldown; }
    public int getTimer() { return timer; }
}