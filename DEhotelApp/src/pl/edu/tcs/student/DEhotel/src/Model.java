import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Objects;
import java.sql.*;

public class Model extends Application {
    private UserController userController;
    private AdminController adminController;
    private LoginController loginController;
    private Integer showUserForm = null;
    public static Connection connection;

    @Override
    public void start(Stage primaryStage) throws Exception{
        connection();
        //some global settings go
        primaryStage.setWidth(750);
        primaryStage.setHeight(800);
        primaryStage.setTitle("DEhotel - from Krakow with love :)");
        //login form goes first
        Stage loginStage = new Stage();
        loginStage.setTitle("Log in");
        loginStage.setResizable(false);
        FXMLLoader loginLoader = new FXMLLoader();
        loginLoader.setLocation(getClass().getClassLoader().getResource("login.fxml"));
        AnchorPane loginRoot = loginLoader.load();
        loginController = loginLoader.getController();
        loginController.loginB.setOnAction(e -> {
            showUserForm = loginController.tryLogin();
            if(showUserForm != 2) {
                loginStage.close();
            }
            else{
                loginController.bringToInitialState();
            }
        });
        Scene loginScene = new Scene(loginRoot);
        loginStage.setScene(loginScene);
        loginStage.showAndWait();
        if(Objects.equals(showUserForm, null) || Objects.equals(showUserForm, 2)){
            System.exit(0);
        }
        if(showUserForm == 1) {
            FXMLLoader userLoader = new FXMLLoader(getClass().getResource("userForm.fxml"));
            AnchorPane userFormRoot = userLoader.load();
            userController = userLoader.getController();
            userController.usernameLabel.setText(loginController.userName);
            userController.idGast = loginController.idGast;
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

    private void connection() {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("PostgreSQL JDBC Driver not found, exiting...");
            return;
        }

        try {
            File file = new File("db-connection.config");

            BufferedReader br = new BufferedReader(new FileReader(file));
            String url, user,password;
            url = br.readLine();
            user = br.readLine();
            password = br.readLine();

            connection = DriverManager.getConnection(url, user, password);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Connection Failed!");
        }
    }
}