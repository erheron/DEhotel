import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;

import java.time.LocalDate;
import java.util.Collection;
import java.util.stream.Stream;

public class ExtraServicesController {
    @FXML public AnchorPane root;
    @FXML public Button confirmB;
    @FXML public MenuButton selectServiceMB;

    LocalDate dateFrom = null, dateTo = null;
    String chosenOption;

    public void confirmBaction(ActionEvent actionEvent) {
        //TODO=communicating with UserController, that way???
        if(check()){
            //todo=do what???
        }
    }

    void initMenuButton(Collection<String> collection){
        try {
            Stream<String> stringStream = collection.stream();
            stringStream.forEach(str -> {
                MenuItem nextMI = new MenuItem(str);
                nextMI.setOnAction(e -> {
                    selectServiceMB.setText(nextMI.getText());
                    chosenOption = nextMI.getText();
                });
                selectServiceMB.getItems().add(nextMI);
            });
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    boolean check(){
        if(chosenOption == null || dateFrom == null || dateTo == null) return false;
        return !dateFrom.isAfter(dateTo);
    }
}
