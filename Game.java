import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import javafx.stage.Stage;

/**
 * Game class - controls the main game logic.
 * Keeps track of all plants, zombies, bullets, and game state.
 *
 * @author Illia Putintsev
 * @version 1.3
 */
public class Game extends Board
{
    List<Zombies> Zombie;
    List<Plants> Plant;
    List<Bullet> bullets;
    int sun;
    boolean gameRunning;
    boolean gameOver;
    boolean levelComplete;
    boolean gameWon;
    int score;

    int level;
    int phase; // 0 = prep, 1 = buildup, 2 =final wave warning, 3 = final wave, 4 =level complete
    int spawnedTotal; //amount of zombies spawned so far
    int zombieCount; //amount of zombies before the final wave
    int finalWaveZombieAmount; //amount of zombies in the final wave
    int finalWaveSpawnedCount; //amount of zombies in the final wave spawned so far
    double phaseTimer;
    String message;
    double messageTimer;
    
    // player's progress
    int maxLevel;

    // Grid layout constants
    public static final int WIDTH = 1000;
    public static final int HEIGHT = 600;
    public static final int ROWS = 5;
    public static final int COLS = 9;
    public static final int CELL_W = 80;
    public static final int CELL_H = 100;
    public static final int GRID_X = 200;
    public static final int GRID_Y = 80;
    
    // Sun
    private List<Sun> suns = new ArrayList<>();
    private double skySunTimer = 0;
    
    // Shop constants
    public static final int SHOP_X = 120;
    public static final int SHOP_Y = 10;
    public static final int SHOP_CELL_W = 80;
    public static final int SHOP_CELL_H = 60;
    public static final int SHOVEL_INDEX = 4;

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
    private static final double SUN_DROP_INTERVAL = 15.0;
    static final int TOTAL_LEVELS = 7;
    int selectedPlant;

    // Store reference for switching screens (level/menu/game)
    private Stage stage;

    public Game()
    {
        Zombie = new ArrayList<>();
        Plant = new ArrayList<>();
        bullets = new ArrayList<>();
        sun = 50;
        score = 0;
        gameRunning = false;
        gameOver = false;
        levelComplete = false;
        gameWon = false;
        zombieSpawnTimer = 0;
        spawnInterval = 10.0;
        rng = new Random();
        selectedPlant = -1;
        level = 0;
        phase = 0;
        maxLevel = 0;
        message = "";
        messageTimer = 0;
    }

    /**
     * Starts a specific level from the level select screen.
     */
    public void startGame(GameUI ui, Stage stage, int chosenLevel)
    {
        this.stage = stage;
        Zombie.clear();
        Plant.clear();
        bullets.clear();
        suns.clear();
        skySunTimer = 0;
        sun = 50;
        score = 0;
        zombieSpawnTimer = 0;
        selectedPlant = -1;
        gameRunning = true;
        gameOver = false;
        levelComplete = false;
        gameWon = false;
        level = chosenLevel - 1; // increments by startLevel()
        phase = 0;
        phaseTimer = 10.0; // 10 seconds to plant initial plants
        message = "The zombies are approaching... Start to plant NOW!";
        messageTimer = 4.0;

        ui.launch();
    }

    public void update(double deltaTime)
    {
        if (!gameRunning) {
            return;
        }

        // message display countdown
        if (messageTimer > 0) {
            messageTimer -= deltaTime;
        }

        // game phases logic
        switch (phase) {
            case 0: // waiting before zombies spawn (prep phase)
                phaseTimer -= deltaTime;
                if (phaseTimer <= 0) {
                    startLevel();
                }
                break;

            case 1: // zombies start spawning
                zombieSpawnTimer += deltaTime;
                if (spawnedTotal < zombieCount && zombieSpawnTimer >= spawnInterval) {
                    spawnZombie();
                    SoundManager.playZombie();
                    spawnedTotal++;
                    zombieSpawnTimer = 0;
                }
                //  final wave warning as soon as all first phase zombies spawned
                if (spawnedTotal >= zombieCount) {
                    phase = 2;
                    phaseTimer = 3.0;
                    message = "A huge wave is approaching!";
                    messageTimer = 3.0;
                }
                break;

            case 2: // final wave warning
                phaseTimer -= deltaTime;
                if (phaseTimer <= 0) {
                    phase = 3;
                    finalWaveSpawnedCount = 0;
                    zombieSpawnTimer = 0;
                    message = "FINAL WAVE!";
                    messageTimer = 2.0;
                }
                break;

            case 3: // final wave, big concetration of zombies
                zombieSpawnTimer += deltaTime;
                double rushInterval = 0.4;
                if (finalWaveSpawnedCount < finalWaveZombieAmount && zombieSpawnTimer >= rushInterval) {
                    spawnZombie();
                    SoundManager.playZombie();
                    finalWaveSpawnedCount++;
                    zombieSpawnTimer = 0;
                }
                // level done if all final wave zombies spawned and all dead
                if (finalWaveSpawnedCount >= finalWaveZombieAmount && Zombie.isEmpty()) {
                    phase = 4;
                    phaseTimer = 2.0;
                    if (level > maxLevel) {
                        maxLevel = level;
                    }

                    if (level >= TOTAL_LEVELS) {
                        message = "You beat all " + TOTAL_LEVELS + " levels!";
                        messageTimer = 5.0;
                        gameRunning = false;
                        gameWon = true;
                    } else {
                        message = "Level " + level + " Complete!";
                        messageTimer = 3.0;
                        levelComplete = true;
                        gameRunning = false;
                    }
                }
                break;

            case 4: // level completed, waiting for player to click Continue
                break;
        }

        // update plants
        List<Entity> entityList = new ArrayList<>(Zombie);
        for (Plants p : Plant) {
            if (!p.isAlive()) continue;
            p.act(entityList, bullets, this, deltaTime);
        }

        // update zombies
        for (Zombies z : Zombie) {
            if (!z.isAlive()) continue;
            z.act(Plant, Zombie, deltaTime);
        }

        // update bullets
        for (Bullet b : bullets) {
            b.update(deltaTime);
            for (Zombies z : Zombie) {
                if (z.isAlive() && b.contact(z)) {
                    z.takeDamage();
                    break;
                }
            }
        }
        
        for (Sun sun : suns) {
            sun.update(deltaTime);
        }
        suns.removeIf(s -> !s.isAlive());
        
        skySunTimer += deltaTime;
        
        skySunTimer += deltaTime;
        
        if (skySunTimer >= 10.0) {
            double x = Game.GRID_X + Math.random() * (Game.COLS * Game.CELL_W);
            spawnSun(x, 20, 25, true);
            skySunTimer = 0;
        }

        removeDeadEntities();
        checkLoseCondition();
    }

