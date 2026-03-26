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
    private static final int COOLDOWN = 360;

    /**
     * Constructor for objects of class Sunflower
     */
    public Sunflower(int row, int col)
    {
        super(100, 50, row, col);
        this.cooldown = COOLDOWN;
        this.sunAmount = 25;
        this.timer = COOLDOWN - 60; // produces sun after ~1 second
    }

    @Override
    public void act(List<Entity> entities, List<Bullet> bullets, Game game){
        if (!alive) return;

        timer++;
        if (timer >= cooldown) {
            game.addSun(sunAmount);
            timer = 0;
        }
    }

    public void draw(GraphicsContext gc) {
        // stem
        gc.setFill(Color.GREEN);
        gc.fillRect(x - 3, y + 5, 6, 22);

        // petals
        gc.setFill(Color.YELLOW);
        for (int i = 0; i < 8; i++) {
            double angle = i * Math.PI / 4;
            double px = x + Math.cos(angle) * 14;
            double py = y - 8 + Math.sin(angle) * 14;
            gc.fillOval(px - 7, py - 7, 14, 14);
        }

        // center face
        gc.setFill(Color.SADDLEBROWN);
        gc.fillOval(x - 10, y - 18, 20, 20);

        // eyes
        gc.setFill(Color.BLACK);
        gc.fillOval(x - 6, y - 12, 4, 4);
        gc.fillOval(x + 2, y - 12, 4, 4);

        // smile
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1);
        gc.strokeArc(x - 4, y - 8, 8, 6, 180, 180, javafx.scene.shape.ArcType.OPEN);
    }
}