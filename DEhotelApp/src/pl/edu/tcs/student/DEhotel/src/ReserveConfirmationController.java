import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

import java.io.FilterInputStream;
import java.util.List;

public class ReserveConfirmationController {
    @FXML public Button confirmB;
    @FXML public Button backB;
    @FXML public TextField totalCostTF;
    @FXML public TableView<ReservationTableView> mainTableView;
    private List<UserController.Pair<UserController.Reservation, List<UserController.Services>>> list;


    public static class ReservationTableView{
        private String room;
        private String checkIn;
        private String checkOut;
        private int price;
        public SimpleBooleanProperty checked;
        ReservationTableView(String room, String in, String out, int price){
            this.room = room;
            checkIn = in;
            checkOut = out;
            this.price = price;
            checked = new SimpleBooleanProperty(true);
        }

        public String getRoom() {
            return room;
        }

        public String getCheckOut() {
            return checkOut;
        }

        public String getCheckIn() {
            return checkIn;
        }

        public int getPrice(){
            return price;
        }

        public SimpleBooleanProperty isChecked() {
            return checked;
        }
    }
    public void reservationList(List<UserController.Pair<UserController.Reservation, List<UserController.Services>>> list){
        this.list = list;
    }
    public void setMainTableView() {
        //mainTableView.getSelectionModel().setCellSelectionEnabled(true);
        mainTableView.setEditable(true);
        TableColumn roomTC = new TableColumn("Room type");
        TableColumn checkInTC = new TableColumn("Check in");
        TableColumn checkOutTC = new TableColumn("Check out");
        TableColumn priceTC = new TableColumn("Price");
        TableColumn<ReservationTableView, Boolean> checkColumn = new TableColumn<>("Checked");
        ObservableList<ReservationTableView> data =
                FXCollections.observableArrayList();
        for(UserController.Pair<UserController.Reservation, List<UserController.Services>> pair : list){
            data.add(new ReservationTableView(pair.t.roomType, pair.t.checkinDate, pair.t.checkoutDate, pair.t.price));
        }
        roomTC.setCellValueFactory(
                new PropertyValueFactory<>("room"));
        checkInTC.setCellValueFactory(
                new PropertyValueFactory<>("checkIn"));
        checkOutTC.setCellValueFactory(
                new PropertyValueFactory<>("checkOut"));
        priceTC.setCellValueFactory(
                new PropertyValueFactory<>("price"));
        checkColumn.setCellFactory(tc -> {
            CheckBoxTableCell<ReservationTableView, Boolean> checkBox = new CheckBoxTableCell<>();
            checkBox.setFocusTraversable(false);
            checkBox.setEditable(true);
            checkBox.setSelectedStateCallback(index -> {
                ReservationTableView r = mainTableView.getItems().get(index);
                return r.checked;
            });
            return checkBox;
        });

        mainTableView.setItems(data);
        mainTableView.getColumns().addAll(roomTC, checkInTC, checkOutTC, priceTC, checkColumn);
    }

    public void setTotalCostTF(){
        Integer sum =0;
        for(UserController.Pair<UserController.Reservation, List<UserController.Services>> pair : list){
            sum+=pair.t.price;
        }
        totalCostTF.setText(sum.toString());
    }
}