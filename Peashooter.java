import java.util.List;
import java.util.ArrayList;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 *  Represents a Peashooter plant that shoots a single bullet at zombies
 *  in the same row within a range of 5 columns.
 */
public class Peashooter extends Plant
{
    private static final double COOLDOWN = 1.4;

    /**
     * Creates a Peashooter at the given grid position.
     * @param row the grid row
     * @param col the grid column
     */
    public Peashooter(int row, int col)
    {
        super(100, 100, row, col);
        this.timer = COOLDOWN - 0.2; //FIX: fired straight away after planting
    }

    /**
     * Fires a single pea if a zombie is ahead in the same row.
     */
    @Override
    public void act(List<Entity> entities, List<Bullet> bullets, Game game, double deltaTime){
        if (!alive) return;

        timer += deltaTime;
        if (timer < COOLDOWN) return;

        // check if any zombie is in the same row and ahead of us
        for (Entity entity : entities) {
            if (entity instanceof Zombie) {
                Zombie zombie = (Zombie) entity;
                if (zombie.isAlive() && zombie.getRow() == this.getRow()
                    && zombie.getX() > this.getX())
                {
                    bullets.addAll(shoot());
                    timer = 0;
                    SoundManager.playShoot1();
                    return;
                }
            }
        }
    }

    /**
     * Creates a single bullet at the Peashooter's snout position.
     * @return list containing one bullet
     */
    public List<Bullet> shoot(){
        List<Bullet> bullets = new ArrayList<>();
        bullets.add(new Bullet(this.getX() + 20, this.getY() - 10));
        return bullets;
    }

    /**
     * Draws the peashooter with head, snout, stem, and leaves.
     * @param gc the graphics context to draw on
     */
    public void draw(GraphicsContext gc) {
        // shadow
        gc.setFill(Color.rgb(0, 0, 0, 0.15));
        gc.fillOval(x - 18, y + 18, 36, 10);
    
        // stem
        gc.setFill(Color.FORESTGREEN);
        gc.fillRoundRect(x - 4, y - 2, 8, 30, 6, 6);
    
        // leaves
        gc.setFill(Color.LIMEGREEN);
        gc.fillOval(x - 18, y + 8, 16, 10);
        gc.fillOval(x + 2, y + 8, 16, 10);
    
        // head
        gc.setFill(Color.YELLOWGREEN);
        gc.fillOval(x - 18, y - 24, 30, 30);
    
        // snout

            // base rectangle
            gc.setFill(Color.YELLOWGREEN);
            gc.fillRoundRect(x + 4, y - 16, 16, 12, 6, 6);
            
            // front circle
            gc.fillOval(x + 15, y - 18, 10, 14);
            
            // inner hole
            gc.setFill(Color.BLACK);
            gc.fillOval(x + 19, y - 16, 6, 11);
    
        // eye
        gc.setFill(Color.WHITE);
        gc.fillOval(x - 7, y - 17, 10, 12);
        gc.setFill(Color.BLACK);
        gc.fillOval(x - 2, y - 12, 4, 6);
    
        // highlight
        gc.setFill(Color.rgb(255, 255, 255, 0.28));
        gc.fillOval(x - 10, y - 20, 8, 6);
    }
    
    /**
     * Draws a reusable peashooter icon at the given position and scale.
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
            gc.fillRoundRect(-4, -2, 8, 30, 6, 6);
            gc.setFill(Color.LIMEGREEN);
            gc.fillOval(-18, 8, 16, 10);
            gc.fillOval(2, 8, 16, 10);
        }
    
        gc.setFill(Color.YELLOWGREEN);
        gc.fillOval(-18, -24, 30, 30);
    
        gc.setFill(Color.YELLOWGREEN);
        gc.fillRoundRect(4, -16, 16, 12, 6, 6);
        gc.fillOval(15, -18, 10, 14);
    
        gc.setFill(Color.BLACK);
        gc.fillOval(19, -16, 6, 11);
    
        gc.setFill(Color.WHITE);
        gc.fillOval(-7, -17, 10, 12);
        gc.setFill(Color.BLACK);
        gc.fillOval(-2, -12, 4, 6);
    
        gc.setFill(Color.rgb(255, 255, 255, 0.28));
        gc.fillOval(-10, -20, 8, 6);
    
        gc.restore();
    }
}