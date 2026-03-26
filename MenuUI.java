import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.AnchorPane;
import javafx.geometry.Pos;

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
        // 1. Create the canvas (background drawing)
        Canvas canvas = new Canvas(Game.WIDTH, Game.HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        
        // sky
        gc.setFill(Color.LIGHTBLUE);
        gc.fillRect(0, 0, 1000, 500);
        
        // gravestone
        gc.setFill(Color.LIGHTGRAY);
        //               x,   y,  w,   h,    arcW, arcH
        gc.fillRoundRect(705, 350, 200, 400, 80, 60);
        
        // 2. Create all your controls
        Label titleLabel = new Label("Plants vs Zombies");
        Button btn = new Button("Adventure");
        Label label1 = new Label("Illia");
        Label label2 = new Label("Mario");
        Label label3 = new Label("Mark");
        Label label4 = new Label("Pranay");
        
        titleLabel.setFont(Font.font("Imperial", 48));
        btn.setFont(Font.font("Imperial", 26));
        label1.setFont(Font.font("Imperial", 26));
        label2.setFont(Font.font("Imperial", 26));
        label3.setFont(Font.font("Imperial", 26));
        label4.setFont(Font.font("Imperial", 26));
        
        // 3. Create AnchorPane and position everything
        AnchorPane overlay = new AnchorPane();
        
        AnchorPane.setTopAnchor(titleLabel, 50.0);
        AnchorPane.setLeftAnchor(titleLabel, 350.0);
        
        AnchorPane.setTopAnchor(btn, 360.0);
        AnchorPane.setLeftAnchor(btn, 730.0);
        
        AnchorPane.setTopAnchor(label1, 415.0);
        AnchorPane.setLeftAnchor(label1, 770.0);
        
        AnchorPane.setTopAnchor(label2, 458.0);
        AnchorPane.setLeftAnchor(label2, 768.0);
        
        AnchorPane.setTopAnchor(label3, 501.0);
        AnchorPane.setLeftAnchor(label3, 769.0);
        
        AnchorPane.setTopAnchor(label4, 544.0);
        AnchorPane.setLeftAnchor(label4, 765.0);
        
        overlay.getChildren().addAll(titleLabel, btn, label1, label2, label3, label4);
        
        // 4. Layer canvas + overlay in a StackPane
        StackPane root = new StackPane();
        root.getChildren().addAll(canvas, overlay);
        
        btn.setOnAction(e -> startGame());
        
        // 5. Build and show the scene
        Scene scene = new Scene(root, Game.WIDTH, Game.HEIGHT);
        stage.setTitle("Plants vs Zombies");
        stage.setScene(scene);
        stage.show();
    }
    
    private void startGame() {
        Game game = new Game();
        GameUI ui = new GameUI(game, stage);
        game.startGame(ui);
    }
}