import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.ResultSet;
import java.sql.Statement;

public class RegistrationController {
    @FXML public Button submitButton;
    @FXML public TextField nameTF;
    @FXML public TextField surnameTF;
    @FXML public TextField emailTF;
    @FXML public TextField phoneNumberTF;
    @FXML public PasswordField passwordF;

    public void submitBaction(ActionEvent actionEvent) {
        try {
            Statement statement = Model.connection.createStatement();
            Model.connection.setAutoCommit(false);

            //TODO=check and insert
            takeAndInsertData();
            String insertGast = "insert into goscie values (default, '" + nameTF.getText() + "', '" + surnameTF.getText() + "', '" +  phoneNumberTF.getText() + "', '" + emailTF.getText() +"');";
            Long hash = Hasher.hash(passwordF.getText());
            String insertHash = "insert into email_hash values ('"+ emailTF.getText() + "', " + hash+ ");";
            statement.executeUpdate(insertHash);
            statement.executeUpdate(insertGast);
            /*PrintWriter writer = new PrintWriter("userspasswords.txt", true);
            writer.append("<email>:"+emailTF.getText() +"     "+"<password>:"+passwordF.getText()+"\n");
            writer.close();*/

            Files.write(Paths.get("userspasswords.txt"), new String("<email>:"+emailTF.getText() +"     "+"<password>:"+passwordF.getText()+"\n").getBytes(), StandardOpenOption.APPEND);
            Model.connection.commit();
            Model.connection.setAutoCommit(true);
            ((Stage) submitButton.getParent().getScene().getWindow()).close();

        }catch (Exception e){
            System.err.println(e.getMessage());
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setHeaderText("Incorrect data!");
            alert.setContentText("Please, enter your personal information.");
            alert.showAndWait();
        }
    }

    private void takeAndInsertData() throws Exception{
        checkData(nameTF.getText());
        checkData(surnameTF.getText());
        checkData(emailTF.getText());
        checkData(phoneNumberTF.getText());
        checkData(passwordF.getText());

    }

    private void checkData(String data) throws Exception{
        if(data.contains(";") || data.contains(" "))
            throw new Exception();
    }



}
