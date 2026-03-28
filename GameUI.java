import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 * GameUI class - handles rendering.
 *
 * @author Mark Tarnavskyi
 */
public class GameUI {
    private Game game;
    private Stage stage;

    // Shop entries: name, cost (cost = 0 means free / shovel)
    private static final String[] SHOP_NAMES = {
        "Pea", "Sun", "Nut", "Rep", "Shovel"
    };
    private static final int[] SHOP_COSTS = { 100, 50, 50, 200, 0 };

    // button area for "Continue" and "Home"
    private static final double BUTTON_X = Game.WIDTH / 2.0 - 80;
    private static final double BUTTON_Y = Game.HEIGHT / 2.0 + 80;
    private static final double BUTTON_W = 160;
    private static final double BUTTON_H = 40;

    public GameUI(Game game, Stage stage) {
        this.game = game;
        this.stage = stage;
        SoundManager.playLevelTheme();
    }

    /**
     * Launches the gameplay screen with the animation loop.
     */
    public void launch() {
        Canvas canvas = new Canvas(Game.WIDTH, Game.HEIGHT);
        Scene scene = new Scene(new StackPane(canvas));
        stage.setScene(scene);
        stage.setTitle("Plants vs Zombies");
        stage.show();

        GraphicsContext gc = canvas.getGraphicsContext2D();
        canvas.setOnMouseClicked(e -> handleClick(e.getX(), e.getY()));

        new AnimationTimer() {
            private long lastTime = -1;

            @Override
            public void handle(long now) {
                if (lastTime < 0) { lastTime = now; return; }
                double deltaTime = (now - lastTime) / 1_000_000_000.0;
                lastTime = now;

                // stop loop if game is no longer running and an end state is reached
                if (!game.gameRunning && (game.levelComplete || game.gameOver || game.gameWon)) {
                    render(gc);
                    stop();
                    return;
                }

                game.update(deltaTime);
                render(gc);
            }
        }.start();
    }

    private void handleClick(double mx, double my) {

        // click button to go back to level select
        if (game.levelComplete || game.gameOver || game.gameWon) {
            if (mx >= BUTTON_X && mx <= BUTTON_X + BUTTON_W
                && my >= BUTTON_Y && my <= BUTTON_Y + BUTTON_H) {
                goToLevelSelect();
                SoundManager.playMenuBtn();
            }
            return;
        }

        // Don't allow clicks when game is over
        if (game.gameOver) return;

        // Check if the click is inside the shop bar
        if (my >= Game.SHOP_Y && my <= Game.SHOP_Y + Game.SHOP_CELL_H) {
            int slot = (int) ((mx - Game.SHOP_X) / Game.SHOP_CELL_W);

            if (slot < 0 || slot >= SHOP_NAMES.length) {
                return;
            }

            // Clicking the already-selected slot deselects it
            if (slot == game.selectedPlant) {
                game.selectedPlant = -1;
                return;
            }

            // Shovel is always selectable (no sun cost)
            if (slot == Game.SHOVEL_INDEX) {
                game.selectedPlant = Game.SHOVEL_INDEX;
                return;
            }

            // For plants, only select if the player can afford it
            if (game.sun >= SHOP_COSTS[slot]) {
                game.selectedPlant = slot;
            }
            return;
        }

        // Clicks handling outside the shop bar
        int col = (int) ((mx - Game.GRID_X) / Game.CELL_W);
        int row = (int) ((my - Game.GRID_Y) / Game.CELL_H);

        // Make sure click is inside the grid bounds
        if (row < 0 || row >= Game.ROWS || col < 0 || col >= Game.COLS) {
            return;
        }

        // Shovel selected: remove plant if one is present
        if (game.selectedPlant == Game.SHOVEL_INDEX) {
            if (game.isTileOccupied(row, col)) {
                game.removePlant(row, col);
                SoundManager.playShowel();
            }
            game.selectedPlant = -1;
            return;
        }

        // Plant selected: place it if the tile is empty
        if (game.selectedPlant != -1) {
            Plants p = null;
            if (game.selectedPlant == 0) {
                p = new Peashooter(row, col);
            } else if (game.selectedPlant == 1) {
                p = new Sunflower(row, col);
            } else if (game.selectedPlant == 2) {
                p = new Walnut(row, col);
            } else if (game.selectedPlant == 3) {
                p = new Repeater(row, col);
            }

            if (p != null) {
                game.placePlant(p, row, col);
                game.selectedPlant = -1;
            }
        }
    }

