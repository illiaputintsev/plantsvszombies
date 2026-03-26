import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;

public class MenuUI {
    private Stage stage;

    public MenuUI(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        Canvas canvas = new Canvas(Game.WIDTH, Game.HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        drawBackground(gc);
        drawHouse(gc);

        AnchorPane overlay = createOverlay();

        StackPane root = new StackPane();
        root.getChildren().addAll(canvas, overlay);

        Scene scene = new Scene(root, Game.WIDTH, Game.HEIGHT);
        stage.setTitle("Plants vs Zombies");
        stage.setScene(scene);
        stage.show();
    }

    private void drawBackground(GraphicsContext gc) {
        // sky
        gc.setFill(Color.LIGHTBLUE);
        gc.fillRect(0, 0, 1000, 1000);

        // sun
        gc.setFill(Color.YELLOW);
        gc.fillOval(25, 25, 150, 150);
        
        // gravestone body
        gc.setFill(Color.LIGHTGRAY);
        gc.fillRoundRect(705, 280, 200, 400, 80, 60);
        
        // ground
        gc.setFill(Color.LIGHTGREEN);
        gc.fillOval(-200, 400, 900, 300);

        // dark ground under gravestone
        gc.setFill(Color.DARKGREEN);
        gc.fillOval(650, 520, 400, 150);
        gc.fillOval(400, 500, 400, 170);
    }

    private void drawHouse(GraphicsContext gc) {
        // walls
        gc.setFill(Color.web("#f5deb3"));
        gc.fillRect(200, 360, 60, 40);

        // roof
        gc.setFill(Color.web("#8b3a3a"));
        gc.fillPolygon(
            new double[]{195, 230, 265},    
            new double[]{360, 330, 360}, 
            3);

        // door
        gc.setFill(Color.BROWN);
        gc.fillRect(205, 380, 10, 20);

        // window
        gc.setFill(Color.WHITE);
        gc.fillRect(230, 375, 15, 15);
    }

    private AnchorPane createOverlay() {
        AnchorPane overlay = new AnchorPane();

        Label titleLabel = createLabel("Plants vs Zombies", 48);
        Button btn = createButton("Adventure");
        Label label1 = createLabel("Illia", 22);
        Label label2 = createLabel("Mario", 22);
        Label label3 = createLabel("Mark", 22);
        Label label4 = createLabel("Pranay", 22);

        Circle sun = new Circle(100, 100, 75, Color.YELLOW);

        anchor(titleLabel, 50.0,  350.0);
        anchor(btn,        300.0, 730.0);
        anchor(label1,     355.0, 770.0);
        anchor(label2,     398.0, 768.0);
        anchor(label3,     439.0, 769.0);
        anchor(label4,     484.0, 765.0);

        overlay.getChildren().addAll(sun, titleLabel, btn, label1, label2, label3, label4);
        return overlay;
    }

    private Label createLabel(String text, double fontSize) {
        Label label = new Label(text);
        label.setFont(Font.font("Imperial", fontSize));
        label.setTextFill(Color.BLACK);
        return label;
    }

    private Button createButton(String text) {
        Button btn = new Button(text);
        btn.setFont(Font.font("Imperial", 26));
        btn.setOnAction(e -> startGame());
        return btn;
    }

    private void anchor(javafx.scene.Node node, double top, double left) {
        AnchorPane.setTopAnchor(node, top);
        AnchorPane.setLeftAnchor(node, left);
    }

    private void startGame() {
        Game game = new Game();
        GameUI ui = new GameUI(game, stage);
        game.startGame(ui);
    }
}