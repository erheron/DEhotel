import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

import java.util.Objects;

public class LoginController {
    


    /*-------------------1---------------
     *           global variables        */
    @FXML public Button loginB;
    @FXML public PasswordField passwordF;
    @FXML public TextField loginF;
    @FXML public AnchorPane root;
    @FXML public Button registrationB;

    private RegistrationController registrationController;
    //SimpleBooleanProperty loginBPressed = new SimpleBooleanProperty(false);
    String userName = null;
    int idGast;


    /*-------------end of block-----------
     *                  1               */


    /*------------------2-----------------
     *       initializer and "hash"
     *       to work from Model            */
    public void initialize(){
        root.requestFocus();

    }

    public void bringToInitialState(){
        passwordF.setText("");
        loginF.setText("");
        /*loginBPressed = new SimpleBooleanProperty(false);
        loginBPressed.set(false);*/
    }


    int tryLogin() {
        //returns 0 if login was performed as ADMIN
        //otherwise 1 if user's match
        //otherwise 2 - error code
        Statement stmt = null;
        try{
            String login = loginF.getText();
            String password = passwordF.getText();
            Long hash = Hasher.hash(password);

            //admin
            if(Objects.equals(login, "admin") && Objects.equals(password, "admin")) {
                userName = "admin";
                return 0;
            }

            //execute query
            stmt = Model.connection.createStatement();
            String select = "select imie, nazwisko, id_goscia from goscie where email = '" +login+ "' and hash = " + hash+ ";";
            ResultSet rs = stmt.executeQuery(select);
            if (!rs.isBeforeFirst() ) { //user or password don't exist
                throw new Exception();
            }
            rs.next();
            userName = rs.getString("imie") + " " +rs.getString("nazwisko");
            idGast = rs.getInt("id_goscia");
            rs.close();
            return 1;
        }catch(Exception e){
            System.err.println(e.getMessage());
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error Dialog");
                alert.setHeaderText("Login error!");
                alert.setContentText("User or password doesn't exist.");
                alert.showAndWait();

        }
        return 2;
    }
    /*-------------end of block-----------
     *                  2               */


    /*------------------3-----------------
     *                handlers          */
    public void passwordFkPressed(KeyEvent keyEvent) {
        if(keyEvent.getCode().equals(KeyCode.TAB) || keyEvent.getCode().equals(KeyCode.ENTER))
            loginB.requestFocus();
    }

    public void loginFkPressed(KeyEvent keyEvent) {
        if(keyEvent.getCode().equals(KeyCode.TAB) || keyEvent.getCode().equals(KeyCode.ENTER))
            passwordF.requestFocus();
    }

    public void registrationBaction(ActionEvent actionEvent) {
        try {
            FXMLLoader registrationLoader = new FXMLLoader(getClass().getResource("registrationForm.fxml"));
            AnchorPane regFormRoot = registrationLoader.load();
            Stage registrationStage  = new Stage();
            registrationStage.setTitle("Register");
            registrationStage.initModality(Modality.WINDOW_MODAL);
            registrationStage.initOwner(root.getScene().getWindow());
            registrationController = registrationLoader.getController();
            Scene registrationScene = new Scene(regFormRoot);
            registrationStage.setScene(registrationScene);
            registrationStage.show();

        }catch(IOException e){
            e.printStackTrace();

        }
        
    }
    /*-------------end of block-----------
     *                  3               */

    //TODO=move checkString method to separate class
    public boolean checkString(String q){
        if(q.contains("SELECT"))
            return false;
        return true;
    }

    
}