    /**
     * Opens the level selection screen
     */
    private void goToLevelSelect() {
        LevelUI levelSelect = new LevelUI(game, stage);
        levelSelect.show();
    }

    private void render(GraphicsContext gc) {
        gc.clearRect(0, 0, Game.WIDTH, Game.HEIGHT);

        drawShop(gc);
        drawPavement(gc);
        drawHouse(gc);

        // draw the grid
        for (int r = 0; r < Game.ROWS; r++) {
            for (int c = 0; c < Game.COLS; c++) {
                double cellX = Game.GRID_X + c * Game.CELL_W;
                double cellY = Game.GRID_Y + r * Game.CELL_H;

                if ((r + c) % 2 == 0) {
                    gc.setFill(Color.PALEGREEN);
                } else {
                    gc.setFill(Color.SEAGREEN);
                }
                gc.fillRect(cellX, cellY, Game.CELL_W, Game.CELL_H);
            }
        }

        for (Plants p : game.Plant) {
            if (p.isAlive()) p.draw(gc);
        }
        for (Zombies z : game.Zombie) {
            if (z.isAlive()) z.draw(gc);
        }
        for (Bullet b : game.bullets) {
            b.draw(gc);
        }

        // Sun and score counters
        gc.setFill(Color.BLACK);
        gc.setFont(Font.font(14));
        gc.fillText("Sun: " + game.sun, 10, 30);
        gc.fillText("Score: " + game.score, 10, 50);

        // Level indicator
        if (game.level > 0) {
            gc.setFont(Font.font(14));
            gc.setFill(Color.BLACK);
            gc.fillText("Level: " + game.level + " / " + Game.TOTAL_LEVELS, Game.WIDTH - 100, 30);
        }

        // Level progress bar
        if (game.phase == 1 || game.phase == 3) {
            double progressBarX = Game.WIDTH - 180;
            double progressBarY = 45;
            double progressBarWidth = 150;
            double progressBarHeight = 10;
            int totalZombies = game.zombieCount + game.finalWaveZombieAmount;
            int spawnedSoFar = game.spawnedTotal + game.finalWaveSpawnedCount;
            double progress = (double) spawnedSoFar / totalZombies;

            gc.setFill(Color.GRAY);
            gc.fillRect(progressBarX, progressBarY, progressBarWidth, progressBarHeight);
            gc.setFill(game.phase == 3 ? Color.RED : Color.ORANGERED);
            gc.fillRect(progressBarX, progressBarY, progressBarWidth * progress, progressBarHeight);
            gc.setStroke(Color.BLACK);
            gc.strokeRect(progressBarX, progressBarY, progressBarWidth, progressBarHeight);
        }

        // Center message
        if (game.message != null && !game.message.isEmpty() && game.messageTimer > 0) {
            double textWidth = game.message.length() * 12;
            double messageX = Game.WIDTH / 2.0 - textWidth / 2 - 10;

            //final wave warning
            if (game.phase == 2 || game.phase == 3) {
                gc.setFill(Color.color(0.8, 0, 0, 0.7));
                SoundManager.playDrums();
            } else {
                gc.setFill(Color.color(0, 0, 0, 0.5));
            }
            gc.fillRoundRect(messageX, Game.GRID_Y - 30, textWidth + 20, 36, 8, 8);

            gc.setFill(Color.WHITE);
            gc.setFont(Font.font(20));
            gc.fillText(game.message, messageX + 10, Game.GRID_Y - 4);
        }

        
        if (!(game.levelComplete || game.gameOver || game.gameWon)){
            if (!(SoundManager.isLevelThemePlaying())){
                SoundManager.playLevelTheme();
            }
        }
        
        // Level complete
        if (game.levelComplete) {
            SoundManager.stopLevelTheme();
            SoundManager.playLVLComplete();
            gc.setFill(Color.color(0, 0, 0, 0.6));
            gc.fillRect(0, 0, Game.WIDTH, Game.HEIGHT);
            gc.setFill(Color.LIMEGREEN);
            gc.setFont(Font.font(48));
            gc.fillText("LEVEL COMPLETE!", Game.WIDTH / 2.0 - 200, Game.HEIGHT / 2.0 - 10);
            gc.setFill(Color.WHITE);
            gc.setFont(Font.font(20));
            gc.fillText("Score: " + game.score, Game.WIDTH / 2.0 - 40, Game.HEIGHT / 2.0 + 30);

            drawButton(gc, "Continue", BUTTON_X, BUTTON_Y, Color.FORESTGREEN);
        }

        // Game over
        if (game.gameOver) {
            SoundManager.stopLevelTheme();
            SoundManager.playGameOver();
            gc.setFill(Color.color(0, 0, 0, 0.6));
            gc.fillRect(0, 0, Game.WIDTH, Game.HEIGHT);
            gc.setFill(Color.RED);
            gc.setFont(Font.font(48));
            gc.fillText("GAME OVER", Game.WIDTH / 2.0 - 140, Game.HEIGHT / 2.0 - 10);
            gc.setFill(Color.WHITE);
            gc.setFont(Font.font(20));
            gc.fillText("Score: " + game.score, Game.WIDTH / 2.0 - 40, Game.HEIGHT / 2.0 + 30);
            gc.fillText("Reached Level " + game.level, Game.WIDTH / 2.0 - 55, Game.HEIGHT / 2.0 + 60);

            drawButton(gc, "Home", BUTTON_X, BUTTON_Y, Color.DARKRED);
        }

        // Game won
        if (game.gameWon) {
            SoundManager.stopLevelTheme();
            gc.setFill(Color.color(0, 0, 0, 0.6));
            gc.fillRect(0, 0, Game.WIDTH, Game.HEIGHT);
            gc.setFill(Color.GOLD);
            gc.setFont(Font.font(48));
            gc.fillText("YOU WIN!", Game.WIDTH / 2.0 - 120, Game.HEIGHT / 2.0 - 10);
            gc.setFill(Color.WHITE);
            gc.setFont(Font.font(20));
            gc.fillText("Score: " + game.score, Game.WIDTH / 2.0 - 40, Game.HEIGHT / 2.0 + 30);
            gc.fillText("All " + Game.TOTAL_LEVELS + " levels cleared!", Game.WIDTH / 2.0 - 70, Game.HEIGHT / 2.0 + 60);

            drawButton(gc, "Home", BUTTON_X, BUTTON_Y, Color.GOLDENROD);
        }
    }

