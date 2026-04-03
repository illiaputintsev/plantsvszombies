import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.geometry.VPos;
import javafx.scene.shape.ArcType;

/**
 * GameUI class - handles rendering.
 *
 * @author Mark Tarnavskyi
 */
public class GameUI {
    private Game game;
    private Stage stage;

    // Shop entries
    private static final String[] SHOP_NAMES = {"Sunflower", "Walnut", "Peashooter", "Repeater", "Shovel"};
    private static final int[] SHOP_COSTS = {50, 50, 100, 200, 0};

    // button area for "Continue" and "Home"
    private static final double BUTTON_X = Game.WIDTH / 2.0 - 80;
    private static final double BUTTON_Y = Game.HEIGHT / 2.0 + 80;
    private static final double BUTTON_W = 160;
    private static final double BUTTON_H = 40;
    
    // Pause button 
    private static final double PAUSE_BTN_X = Game.WIDTH - 50;
    private static final double PAUSE_BTN_Y = 20;
    private static final double PAUSE_BTN_SIZE = 32;
    
    // Pause overlay buttons
    private static final double OVERLAY_BTN_W = 200;
    private static final double OVERLAY_BTN_H = 50;
    private static final double OVERLAY_BTN_GAP = 20;

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
        VBox root = new VBox();
        root.getChildren().addAll(Main.createMenuBar(), new StackPane(canvas));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Plants vs Zombies");
        stage.show();

        GraphicsContext gc = canvas.getGraphicsContext2D();
        canvas.setOnMouseMoved(e -> {
            for (Sun sun : game.getSuns()) {
                if (sun.isClicked(e.getX(), e.getY())) {
                    game.addSun(sun.collect());
                    return;
                }
            }
        });

        canvas.setOnMouseClicked(e -> {
            handleClick(e.getX(), e.getY());
        }); 
        
