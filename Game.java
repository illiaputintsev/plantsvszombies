import java.util.List;
import java.util.ArrayList;
import java.util.Random;

/**
 * Game class - controls the main game logic.
 * Keeps track of all plants, zombies, bullets, and game state.
 *
 * @author Illia
 * @version 1.1
 */
public class Game extends Board
{
    List<Zombies> Zombie;
    List<Plants> Plant;
    List<Bullet> bullets;
    int sun;
    boolean gameRunning;
    boolean gameOver;
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
    
    // Shop constants
    public static final int SHOP_X = 120;
    public static final int SHOP_Y = 10;
    public static final int SHOP_CELL_W = 80;
    public static final int SHOP_CELL_H = 60;
    public static final int SHOVEL_INDEX = 4; // last slot, after 4 plants

    public static double colToPixelX(int col) {
        return GRID_X + col * CELL_W + CELL_W / 2.0;
    }

    public static double rowToPixelY(int row) {
        return GRID_Y + row * CELL_H + CELL_H / 2.0;
    }

    public static int pixelXToCol(double x) {
        return (int) ((x - GRID_X) / CELL_W);
    }

    private double zombieSpawnTimer;
    private double spawnInterval;
    private Random rng;
    private double sunDropTimer;
    private static final double SUN_DROP_INTERVAL = 10.0;
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
        sun = 50;
        score = 0;
        gameRunning = false;
        gameOver = false;
        zombieSpawnTimer = 0;
        spawnInterval = 10.0;
        rng = new Random();
        selectedPlant = -1;
    }

    /**
     * Resets game state and launches the UI
     */
    public void startGame(GameUI ui)
    {
        Zombie.clear();
        Plant.clear();
        bullets.clear();
        sun = 50;
        score = 0;
        sunDropTimer = 0;
        zombieSpawnTimer = 0;
        spawnInterval = 15.0;
        selectedPlant = -1;
        gameRunning = true;
        gameOver = false;
        
        ui.launch();
    }

    public void update(double deltaTime)
    {
        if (!gameRunning){
            return;
        }
        // passive sun income
        sunDropTimer += deltaTime;
        if (sunDropTimer >= SUN_DROP_INTERVAL) {
            addSun(25);
            sunDropTimer = 0;
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
            p.act(entityList, bullets, this);
        }

        // update zombies
        for (Zombies z : Zombie) {
            if (!z.isAlive()) continue;
            z.act(Plant, Zombie);
        }

        // update bullets
        for (Bullet b : bullets) {
            b.update();
            for (Zombies z : Zombie) {
                if (z.isAlive() && b.contact(z)) {
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

    /**
     * FIX: Override removePlant to also remove from the active Plant list.
     * Previously the shovel only cleared the tile, leaving a ghost plant
     * that kept acting and drawing.
     */
    @Override
    public void removePlant(int row, int col)
    {
        Tile tile = getTile(row, col);
        if (tile != null) {
            Plants p = tile.getPlant();
            if (p != null) {
                Plant.remove(p);
            }
        }
        super.removePlant(row, col);
    }

    public void removeDeadEntities()
    {
        for (Zombies z : Zombie) {
            if (!z.isAlive()) score++;
        }
        Plant.removeIf(p -> !p.isAlive());
        Zombie.removeIf(z -> !z.isAlive());
        bullets.removeIf(b -> !b.onScreen());

        // clean dead plants from the board grid
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                Plants p = getTile(r, c).getPlant();
                if (p != null && !p.isAlive()) {
                    // Use super to avoid double-removal from list
                    super.removePlant(r, c);
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
                gameOver = true;
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
     * Adds the given amount of sun to the player's total sun count
     */
    public void addSun(int amount){
        sun += amount;
    }
}