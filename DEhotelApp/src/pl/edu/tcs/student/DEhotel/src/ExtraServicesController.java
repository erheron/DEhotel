import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.sql.ResultSet;
import java.sql.Statement;
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
    private UserController controller;

    LocalDate dateFrom = null, dateTo = null;
    String chosenOption;
    int amountOfPeople = 1;

    void addUser(UserController controller){
        this.controller = controller;
    }

    public void setValueFactoryForMB(){
        spinner1.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1,amountOfPeople,1));
    }
    public void confirmBaction(ActionEvent actionEvent) {
        if(check()){
            try {

                Statement statement = Model.connection.createStatement();
                String selectIdAndPrice = "select id_uslugi_dod, cena from uslugi_dod where nazwa = '" + chosenOption + "';";
                ResultSet rs4 = statement.executeQuery(selectIdAndPrice);
                rs4.next();
                int daysServices = dateFrom.until(dateTo).getDays();
                UserController.Services services = controller.new Services(rs4.getInt("id_uslugi_dod"), dateFrom.toString(), dateTo.toString(), amountOfPeople, rs4.getInt("cena") * daysServices * amountOfPeople, chosenOption);
                controller.actualServices.add(services);
                ((Stage) confirmB.getParent().getScene().getWindow()).close();

            }catch (Exception e){
                e.printStackTrace();
            }
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
