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
        // draw the same body as BasicZombie
        super.draw(gc);

        double dx = x;
        double dy = y;
        gc.setFill(Color.ORANGE);
        double[] xPoints = {dx - 8, dx + 2, dx + 12};
        double[] yPoints = {dy - 32, dy - 50, dy - 32};
        gc.fillPolygon(xPoints, yPoints, 3);

        // cone stripes
        gc.setStroke(Color.DARKORANGE);
        gc.setLineWidth(1);
        gc.strokeLine(dx - 3, dy - 36, dx + 7, dy - 36);
        gc.strokeLine(dx - 1, dy - 42, dx + 5, dy - 42);
    }
}
