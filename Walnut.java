import java.util.List;

/**
 * Write a description of class Wallnut here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class Walnut extends Plants
{
    private static final int COOLDOWN = 0;
    
    /**
     * Constructor for objects of class Wallnut
     */
    public Walnut(int col, int row)
    {
        super(200, 50, row, col);
    }
    
    @Override
    public void act(List<Plants> newWallnut) {
        if (!alive) return;
    }
}