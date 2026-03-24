import javafx.scene.paint.Color;
import javafx.scene.canvas.GraphicsContext;

/**
 *  Game class will keep track of bullets 
 */
public class Bullet
{
    public static final Color COLOR = Color.GREEN;
    public static final int SIZE = 7;
    private double x, y;
    private double dx = 1;
    private double row, col;
    private boolean hit = false;
    
    /**
     * Constructor for objects of class Bullet
     */
    public Bullet(double x, double y)
    {
        this.x = x;
        this.y = y;
    }

    /**
     *  Modifies the position of the bullet. 
     * 6 is the speed
     */
    public void update(){
        x = x + (dx * 6);
        
    }
    
    /**
     * Returns true if the projectile is on screen
     */
    public boolean onScreen(){
        return !hit && x < Game.WIDTH;
    }
    
    /**
     * Get the projectile coordinates
     */
    public double[] getPos(){
        return new double[]{x, y};
    }
    
    /**
     * Returns true if in contact with the Zombie
     * 
     * Mark's Update 24/03: collisions are checked using pixels instead of grid pos
     */
    public boolean contact(Zombies zombie){
        if (Math.abs(x - zombie.getX() - Game.CELL_W * 0.5) < Game.CELL_W * 0.5 && Math.abs(y - zombie.getY()) < Game.CELL_H * 0.4){
            hit = true;
            return true;
        }
        return false;
    }
    
    /**
     * Draw the bullet
     */
    public void draw(GraphicsContext gc){
        gc.setFill(COLOR);
        gc.fillOval(x - SIZE / 2, y - SIZE / 2, SIZE, SIZE);
    }
}