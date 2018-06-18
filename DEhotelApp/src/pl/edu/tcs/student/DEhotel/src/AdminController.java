import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;

import java.util.List;

public class AdminController {
    @FXML public Button penaltyButton;
    @FXML public Button oneMoreButton;
    @FXML public Button submitButton;
    @FXML public MenuButton selectMenu;
    @FXML public TextField reservationTF;
    @FXML public AnchorPane root;


    class Penalty{
        String reservationID, equipment;

        Penalty(String r,String e){
            reservationID = r;
            equipment = e;
        }
    }

    List<Penalty> penaltyList;

    public void initialize(){
        hideAll();
    }

    public void reservationTFOnKeyPressed(KeyEvent keyEvent) {
        if(keyEvent.getCode().equals(KeyCode.ENTER)){
            //TODO=select
            reservationTF.getCharacters();

        }
    }
    public void penaltyButtonAction(ActionEvent actionEvent) {
        setPenaltyVisible();
    }

    public void oneMoreButtonAction(ActionEvent actionEvent) {
        //create new Penalty
        addPenalty();
        clearPenaltyState();
    }

    public void submitButtonAction(ActionEvent actionEvent) {
        addPenalty();
        submitPenalty();
        hideAll();
    }

    private void hideAll() {
        selectMenu.setVisible(false);
        oneMoreButton.setVisible(false);
        submitButton.setVisible(false);
        reservationTF.setVisible(false);
    }

    private void submitPenalty() {
        //TODO=sql insert (all avaible penalties)
    }

    public void setPenaltyVisible(){
        selectMenu.setVisible(true);
        oneMoreButton.setVisible(true);
        submitButton.setVisible(true);
        reservationTF.setVisible(true);
    }

    boolean checkPenaltyData(){
        //TODO=postgresql select for reservation id
        return false;
    }

    void clearPenaltyState(){
        selectMenu.setText("Select equipment");
        reservationTF.setPromptText("type reservation id");
    }
    void addPenalty(){
        if(checkPenaltyData()){
            Penalty penalty = new Penalty(reservationTF.getText(), selectMenu.getText());
            penaltyList.add(penalty);
        }

    }
}
