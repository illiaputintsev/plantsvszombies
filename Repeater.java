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
public class Repeater extends Plant
{
    private static final double COOLDOWN = 1.4;
    private static final double BURST_DELAY = 0.15;
    private int shootPhase = 0;

    /**
     * Constructor for objects of class Repeater
     */
    public Repeater(int row, int col)
    {
        super(100, 200, row, col);
        this.timer = COOLDOWN - 0.2;
    }

    /**
     * Called every game tick
     * Only shoots if a zombie is in the same row
     */
    @Override
    public void act(List<Entity> entities, List<Bullet> bullets, Game game, double deltaTime){
        if (!alive) return;

        timer += deltaTime;

        boolean zombieInRange = false;
        for (Entity entity : entities) {
            if (entity instanceof Zombie) {
                Zombie zombie = (Zombie) entity;
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
            SoundManager.playShoot1();
        } else if (shootPhase == 1 && timer >= COOLDOWN + BURST_DELAY) {
            bullets.addAll(shoot());
            timer = 0;
            shootPhase = 0;
            SoundManager.playShoot2();
        }
    }

    /**
     * creates and returns a Bullet at the Repeater's location
     */
    public List<Bullet> shoot() {
        List<Bullet> bullets = new ArrayList<>();
        
        if (shootPhase == 0) {
        // first shot from back head
        bullets.add(new Bullet(x + 18, y - 21));        
        } 
        else {
            // second shot from front head
        bullets.add(new Bullet(x + 30, y));        
        }
    
        return bullets;
    }

    public void draw(GraphicsContext gc) {
        // shadow
        gc.setFill(Color.rgb(0, 0, 0, 0.15));
        gc.fillOval(x - 20, y + 18, 40, 10);
    
        // main stem
        gc.setFill(Color.FORESTGREEN);
        gc.fillRoundRect(x - 3, y + 4, 6, 20, 6, 6);
    
        // leaves
        gc.setFill(Color.LIMEGREEN);
        gc.fillOval(x - 18, y + 10, 16, 10);
        gc.fillOval(x + 2, y + 10, 16, 10);
    
        // -------------------------
        // SINGLE CURVED STEM (connected)
        // -------------------------
        gc.setStroke(Color.FORESTGREEN);
        gc.setLineWidth(4);
        
        // start lower + slightly right → connects to stem center
        gc.strokeArc(x - 14, y - 12, 30, 20, 165, 85, javafx.scene.shape.ArcType.OPEN);

        gc.setLineWidth(1);
    
        // -------------------------
        // HEAD POSITIONS (along same stem)
        // -------------------------
        double bx = x - 6;
        double by = y - 10;
    
        double fx = x + 6;
        double fy = y - 0;
    
        // -------------------------
        // BACK HEAD
        // -------------------------
        gc.setFill(Color.YELLOWGREEN);
        gc.fillOval(bx - 18, by - 24, 30, 30);
    
        gc.fillRoundRect(bx + 4, by - 16, 16, 12, 6, 6);
        gc.fillOval(bx + 15, by - 18, 10, 14);
    
        gc.setFill(Color.BLACK);
        gc.fillOval(bx + 19, by - 16, 6, 11);
    
        gc.setFill(Color.rgb(255, 255, 255, 0.20));
        gc.fillOval(bx - 10, by - 20, 8, 6);
    
        // -------------------------
        // FRONT HEAD
        // -------------------------
    
        gc.setFill(Color.YELLOWGREEN);
        gc.fillOval(fx - 18, fy - 24, 30, 30);
    
        gc.fillRoundRect(fx + 4, fy - 16, 16, 12, 6, 6);
        gc.fillOval(fx + 15, fy - 18, 10, 14);
    
        gc.setFill(Color.BLACK);
        gc.fillOval(fx + 19, fy - 16, 6, 11);
    
        gc.setFill(Color.WHITE);
        gc.fillOval(fx - 7, fy - 17, 10, 12);
        gc.setFill(Color.BLACK);
        gc.fillOval(fx - 2, fy - 12, 4, 6);
    
        gc.setFill(Color.rgb(255, 255, 255, 0.28));
        gc.fillOval(fx - 10, fy - 20, 8, 6);
        
        // Back Head Eyes
        gc.setFill(Color.WHITE);
        gc.fillOval(bx - 7, by - 17, 10, 12);
        gc.setFill(Color.BLACK);
        gc.fillOval(bx - 2, by - 12, 4, 6);
    }
    
    public static void drawIcon(GraphicsContext gc, double cx, double cy, double scale, boolean headOnly) {
        gc.save();
        gc.translate(cx, cy);
        gc.scale(scale, scale);
    
        if (!headOnly) {
            gc.setFill(Color.rgb(0, 0, 0, 0.15));
            gc.fillOval(-20, 18, 40, 10);
            gc.setFill(Color.FORESTGREEN);
            gc.fillRoundRect(-3, 4, 6, 20, 6, 6);
            gc.setFill(Color.LIMEGREEN);
            gc.fillOval(-18, 10, 16, 10);
            gc.fillOval(2, 10, 16, 10);
        }
    
        double bx = -5, by = -8;
        gc.setFill(Color.YELLOWGREEN);
        gc.fillOval(bx - 11, by - 14, 20, 20);
        gc.fillRoundRect(bx + 3, by - 8, 10, 7, 4, 4);
        gc.fillOval(bx + 10, by - 10, 6, 9);
        gc.setFill(Color.BLACK);
        gc.fillOval(bx + 13, by - 8, 3, 6);
        gc.setFill(Color.WHITE);
        gc.fillOval(bx - 4, by - 9, 6, 7);
        gc.setFill(Color.BLACK);
        gc.fillOval(bx - 1, by - 6, 2, 3);
    
        double fx = 4, fy = 1;
        gc.setFill(Color.YELLOWGREEN);
        gc.fillOval(fx - 11, fy - 14, 20, 20);
        gc.fillRoundRect(fx + 3, fy - 8, 10, 7, 4, 4);
        gc.fillOval(fx + 10, fy - 10, 6, 9);
        gc.setFill(Color.BLACK);
        gc.fillOval(fx + 13, fy - 8, 3, 6);
        gc.setFill(Color.WHITE);
        gc.fillOval(fx - 4, fy - 9, 6, 7);
        gc.setFill(Color.BLACK);
        gc.fillOval(fx - 1, fy - 6, 2, 3);
    
        gc.restore();
    }
}