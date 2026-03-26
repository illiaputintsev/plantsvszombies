import java.util.List;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Write a description of class Wallnut here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class Walnut extends Plants
{
    /**
     * Constructor for objects of class Wallnut
     */
    public Walnut(int row, int col)
    {
        super(1000, 50, row, col);
    }

    @Override
    public void act(List<Entity> entities, List<Bullet> bullets, Game game) {
        if (!alive) return;
    }

    public void draw(GraphicsContext gc) {
        // main body
        gc.setFill(Color.PERU);
        gc.fillOval(x - 18, y - 26, 36, 52);

        // face area
        gc.setFill(Color.SANDYBROWN);
        gc.fillOval(x - 12, y - 16, 24, 32);

        // eyes
        gc.setFill(Color.BLACK);
        gc.fillOval(x - 7, y - 10, 5, 6);
        gc.fillOval(x + 3, y - 10, 5, 6);

        // mouth
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1.5);
        gc.strokeLine(x - 4, y + 4, x + 4, y + 4);

        // cracks when damaged
        if (hp < maxHp * 0.6) {
            gc.setStroke(Color.SADDLEBROWN);
            gc.setLineWidth(2);
            gc.strokeLine(x - 10,y - 18, x - 4, y);
            gc.strokeLine(x + 8, y - 14, x + 12, y + 6);
        }
        if (hp < maxHp * 0.3) {
            gc.strokeLine(x - 2, y - 22,x + 6,y - 6);
            gc.strokeLine(x - 12, y + 2, x - 6, y + 16);
        }
        gc.setLineWidth(1);
    }
}