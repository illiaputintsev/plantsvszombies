import java.util.List;
import java.util.ArrayList;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Write a description of class Repeater here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class Repeater extends Plants
{
    private static final int COOLDOWN = 90;
    private static final int BURST_DELAY = 8;
    private int shootPhase = 0;

    /**
     * Constructor for objects of class Repeater
     */
    public Repeater(int row, int col)
    {
        super(100, 200, row, col);
        this.timer = COOLDOWN - 10;
    }

    /**
     * Called every game tick
     * Only shoots if a zombie is in the same row
     */
    @Override
    public void act(List<Entity> entities, List<Bullet> bullets, Game game){
        if (!alive) return;

        timer++;

        boolean zombieInRange = false;
        for (Entity entity : entities) {
            if (entity instanceof Zombies) {
                Zombies zombie = (Zombies) entity;
                if (zombie.isAlive()
                    && zombie.getRow() == this.getRow()
                    && zombie.getX() > this.getX())
                {
                    zombieInRange = true;
                    break;
                }
            }
        }

        if (!zombieInRange) return;

        if (shootPhase == 0 && timer >= COOLDOWN) {
            bullets.addAll(shoot());
            shootPhase = 1;
        } else if (shootPhase == 1 && timer >= COOLDOWN + BURST_DELAY) {
            bullets.addAll(shoot());
            timer = 0;
            shootPhase = 0;
        }
    }

    /**
     * creates and returns a Bullet at the Repeater's location
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

        // head (darker than peashooter)
        gc.setFill(Color.FORESTGREEN);
        gc.fillOval(x - 16, y - 22, 32, 32);

        // double barrel
        gc.setFill(Color.DARKGREEN);
        gc.fillRoundRect(x + 10, y - 16, 16, 8, 4, 4);
        gc.fillRoundRect(x + 10, y - 6, 16, 8, 4, 4);

        // eye
        gc.setFill(Color.WHITE);
        gc.fillOval(x - 4, y - 16, 12, 12);
        gc.setFill(Color.BLACK);
        gc.fillOval(x + 1, y - 13, 6, 6);
    }
}