    /**
     * Sets up the current level with zombie counts and spawn speed
     */
    private void startLevel()
    {
        level++;
        phase = 1;
        spawnedTotal = 0;
        finalWaveSpawnedCount = 0;
        zombieSpawnTimer = 0;
        message = "Level " + level;
        messageTimer = 2.0;

        // more zombies and faster spawns with each level
        switch (level) {
            case 1:
                zombieCount = 4;
                finalWaveZombieAmount = 3;
                spawnInterval = 8.0;
                break;
            case 2:
                zombieCount = 6;
                finalWaveZombieAmount = 5;
                spawnInterval = 7.0;
                break;
            case 3:
                zombieCount = 8;
                finalWaveZombieAmount = 6;
                spawnInterval = 6.0;
                break;
            case 4:
                zombieCount = 10;
                finalWaveZombieAmount = 8;
                spawnInterval = 5.5;
                break;
            case 5:
                zombieCount = 12;
                finalWaveZombieAmount = 10;
                spawnInterval = 5.0;
                break;
            case 6:
                zombieCount = 14;
                finalWaveZombieAmount = 13;
                spawnInterval = 4.7;
                break;
            case 7:
                zombieCount = 16;
                finalWaveZombieAmount = 16;
                spawnInterval = 4.3;
                break;
            default:
                zombieCount = 10 + level * 2;
                finalWaveZombieAmount = 8 + level * 2;
                spawnInterval = Math.max(2.0, 8.0 - level);
                break;
        }
    }

    /**
     * Spawns a zombie in a random row.
     * Stronger zombie spawn chance depends on level and phase
     */
    public void spawnZombie()
    {
        int row = rng.nextInt(ROWS);

        // increased chance of stronger zombie with each level
        int strongChance = 0;
        if (level >= 3) strongChance = 20 + (level - 3) * 15;

        // increased chance of strong zombiews in the final wave
        if (phase == 3) strongChance += 15;

        if (rng.nextInt(100) < strongChance) {
            Zombie.add(new StrongZombie(row, COLS));
        } else {
            Zombie.add(new BasicZombie(row, COLS));
        }
    }

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
        SoundManager.playPlant();
    }

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

        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                Plants p = getTile(r, c).getPlant();
                if (p != null && !p.isAlive()) {
                    super.removePlant(r, c);
                    SoundManager.playSwallow();
                }
            }
        }
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
     * Clears all plants, zombies, and bullets.
     * Resets sun so the player starts fresh each level
     */
    public void clearBoard()
    {
        Plant.clear();
        Zombie.clear();
        bullets.clear();
        suns.clear();
        sun = 50;

        // clear all tiles on the grid
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                Tile tile = getTile(r, c);
                if (tile != null) {
                    tile.removePlant();
                }
            }
        }
    }

    /**
     * Go back to the level select screen.
     */
    public void returnToLevelSelect()
    {
        gameRunning = false;
        LevelUI levelSelect = new LevelUI(this, stage);
        levelSelect.show();
    }

    public void addSun(int amount) {
        sun += amount;
    }

    public Stage getStage() {
        return stage;
    }
    
    public void spawnSun(double x, double y, int value, boolean falling) {
        suns.add(new Sun(x, y, value, falling));
    }

    public List<Sun> getSuns() {
        return suns;
    }
}
