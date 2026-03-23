import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        Game game = new Game();
        GameUI ui = new GameUI(game, stage);
        game.startGame(ui);
    }

    public static void main(String[] args) {
        launch(args); // JavaFX takes over from here
    }
}