        new AnimationTimer() {
            private long lastTime = -1;
            private boolean wasPaused = false;
        
            @Override
            public void handle(long now) {
                if (lastTime < 0) { lastTime = now; return; }
        
                // After unpausing, skip one frame's delta so nothing jumps
                if (wasPaused && !game.isPaused()) {
                    lastTime = now;
                    wasPaused = false;
                }
                if (game.isPaused()) {
                    wasPaused = true;
                }
        
                double deltaTime = (now - lastTime) / 1_000_000_000.0;
                lastTime = now;
        
                if (!game.gameRunning) {
                    render(gc);
                    stop();
                    return;
                }
        
                if (!game.isPaused()) {
                    game.update(deltaTime);
                }
                render(gc);
            }
        }.start();  
    }

    /**
     * 
     * 
     */
    private void handleClick(double mx, double my) {
        // Pause overlay clicks | checked first, swallows all input
        if (game.isPaused()) {
            double btnX = Game.WIDTH / 2.0 - OVERLAY_BTN_W / 2;
            double resumeY = Game.HEIGHT / 2.0 - 10;
            double levelsY = resumeY + OVERLAY_BTN_H + OVERLAY_BTN_GAP;

            if (isInsideRect(mx, my, btnX, resumeY, OVERLAY_BTN_W, OVERLAY_BTN_H)) {
                game.resume();
                SoundManager.playMenuBtn();
            } else if (isInsideRect(mx, my, btnX, levelsY, OVERLAY_BTN_W, OVERLAY_BTN_H)) {
                game.resume();
                SoundManager.playMenuBtn();
                game.returnToLevelSelect();
            }
            return; // nothing else responds while paused
        }
        
        // Pause button click
        if (game.gameRunning) {
            double cx = PAUSE_BTN_X + PAUSE_BTN_SIZE / 2;
            double cy = PAUSE_BTN_Y + PAUSE_BTN_SIZE / 2;
            double dx = mx - cx;
            double dy = my - cy;
            if (dx * dx + dy * dy <= (PAUSE_BTN_SIZE / 2) * (PAUSE_BTN_SIZE / 2)) {
                game.pause();
                SoundManager.playMenuBtn();
                return;
            }
        }
        
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
                p = new Sunflower(row, col);
            } else if (game.selectedPlant == 1) {
                p = new Walnut(row, col);
            } else if (game.selectedPlant == 2) {
                p = new Peashooter(row, col);
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
    
    private boolean isInsideRect(double mx, double my,
                                  double rx, double ry, double rw, double rh) {
        return mx >= rx && mx <= rx + rw && my >= ry && my <= ry + rh;
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

        // Level indicator
        int displayLevel = (game.phase == 0) ? game.level + 1 : game.level;
        if (displayLevel > 0) {
            gc.setFont(Font.font(14));
            gc.setFill(Color.BLACK);
            gc.fillText("Level: " + displayLevel + " / " + Game.TOTAL_LEVELS, Game.WIDTH - 200, 30);
        }

        // Level progress bar
        if (game.phase == 1 || game.phase == 2||game.phase == 3) {
            double progressBarX = Game.WIDTH - 280;

            double progressBarY = 45;
            double progressBarWidth = 150;
            double progressBarHeight = 10;
            int totalZombies = game.zombieCount + game.finalWaveZombieAmount;
            int spawnedSoFar = game.spawnedTotal + game.finalWaveSpawnedCount;
            double progress = (double) spawnedSoFar / totalZombies;
            double headX = progressBarX + (progressBarWidth * progress);
            double headY = progressBarY;

            gc.setFill(Color.GRAY);
            gc.fillRect(progressBarX, progressBarY, progressBarWidth, progressBarHeight);
            gc.setFill(game.phase == 3 ? Color.RED : Color.ORANGERED);
            gc.fillRect(progressBarX, progressBarY, progressBarWidth * progress, progressBarHeight);
            gc.setStroke(Color.BLACK);
            gc.strokeRect(progressBarX, progressBarY, progressBarWidth, progressBarHeight);
            
            // head following the progress bar            
            gc.setFill(Color.YELLOWGREEN);
            gc.fillOval(headX - 10, headY - 10, 20, 20);
            gc.setFill(Color.RED);
            gc.fillOval(headX - 5, headY - 5, 4, 4);
            gc.fillOval(headX + 3, headY - 5, 4, 4);
        }
        
        for (Sun s : game.getSuns()) {
        if (s.isAlive()) {
            s.draw(gc);
        }}

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
            gc.fillRoundRect(messageX, Game.GRID_Y + 10, textWidth + 20, 36, 8, 8);

            gc.setFill(Color.WHITE);
            gc.setFont(Font.font(20));
            gc.setTextAlign(TextAlignment.CENTER);
            gc.fillText(game.message, Game.WIDTH / 2.0, Game.GRID_Y + 36);
            gc.setTextAlign(TextAlignment.LEFT);
        }

        
        if (!(game.levelComplete || game.gameOver || game.gameWon)){
            if (!(SoundManager.isLevelThemePlaying())){
                SoundManager.playLevelTheme();
            }
        }
        
        // pausing checks
        if (game.gameRunning) {
            drawPauseButton(gc);
        }
        if (game.isPaused()) {
            drawPauseOverlay(gc);
        }
        
        // Level complete
        if (game.levelComplete) {
            SoundManager.stopLevelTheme();
            SoundManager.playLVLComplete();
            gc.setFill(Color.color(0, 0, 0, 0.6));
            gc.fillRect(0, 0, Game.WIDTH, Game.HEIGHT);
            gc.setFill(Color.LIMEGREEN);
            gc.setFont(Font.font(48));
            gc.setTextAlign(TextAlignment.CENTER);
            gc.fillText("LEVEL COMPLETE!", Game.WIDTH / 2.0, Game.HEIGHT / 2.0 - 10);
            gc.setTextAlign(TextAlignment.LEFT);
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
            gc.setTextAlign(TextAlignment.CENTER);
            gc.fillText("GAME OVER", Game.WIDTH / 2.0, Game.HEIGHT / 2.0 - 10);
            gc.setFill(Color.WHITE);
            gc.setFont(Font.font(20));
            gc.fillText("Score: " + game.score, Game.WIDTH / 2.0, Game.HEIGHT / 2.0 + 30);
            gc.fillText("Reached Level " + game.level, Game.WIDTH / 2.0, Game.HEIGHT / 2.0 + 60);
            gc.setTextAlign(TextAlignment.LEFT);

            drawButton(gc, "Home", BUTTON_X, BUTTON_Y, Color.DARKRED);
        }

        // Game won
        if (game.gameWon) {
            SoundManager.stopLevelTheme();
            gc.setFill(Color.color(0, 0, 0, 0.6));
            gc.fillRect(0, 0, Game.WIDTH, Game.HEIGHT);
            gc.setFill(Color.GOLD);
            gc.setFont(Font.font(48));
            gc.setTextAlign(TextAlignment.CENTER);
            gc.fillText("YOU WIN!", Game.WIDTH / 2.0, Game.HEIGHT / 2.0 - 10);
            gc.setFill(Color.WHITE);
            gc.setFont(Font.font(20));
            gc.fillText("Score: " + game.score, Game.WIDTH / 2.0, Game.HEIGHT / 2.0 + 30);
            gc.fillText("All " + Game.TOTAL_LEVELS + " levels completed!", Game.WIDTH / 2.0, Game.HEIGHT / 2.0 + 60);
            gc.setTextAlign(TextAlignment.LEFT);

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
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText(text, bx + BUTTON_W / 2.0, by + 26);
        gc.setTextAlign(TextAlignment.LEFT);
    }

    private void drawShop(GraphicsContext gc) {
        // top bar background
        gc.setFill(Color.rgb(222, 184, 135));
        gc.fillRect(0, 0, Game.WIDTH, 80);

        // sun counter box
        double boxX = 10, boxY = 10, boxW = 90, boxH = 40;
        gc.setFill(Color.GOLDENROD);
        gc.fillRoundRect(boxX, boxY, boxW, boxH, 8, 8);

        // measure text width to center sun icon + text together
        String sunText = String.valueOf(game.getSunAmount());
        double iconSize = 16;
        double gap = 8;
        double textWidth = sunText.length() * 10;
        double totalW = iconSize + gap + textWidth;
        double startX = boxX + (boxW - totalW) / 2.0;
        double centerY = boxY + boxH / 2.0;

        // sun icon
        gc.save();
        gc.translate(startX + iconSize / 2.0, centerY - 2);
        gc.scale(0.5, 0.5);
        new Sun(0, 0, 25, false).draw(gc);
        gc.restore();

        // sun amount text
        gc.setFill(Color.BLACK);
        gc.setFont(Font.font(16));
        gc.setTextAlign(TextAlignment.LEFT);
        gc.fillText(sunText, startX + iconSize + gap, centerY + 5);

        // shop slots
        for (int i = 0; i < SHOP_NAMES.length; i++) {
            double x = Game.SHOP_X + i * Game.SHOP_CELL_W;
            double y = Game.SHOP_Y;
            drawSeedPacket(gc, i, x, y);
        }
    }

    private void drawSeedPacket(GraphicsContext gc, int index, double x, double y) {
        double w = Game.SHOP_CELL_W - 10;
        double h = Game.SHOP_CELL_H;
    
        boolean isShovel = (index == Game.SHOVEL_INDEX);
        boolean affordable = isShovel || game.getSunAmount() >= SHOP_COSTS[index];
    
            // card background
        if (game.selectedPlant == index) {
            gc.setFill(Color.rgb(255, 220, 90)); // selected = yellow
        } else if (isShovel) {
            gc.setFill(Color.rgb(170, 85, 0));
        } else if (affordable) {
            gc.setFill(Color.rgb(185, 230, 240)); // normal = blue
        } else {
            gc.setFill(Color.rgb(160, 160, 160));
        }
        gc.fillRoundRect(x, y, w, h, 8, 8);
    
            // border
        gc.setStroke(Color.rgb(110, 80, 40));
        gc.setLineWidth(2);
        gc.strokeRoundRect(x, y, w, h, 8, 8);
    
        if (isShovel) {
            drawShovelIcon(gc, x + w / 2, y + h / 2);
            return;
        }
    
        drawShopPlantIcon(gc, index, x + w / 2, y + 23);
    
        gc.setFill(Color.BLACK);
        gc.setFont(Font.font(12));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText("☀ " + SHOP_COSTS[index], x + w / 2, y + 50);
        gc.setTextAlign(TextAlignment.LEFT);
    
        if (!affordable) {
            gc.setFill(Color.rgb(80, 80, 80, 0.35));
            gc.fillRoundRect(x, y, w, h, 8, 8);
        }
    }
    
    private void drawShopPlantIcon(GraphicsContext gc, int index, double cx, double cy) {
        switch (index) {
            case 0:
                drawSunflowerIcon(gc, cx, cy);
                break;
            case 1:
                drawWallnutIcon(gc, cx, cy);
                break;
            case 2:
                drawPeashooterIcon(gc, cx, cy);
                break;
            case 3:
                drawRepeaterIcon(gc, cx, cy);
                break;
        }
    }
    
    private void drawPauseButton(GraphicsContext gc) {
        double x = PAUSE_BTN_X;
        double y = PAUSE_BTN_Y;
        double s = PAUSE_BTN_SIZE;

        // Circle background
        gc.setFill(Color.rgb(60, 60, 60, 0.85));
        gc.fillOval(x, y, s, s);
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(1.5);
        gc.strokeOval(x, y, s, s);

        // Two vertical bars (standard pause icon)
        double barW = s * 0.12;
        double barH = s * 0.40;
        double cx = x + s / 2;
        double cy = y + s / 2;
        double gap = s * 0.10;

        gc.setFill(Color.WHITE);
        gc.fillRoundRect(cx - gap - barW, cy - barH / 2, barW, barH, 2, 2);
        gc.fillRoundRect(cx + gap,        cy - barH / 2, barW, barH, 2, 2);
    }

    /**
     * Draws the full-screen pause overlay with Resume and Back to Levels buttons.
     */
    private void drawPauseOverlay(GraphicsContext gc) {
        double w = Game.WIDTH;
        double h = Game.HEIGHT;

        // Dim background
        gc.setFill(Color.rgb(0, 0, 0, 0.55));
        gc.fillRect(0, 0, w, h);

        // "PAUSED" title
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 52));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);
        gc.fillText("PAUSED", w / 2, h / 2 - 80);

        // Buttons
        double btnX = w / 2 - OVERLAY_BTN_W / 2;
        double resumeY = h / 2 - 10;
        double levelsY = resumeY + OVERLAY_BTN_H + OVERLAY_BTN_GAP;

        drawOverlayButton(gc, btnX, resumeY, "Resume");
        drawOverlayButton(gc, btnX, levelsY, "Back to Levels");

        // Reset text alignment so other drawing isn't affected next frame
        gc.setTextAlign(TextAlignment.LEFT);
        gc.setTextBaseline(VPos.BASELINE);
    }

    /**
     * Draws a single styled button for the pause overlay.
     */
    private void drawOverlayButton(GraphicsContext gc, double x, double y, String label) {
        // Button background (PvZ-style green)
        gc.setFill(Color.DARKGREY);
        gc.fillRoundRect(x, y, OVERLAY_BTN_W, OVERLAY_BTN_H, 12, 12);

        // Border
        gc.setStroke(Color.rgb(50, 100, 25));
        gc.setLineWidth(2);
        gc.strokeRoundRect(x, y, OVERLAY_BTN_W, OVERLAY_BTN_H, 12, 12);

        // Label
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);
        gc.fillText(label, x + OVERLAY_BTN_W / 2, y + OVERLAY_BTN_H / 2);
    }
    
    private void drawPeashooterIcon(GraphicsContext gc, double x, double y) {
    
        gc.setFill(Color.YELLOWGREEN);
        gc.fillOval(x - 12, y - 8, 20, 20);
    
        gc.fillRoundRect(x + 2, y - 3, 10, 7, 4, 4);
        gc.fillOval(x + 9, y - 5, 7, 10);
    
        gc.setFill(Color.BLACK);
        gc.fillOval(x + 12, y - 2, 3, 5);
    
        gc.setFill(Color.WHITE);
        gc.fillOval(x - 4, y - 3, 5, 6);
        gc.setFill(Color.BLACK);
        gc.fillOval(x - 1.5, y, 2, 3);
    }

    private void drawSunflowerIcon(GraphicsContext gc, double x, double y) {
        gc.setFill(Color.GOLD);
        for (int i = 0; i < 10; i++) {
            double a = i * (Math.PI * 2 / 10.0);
            double px = x + Math.cos(a) * 10;
            double py = y + Math.sin(a) * 10;
            gc.fillOval(px - 4, py - 4, 8, 8);
    
        }
        gc.setFill(Color.rgb(170, 120, 60));
        gc.fillOval(x - 9, y - 9, 18, 18);
        gc.setFill(Color.BLACK);
        gc.fillOval(x - 4, y - 2, 2, 3);
        gc.fillOval(x + 2, y - 2, 2, 3);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1);
        gc.strokeArc(x - 3, y + 1, 6, 3, 180, 180, javafx.scene.shape.ArcType.OPEN);
    }

    private void drawWallnutIcon(GraphicsContext gc, double x, double y) {
        gc.setFill(Color.rgb(190, 150, 70));
        gc.fillOval(x - 10, y - 12, 20, 26);
        gc.setFill(Color.rgb(250, 245, 220));
        gc.fillOval(x - 6, y - 3, 5, 7);
        gc.fillOval(x + 1, y - 3, 5, 7);
        gc.setFill(Color.BLACK);
        gc.fillOval(x - 4, y, 1.8, 2);
        gc.fillOval(x + 3, y, 1.8, 2);
        gc.setStroke(Color.rgb(90, 70, 30));
        gc.setLineWidth(1);
        gc.strokeArc(x - 3, y + 4, 6, 3, 180, 180, javafx.scene.shape.ArcType.OPEN);
    }
    
    private void drawRepeaterIcon(GraphicsContext gc, double x, double y) {
        double bx = x - 5;
        double by = y - 4;
        gc.setFill(Color.YELLOWGREEN);
        gc.fillOval(bx - 9, by - 6, 14, 14);
        gc.fillRoundRect(bx + 1, by - 1, 7, 5, 4, 4);
        gc.fillOval(bx + 6, by - 2, 5, 8);
        gc.setFill(Color.BLACK);
        gc.fillOval(bx + 8.5, by, 2, 3.5);
        double fx = x + 4;
        double fy = y + 1;
        gc.setFill(Color.rgb(140, 180, 70));
        gc.fillOval(fx - 10, fy - 7, 16, 16);
        gc.setFill(Color.YELLOWGREEN);
        gc.fillOval(fx - 9, fy - 6, 14, 14);
        gc.fillRoundRect(fx + 1, fy - 1, 7, 5, 4, 4);
        gc.fillOval(fx + 6, fy - 2, 5, 8);
        gc.setFill(Color.BLACK);
        gc.fillOval(fx + 8.5, fy, 2, 3.5);
        gc.setFill(Color.WHITE);
        gc.fillOval(fx - 1, fy - 1, 3.5, 4.5);
        gc.setFill(Color.BLACK);
        gc.fillOval(fx + 0.5, fy + 1, 1.4, 2);
        gc.setFill(Color.WHITE);
        gc.fillOval(bx - 1, by - 1, 3.5, 4.5);
        gc.setFill(Color.BLACK);
        gc.fillOval(bx + 0.5, by + 1, 1.4, 2);
    }
    
    private void drawShovelIcon(GraphicsContext gc, double cx, double cy) {
        gc.save();
        gc.translate(cx, cy);
        gc.rotate(30);
        gc.setFill(Color.rgb(180, 120, 60));
        gc.fillRoundRect(-3, -22, 6, 30, 3, 3);
        gc.setFill(Color.rgb(140, 90, 40));
        gc.fillRoundRect(-5, -24, 10, 6, 3, 3);
        gc.setFill(Color.SILVER);
        gc.fillRoundRect(-10, 6, 20, 16, 6, 6);
        gc.setFill(Color.rgb(220, 220, 220, 0.5));
        gc.fillRoundRect(-6, 8, 5, 12, 2, 2);
        gc.restore();
    }
    
    private void drawHouse(GraphicsContext gc) {
        // === WALL (front face — full area behind roof) ===
        // extends from roof ridge down to ground, covers everything
        double[] wallX = {0, 114, 114, 64, 0};
        double[] wallY = {525, 525, 175, 110, 80};
        gc.setFill(Color.web("#F5F5DC"));
        gc.fillPolygon(wallX, wallY, 5);
        // subtle horizontal lines for siding
        gc.setStroke(Color.web("#E8E0C8"));
        gc.setLineWidth(1);
        for (int i = 1; i <= 12; i++) {
            double t = i / 13.0;
            double yRight = 175 + (525 - 175) * t;
            double yLeft = 80 + (525 - 80) * t;
            double xLeft = 0 + (0) * t;
            gc.strokeLine(xLeft, yLeft, 114, yRight);
        }
        // wall outline (only the visible edges: right side + bottom + top diagonal)
        gc.setStroke(Color.web("#3B3B3B"));
        gc.setLineWidth(4);
        gc.strokeLine(114, 175, 114, 525); // right edge
        gc.strokeLine(0, 525, 114, 525);   // bottom edge
        gc.strokeLine(64, 110, 114, 175);  // top diagonal
    
        // === ROOF (side strip — drawn on top of wall) ===
        double[] roofX = {0, 64, 64, 0};
        double[] roofY = {80, 85, 545, 525};
        gc.setFill(Color.web("#C4736A"));
        gc.fillPolygon(roofX, roofY, 4);
        // roof tile lines
        gc.setStroke(Color.web("#A85A50"));
        gc.setLineWidth(1.5);
        for (int i = 1; i < 14; i++) {
            double t = i / 14.0;
            double ly = 80 + (525 - 80) * t;
            double ry = 85 + (545 - 85) * t;
            gc.strokeLine(0, ly, 64, ry);
        }
        gc.setStroke(Color.web("#3B3B3B"));
        gc.setLineWidth(4);
        gc.strokePolygon(roofX, roofY, 4);
    
        //door
        double[] doorX = {114, 90, 90, 114};
        double[] doorY = {215, 200, 260, 275};
        gc.setFill(Color.web("#8B4513"));
        gc.fillPolygon(doorX, doorY, 4);
        // door panel detail
        gc.setStroke(Color.web("#6B3410"));
        gc.setLineWidth(1.5);
        gc.strokePolygon(
            new double[]{110, 93, 93, 110},
            new double[]{225, 213, 250, 262}, 4);
        // door handle
        gc.setFill(Color.web("#DAA520"));
        gc.fillOval(107, 243, 5, 5);
        // door outline
        gc.setStroke(Color.web("#3B3B3B"));
        gc.setLineWidth(3);
        gc.strokePolygon(doorX, doorY, 4);
    
        // window
        double[] winX = {80, 80, 110, 110};
        double[] winY = {315, 415, 425, 325};
        gc.setFill(Color.web("#87CEEB"));
        gc.fillPolygon(winX, winY, 4);
        // window reflection
        gc.setFill(Color.web("#ADDFFF"));
        gc.fillPolygon(
            new double[]{82, 82, 93, 93},
            new double[]{320, 370, 375, 328}, 4);
        // window cross frame
        gc.setStroke(Color.web("#F5F5DC"));
        gc.setLineWidth(3);
        double midWinY = (315 + 425) / 2.0;
        double midWinY2 = (325 + 415) / 2.0;
        gc.strokeLine(80, midWinY, 110, midWinY2);
        double midWinX = 95;
        gc.strokeLine(midWinX, (315 + 325) / 2.0, midWinX, (415 + 425) / 2.0);
        // window outline
        gc.setStroke(Color.web("#3B3B3B"));
        gc.setLineWidth(3);
        gc.strokePolygon(winX, winY, 4);
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