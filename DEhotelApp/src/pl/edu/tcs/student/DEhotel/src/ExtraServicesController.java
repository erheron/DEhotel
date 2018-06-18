import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;

import java.time.LocalDate;
import java.util.Collection;
import java.util.stream.Stream;

public class ExtraServicesController {
    @FXML public AnchorPane root;
    @FXML public Button confirmB;
    @FXML public MenuButton selectServiceMB;
    public DatePicker calendar1;
    public DatePicker calendar2;
    public Spinner<Integer> spinner1;

    LocalDate dateFrom = null, dateTo = null;
    String chosenOption;
    int amountOfPeople = 1;


    public void setValueFactoryForMB(){
        spinner1.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1,amountOfPeople,1));
    }
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

    public void calendar1action(ActionEvent actionEvent) {
        dateFrom = calendar1.getValue();
    }

    public void calendar2action(ActionEvent actionEvent) {
        dateTo = calendar2.getValue();
    }
}
