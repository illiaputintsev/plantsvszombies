import java.util.List;

/**
 * Write a description of class Sunflower here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class Sunflower extends Plants
{
    private int sunAmount;
    private static final int COOLDOWN = 300;
    private int timer;
    
    /**
     * Constructor for objects of class Sunflower
     */
    public Sunflower(int row, int col)
    {
        super(100, 50, row, col); // 100 -> hp, 50 -> cost
        this.cooldown = 300;
    }

    @Override
    public void act(List<Entity> entities){
        if (!alive) return;
        
        timer++;
        if (timer >= cooldown) {
            generateSun();
            timer = 0;
        }
    }
    
    public void generateSun(){
        System.out.println("Sunflower produced " + sunAmount + " sun");
        // Game.addSun(sunAmount);
    }
}