import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
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

    public static MenuBar createMenuBar() {
        MenuItem quit = new MenuItem("Quit");
        quit.setOnAction(e -> {
            Platform.exit();
            System.exit(0);
        });
        
        Menu fileMenu = new Menu("File");
        fileMenu.getItems().add(quit);

        MenuItem about = new MenuItem("About");
        about.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("About");
            alert.setHeaderText("Plants vs Zombies");
            alert.setContentText("By Illia, Mario, Mark and Pranay");
            alert.showAndWait();
        });
        Menu helpMenu = new Menu("Help");
        helpMenu.getItems().add(about);

        return new MenuBar(fileMenu, helpMenu);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
