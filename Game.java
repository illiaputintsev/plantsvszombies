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

    public void update(double deltaTime){
    
    }

    /**
     * Spawns a zombie in a random row
     */
    public void spawnZombie()
    {
        
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
        Plant.add(plant);
        sun -= plant.getCost();
    }

    public void removeDeadEntities()
    {
        
    }

    public void checkWinCondition()
    {
        
    }

    public void checkLoseCondition()
    {
        
    }

    /**
     * Generates sun that falls from the sky
     */
    public void genFallingSun()
    {
        
    }
}
