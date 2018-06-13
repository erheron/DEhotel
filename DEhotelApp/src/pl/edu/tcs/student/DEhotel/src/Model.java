import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import java.util.Objects;

public class Model extends Application {
    private UserController userController;
    private AdminController adminController;
    private LoginController loginController;
    private Integer showUserForm = null;
    @Override
    public void start(Stage primaryStage) throws Exception{
        //some global settings go
        primaryStage.setWidth(700);
        primaryStage.setHeight(800);
        primaryStage.setTitle("DEhotel - from Krakow with love :)");
        //login form goes first
        Stage loginStage = new Stage();
        FXMLLoader loginLoader = new FXMLLoader(getClass().getResource("login.fxml"));
        AnchorPane loginRoot = loginLoader.load();
        loginController = loginLoader.getController();
        loginController.loginBPressed.addListener(e -> {
            showUserForm = loginController.tryLogin();
            loginStage.close();
        });
        Scene loginScene = new Scene(loginRoot);
        loginStage.setScene(loginScene);
        loginStage.showAndWait();

        if(Objects.equals(showUserForm, null) || showUserForm == 2){
            System.exit(0);
        }
        if(showUserForm == 1) {
            FXMLLoader userLoader = new FXMLLoader(getClass().getResource("userForm.fxml"));
            AnchorPane userFormRoot = userLoader.load();
            userController = userLoader.getController();
            userController.usernameLabel.setText(loginController.userName);
            Scene userScene = new Scene(userFormRoot);
            primaryStage.setScene(userScene);
            primaryStage.show();
        }else{
            FXMLLoader adminLoader = new FXMLLoader(getClass().getResource("adminForm.fxml"));
            AnchorPane adminFormRoot = adminLoader.load();
            adminController = adminLoader.getController();
            Scene adminScene = new Scene(adminFormRoot);
            primaryStage.setScene(adminScene);
            primaryStage.show();

        }
    }
    public static void main(String[] args){
        launch(args);
    }
    /* Logics for interacting with PostgreSQL*/
    //TODO
}
