import java.util.List;

public abstract class Zombies extends Entity
{
    public static final int ATTACK_DAMAGE = 25;
    protected static final int DEFAULT_MOVE_INTERVAL = 60;
    protected static final int DEFAULT_ATTACK_INTERVAL = 40;

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

        moveTimer++;
        if (moveTimer < moveCooldown) return false;

        moveTimer = 0;

        // Check whether the next tile (col - 1) has a plant in the same row.
        int nextCol = col - 1;
        boolean blocked = false;
        for (Plants p : plants) {
            if (p.isAlive() && p.getRow() == row && p.getCol() == nextCol) {
                blocked = true;
                break;
            }
        }

        if (!blocked) {
            col = nextCol;
            return true;
        }
        return false;
    }

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
    
    public abstract void act(List<Plants> plants, List<Zombies> newZombies);
}