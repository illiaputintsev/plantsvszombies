import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;

public class MenuUI {
    private Stage stage;
    private Game game;
    private TutorialUI tutorial;

    public MenuUI(Stage stage, Game game) {
        this.stage = stage;
        this.game = game;
        SoundManager.playMenuTheme();
    }

    public void show() {
        Canvas canvas = new Canvas(Game.WIDTH, Game.HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        drawBackground(gc);
        drawHouse(gc);
        drawZombies(gc);
        
        // tutorial overlay canvas (sits on top of everything)
        Canvas tutorialCanvas = new Canvas(Game.WIDTH, Game.HEIGHT);
        tutorialCanvas.setMouseTransparent(true);
        GraphicsContext tgc = tutorialCanvas.getGraphicsContext2D();
 
        tutorial = new TutorialUI(Game.WIDTH, Game.HEIGHT, () -> {
            tgc.clearRect(0, 0, Game.WIDTH, Game.HEIGHT);
            tutorialCanvas.setMouseTransparent(true);
        });

        AnchorPane overlay = createOverlay(tutorialCanvas, tgc);

        StackPane gameLayer = new StackPane();
        gameLayer.getChildren().addAll(canvas, overlay, tutorialCanvas);

        VBox root = new VBox();
        root.getChildren().addAll(Main.createMenuBar(), gameLayer);

        Scene scene = new Scene(root, Game.WIDTH, Game.HEIGHT);
        stage.setTitle("Plants vs Zombies");
        stage.setScene(scene);
        
        stage.setResizable(false);
        stage.show();
    }

    private void drawBackground(GraphicsContext gc) {
        // sky
        gc.setFill(Color.LIGHTBLUE);
        gc.fillRect(0, 0, 1000, 1000);

        // gravestone body 
        gc.setFill(Color.LIGHTGRAY);
        gc.fillRoundRect(680, 250, 250, 450, 80, 60);

        // ground
        gc.setFill(Color.LIGHTGREEN);
        gc.fillOval(-200, 390, 900, 300);

        // dark ground under gravestone
        gc.setFill(Color.DARKGREEN);
        gc.fillOval(650, 520, 400, 150);
        gc.fillOval(400, 500, 400, 170);
    }

    private void drawHouse(GraphicsContext gc) {
        // Base / Foundation 
        gc.setFill(Color.web("#8D8075"));
        gc.fillRect(206, 395, 48, 2);

        // Walls 
        gc.setFill(Color.web("#F4F0D6"));
        gc.fillRect(210, 365, 40, 30);

        // Roof
        gc.setFill(Color.web("#C86A66"));
        gc.fillPolygon(
            new double[]{205, 230, 255}, 
            new double[]{365, 345, 365},
            3);

        // Door 
        gc.setFill(Color.web("#875126"));
        gc.fillRect(225, 378, 10, 17);

        // Windows 
        gc.setFill(Color.web("#6BB3E3"));
        gc.fillRect(213, 370, 8, 8);
        gc.fillRect(239, 370, 8, 8);
    }
    
    private void drawZombies(GraphicsContext gc) {
        // Left side of the house
        drawBackgroundZombie(gc, 60, 450);
        drawBackgroundZombie(gc, 100, 410);
        drawBackgroundZombie(gc, 140, 490);

        // Right side of the house
        drawBackgroundZombie(gc, 330, 420);
        drawBackgroundZombie(gc, 360, 480);
        drawBackgroundZombie(gc, 390, 450);
        drawBackgroundZombie(gc, 420, 510);
        drawBackgroundZombie(gc, 450, 440);
        drawBackgroundZombie(gc, 480, 490);
    }

    private void drawBackgroundZombie(GraphicsContext gc, double x, double y) {
        gc.save(); // Save the current state of the canvas
        
        // Move to the target X/Y, then scale everything down to 30% size
        gc.translate(x, y); 
        gc.scale(0.3, 0.3); 

        // legs
        gc.setFill(Color.DARKSLATEGRAY);
        gc.fillRect(-8, 8, 7, 16);
        gc.fillRect(4, 8, 7, 16);

        // body
        gc.setFill(Color.DARKKHAKI);
        gc.fillRect(-10, -16, 24, 26);

        // arms
        gc.setFill(Color.DARKSEAGREEN);
        gc.fillRect(-18, -8, 10, 6);
        gc.fillRect(14, -12, 10, 6);

        // head
        gc.setFill(Color.YELLOWGREEN);
        gc.fillOval(-10, -34, 22, 22);

        // eyes
        gc.setFill(Color.RED);
        gc.fillOval(-5, -28, 4, 4);
        gc.fillOval(4, -28, 4, 4);

        gc.restore(); // Restore the canvas
    }

    private AnchorPane createOverlay(Canvas tutorialCanvas, GraphicsContext tgc) {
        AnchorPane overlay = new AnchorPane();

        Label titleLabel = createLabel("Plants vs Zombies", 48);
        
        //font sizes
        Button adventureBtn = createButton("Adventure", 24, e -> startGame());
        Button howToPlayBtn = createButton("How To Play", 24, e -> openTutorial(tutorialCanvas, tgc));

        //both buttons to be the exact same width
        adventureBtn.setPrefWidth(200);
        howToPlayBtn.setPrefWidth(200);

        Label creditsLabel1 = createLabel("Illia  ·  Mario", 16);
        Label creditsLabel2 = createLabel("Mark  ·  Pranay", 16);
        
        // Draw sun icon on a small canvas
        Canvas sunCanvas = new Canvas(250, 250);
        Sun.drawSunIcon(sunCanvas.getGraphicsContext2D(), 75, 75, 3.0);

        // VBox to centre and stack
        VBox menuBox = new VBox(15); 
        menuBox.setAlignment(javafx.geometry.Pos.TOP_CENTER);
        menuBox.setPrefWidth(250); 
        
        menuBox.getChildren().addAll(adventureBtn, howToPlayBtn, creditsLabel1, creditsLabel2);

        // Anchor the title and the menu box
        anchor(titleLabel, 50.0, 350.0);
        anchor(menuBox, 270.0, 680.0); 

        overlay.getChildren().addAll(sunCanvas, titleLabel, menuBox);
        return overlay;
    }

    private void openTutorial(Canvas tutorialCanvas, GraphicsContext tgc) {
        SoundManager.playMenuBtn();
        tutorial.open();
        tutorialCanvas.setMouseTransparent(false);
        tutorial.draw(tgc);
 
        tutorialCanvas.setOnMouseClicked(e -> {
            if (!tutorial.isVisible()) return;
            tutorial.handleClick(e.getX(), e.getY());
            tgc.clearRect(0, 0, Game.WIDTH, Game.HEIGHT);
            if (tutorial.isVisible()) {
                tutorial.draw(tgc);
            } else {
                tutorialCanvas.setMouseTransparent(true);
            }
        });
    }
    
    private Label createLabel(String text, double fontSize) {
        Label label = new Label(text);
        label.setFont(Font.font("Imperial", fontSize));
        label.setTextFill(Color.BLACK);
        return label;
    }

    private Button createButton(String text, double fontSize, javafx.event.EventHandler<javafx.event.ActionEvent> handler) {
        Button btn = new Button(text);
        btn.setFont(Font.font("Imperial", fontSize));
        btn.setOnAction(handler);
        return btn;
    }

    private void anchor(javafx.scene.Node node, double top, double left) {
        AnchorPane.setTopAnchor(node, top);
        AnchorPane.setLeftAnchor(node, left);
    }

    private void startGame() {
        LevelUI levelSelect = new LevelUI(game, stage);
        SoundManager.playMenuBtn();
        levelSelect.show();
    }
}
