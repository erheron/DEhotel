import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
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

    private RegistrationController registrationController;
    SimpleBooleanProperty loginBPressed = new SimpleBooleanProperty(false);
    String userName = null;
    private Connection conn;


    /*-------------end of block-----------
     *                  1               */


    /*------------------2-----------------
     *       initializer and "hash"
     *       to work from Model            */
    public void initialize(){
        root.requestFocus();

    }

    public void addConnection(Connection conn){
        this.conn = conn;
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
            stmt = conn.createStatement();
            String select = "select imie, nazwisko from email_hash natural join goscie where email = '" +login+ "' and hash = " + hash + ";";
            ResultSet rs = stmt.executeQuery(select);
            if (!rs.isBeforeFirst() ) { //user or password don't exist
                return 2;
            }
            rs.next();
            userName = rs.getString("imie") + " " +rs.getString("nazwisko");
            rs.close();
            //userName = "Katarzyna Grygiel";//TODO - data from postgresql
            return 1;
        }catch(Exception e){
            System.err.println(e.getMessage());
            //TODO = alert
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

    public void loginBpressed(ActionEvent actionEvent) {
        loginBPressed.set(true);
    }

    public void registrationBaction(ActionEvent actionEvent) {
        try {
            FXMLLoader registrationLoader = new FXMLLoader(getClass().getResource("registrationForm.fxml"));
            AnchorPane regFormRoot = registrationLoader.load();
            Stage registrationStage  = new Stage();
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