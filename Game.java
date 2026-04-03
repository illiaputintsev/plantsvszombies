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
public class Game
{
    private List<Zombie> zombies;
    private List<Plant> plants;
    private List<Bullet> bullets;
    private int sun;
    private boolean gameRunning;
    private boolean gameOver;
    private boolean levelComplete;
    private boolean gameWon;        
    private int score;
    private int level;
    private int phase;
    private int spawnedTotal;
    private int zombieCount;
    private int finalWaveZombieAmount;
    private int finalWaveSpawnedCount;
    private double phaseTimer;
    private String message;
    private double messageTimer;
    private int maxLevel;
    private int selectedPlant;

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
    public static final int SHOP_Y = 5;
    public static final int SHOP_CELL_W = 80;
    public static final int SHOP_CELL_H = 60;
    public static final int SHOVEL_INDEX = 4;
    
    // Pause state
    private boolean paused = false;

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
    private Board board;

    // Store reference for switching screens (level/menu/game)
    private Stage stage;

    public Game()
    {
        zombies = new ArrayList<>();
        plants = new ArrayList<>();
        bullets = new ArrayList<>();
        sun = 75;
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
        board = new Board();
    }

    /**
     * Starts a specific level from the level select screen.
     */
    public void startGame(GameUI ui, Stage stage, int chosenLevel)
    {
        this.stage = stage;
        zombies.clear();
        plants.clear();
        bullets.clear();
        suns.clear();
        skySunTimer = 0;
        sun = 75;
        score = 0;
        zombieSpawnTimer = 0;
        selectedPlant = -1;
        gameRunning = true;
        gameOver = false;
        levelComplete = false;
        gameWon = false;
        paused = false;
        level = chosenLevel - 1; // increments by startLevel()
        phase = 0;
        phaseTimer = 13.0; // 13 seconds to plant initial plants
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
                // final wave warning as soon as all first phase zombies spawned
                if (spawnedTotal >= zombieCount) {
                    phase = 2;
                    phaseTimer = 3.0;
                    message = "A huge wave is approaching!";
                    messageTimer = 3.0;
                    SoundManager.playDrums();
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
                    SoundManager.playDrums();
                }
                break;

            case 3: // final wave, big concentration of zombies
                zombieSpawnTimer += deltaTime;
                double rushInterval = 0.4;
                if (finalWaveSpawnedCount < finalWaveZombieAmount && zombieSpawnTimer >= rushInterval) {
                    spawnZombie();
                    SoundManager.playZombie();
                    finalWaveSpawnedCount++;
                    zombieSpawnTimer = 0;
                }
                // level done if all final wave zombies spawned and all dead
                if (finalWaveSpawnedCount >= finalWaveZombieAmount && zombies.isEmpty()) {
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
                        SoundManager.stopLevelTheme();
                    } else {
                        message = "Level " + level + " Complete!";
                        messageTimer = 3.0;
                        levelComplete = true;
                        gameRunning = false;
                        SoundManager.stopLevelTheme();
                        SoundManager.playLVLComplete();
                    }
                }
                break;

