import java.util.List;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public abstract class Zombies extends Entity
{
    public static final int ATTACK_DAMAGE = 25;
    protected static final int DEFAULT_MOVE_INTERVAL = 200;
    protected static final int DEFAULT_ATTACK_INTERVAL = 60;

    protected int moveCooldown;
    protected int attackCooldown;
    protected int moveTimer;
    protected int attackTimer;

    public Zombies(int hp, int row, int col)
    {
        super(hp, row, col, true);
        this.moveCooldown   = DEFAULT_MOVE_INTERVAL;
        this.attackCooldown = DEFAULT_ATTACK_INTERVAL;
        this.moveTimer   = 0;
        this.attackTimer = 0;
    }

    protected boolean move(List<Plants> plants)
    {
        if (!alive) return false;

        // If there is a plant on the zombie's current cell, stay and eat it
        for (Plants p : plants) {
            if (p.isAlive() && p.getRow() == row && p.getCol() == col) {
                return false; // blocked — will attack this cell
            }
        }

        moveTimer++;
        if (moveTimer < moveCooldown) return false;

        moveTimer = 0;
        col = col - 1;
        x = Game.colToPixelX(col);
        return true;
    }

    /**
     * Attacks the plant on the zombie's current cell.
     */
    protected void attack(List<Plants> plants)
    {
        if (!alive) return;

        attackTimer++;
        if (attackTimer < attackCooldown) return;

        attackTimer = 0;

        for (Plants p : plants) {
            if (p.isAlive() && p.getRow() == row && p.getCol() == col) {
                p.takeDamage();
                break;   
            }
        }
    }

    public boolean hasReachedHouse()
    {
        return col < 0;
    }
    
    public void draw(GraphicsContext gc)
    {
        // Body
        gc.setFill(Color.LIGHTGRAY);
        gc.fillRect(x + 5, y - 15, 40, 55);
        // Head
        gc.setFill(Color.DARKGRAY);
        gc.fillOval(x + 9, y - 35, 32, 32);
        // HP bar background
        gc.setFill(Color.DARKRED);
        gc.fillRect(x + 5, y - 45, 40, 5);
        // HP bar fill — FIX: use maxHp instead of hardcoded BasicZombie.HP
        gc.setFill(Color.RED);
        gc.fillRect(x + 5, y - 45, 40.0 * hp / maxHp, 5);
    }
    
    public abstract void act(List<Plants> plants, List<Zombies> newZombies);
}