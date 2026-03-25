import javafx.animation.AnimationTimer;
import javafx.application.Platform;
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

    public GameUI(Game game, Stage stage) {
        this.game = game;
        this.stage = stage;
    }

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
                game.update(deltaTime);
                render(gc);
            }
        }.start();
    }

    private void handleClick(double mx, double my) {
    
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
            }
            // FIX: Reset selection after using the shovel
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

    private void render(GraphicsContext gc) {
        gc.clearRect(0, 0, Game.WIDTH, Game.HEIGHT);

        drawShop(gc);

        for (int r = 0; r < Game.ROWS; r++) {
            for (int c = 0; c < Game.COLS; c++) {
                double x = Game.GRID_X + c * Game.CELL_W;
                double y = Game.GRID_Y + r * Game.CELL_H;

                if ((r + c) % 2 == 0) {
                    gc.setFill(Color.PALEGREEN);
                } else {
                    gc.setFill(Color.SEAGREEN);
                }
                gc.fillRect(x, y, Game.CELL_W, Game.CELL_H);
            }
        }

        for (Plants p : game.Plant) {
            if (p.isAlive()) {
                p.draw(gc);
            }
        }
        for (Zombies z : game.Zombie) {
            if (z.isAlive()) {
                z.draw(gc);
            }
        }
        for (Bullet b : game.bullets) {
            b.draw(gc);
        }

        // Sun and score counters
        gc.setFill(Color.BLACK);
        gc.setFont(Font.font(14));
        gc.fillText("Sun: " + game.sun, 10, 30);
        gc.fillText("Score: " + game.score, 10, 50);

        if (game.gameOver) {
            gc.setFill(Color.color(0, 0, 0, 0.6));
            gc.fillRect(0, 0, Game.WIDTH, Game.HEIGHT);
            gc.setFill(Color.RED);
            gc.setFont(Font.font(48));
            gc.fillText("GAME OVER", Game.WIDTH / 2.0 - 140, Game.HEIGHT / 2.0 - 10);
            gc.setFill(Color.WHITE);
            gc.setFont(Font.font(20));
            gc.fillText("Score: " + game.score, Game.WIDTH / 2.0 - 40, Game.HEIGHT / 2.0 + 30);
        }
    }

    private void drawShop(GraphicsContext gc) {
        for (int i = 0; i < SHOP_NAMES.length; i++) {
            double x = Game.SHOP_X + i * Game.SHOP_CELL_W;
            double y = Game.SHOP_Y;

            // Highlighting slots 
            if (i == game.selectedPlant) {
                //currently selected
                gc.setFill(Color.GOLD);
            } else if (i == Game.SHOVEL_INDEX) {
                // always brown for shovel
                gc.setFill(Color.SADDLEBROWN);
            } else if (game.sun >= SHOP_COSTS[i]) {
                // the plants you can buy
                gc.setFill(Color.LIGHTBLUE);
            } else {
                // cannot buy
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
}