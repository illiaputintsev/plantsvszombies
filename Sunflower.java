import java.util.List;

/**
 * Write a description of class Sunflower here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class Sunflower extends Plants
{
    /**
     * Constructor for objects of class Sunflower
     */
    public Sunflower(int hp, int cost, boolean alive)
    {
        super(hp, cost, alive);
        hp = 75;
        cost = 50;
        alive = true;
    }

    /**
     * An example of a method - replace this comment with your own
     *
     * @param  y  a sample parameter for a method
     * @return    the sum of x and y
     */
    public void act(List<Plants> newSunflower){
        generateSun();
    }
    
    public int generateSun(){
        return 50;
    }
}