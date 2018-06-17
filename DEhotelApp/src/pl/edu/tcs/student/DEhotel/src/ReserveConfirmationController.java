import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class ReserveConfirmationController {
    @FXML public Button confirmB;
    @FXML public Button backB;
    @FXML public TextField totalCostTF;
    @FXML public TableView mainTableView;
    private List<UserController.Pair<UserController.Reservation, List<UserController.Services>>> list;
    public static class ReservationTableView{
        private String room;
        private String checkIn;
        private String checkOut;
        private int price;
        ReservationTableView(String room, String in, String out, int price){
            this.room = room;
            checkIn = in;
            checkOut = out;
            this.price = price;
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
    }
    public void reservationList(List<UserController.Pair<UserController.Reservation, List<UserController.Services>>> list){
        this.list = list;
    }
    public void setMainTableView() {
        TableColumn roomTC = new TableColumn("Room type");
        TableColumn checkInTC = new TableColumn("Check in");
        TableColumn checkOutTC = new TableColumn("Check out");
        TableColumn priceTC = new TableColumn("Price");
        TableColumn checkColumn = new TableColumn("Checked");
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
        checkColumn.setCellFactory(tc -> new CheckBoxTableCell<>());
        checkColumn.setCellValueFactory(
                new PropertyValueFactory<>("true"));
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
