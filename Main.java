import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        MenuUI ui = new MenuUI(stage);
        ui.show();
    }

    public static void main(String[] args) {
        launch(args); // JavaFX takes over from here
    }
}