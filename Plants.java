import java.util.List;
/**
 * Write a description of class Plants here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public abstract class Plants extends Entity
{
    int cooldown;
    int cost;
    
    /**
     * Constructor for objects of class Plants
     */
    public Plants(int hp, int cost, boolean alive)
    {
        super(hp, cost, alive);
    }
    
    protected void shoot(){
        //shoots zombies
    }
    
    abstract public void act(List<Plants> newPlant);
    
    protected void projectile(){
        
    }
}