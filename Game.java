import java.util.List;
import java.util.ArrayList;
import java.util.Random;

/**
 * Game class - controls the main game logic.
 * Keeps track of all plants, zombies, bullets, and game state.
 *
 * @author Illia
 * @version 1.0
 */
public class Game extends Board
{
    List<Zombies> Zombie;
    List<Plants> Plant;
    List<Bullet> bullets;
    int sun;
    boolean gameRunning;
    int time;
    int score;

    // Grid layout constants
    public static final int WIDTH = 1000;
    public static final int HEIGHT = 600;
    public static final int ROWS = 5;
    public static final int COLS = 9;
    public static final int CELL_W = 80;
    public static final int CELL_H = 100;
    public static final int GRID_X = 120;
    public static final int GRID_Y = 80;

    public static double colToPixelX(int col) {
        return GRID_X + col * CELL_W + CELL_W / 2;
    }

    public static double rowToPixelY(int row) {
        return GRID_Y + row * CELL_H + CELL_H / 2;
    }

    public static int pixelXToCol(double x) {
        return (int) ((x - GRID_X) / CELL_W);
    }

    private double zombieSpawnTimer;
    private double spawnInterval;
    private Random rng;
    int selectedPlant;

    /**
     * Constructor for objects of class Game.
     * Initialises entity lists, game variables, and the board grid.
     */
    public Game()
    {
        Zombie = new ArrayList<>();
        Plant = new ArrayList<>();
        bullets = new ArrayList<>();
        sun = 150;
        score = 0;
        gameRunning = false;
        zombieSpawnTimer = 0;
        spawnInterval = 10.0;
        rng = new Random();
        selectedPlant = 0;
    }

    /**
     * Resets game state and launches the UI
     */
    public void startGame()
    {
        Zombie.clear();
        Plant.clear();
        bullets.clear();
        sun = 150;
        score = 0;
        zombieSpawnTimer = 0;
        spawnInterval = 6.0;
        selectedPlant = 0;
        gameRunning = true;

        GameUI ui = new GameUI(this);
        ui.launch();
    }

    public void update(double deltaTime)
    {
        if (!gameRunning){
            return;
        }
        // spawn zombies on timer
        zombieSpawnTimer += deltaTime;
        if (zombieSpawnTimer >= spawnInterval) {
            spawnZombie();
            zombieSpawnTimer = 0;
            if (spawnInterval > 3.0) spawnInterval -= 0.3;
        }

        // update plants
        List<Entity> entityList = new ArrayList<>(Zombie);
        for (Plants p : Plant) {
            if (!p.isAlive()) continue;
            //p.act(entityList);
            List<Bullet> newBullets = p.shoot();
            bullets.addAll(newBullets);
        }

        // update bullets
        for (Bullet b : bullets) {
            b.update();
            for (Zombies z : Zombie) {
                if (b.contact(z)) {
                    z.takeDamage();
                    break;
                }
            }
        }

        removeDeadEntities();
        checkLoseCondition();
    }

    /**
     * Spawns a zombie in a random row
     */
    public void spawnZombie()
    {
        int row = rng.nextInt(ROWS);
        Zombie.add(new BasicZombie(row, COLS));
    }

    /**
     * Places a plant on the grid if the tile is free and
     * the player has enough sun. Deducts the sun cost.
     */
    @Override
    public void placePlant(Plants plant, int row, int col)
    {
        if (isTileOccupied(row, col)) {
            return;
        }
        if (sun < plant.getCost()) {
            return;
        }

        super.placePlant(plant, row, col);
        plant.setX(colToPixelX(col));
        plant.setY(rowToPixelY(row));
        Plant.add(plant);
        sun -= plant.getCost();
    }

    public void removeDeadEntities()
    {
        Plant.removeIf(p -> !p.isAlive());
        Zombie.removeIf(z -> !z.isAlive());
        bullets.removeIf(b -> !b.onScreen());

        // clean dead plants from the board grid
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                Plants p = getTile(r, c).getPlant();
                if (p != null && !p.isAlive()) {
                    removePlant(r, c);
                }
            }
        }
    }

    public void checkWinCondition()
    {

    }

    public void checkLoseCondition()
    {
        for (Zombies z : Zombie) {
            if (z.hasReachedHouse()) {
                gameRunning = false;
            }
        }
    }

    /**
     * Generates sun that falls from the sky
     */
    public void genFallingSun()
    {

    }

    private void checkCollision()
    {

    }
    
    /**
     * Called by SunFlower when it produces sun
     * Adds the given amount of sun to the player`s total sun count
     */
    public void addSun(int amount){
        sun += amount;
    }
}
