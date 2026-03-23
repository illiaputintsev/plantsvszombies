import java.util.List;

public class BasicZombie extends Zombies
{
    public static final int HP = 125;
    public BasicZombie(int row, int col)
    {
        super(HP, row, col);
    }

    @Override
    public void act(List<Plants> plants, List<Zombies> newZombies)
    {
        if (!alive) return;

        boolean moved = move(plants);

        if (!moved) {
            attack(plants);
        }
    }
}