import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class ReserveConfirmationController {
    @FXML public Button confirmB;
    @FXML public Button backB;
    @FXML public TextField totalCostTF;
    @FXML public TableView<ReservationTableView> mainTableView;
    private List<UserController.Pair<UserController.Reservation, List<UserController.Services>>> list;
    Integer totalSum =0;

    ObservableList<ReservationTableView> data =
            FXCollections.observableArrayList();

    public static class ReservationTableView{
        private String room;
        private String checkIn;
        private String checkOut;
        private Integer price;
        private String priceSee;
        public SimpleBooleanProperty checked;
        private StringBuilder extraService;
        ReservationTableView(String room, String in, String out, Integer price){
            this.room = room;
            checkIn = in;
            checkOut = out;
            this.price = price;
            checked = new SimpleBooleanProperty(true);
            extraService = new StringBuilder("{");
        }

        ReservationTableView(Integer id){
            extraService = new StringBuilder(id.toString());
            room = "";
            checkIn = "";
            checkOut = "";
            priceSee = "";
        }

        ReservationTableView(Integer room, String in, String out, String price){
            extraService = new StringBuilder("");
            this.room = room.toString();
            checkIn = in;
            checkOut = out;
            this.priceSee = price;
            checked = new SimpleBooleanProperty(true);
        }

        void addService(String service){
            if(!extraService.toString().equals("{") && !service.equals("}"))
                extraService.append(" ,");
            extraService.append(service);
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

        public String getExtraService() {
            return extraService.toString();
        }

        public String getPriceSee() {
            return priceSee;
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
        TableColumn servicesTC = new TableColumn("Extra Services List");
        for(UserController.Pair<UserController.Reservation, List<UserController.Services>> pair : list){
            ReservationTableView reservationTableView = new ReservationTableView(pair.t.roomType, pair.t.checkinDate, pair.t.checkoutDate, pair.t.price);
            for(UserController.Services serv : pair.u){
                reservationTableView.addService(serv.name);
            }
            reservationTableView.addService("}");
            data.add(reservationTableView);
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
               // if(r.checked.getValue()) {System.err.println("you suck 1"); totalSum += mainTableView.getItems().get(index).price;}
                //else {System.err.println("you suck 2");totalSum -= mainTableView.getItems().get(index).price;}
                //totalCostTF.setText(totalSum.toString());
                return r.checked;
            });
            return checkBox;
        });
        servicesTC.setCellValueFactory(
                new PropertyValueFactory<>("extraService"));

        mainTableView.setItems(data);
        mainTableView.getColumns().addAll(roomTC, checkInTC, checkOutTC, priceTC, checkColumn, servicesTC);
    }

    public void setTotalCostTF(){
        for(UserController.Pair<UserController.Reservation, List<UserController.Services>> pair : list){
            totalSum +=pair.t.price;
        }
        totalCostTF.setText(totalSum.toString());
    }
}