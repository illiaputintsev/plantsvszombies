import javafx.application.Application;
import javafx.stage.Stage;
import javafx.application.Platform;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        SoundManager.init();
        Game game = new Game();
        MenuUI ui = new MenuUI(stage, game);
        
        stage.setOnCloseRequest(event -> {
            Platform.exit();
            System.exit(0);
        });
        
        ui.show();
    }

    public static void main(String[] args) {
        launch(args);
    } 
}