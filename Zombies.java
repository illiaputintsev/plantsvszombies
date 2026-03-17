import java.util.List;
/**
 * Write a description of class Zombies here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public abstract class Zombies extends Entity
{
    private int speed;

    /**
     * Constructor for objects of class Zombies
     */
    public Zombies(int hp, int cost, boolean alive)
    {
        super(hp, cost, alive);
        cost = 0;
        hp = 125;
        alive = true;        
    }

    private void move(){
        
    }
    
    private void attack(Plants plant){
        
    }
    
    abstract public void act(List<Zombies> newZombie);
}