            case 4: // level completed, waiting for player to click Continue
                break;
        }

        // update plants
        List<Entity> entityList = new ArrayList<>(zombies);
        for (Plant p : plants) {
            if (!p.isAlive()) continue;
            p.act(entityList, bullets, this, deltaTime);
        }

        // update zombies
        for (Zombie z : zombies) {
            if (!z.isAlive()) continue;
            z.act(plants, zombies, deltaTime);
            z.updateFlash(deltaTime);
        }

        // update bullets
        for (Bullet b : bullets) {
            b.update(deltaTime);
            for (Zombie z : zombies) {
                if (z.isAlive() && b.contact(z)) {
                    z.takeDamage();
                    z.triggerFlash();
                    break;
                }
            }
        }
        
        for (Sun sun : suns) {
            sun.update(deltaTime);
        }
        suns.removeIf(s -> !s.isAlive());
                
        skySunTimer += deltaTime;
        
        if (skySunTimer >= 10.0) {
            double x = Game.GRID_X + Math.random() * (Game.COLS * Game.CELL_W);
            spawnSun(x, 20, 25, true);
            skySunTimer = 0;
        }

        if (!(SoundManager.isLevelThemePlaying())){
            SoundManager.playLevelTheme();
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
                zombieCount = 11;
                finalWaveZombieAmount = 9;
                spawnInterval = 5.3;
                break;
            case 6:
                zombieCount = 12;
                finalWaveZombieAmount = 11;
                spawnInterval = 5.5;
                break;
            case 7:
                zombieCount = 13;
                finalWaveZombieAmount = 13;
                spawnInterval = 5.7;
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

        // increased chance of strong zombies in the final wave
        if (phase == 3) strongChance += 15;

        if (rng.nextInt(100) < strongChance) {
            zombies.add(new StrongZombie(row, COLS));
        } else {
            zombies.add(new BasicZombie(row, COLS));
        }
    }

    public void placePlant(Plant plant, int row, int col)
    {
        if (board.isTileOccupied(row, col)) {
            return;
        }
        if (sun < plant.getCost()) {
            return;
        }

        board.placePlant(plant, row, col);
        plant.setX(colToPixelX(col));
        plant.setY(rowToPixelY(row));
        plants.add(plant);
        sun -= plant.getCost();
        SoundManager.playPlant();
    }

    public void removePlant(int row, int col)
    {
        Tile tile = board.getTile(row, col);
        if (tile != null) {
            Plant p = tile.getPlant();
            if (p != null) {
                plants.remove(p);
            }
        }
        board.removePlant(row, col);
    }

    public void removeDeadEntities()
    {
        for (Zombie z : zombies) {
            if (!z.isAlive()) score++;
        }
        plants.removeIf(p -> !p.isAlive());
        zombies.removeIf(z -> !z.isAlive());
        bullets.removeIf(b -> !b.onScreen());

        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                Plant p = board.getTile(r, c).getPlant();
                if (p != null && !p.isAlive()) {
                    board.removePlant(r, c);
                    SoundManager.playSwallow();
                }
            }
        }
    }

    public void checkLoseCondition()
    {
        for (Zombie z : zombies) {
            if (z.hasReachedHouse()) {
                if (!gameOver) {
                    SoundManager.stopLevelTheme();
                    SoundManager.playGameOver();
                }
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
        plants.clear();
        zombies.clear();
        bullets.clear();
        suns.clear();
        sun = 75;

        // clear all tiles on the grid
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                Tile tile = board.getTile(r, c);
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
        SoundManager.stopLevelTheme();
    }

    public void addSun(int amount) {
        SoundManager.playSunPicked();
        sun += amount;
    }

    public int getSunAmount() {
        return sun;
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

    /**
     * Returns true if the game is currently paused.
     */
    public boolean isPaused() {
        return paused;
    }

    /**
     * Pauses the game — stops all updates while keeping rendering active.
     */
    public void pause() {
        paused = true;
    }

    /**
     * Resumes the game from a paused state.
     */
    public void resume() {
        paused = false;
    }
    
    public boolean isTileOccupied(int row, int col) {
    return board.isTileOccupied(row, col);
    }
    
    // Getters for GameUI rendering
    public List<Zombie> getZombies()       { return zombies; }
    public List<Plant> getPlants()         { return plants; }
    public List<Bullet> getBullets()        { return bullets; }
    public int getScore()                   { return score; }
    public int getLevel()                   { return level; }
    public int getPhase()                   { return phase; }
    public int getSpawnedTotal()            { return spawnedTotal; }
    public int getZombieCount()             { return zombieCount; }
    public int getFinalWaveZombieAmount()   { return finalWaveZombieAmount; }
    public int getFinalWaveSpawnedCount()   { return finalWaveSpawnedCount; }
    public String getMessage()              { return message; }
    public double getMessageTimer()         { return messageTimer; }
    public int getMaxLevel()                { return maxLevel; }
    public boolean isGameRunning()          { return gameRunning; }
    public boolean isGameOver()             { return gameOver; }
    public boolean isLevelComplete()        { return levelComplete; }
    public boolean isGameWon()              { return gameWon; }
    public int getSelectedPlant()           { return selectedPlant; }
    public void setSelectedPlant(int s)     { selectedPlant = s; }
}