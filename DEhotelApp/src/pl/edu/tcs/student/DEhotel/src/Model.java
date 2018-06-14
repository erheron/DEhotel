import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import java.sql.*;

import java.sql.DriverManager;
import java.util.Objects;

public class Model extends Application{
    private UserController userController;
    private AdminController adminController;
    private LoginController loginController;
    private Integer showUserForm = null;
    private Connection connection;
    @Override
    public void start(Stage primaryStage) throws Exception{
        connection();
        //some global settings go
        primaryStage.setWidth(600);
        primaryStage.setHeight(800);
        primaryStage.setTitle("DEhotel - from Krakow with love :)");
        //login form goes first
        Stage loginStage = new Stage();
//        loginStage.setWidth(400);
//        loginStage.setHeight(500);
        FXMLLoader loginLoader = new FXMLLoader(getClass().getResource("login.fxml"));
        AnchorPane loginRoot = loginLoader.load();
        loginController = loginLoader.getController();
        loginController.addConnection(connection);
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
    //TODO*/
    private void connection() {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("Where is your PostgreSQL JDBC Driver?");
            e.printStackTrace();
            return;
        }
        try {

            connection = DriverManager.getConnection(
                    "jdbc:postgresql://localhost:5432/hotel", "erheron",
                    "erheron");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
