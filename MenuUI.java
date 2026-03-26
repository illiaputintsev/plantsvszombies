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
import javafx.scene.shape.*;

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
        
        // sky
        gc.setFill(Color.LIGHTBLUE);
        gc.fillRect(0, 0, 1000, 1000);
        
        // ground
        gc.setFill(Color.LIGHTGREEN);
        gc.fillOval(-200, 400, 900, 300);
        
        // gravestone
        gc.setFill(Color.LIGHTGRAY);
        //               x,   y,  w,   h,    arcW, arcH
        gc.fillRoundRect(705, 280, 200, 400, 80, 60);
        
        // ground under gravestone
        gc.setFill(Color.DARKGREEN);
        //          x,  y,   w,   h
        gc.fillOval(650, 520, 400, 150);
        gc.setFill(Color.DARKGREEN);
        gc.fillOval(400, 500, 400, 170);
        
        //house
        gc.setFill(Color.web("#f5deb3"));
        gc.fillRect(200, 360, 60, 40);
        
        //roof
        gc.setFill(Color.web("#8b3a3a"));
        gc.fillPolygon(
        new double[]{195, 230, 265},  // x points
        new double[]{360, 330, 360},  // y points
        3);
        
        // door
        gc.setFill(Color.BROWN);
        gc.fillRect(205, 380, 10, 20);
        
        //window
        gc.setFill(Color.WHITE);
        gc.fillRect(230,  375, 15, 15);
        
        //sun
        Circle circle = new Circle();
        circle.setFill(Color.YELLOW);
        circle.setCenterX(100.0f);
        circle.setCenterY(100.0f);
        circle.setRadius(75.0f);
        
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
        
        AnchorPane overlay = new AnchorPane();
        
        AnchorPane.setTopAnchor(titleLabel, 50.0);
        AnchorPane.setLeftAnchor(titleLabel, 350.0);
        
        AnchorPane.setTopAnchor(btn, 300.0);
        AnchorPane.setLeftAnchor(btn, 730.0);
        
        //illia
        AnchorPane.setTopAnchor(label1, 355.0);
        AnchorPane.setLeftAnchor(label1, 770.0);
        
        //mario
        AnchorPane.setTopAnchor(label2, 398.0);
        AnchorPane.setLeftAnchor(label2, 768.0);
        
        //mark
        AnchorPane.setTopAnchor(label3, 439.0);
        AnchorPane.setLeftAnchor(label3, 769.0);
        
        //pranay
        AnchorPane.setTopAnchor(label4, 484.0);
        AnchorPane.setLeftAnchor(label4, 765.0);
        
        overlay.getChildren().addAll(titleLabel, btn, label1, label2, label3, label4);
        overlay.getChildren().add(circle);

        StackPane root = new StackPane();
        root.getChildren().addAll(canvas, overlay);
        
        btn.setOnAction(e -> startGame());
        
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