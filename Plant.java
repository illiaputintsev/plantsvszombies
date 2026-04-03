import java.util.List;
import java.util.ArrayList;
import javafx.scene.canvas.GraphicsContext;
/**
 * Write a description of class Plants here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public abstract class Plant extends Entity
{
    protected double cooldown;
    protected int cost;
    protected double timer;
    
    /**
     * Constructor for objects of class Plants
     */
    public Plant(int hp, int cost, int row, int col)
    {
        super(hp, row, col, true);
        this.cost = cost;
        this.cooldown = 0;
        this.timer = 0;
    }
    
    protected List<Bullet> shoot(){ return new ArrayList<>(); }
    
    abstract public void act(List<Entity> entities, List<Bullet> bullets, Game game, double deltaTime);
    
    public int getCost() { return cost; }
    public double getCooldown() { return cooldown; }
    public double getTimer() { return timer; }
    
    public abstract void draw(GraphicsContext gc);
}