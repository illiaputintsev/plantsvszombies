import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.FontPosture;
import javafx.stage.Stage;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.AnchorPane;
import javafx.animation.AnimationTimer;

public class MenuUI {
    private Stage stage;
    private Game game;
    private TutorialUI tutorial;
    private AnimationTimer menuAnimation;

    // zombie positions (base x, base y)
    private double[][] zombiePositions = {
        {60, 450}, {100, 410}, {140, 490},
        {330, 420}, {360, 480}, {390, 450},
        {420, 510}, {450, 440}, {480, 490}
    };

    public MenuUI(Stage stage, Game game) {
        this.stage = stage;
        this.game = game;
        SoundManager.playMenuTheme();
    }

    public void show() {
        // static background canvas (sky, ground, gravestone, house)
        Canvas bgCanvas = new Canvas(Game.WIDTH, Game.HEIGHT);
        GraphicsContext bgGc = bgCanvas.getGraphicsContext2D();
        drawBackground(bgGc);
        drawHouse(bgGc);

        // animated canvas for zombies and sun
        Canvas animCanvas = new Canvas(Game.WIDTH, Game.HEIGHT);
        GraphicsContext agc = animCanvas.getGraphicsContext2D();
        animCanvas.setMouseTransparent(true);

        // tutorial overlay canvas
        Canvas tutorialCanvas = new Canvas(Game.WIDTH, Game.HEIGHT);
        tutorialCanvas.setMouseTransparent(true);
        GraphicsContext tgc = tutorialCanvas.getGraphicsContext2D();

        tutorial = new TutorialUI(Game.WIDTH, Game.HEIGHT, () -> {
            tgc.clearRect(0, 0, Game.WIDTH, Game.HEIGHT);
            tutorialCanvas.setMouseTransparent(true);
        });

        AnchorPane overlay = createOverlay(tutorialCanvas, tgc);

        // start animation loop
        menuAnimation = new AnimationTimer() {
            private long startTime = -1;
            public void handle(long now) {
                if (startTime < 0) startTime = now;
                double time = (now - startTime) / 1_000_000_000.0;
                agc.clearRect(0, 0, Game.WIDTH, Game.HEIGHT);
                drawAnimatedZombies(agc, time);
                drawAnimatedSun(agc, time);
                drawTitle(agc, time);
            }
        };
        menuAnimation.start();

        StackPane gameLayer = new StackPane();
        gameLayer.getChildren().addAll(bgCanvas, animCanvas, overlay, tutorialCanvas);

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

    private void drawAnimatedZombies(GraphicsContext gc, double time) {
        for (int i = 0; i < zombiePositions.length; i++) {
            double baseX = zombiePositions[i][0];
            double baseY = zombiePositions[i][1];
            // each zombie has a different phase so they don't all move together
            double phase = i * 0.7;
            double offsetX = Math.sin(time * 1.2 + phase) * 5;
            double offsetY = Math.cos(time * 0.9 + phase) * 4;
            drawBackgroundZombie(gc, baseX + offsetX, baseY + offsetY);
        }
    }

    private void drawAnimatedSun(GraphicsContext gc, double time) {
        // sun orbits clockwise in a circle
        double centerX = 110;
        double centerY = 110;
        double radius = 25;
        double sunX = centerX + Math.cos(time * 0.5) * radius;
        double sunY = centerY + Math.sin(time * 0.5) * radius;
        Sun.drawSunIcon(gc, sunX, sunY, 3.0);
    }

    private void drawTitle(GraphicsContext gc, double time) {
        double titleX = 290;
        double titleY = 140;

        double bobY = Math.sin(time * 1.5) * 3;

        // shadow
        gc.setFont(Font.font("Serif", FontWeight.BLACK, FontPosture.ITALIC, 80));
        gc.setFill(Color.rgb(0, 0, 0, 0.3));
        gc.fillText("Plants", titleX + 3, titleY + bobY + 3);
        gc.fillText("vs", titleX + 223, titleY + bobY + 3);
        gc.fillText("Zombies", titleX + 303, titleY + bobY + 3);

        // "Plants"
        gc.setFont(Font.font("Serif", FontWeight.BLACK, FontPosture.ITALIC, 80));
        gc.setFill(Color.web("#2E7D32"));
        gc.fillText("Plants", titleX, titleY + bobY);

        // "vs"
        gc.setFont(Font.font("Serif", FontWeight.BLACK, FontPosture.ITALIC, 80));
        gc.setFill(Color.web("#333333"));
        gc.fillText("vs", titleX + 220, titleY + bobY);

        // "Zombies"s
        gc.setFont(Font.font("Serif", FontWeight.BLACK, FontPosture.ITALIC, 80));
        gc.setFill(Color.web("#6B8E23"));
        gc.fillText("Zombies", titleX + 300, titleY + bobY);

    }

    private void drawBackgroundZombie(GraphicsContext gc, double x, double y) {
        gc.save();
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

        gc.restore();
    }

    private AnchorPane createOverlay(Canvas tutorialCanvas, GraphicsContext tgc) {
        AnchorPane overlay = new AnchorPane();

        //font sizes
        Button adventureBtn = createButton("Adventure", 24, e -> startGame());
        Button howToPlayBtn = createButton("How To Play", 24, e -> openTutorial(tutorialCanvas, tgc));

        //both buttons to be the exact same width
        adventureBtn.setPrefWidth(200);
        howToPlayBtn.setPrefWidth(200);

        Label creditsLabel1 = createLabel("Illia  ·  Mario", 16);
        Label creditsLabel2 = createLabel("Mark  ·  Pranay", 16);

        // VBox to centre and stack
        VBox menuBox = new VBox(15);
        menuBox.setAlignment(javafx.geometry.Pos.TOP_CENTER);
        menuBox.setPrefWidth(250);

        menuBox.getChildren().addAll(adventureBtn, howToPlayBtn, creditsLabel1, creditsLabel2);

        // Anchor the menu box on the gravestone
        anchor(menuBox, 270.0, 680.0);

        overlay.getChildren().addAll(menuBox);
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
        label.setFont(Font.font("Serif", FontWeight.BOLD, fontSize));
        label.setTextFill(Color.BLACK);
        return label;
    }

    private Button createButton(String text, double fontSize, javafx.event.EventHandler<javafx.event.ActionEvent> handler) {
        Button btn = new Button(text);
        btn.setFont(Font.font("Serif", FontWeight.BOLD, fontSize));
        btn.setOnAction(handler);
        return btn;
    }

    private void anchor(javafx.scene.Node node, double top, double left) {
        AnchorPane.setTopAnchor(node, top);
        AnchorPane.setLeftAnchor(node, left);
    }

    private void startGame() {
        if (menuAnimation != null) menuAnimation.stop();
        LevelUI levelSelect = new LevelUI(game, stage);
        SoundManager.playMenuBtn();
        levelSelect.show();
    }
}
