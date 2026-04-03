import javafx.scene.paint.Color;
import javafx.scene.canvas.GraphicsContext;

/**
 *  Game class will keep track of bullets 
 */
public class Bullet
{
    public static final Color COLOR = Color.GREEN;
    public static final int SIZE = 15;
    private double x, y;
    private double dx = 1;
    private double row, col;
    private boolean hit = false;
    
    /**
     * Creates a bullet at the given pixel position.
     * @param x starting x coordinate
     * @param y starting y coordinate
     */
    public Bullet(double x, double y)
    {
        this.x = x;
        this.y = y;
    }

    /**
     * Moves the bullet to the right each frame.
     * @param deltaTime seconds since last frame
     */
    public void update(double deltaTime){
        
        double speed = 300;
        x = x + (dx * speed * deltaTime);
        
    }
    
    /**
     * Checks whether the bullet is still active and on screen.
     * @return true if the bullet has not hit anything and is within bounds
     */
    public boolean onScreen(){
        return !hit && x < Game.WIDTH;
    }
    
    /**
     * Returns the bullet's current pixel coordinates.
     * @return array of [x, y]
     */
    public double[] getPos(){
        return new double[]{x, y};
    }
    
    /**
     * Checks for collision with a zombie using pixel distance.
     * @param zombie the zombie to test against
     * @return true if the bullet hit the zombie
     */
    public boolean contact(Zombie zombie){
        if (Math.abs(x - zombie.getX() - Game.CELL_W * 0.5) < Game.CELL_W * 0.5 && Math.abs(y - zombie.getY()) < Game.CELL_H * 0.4){
            hit = true;
            SoundManager.playHitSound();
            return true;
        }
        return false;
    }
    
    /**
     * Draws the bullet as a green circle.
     * @param gc the graphics context to draw on
     */
    public void draw(GraphicsContext gc){
        gc.setFill(COLOR);
        gc.fillOval(x - SIZE / 2, y - SIZE / 2, SIZE, SIZE);
    }
}