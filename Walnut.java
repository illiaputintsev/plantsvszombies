import java.util.List;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * A defensive plant with high health that blocks zombies from advancing.
 * Does not attack — absorbs damage while other plants deal with threats.
 * Visual cracks appear as it takes damage to indicate remaining health.
 */
public class Walnut extends Plant
{
    /**
     * Creates a Walnut with high HP at the given grid position.
     * @param row the grid row
     * @param col the grid column
     */
    public Walnut(int row, int col)
    {
        super(1000, 50, row, col);
    }

    /**
     * Walnut has no active behaviour — it simply absorbs damage.
     */
    @Override
    public void act(List<Entity> entities, List<Bullet> bullets, Game game, double deltaTime) {
        if (!alive) return;
    }

    /**
     * Draws the walnut with crack detail based on remaining HP.
     * @param gc the graphics context to draw on
     */ 
    public void draw(GraphicsContext gc) {
        // shadow
        gc.setFill(Color.rgb(0, 0, 0, 0.12));
        gc.fillOval(x - 20, y + 20, 40, 10);
    
        // main body
        gc.setFill(Color.rgb(190, 150, 70));
        gc.fillOval(x - 21, y - 30, 42, 56);
    
        // inner face area
        gc.setFill(Color.rgb(210, 170, 90));
        gc.fillOval(x - 13, y - 14, 26, 30);
    
        // eyes (longer / oval)
        gc.setFill(Color.rgb(250, 245, 220));
        gc.fillOval(x - 12, y - 14, 12, 16); // taller eyes
        gc.fillOval(x + 2, y - 14, 12, 16);
    
        // pupils
        gc.setFill(Color.BLACK);
        gc.fillOval(x - 6, y - 10, 5, 5);
        gc.fillOval(x + 8, y - 10, 5, 5);
    
        gc.setStroke(Color.rgb(90, 70, 30));
        gc.setLineWidth(1.3);
        
        if (hp >= maxHp * 0.6) {
            // happy
            gc.strokeArc(x - 4, y + 3, 8, 4, 180, 180, javafx.scene.shape.ArcType.OPEN);
        } else if (hp >= maxHp * 0.3) {
            // neutral
            gc.strokeLine(x - 4, y + 5, x + 4, y + 5);
        } else {
            // sad
            gc.strokeArc(x - 4, y + 5, 8, 4, 0, 180, javafx.scene.shape.ArcType.OPEN);
        }
        
        gc.setLineWidth(1);
    
        gc.setStroke(Color.rgb(110, 80, 35));
        gc.setLineWidth(1.5);
        
        // main crack
        if (hp < maxHp * 0.6) {
            gc.strokeLine(x - 11, y - 24, x - 7, y - 18);
            gc.strokeLine(x - 7, y - 18, x - 12, y - 12);
            gc.strokeLine(x - 12, y - 12, x - 9, y - 6);
        }
        
        // extended + branching crack (more obvious damage)
        if (hp < maxHp * 0.3) {
            // extend downward (main crack grows)
            gc.strokeLine(x - 9, y - 6, x - 13, y + 2);
        
            // strong branch (clearly visible split)
            gc.strokeLine(x - 7, y - 18, x - 2, y - 14);
            gc.strokeLine(x - 2, y - 14, x - 6, y - 8);
        }
    
        gc.setLineWidth(1);
    }
    
    /**
     * Draws a reusable walnut icon at the given position and scale.
     * @param gc the graphics context to draw on
     * @param cx centre x position
     * @param cy centre y position
     * @param scale size multiplier (1.0 = full size)
     * @param headOnly true to skip the shadow (used for shop icons)
     */
    public static void drawIcon(GraphicsContext gc, double cx, double cy, double scale, boolean headOnly) {
        gc.save();
        gc.translate(cx, cy);
        gc.scale(scale, scale);
    
        if (!headOnly) {
            gc.setFill(Color.rgb(0, 0, 0, 0.12));
            gc.fillOval(-20, 20, 40, 10);
        }
    
        gc.setFill(Color.rgb(190, 150, 70));
        gc.fillOval(-21, -30, 42, 56);
    
        gc.setFill(Color.rgb(210, 170, 90));
        gc.fillOval(-13, -14, 26, 30);
    
        gc.setFill(Color.rgb(250, 245, 220));
        gc.fillOval(-12, -14, 12, 16);
        gc.fillOval(2, -14, 12, 16);
    
        gc.setFill(Color.BLACK);
        gc.fillOval(-6, -10, 5, 5);
        gc.fillOval(8, -10, 5, 5);
    
        gc.setStroke(Color.rgb(90, 70, 30));
        gc.setLineWidth(1.3);
        gc.strokeArc(-4, 3, 8, 4, 180, 180, javafx.scene.shape.ArcType.OPEN);
        gc.setLineWidth(1);
    
        gc.restore();
    }
}