import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

/**
 * LevelUI class - shows the level selection screen.
 * Displays completed, available and locked levels
 */
public class LevelUI {
    private Game game;
    private Stage stage;

    // level button layout
    private static final double LEVEL_BTN_SIZE = 80;
    private static final double LEVEL_BTN_GAP = 20;
    private static final double LEVEL_START_X = 150;
    private static final double LEVEL_START_Y = 250;

    // back button
    private static final double BACK_X = 30;
    private static final double BACK_Y = 30;
    private static final double BACK_W = 120;
    private static final double BACK_H = 40;

    public LevelUI(Game game, Stage stage) {
        this.game = game;
        this.stage = stage;
        SoundManager.stopGameOver();
    }

    /**
     * Shows the level select screen on the stage.
     */
    public void show() {
        SoundManager.stopLevelTheme();
        Canvas canvas = new Canvas(Game.WIDTH, Game.HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        drawLevelSelect(gc, -1);

        canvas.setOnMouseMoved(e -> {
            int hovered = getLevelAtPosition(e.getX(), e.getY());
            drawLevelSelect(gc, hovered);
        });

        canvas.setOnMouseClicked(e -> {
            double clickX = e.getX();
            double clickY = e.getY();

            // back button
            if (clickX >= BACK_X && clickX <= BACK_X + BACK_W
                && clickY >= BACK_Y && clickY <= BACK_Y + BACK_H) {
                MenuUI menu = new MenuUI(stage, game);
                menu.show();
                SoundManager.playMenuBtn();
                return;
            }

            // check if a level button was clicked
            int clickedLevel = getLevelAtPosition(clickX, clickY);
            if (clickedLevel > 0 && clickedLevel <= game.getMaxLevel() + 1) {
                game.clearBoard();
                GameUI ui = new GameUI(game, stage);
                SoundManager.playMenuBtn();
                SoundManager.stopMenuTheme();
                game.startGame(ui, stage, clickedLevel);
            }
        });

        VBox root = new VBox();
        root.getChildren().addAll(Main.createMenuBar(), new StackPane(canvas));
        stage.setScene(new Scene(root));
        stage.setTitle("Plants vs Zombies - Level Select");
        stage.show();
    }

    /**
     * Figures out which level button the mouse is over.
     * Returns -1 if not hovering over any
     */
    private int getLevelAtPosition(double mouseX, double mouseY) {
        for (int i = 0; i < Game.TOTAL_LEVELS; i++) {
            double btnX = LEVEL_START_X + i * (LEVEL_BTN_SIZE + LEVEL_BTN_GAP);
            double btnY = LEVEL_START_Y;
            if (mouseX >= btnX && mouseX <= btnX + LEVEL_BTN_SIZE
                && mouseY >= btnY && mouseY <= btnY + LEVEL_BTN_SIZE) {
                return i + 1;
            }
        }
        return -1;
    }

    /**
     * Draws the level select screen with all buttons.
     */
    private void drawLevelSelect(GraphicsContext gc, int hoveredLevel) {
        gc.clearRect(0, 0, Game.WIDTH, Game.HEIGHT);

        // background
        gc.setFill(Color.rgb(40, 80, 40));
        gc.fillRect(0, 0, Game.WIDTH, Game.HEIGHT);

        // title
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Impact", FontWeight.BOLD, 42));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText("Select Level", Game.WIDTH / 2.0, 120);

        // subtitle
        gc.setFont(Font.font(16));
        gc.setFill(Color.LIGHTGRAY);
        if (game.getMaxLevel() == 0) {
            gc.fillText("Start your adventure!", Game.WIDTH / 2.0, 160);
        } else if (game.getMaxLevel() >= Game.TOTAL_LEVELS) {
            gc.fillText("All levels completed!", Game.WIDTH / 2.0, 160);
        } else {
            gc.fillText("Levels completed: " + game.getMaxLevel() + " / " + Game.TOTAL_LEVELS,
                Game.WIDTH / 2.0, 160);
        }
        gc.setTextAlign(TextAlignment.LEFT);

        // draw level buttons
        for (int i = 0; i < Game.TOTAL_LEVELS; i++) {
            int levelNum = i + 1;
            double btnX = LEVEL_START_X + i * (LEVEL_BTN_SIZE + LEVEL_BTN_GAP);
            double btnY = LEVEL_START_Y;

            boolean isCompleted = levelNum <= game.getMaxLevel();
            boolean isAvailable = levelNum == game.getMaxLevel() + 1;
            boolean isLocked = levelNum > game.getMaxLevel() + 1;
            boolean isHovered = levelNum == hoveredLevel;

            // button background colour
            if (isCompleted) {
                gc.setFill(Color.FORESTGREEN);
            } else if (isAvailable) {
                gc.setFill(isHovered ? Color.LIMEGREEN : Color.YELLOWGREEN);
            } else {
                gc.setFill(Color.rgb(80, 80, 80));
            }
            gc.fillRoundRect(btnX, btnY, LEVEL_BTN_SIZE, LEVEL_BTN_SIZE, 12, 12);

            // border
            if (isAvailable) {
                gc.setStroke(Color.YELLOW);
                gc.setLineWidth(3);
            } else if (isCompleted && isHovered) {
                gc.setStroke(Color.WHITE);
                gc.setLineWidth(2);
            } else {
                gc.setStroke(Color.rgb(60, 60, 60));
                gc.setLineWidth(1);
            }
            gc.strokeRoundRect(btnX, btnY, LEVEL_BTN_SIZE, LEVEL_BTN_SIZE, 12, 12);

            // level number
            gc.setFont(Font.font("Impact", FontWeight.BOLD, 32));
            gc.setFill(isLocked ? Color.GRAY : Color.WHITE);
            double textX = btnX + LEVEL_BTN_SIZE / 2.0 - 8;
            double textY = btnY + LEVEL_BTN_SIZE / 2.0 + 10;
            gc.fillText("" + levelNum, textX, textY);

            // label
            gc.setFont(Font.font(12));
            if (isCompleted) {
                gc.setFill(Color.LIGHTGREEN);
                gc.fillText("Done", btnX + LEVEL_BTN_SIZE / 2.0 - 14, btnY + LEVEL_BTN_SIZE + 18);
            } else if (isLocked) {
                gc.setFill(Color.GRAY);
                gc.fillText("Locked", btnX + LEVEL_BTN_SIZE / 2.0 - 18, btnY + LEVEL_BTN_SIZE + 18);
            }
        }

        // back button
        gc.setFill(Color.DARKGRAY);
        gc.fillRoundRect(BACK_X, BACK_Y, BACK_W, BACK_H, 8, 8);
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font(16));
        gc.fillText("< Back", BACK_X + 30, BACK_Y + 26);

        gc.setLineWidth(1);
    }
}
