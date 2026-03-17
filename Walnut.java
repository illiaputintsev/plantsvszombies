import java.util.List;

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
    public Walnut(int hp, int cost, boolean alive)//int HP, int cost)
    {
        super(hp, cost, alive);
        hp = 125;
        cost = 75;
        alive = true;
    }
    
    public void act(List<Plants> newWallnut) {
        takeDamage(); //if zombie attack
    }
}