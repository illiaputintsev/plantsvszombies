import java.util.List;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public abstract class Zombie extends Entity
{
    public static final int ATTACK_DAMAGE = 25;
    protected double speed;
    protected boolean eating;
    protected double eatTimer;
    protected double attackInterval;
    protected double flashTimer;
    private static final double ZOMBIE_SPACING = 20; // min gap between zombies in same row

    public Zombie(int hp, int row, int col, double speed)
    {
        super(hp, row, col, true);
        this.speed = speed;
        this.eating = false;
        this.eatTimer = 0;
        this.attackInterval = 1.0;
        this.flashTimer = 0;
        this.x = Game.WIDTH + 20 + Math.random() * 30;
    }

    protected boolean move(double deltaTime, List<Plant> plants, List<Zombie> allZombies)
    {
        if (!alive) return false;

        for (Plant p : plants) {
            if (p.isAlive() && p.getRow() == row
                && Math.abs(x - p.getX()) < Game.CELL_W * 0.5)
            {
                eating = true;
                return false;
            }
        }

        eating = false;
        eatTimer = 0;

        double newX = x - speed * deltaTime;
        for (Zombie other : allZombies) {
            if (other != this && other.isAlive() && other.row == this.row
                && other.x < this.x)
            {
                double minX = other.x + ZOMBIE_SPACING;
                if (newX < minX) {
                    newX = minX;
                }
            }
        }

        x = newX;
        col = Game.pixelXToCol(x);
        return true;
    }

    /**
     * Attacks the plant on the zombie's current cell.
     */
    protected void attack(double deltaTime, List<Plant> plants)
    {
        if (!alive || !eating) return;

        eatTimer += deltaTime;
        if (eatTimer < attackInterval) return;
        eatTimer = 0;

        for (Plant p : plants) {
            if (p.isAlive() && p.getRow() == row
                && Math.abs(x - p.getX()) < Game.CELL_W * 0.5)
            {
                p.takeHit(ATTACK_DAMAGE);
                break;
            }
        }
    }

    public boolean hasReachedHouse()
    {
        return x < Game.GRID_X - 20;
    }

    public void triggerFlash() { flashTimer = 0.01; }

    public void updateFlash(double deltaTime) {
        if (flashTimer > 0) flashTimer -= deltaTime;
    }

    public boolean isFlashing() { return flashTimer > 0; }

    public boolean isEating() { return eating; }

    public int getCurrentCol()
    {
        return Game.pixelXToCol(x);
    }

    public void draw(GraphicsContext gc) {
        Zombie.drawBody(gc, x, y, 1.0, isFlashing());

        double barW = 28;
        gc.setFill(Color.DARKRED);
        gc.fillRect(x - barW / 2, y - 40, barW, 4);
        gc.setFill(Color.RED);
        gc.fillRect(x - barW / 2, y - 40, barW * hp / maxHp, 4);
    }
    
    /**
     * Draws a zombie body at a given position and scale (reusable across screens).
     * Use scale 1.0 for gameplay, smaller for menu/tutorial.
     */
    public static void drawBody(GraphicsContext gc, double cx, double cy, double scale, boolean flash) {
        gc.save();
        gc.translate(cx, cy);
        gc.scale(scale, scale);
    
        gc.setFill(flash ? Color.WHITE : Color.DARKSLATEGRAY);
        gc.fillRect(-8, 8, 7, 16);
        gc.fillRect(4, 8, 7, 16);
    
        gc.setFill(flash ? Color.WHITE : Color.DARKKHAKI);
        gc.fillRect(-10, -16, 24, 26);
    
        gc.setFill(flash ? Color.WHITE : Color.DARKSEAGREEN);
        gc.fillRect(-18, -8, 10, 6);
        gc.fillRect(14, -12, 10, 6);
    
        gc.setFill(flash ? Color.WHITE : Color.YELLOWGREEN);
        gc.fillOval(-10, -34, 22, 22);
    
        gc.setFill(flash ? Color.WHITE : Color.RED);
        gc.fillOval(-5, -28, 4, 4);
        gc.fillOval(4, -28, 4, 4);
    
        gc.restore();
    }

    public abstract void act(List<Plant> plants, List<Zombie> newZombies, double deltaTime);
}
