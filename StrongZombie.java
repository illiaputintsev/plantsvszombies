import java.util.List;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class StrongZombie extends Zombie
{
    public static final int HP = 400;

    public StrongZombie(int row, int col)
    {
        super(HP, row, col, 22); // slightly slower than basic
    }

    @Override
    public void act(List<Plant> plants, List<Zombie> allZombies, double deltaTime)
    {
        if (!alive) return;

        boolean moved = move(deltaTime, plants, allZombies);

        if (!moved) {
            attack(deltaTime, plants);
        }
    }

    @Override
    public void draw(GraphicsContext gc)
    {
        super.draw(gc);

        gc.setFill(Color.ORANGE);
        double[] xPoints = {x - 8, x + 2, x + 12};
        double[] yPoints = {y - 32, y - 50, y - 32};
        gc.fillPolygon(xPoints, yPoints, 3);

        gc.setStroke(Color.DARKORANGE);
        gc.setLineWidth(1);
        gc.strokeLine(x - 3, y - 36, x + 7, y - 36);
        gc.strokeLine(x - 1, y - 42, x + 5, y - 42);
    }
}
