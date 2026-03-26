import java.util.List;
import java.util.ArrayList;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 *  Represents a Peashooter plant that shoots a single bullet at zombies
 *  in the same row within a range of 5 columns.
 */
public class Peashooter extends Plants
{
    private static final int COOLDOWN = 90;

    /**
     * Constructor for objects of class Peashooter
     */
    public Peashooter(int row, int col)
    {
        super(100, 100, row, col);
        this.timer = COOLDOWN - 10; //FIX: fired straight away after planting
    }

    /**
     * Called every game tick
     * Only shoots if a zombie is in the same row
     */
    @Override
    public void act(List<Entity> entities, List<Bullet> bullets, Game game){
        if (!alive) return;

        timer++;
        if (timer < COOLDOWN) return;

        // check if any zombie is in the same row and ahead of us
        for (Entity entity : entities) {
            if (entity instanceof Zombies) {
                Zombies zombie = (Zombies) entity;
                if (zombie.isAlive() && zombie.getRow() == this.getRow()
                    && zombie.getX() > this.getX())
                {
                    bullets.addAll(shoot());
                    timer = 0;
                    return;
                }
            }
        }
    }

    /**
     * creates and returns a Bullet at the Peashooter's location
     */
    public List<Bullet> shoot(){
        List<Bullet> bullets = new ArrayList<>();
        bullets.add(new Bullet(this.getX() + 20, this.getY() - 5));
        return bullets;
    }

    public void draw(GraphicsContext gc) {
        // stem
        gc.setFill(Color.GREEN);
        gc.fillRect(x - 4, y + 5, 8, 22);

        // head
        gc.setFill(Color.LIMEGREEN);
        gc.fillOval(x - 16, y - 22, 32, 32);

        // barrel
        gc.setFill(Color.DARKGREEN);
        gc.fillRoundRect(x + 10, y - 12, 16, 10, 4, 4);

        // eye
        gc.setFill(Color.WHITE);
        gc.fillOval(x - 4, y - 16, 12, 12);
        gc.setFill(Color.BLACK);
        gc.fillOval(x + 1, y - 13, 6, 6);
    }
}