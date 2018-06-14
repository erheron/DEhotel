import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import java.sql.*;

import java.util.Objects;

public class LoginController {


    /*-------------------1---------------
     *           global variables        */
    @FXML public Button loginB;
    @FXML public PasswordField passwordF;
    @FXML public TextField loginF;
    @FXML public AnchorPane root;
    SimpleBooleanProperty loginBPressed = new SimpleBooleanProperty(false);
    String userName = null;
    private Connection conn;
    int idGast;


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

    private Long hash(String s){
        int mod = 1000000009;
        Long res = 0L;
        Long P = 1L;
        for(char c : s.toCharArray()){
            res += c * P;
            P *= 31;
            res %= mod;
            P %= mod;
        }
        return res;
    }


    int tryLogin() {
        //returns 0 if login was performed as ADMIN
        //otherwise 1 if user's match
        //otherwise 2 - error code
        Statement stmt = null;
        try{
            String login = loginF.getText();
            String password = passwordF.getText();
            Long hash = hash(password);

            //admin
            if(Objects.equals(login, "admin") && Objects.equals(password, "admin")) {
                userName = "admin";
                return 0;
            }

            //execute query
            stmt = conn.createStatement();
            String select = "select imie, nazwisko, id_goscia from email_hash natural join goscie where email = '" +login+ "' and hash = " + /*hash */ 1+ ";";
            ResultSet rs = stmt.executeQuery(select);
            if (!rs.isBeforeFirst() ) { //user or password don't exist
                return 2;
            }
            rs.next();
            userName = rs.getString("imie") + " " +rs.getString("nazwisko");
            idGast = rs.getInt("id_goscia");
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
    /*-------------end of block-----------
     *                  3               */

    public boolean checkString(String q){
        if(q.contains("SELECT"))
            return false;
        return true;
    }
}