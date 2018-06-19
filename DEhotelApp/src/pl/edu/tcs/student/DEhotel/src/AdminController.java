import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import org.postgresql.PGRefCursorResultSet;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class AdminController {
    @FXML public Button penaltyButton;
    @FXML public Button oneMoreButton;
    @FXML public Button submitButton;
    @FXML public MenuButton selectMenu;
    @FXML public TextField reservationTF;
    @FXML public AnchorPane root;
    private int actualIdEquipment;

    class Penalty{
        String reservationID, equipment;
        int price;

        Penalty(String r,String e, int p){
            reservationID = r;
            equipment = e;
            price = p;
        }
    }

    List<Penalty> penaltyList = new ArrayList<>();

    public void initialize(){
        hideAll();
    }

    public void reservationTFOnKeyPressed(KeyEvent keyEvent) {
        if(keyEvent.getCode().equals(KeyCode.ENTER)){
            setSelectMenu();
            reservationTF.getCharacters();

        }
    }
    public void penaltyButtonAction(ActionEvent actionEvent) {
        setPenaltyVisible();
    }

    public void oneMoreButtonAction(ActionEvent actionEvent) {
        //create new Penalty
        selectMenu.getItems().clear();
        addPenalty();
        clearPenaltyState();
    }

    public void submitButtonAction(ActionEvent actionEvent) {
        addPenalty();
        submitPenalty();
        selectMenu.getItems().clear();
        hideAll();
    }

    private void hideAll() {
        selectMenu.setVisible(false);
        oneMoreButton.setVisible(false);
        submitButton.setVisible(false);
        reservationTF.setVisible(false);
    }

    private void submitPenalty() {
        try{
            for(Penalty penalty : penaltyList){
                String insert = "insert into kary values (default, " + penalty.reservationID + ", current_date, " + penalty.price + ");";
                Statement statement = Model.connection.createStatement();
                statement.executeUpdate(insert);
                String deleteEquipment = "select usun_sprzet( " + actualIdEquipment + ");";
                statement.executeQuery(deleteEquipment);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void setPenaltyVisible(){
        selectMenu.setVisible(true);
        oneMoreButton.setVisible(true);
        submitButton.setVisible(true);
        reservationTF.setVisible(true);
    }

    /*boolean checkPenaltyData(){
        //TODO=postgresql select for reservation id
        return false;
    }*/

    void clearPenaltyState(){
        selectMenu.setText("Select equipment");
        reservationTF.setPromptText("type reservation id");
        reservationTF.setText("");
    }
    void addPenalty(){
            try {
                StringBuilder mainID = new StringBuilder();
                StringBuilder id = new StringBuilder();
                if (!reservationTF.getText().contains("/")) {
                    throw new Exception();
                }
                int i = 0;
                for (i = 0; i < reservationTF.getText().length(); i++) {
                    if (reservationTF.getText().charAt(i) == '/')
                        break;
                    mainID.append(reservationTF.getText().charAt(i));
                }
                i++;
                for (; i < reservationTF.getText().length(); i++) {
                    id.append(reservationTF.getText().charAt(i));
                }

                String priceSelect = "select cena_przedmiotu from rodzaje_wyposazenia where nazwa = '" + selectMenu.getText() + "';";
                Statement statement = Model.connection.createStatement();
                ResultSet rs = statement.executeQuery(priceSelect);
                rs.next();
                int price = rs.getInt("cena_przedmiotu");
                Penalty penalty = new Penalty(mainID.toString(), selectMenu.getText(), price);
                penaltyList.add(penalty);
            }catch (Exception e) {
                e.printStackTrace();
            }
    }

    public void setSelectMenu() {
        try {
            StringBuilder mainID = new StringBuilder();
            StringBuilder id = new StringBuilder();
            if (!reservationTF.getText().contains("/")) {
                throw new Exception();
            }
            int i = 0;
            for (i = 0; i < reservationTF.getText().length(); i++) {
                if (reservationTF.getText().charAt(i) == '/')
                    break;
                mainID.append(reservationTF.getText().charAt(i));
            }
            i++;
            for (; i < reservationTF.getText().length(); i++) {
                id.append(reservationTF.getText().charAt(i));
            }
            Statement statement = Model.connection.createStatement();
            String selectRoom = "select id_pokoju from rezerwacje_pokoje where id_rez_pojedynczej = " + id + ";";
            ResultSet rs0 = statement.executeQuery(selectRoom);
            rs0.next();
            int room = rs0.getInt("id_pokoju");
            String selectEquipment = "select distinct nazwa from (pokoje_wyposazenie p join wyposazenie w on p.id_wyposazenia=w.id) join rodzaje_wyposazenia r on w.id_rodzaju=r.id_rodzaju_wyposazenia where p.id_pokoju = " + room + ";";
            ResultSet rs = statement.executeQuery(selectEquipment);
            while(rs.next()){
                MenuItem nextMI = new MenuItem(rs.getString("nazwa"));
                nextMI.setOnAction(e -> {
                    String selectId = "select id from (pokoje_wyposazenie p join wyposazenie w on p.id_wyposazenia=w.id) join rodzaje_wyposazenia r on w.id_rodzaju=r.id_rodzaju_wyposazenia where p.id_pokoju = " + room + " and nazwa = '"+nextMI.getText()+"' limit 1;";
                    try {
                        Statement statement1 = Model.connection.createStatement();
                        ResultSet rs2 = statement1.executeQuery(selectId);
                        rs2.next();
                        actualIdEquipment = rs2.getInt("id");
                    }catch (Exception e2){
                        e2.printStackTrace();
                    }
                    selectMenu.setText(nextMI.getText());
            });
                selectMenu.getItems().add(nextMI);
            }
        }catch(Exception e){
            //TODO alert
            e.printStackTrace();
        }
    }
}
