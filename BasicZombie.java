import java.util.List;

public class BasicZombie extends Zombie
{
    public static final int HP = 200;

    public BasicZombie(int row, int col)
    {
        super(HP, row, col, 24); // 30 pixels per second
    }

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