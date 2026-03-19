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
    public Peashooter(int row, int col)
    {
        super(100, 100, row, col);

    }
    
    @Override
    public void act(List<Plants> newPeashooter){
        if (!alive) { return; }
        
        
    }
}