    /**
     * Draws a clickable button on the canvas
     */
    private void drawButton(GraphicsContext gc, String text, double bx, double by, Color colour) {
        gc.setFill(colour);
        gc.fillRoundRect(bx, by, BUTTON_W, BUTTON_H, 10, 10);
        gc.setStroke(Color.WHITE);
        gc.strokeRoundRect(bx, by, BUTTON_W, BUTTON_H, 10, 10);
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font(18));
        gc.fillText(text, bx + 20, by + 26);
    }

    private void drawShop(GraphicsContext gc) {
        for (int i = 0; i < SHOP_NAMES.length; i++) {
            double x = Game.SHOP_X + i * Game.SHOP_CELL_W;
            double y = Game.SHOP_Y;

            // Highlighting slots
            if (i == game.selectedPlant) {
                gc.setFill(Color.GOLD);
            } else if (i == Game.SHOVEL_INDEX) {
                gc.setFill(Color.SADDLEBROWN);
            } else if (game.sun >= SHOP_COSTS[i]) {
                gc.setFill(Color.LIGHTBLUE);
            } else {
                gc.setFill(Color.DARKGRAY);
            }

            gc.fillRect(x, y, Game.SHOP_CELL_W - 5, Game.SHOP_CELL_H - 5);

            // name and cost
            gc.setFill(Color.BLACK);
            gc.setFont(Font.font(12));
            gc.fillText(SHOP_NAMES[i], x + 10, y + 20);

            if (i != Game.SHOVEL_INDEX) {
                gc.fillText("$" + SHOP_COSTS[i], x + 10, y + 40);
            }
        }
    }
    
    private void drawHouse(GraphicsContext gc) {
        // wall
        gc.setFill(Color.web("#F5F5DC"));
        gc.fillPolygon(
        new double[]{0, 114, 114, 64},  // x  
        new double[]{525, 525, 175, 110}, // y
        4);
        
        // roof
        gc.setFill(Color.web("#C4736A"));
        gc.fillPolygon(
            new double[]{0, 64, 64, 0},  // x  
            new double[]{80, 85, 506, 485}, // y
            4);
            
        // door  
        gc.setFill(Color.web("#8B4513"));
        gc.fillPolygon(
        new double[]{114, 90, 90, 114},  // x  
        new double[]{215, 200, 250, 265}, // y
        4);
        
        //window
        gc.setFill(Color.web("#87CEEB"));
        gc.fillPolygon(
        new double[]{80, 80, 110, 110},  // x  
        new double[]{315, 415, 425, 325}, // y
        4);
        
        // wall
        // horizontal line
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 525, 120, 6);
        
        // perpendicular line
        gc.setFill(Color.BLACK);
        //           x,   y, w,   h
        gc.fillRect(114, 175, 6, 350);
        
        // first diagonal
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(6);
        //            x1, y1, x2, y2
        gc.strokeLine(70, 485, 114, 525);
        
        // second diagonal
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(6);
        gc.strokeLine(70, 110, 114, 175);
        
        // roof
        // perpendicular base line
        gc.setFill(Color.BLACK);
        gc.fillRect(64, 85, 6, 425);     
        
        // first diagonal
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(6);
        gc.strokeLine(64, 85, 0, 75);
        
        // second diagonal
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(6);
        gc.strokeLine(0, 485, 64, 506); 
        
        //door
        // first diagonal
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(4);
        gc.strokeLine(92, 250, 114, 265);
        
        // second diagonal
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(4);
        gc.strokeLine(92, 200, 114, 215);
        
        // top line
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(4);
        gc.strokeLine(90, 200, 90, 250);
        
        //window
        // botton line
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(4);
        gc.strokeLine(110, 325, 110, 425);
        
        // top line
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(4);
        gc.strokeLine(80, 315, 80, 415);
        
        // first diagonal
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(4);
        gc.strokeLine(110, 425, 80, 415);
        
        // second diagonal
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(4);
        gc.strokeLine(110, 325, 80, 315);
    }
    
    private void drawPavement(GraphicsContext gc) {
        // pavement zombie side
        gc.setFill(Color.DARKGRAY);
        gc.fillRect(920, 70, 1000, 600); 
        
        // strip lines road zombie side
        gc.setFill(Color.YELLOW);
        gc.fillRect(950, 70, 7, 530);
        
        gc.setFill(Color.YELLOW);
        gc.fillRect(960, 70, 7, 530);
        
        // bottom side wall
        gc.setFill(Color.web("#8B6914"));
        gc.fillRect(0, 580, 920, 20);
        
        // top side wall
        gc.setFill(Color.web("#8B6914"));
        gc.fillRect(0, 70, 920, 10);
        
        // pavement house side
        gc.setFill(Color.web("#A0A0A0"));
        gc.fillRect(0, 80, 200, 520);
    }
}

