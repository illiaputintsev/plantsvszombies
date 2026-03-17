import java.util.List;

/**
 * Write a description of class Peashooter here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class Peashooter extends Plants
{
    /**
     * Constructor for objects of class Peashooter
     */
    public Peashooter(int hp, int cost, boolean alive)
    {
        super(hp, cost, alive);
        hp = 75;
        cost = 100;
        alive = true;
    }
    
    public void act(List<Plants> newPeashooter){
        
    }
}