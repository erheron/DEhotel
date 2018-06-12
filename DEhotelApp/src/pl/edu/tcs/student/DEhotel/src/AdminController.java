import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class AdminController {
    @FXML public Button penaltyButton;
    @FXML public Button insertONeMoreButton;
    @FXML public Button submitButton;
    @FXML public MenuButton selectMenu;
    @FXML public TextField totalCashTF;
    public TextField reservationTF;

    public void reservationTFOnKeyPressed(KeyEvent keyEvent) {
        if(keyEvent.getCode().equals(KeyCode.ENTER)){
            //TODO=select
            reservationTF.getCharacters();

        }
    }
}
