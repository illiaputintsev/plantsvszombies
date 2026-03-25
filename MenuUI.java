import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.text.FontWeight;
import javafx.scene.effect.DropShadow;

/**
    (0,0) ────────────────── (1000, 0)
    │                          │
    │                          │
    │      your canvas         │
    │                          │
    │                          │
    (0,600) ──────────────── (1000, 600)
 */

public class MenuUI{
    private Stage stage;
    
    public MenuUI(Stage stage) {
        this.stage = stage;
    }
    
    public void show(){
        Canvas canvas = new Canvas(Game.WIDTH, Game.HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        
        drawMenu(gc, false);
        
        canvas.setOnMouseMoved(e -> {
            double mx = e.getX();
            double my = e.getY();
            boolean hovering = (mx >= 730 && mx <= 870 && my >= 360 && my <= 400);
            drawMenu(gc, hovering);
        });
        
        canvas.setOnMouseClicked(e -> {
            double mx = e.getX();
            double my = e.getY();

            // Start button bounds
            if (mx >= 730 && mx <= 870 && my >= 360 && my <= 400) {
                startGame();
            }
        });
        
        stage.setScene(new Scene(new StackPane(canvas)));
        stage.setTitle("Plants vs Zombies");
        stage.show();
    }
    
    private void startGame() {
        Game game = new Game();
        GameUI ui = new GameUI(game, stage);
        game.startGame(ui);
    }
    
    private void drawMenu(GraphicsContext gc, boolean hovering){
        gc.clearRect(0, 0, Game.WIDTH, Game.HEIGHT);
    
        gc.setFill(Color.DARKGREEN);
        gc.fillRect(0, 0, Game.WIDTH, Game.HEIGHT);
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font(48));
        gc.fillText("Plants vs Zombies", 250, 200);
    
        gc.setFill(Color.LIGHTGRAY);
        gc.fillRoundRect(700, 350, 200, 300, 80, 60);
    
        // Adventure button
        if (hovering) {
            gc.setEffect(new DropShadow(15, Color.BLACK));
        }
        
        gc.setFill(Color.DARKGRAY);
        gc.fillRect(730, 360, 140, 40);
        gc.setEffect(null);
        gc.setFont(Font.font("Impact", FontWeight.BOLD, 32));
        gc.setFill(Color.BLACK);
        gc.fillText("Adventure", 733, 390);

        // Illia
        gc.setFont(Font.font("Impact", FontWeight.BOLD, 20));
        gc.setFill(Color.DARKGRAY);
        gc.fillRect(730, 415, 140, 30);
        gc.setFill(Color.BLACK);
        gc.fillText("Illia", 733, 438);

        // Mario
        gc.setFill(Color.DARKGRAY);
        gc.fillRect(730, 458, 140, 30);
        gc.setFill(Color.BLACK);
        gc.fillText("Mario", 733, 481);

        // Mark
        gc.setFill(Color.DARKGRAY);
        gc.fillRect(730, 501, 140, 30);
        gc.setFill(Color.BLACK);
        gc.fillText("Mark", 733, 524);

        // Pranay
        gc.setFill(Color.DARKGRAY);
        gc.fillRect(730, 544, 140, 30);
        gc.setFill(Color.BLACK);
        gc.fillText("Pranay", 733, 567);
    }
}