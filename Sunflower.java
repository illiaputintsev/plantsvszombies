import java.util.List;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Write a description of class Sunflower here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class Sunflower extends Plants
{
    private int sunAmount;
    private static final double COOLDOWN = 12;

    /**
     * Constructor for objects of class Sunflower
     */
    public Sunflower(int row, int col)
    {
        super(100, 50, row, col);
        this.cooldown = COOLDOWN;
        this.sunAmount = 25;
        this.timer = COOLDOWN - 1; // produces sun after ~1 second
    }

    @Override
    public void act(List<Entity> entities, List<Bullet> bullets, Game game, double deltaTime){
        if (!alive) return;

        timer += deltaTime;
        if (timer >= cooldown) {
            game.spawnSun(x, y, sunAmount, false);
            timer = 0;
        }
    }

    // Sunflower.java
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
}