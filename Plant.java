import java.util.List;
import java.util.ArrayList;
import javafx.scene.canvas.GraphicsContext;
/**
 * Abstract base class for all plants in the game.
 * Each plant has a sun cost, a cooldown timer, and can be placed on the grid.
 * Subclasses define specific behaviour such as shooting or producing suns.
 */
public abstract class Plant extends Entity
{
    protected double cooldown;
    protected int cost;
    protected double timer;
    
    /**
     * Creates a new plant with the given stats.
     * @param hp the plant's starting health
     * @param cost the sun cost to place this plant
     * @param row the grid row
     * @param col the grid column
     */
    public Plant(int hp, int cost, int row, int col)
    {
        super(hp, row, col, true);
        this.cost = cost;
        this.cooldown = 0;
        this.timer = 0;
    }

    /**
     * Fires projectiles. Overridden by shooting plants.
     * @return list of bullets produced (empty by default)
     */ 
    protected List<Bullet> shoot(){ return new ArrayList<>(); }
    
    /**
     * Performs this plant's action each frame (shooting, producing suns, etc.).
     * @param entities all entities on the board for targeting
     * @param bullets the shared bullet list to add projectiles to
     * @param game reference to the game for spawning suns etc.
     * @param deltaTime seconds since last frame
     */
    abstract public void act(List<Entity> entities, List<Bullet> bullets, Game game, double deltaTime);
    
    public int getCost() { return cost; }
    public double getCooldown() { return cooldown; }
    public double getTimer() { return timer; }
    
    /**
     * Draws this plant on the canvas.
     * @param gc the graphics context to draw on
     */
    public abstract void draw(GraphicsContext gc);
}