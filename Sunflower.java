import java.util.List;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * A sun-producing plant that generates suns at regular intervals.
 * Essential for the player's economy — suns are used to buy other plants.
 */
public class Sunflower extends Plant
{
    private int sunAmount;
    private static final double COOLDOWN = 12;

    /**
     * Creates a Sunflower at the given grid position.
     * @param row the grid row
     * @param col the grid column
     */
    public Sunflower(int row, int col)
    {
        super(100, 50, row, col);
        this.cooldown = COOLDOWN;
        this.sunAmount = 25;
        this.timer = COOLDOWN - 1; // produces sun after ~1 second
    }

    /**
     * Produces a sun at regular intervals when alive.
     */
    @Override
    public void act(List<Entity> entities, List<Bullet> bullets, Game game, double deltaTime){
        if (!alive) return;

        timer += deltaTime;
        if (timer >= cooldown) {
            game.spawnSun(x, y, sunAmount, false);
            timer = 0;
        }
    }

    /**
     * Draws the sunflower with petals, face, and stem.
     * @param gc the graphics context to draw on
     */
    public void draw(GraphicsContext gc) {
            // shadow
            gc.setFill(Color.rgb(0, 0, 0, 0.15));
            gc.fillOval(x - 18, y + 18, 36, 10);
        
            // stem
            gc.setFill(Color.FORESTGREEN);
            gc.fillRoundRect(x - 3, y - 1, 6, 28, 6, 6);
        
            // leaves
            gc.setFill(Color.LIMEGREEN);
            gc.fillOval(x - 18, y + 8, 16, 10);
            gc.fillOval(x + 2, y + 8, 16, 10);
        
            // petals
        gc.setFill(Color.GOLD);
        for (int i = 0; i < 12; i++) {
            double angle = i * (Math.PI * 2 / 12.0);
            double px = x + Math.cos(angle) * 18;   // was ~15 → wider
            double py = y - 10 + Math.sin(angle) * 18;
            gc.fillOval(px - 7, py - 7, 14, 14);    // slightly bigger petals
        }
        
        // face 
        gc.setFill(Color.rgb(170, 120, 60)); // lighter than SADDLEBROWN
        gc.fillOval(x - 16, y - 24, 32, 28); // wider than tall
        
        // eyes 
        gc.setFill(Color.BLACK);
        gc.fillOval(x - 7, y - 14, 4, 6);
        gc.fillOval(x + 3, y - 14, 4, 6);
        
        // smile
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1.5);
        gc.strokeArc(x - 7, y - 8, 14, 8, 180, 180, javafx.scene.shape.ArcType.OPEN);
        
        gc.setLineWidth(1);
    }
    
    /**
     * Draws a reusable sunflower icon at the given position and scale.
     * @param gc the graphics context to draw on
     * @param cx centre x position
     * @param cy centre y position
     * @param scale size multiplier (1.0 = full size)
     * @param headOnly true to skip stem, leaves, and shadow
     */
    public static void drawIcon(GraphicsContext gc, double cx, double cy, double scale, boolean headOnly) {
        gc.save();
        gc.translate(cx, cy);
        gc.scale(scale, scale);
    
        if (!headOnly) {
            gc.setFill(Color.rgb(0, 0, 0, 0.15));
            gc.fillOval(-18, 18, 36, 10);
            gc.setFill(Color.FORESTGREEN);
            gc.fillRoundRect(-3, -1, 6, 28, 6, 6);
            gc.setFill(Color.LIMEGREEN);
            gc.fillOval(-18, 8, 16, 10);
            gc.fillOval(2, 8, 16, 10);
        }
    
        gc.setFill(Color.GOLD);
        for (int i = 0; i < 12; i++) {
            double a = i * (Math.PI * 2 / 12.0);
            double px = Math.cos(a) * 18;
            double py = -10 + Math.sin(a) * 18;
            gc.fillOval(px - 7, py - 7, 14, 14);
        }
    
        gc.setFill(Color.rgb(170, 120, 60));
        gc.fillOval(-16, -24, 32, 28);
    
        gc.setFill(Color.BLACK);
        gc.fillOval(-7, -14, 4, 6);
        gc.fillOval(3, -14, 4, 6);
    
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1.5);
        gc.strokeArc(-7, -8, 14, 8, 180, 180, javafx.scene.shape.ArcType.OPEN);
        gc.setLineWidth(1);
    
        gc.restore();
    }
}