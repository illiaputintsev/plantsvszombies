import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        SoundManager.init();
        Game game = new Game();
        MenuUI ui = new MenuUI(stage, game);
        
        stage.setOnCloseRequest(event -> {
            SoundManager.stopMenuTheme(); 
            SoundManager.stopLevelTheme();
        });
        
        ui.show();
    }

    public static void main(String[] args) {
        launch(args);
    } 
}