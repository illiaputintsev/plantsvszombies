import java.util.List;

public class BasicZombie extends Zombie
{
    public static final int HP = 200;

    /**
     * Creates a basic zombie in the given row.
     * @param row the lane row
     * @param col starting grid column
     */
    public BasicZombie(int row, int col)
    {
        super(HP, row, col, 24); // 30 pixels per second
    }

    /**
     * Moves forward and eats any plant in its path.
     */
    @Override
    public void act(List<Plant> plants, List<Zombie> allZombies, double deltaTime)
    {
        if (!alive) return;

        boolean moved = move(deltaTime, plants, allZombies);

        if (!moved) {
            attack(deltaTime, plants);
            SoundManager.playEating();
        }
    }
}