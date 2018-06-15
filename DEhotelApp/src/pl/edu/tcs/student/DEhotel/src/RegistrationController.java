import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class RegistrationController {
    @FXML public Button submitButton;
    @FXML public TextField nameTF;
    @FXML public TextField surnameTF;
    @FXML public TextField emailTF;
    @FXML public TextField phoneNumberTF;
    @FXML public PasswordField passwordF;

    public void submitBaction(ActionEvent actionEvent) {
        //TODO=check and insert
        takeAndInsertData();
        //TODO=if data are wrong then alert, or smth. like that
        ((Stage)submitButton.getParent().getScene().getWindow()).close();
    }

    //TODO= check data
    void takeAndInsertData(){
        //TODO=get from fields, check and insert into table
        //TODO= all alerts, and so on, go here to

    }



}
