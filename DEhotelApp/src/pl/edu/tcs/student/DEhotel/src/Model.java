import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class Model extends Application {
    private Controller controller;
    private AdminController adminController;
    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("form.fxml"));
        controller = loader.getController();
        AnchorPane root = loader.load();
        primaryStage.setWidth(600);
        primaryStage.setHeight(800);
        primaryStage.setTitle("DEhotel - from Krakow with love :)");
        Scene mainScene = new Scene(root);
        primaryStage.setScene(mainScene);
        primaryStage.show();
    }
    public static void main(String[] args){
        launch(args);
    }

    /* Logics for interacting with PostgreSQL*/
    //TODO
}
