import java.util.List;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public abstract class Zombies extends Entity
{
    public static final int ATTACK_DAMAGE = 25;
    protected double speed;
    protected boolean eating;
    protected double eatTimer;
    protected double attackInterval;
    protected double flashTimer;

    public Zombies(int hp, int row, int col, double speed)
    {
        super(hp, row, col, true);
        this.speed = speed;
        this.eating = false;
        this.eatTimer = 0;
        this.attackInterval = 1.0;
        this.flashTimer = 0;
        // start at right edge of screen with small random offset so they don't overlap
        this.x = Game.WIDTH + 20 + Math.random() * 30;
        this.y = Game.rowToPixelY(row) + (Math.random() * 16 - 8);
    }

    protected boolean move(double deltaTime, List<Plants> plants)
    {
        if (!alive) return false;

        // If there is a plant on the zombie's current cell, stay and eat it
        for (Plants p : plants) {
            if (p.isAlive() && p.getRow() == row
                && Math.abs(x - p.getX()) < Game.CELL_W * 0.5)
            {
                eating = true;
                return false; // blocked — will attack this cell
            }
        }

        eating = false;
        eatTimer = 0;
        x -= speed * deltaTime;
        col = Game.pixelXToCol(x);
        return true;
    }

    /**
     * Attacks the plant on the zombie's current cell.
     */
    protected void attack(double deltaTime, List<Plants> plants)
    {
        if (!alive || !eating) return;

        eatTimer += deltaTime;
        if (eatTimer < attackInterval) return;
        eatTimer = 0;

        for (Plants p : plants) {
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

    public void draw(GraphicsContext gc)
    {
        boolean flash = isFlashing();

        // legs
        gc.setFill(flash ? Color.WHITE : Color.DARKSLATEGRAY);
        gc.fillRect(x - 8, y + 8, 7, 16);
        gc.fillRect(x + 4, y + 8, 7, 16);

        // body
        gc.setFill(flash ? Color.WHITE : Color.DARKKHAKI);
        gc.fillRect(x - 10, y - 16, 24, 26);

        // arms
        gc.setFill(flash ? Color.WHITE : Color.DARKSEAGREEN);
        gc.fillRect(x - 18, y - 8, 10, 6);
        gc.fillRect(x + 14, y - 12, 10, 6);

        // head
        gc.setFill(flash ? Color.WHITE : Color.YELLOWGREEN);
        gc.fillOval(x - 10, y - 34, 22, 22);

        // eyes
        gc.setFill(flash ? Color.WHITE : Color.RED);
        gc.fillOval(x - 5, y - 28, 4, 4);
        gc.fillOval(x + 4, y - 28, 4, 4);

        // HP bar background
        double barW = 28;
        gc.setFill(Color.DARKRED);
        gc.fillRect(x - barW / 2, y - 40, barW, 4);
        // HP bar fill
        gc.setFill(Color.RED);
        gc.fillRect(x - barW / 2, y - 40, barW * hp / maxHp, 4);
    }

    public abstract void act(List<Plants> plants, List<Zombies> newZombies, double deltaTime);
}
