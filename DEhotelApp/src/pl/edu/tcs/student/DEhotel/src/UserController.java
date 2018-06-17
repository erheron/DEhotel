import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.*;

public class UserController {

    @FXML public AnchorPane root;
    @FXML public Button reserveButton;
    @FXML public Button seeMyVisitsButton;
    @FXML public Label usernameLabel;
    @FXML public TextField checkinTF;
    @FXML public TextField checkoutTF;
    @FXML public Button checkoutButton;
    @FXML public Button checkinButton;
    @FXML public DatePicker calendar;
    @FXML public Label calendarLabel;
    @FXML public TextField peopleTextField;
    @FXML public Button mainReserveB;
    @FXML public Button oneMoreResB;
    @FXML public Button extraServicesB;
    @FXML public MenuButton selRoomTypeMB;
    @FXML public Label howManyPeopleL;

    /*----------------------1-------------------
     *              general fields used        */
    private ReserveConfirmationController resConfirmController;
    private final String pattern = "yyyy-MM-dd";
    private StringBuilder roomType;
    int idGast;
    private int actualPrice;


    //field for confirmation from pressing 'reserve all'
    private enum ConfirmationStatus{
        Default,
        Confirmed,
        Back
    }
    ConfirmationStatus confirmationStatus;

    public class Pair<T, U> {
        T t;
        U u;
        Pair(T t, U u){
            this.t = t;
            this.u = u;
        }
    }
    class Reservation{
        String checkinDate, checkoutDate;
        int amountOfPeople;
        String roomType;
        int idRoom;
        int price;

        Reservation(String indate,String outdate, int i, String type, int id, int price){
            checkinDate = indate;
            checkoutDate = outdate;
            amountOfPeople = i;
            roomType = type;
            idRoom = id;
            this.price = price;
        }
    }
    class Services{
        String services;
        String dateFrom;
        String dateTo;
        int number;
        public Services(){
            services = null;
        }
        public Services(String s, String dateFrom, String dateTo, int number){
            services = s;
            this.dateFrom = dateFrom;
            this.dateTo = dateTo;
            this.number = number;
        }
    }

    List<Pair<Reservation, List<Services>> > reservations = new ArrayList<>();
    private LocalDate checkinDate, checkoutDate;

    /*----------------end of block------------------
     *                      1                       */


    /*----------------------2-------------------
     *              working with database        */


    private void getProperRoomsType(){
        //select from table

    }
    /*----------------end of block------------------
     *                      2                       */


    /*----------------------3-------------------
     *              initializer and similar methods        */
    public void initialize(){
        setConverter();
        //proper 'select room type' initializer
        /*MenuItem mi = new MenuItem("Doesn't matter");
        mi.setOnAction(e -> {
                selRoomTypeMB.setText(mi.getText());});
        selRoomTypeMB.getItems().add(mi);*/
    }

    private void setConverter(){
        StringConverter converter = new StringConverter<LocalDate>() {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(pattern);
            @Override
            public String toString(LocalDate date) {
                if (date != null) {
                    return dateFormatter.format(date);
                } else {
                    return "";
                }
            }
            @Override
            public LocalDate fromString(String string) {
                if (string != null && !string.isEmpty()) {
                    return LocalDate.parse(string, dateFormatter);
                } else {
                    return null;
                }
            }
        };
        calendar.setConverter(converter);
    }
    /*----------------end of block------------------
     *                      3                       */

    /*----------------------4---------------------
     *              bunch of handlers            */
    public void checkinButtonOnAction(ActionEvent actionEvent) {
        LocalDate date = checkinDate;
        checkinDate = calendar.getValue();
        if(checkinDate == null || checkinDate.isBefore(LocalDate.now()) || (checkoutDate != null && checkinDate.isAfter(checkoutDate))) {
            checkinDate = date;
            return;
        }
        checkinTF.setText(calendar.getConverter().toString(calendar.getValue()));
        calendarLabel.setText("Select check-out date");
    }

    public void checkoutButtonOnAction(ActionEvent actionEvent) {
        LocalDate date = checkoutDate;
        checkoutDate = calendar.getValue();
        if(checkDates()) {
            checkoutTF.setText(calendar.getConverter().toString(calendar.getValue()));
            peopleTextField.setEditable(true);
        }else{
            checkoutDate = date;
        }
    }

    public void reserveButtonOnAction(ActionEvent actionEvent) {
        //  String createView = "create or replace view Occupied as select data_od, data_do, id_pokoju from pokoje natural join rezerwacje_pokoje where false"; // create empty view
        try {
            Statement statement = Model.connection.createStatement();
            //    statement.executeUpdate(createView);
        }catch(Exception e){
            e.printStackTrace();
        }
        setAllVisible();
    }

    public void seeMyVisitsButtonOnAction(ActionEvent actionEvent) {
        try {
            bringToSeeVisitsState();
            showDateChooser();
        }catch (Exception e){
            e.printStackTrace();
            System.err.println(e.getMessage());
        }
    }

    /*----------------end of block------------------
     *                      4                       */

    /*----------------------5---------------------
     *         bunch of checker  methods            */
    private boolean checkDates(){
        if(checkinDate == null) return false;
        return checkinDate.isBefore(checkoutDate);
    }

    private void checkOtherMyReservations(){
        if(reservations.isEmpty())
            return;

    }

    private boolean checkDateFormat(String date){
        Pattern pattern2 = Pattern.compile("[0-9]{4}-[0-9]{2}-[0-9]{2}");
        Matcher matcher = pattern2.matcher(date);
        return matcher.matches();
    }

    /*----------------end of block------------------
     *                      5                      */


    /*----------------------6---------------------
     *           bunch of handler  methods            */
    public void mainReserveBaction(ActionEvent actionEvent) {
        addCurrentState();
        showTotalConfirmation();//confirmation status is updated here
        if(confirmationStatus == ConfirmationStatus.Back) {
            return;
        }
        bringToInitialState();
    }

    public void oneMoreResBaction(ActionEvent actionEvent) {
        addCurrentState();
        bringToInitialState();
    }
    public void extraServicesBaction(ActionEvent actionEvent) {
        try{
            Statement statement = Model.connection.createStatement();
            String selectServices = "select * from uslugi_dod";
            ResultSet rs = statement.executeQuery(selectServices);
            // ArrayList<Integer> id = new ArrayList<>(); //id_uslugi
            ArrayList<String> names = new ArrayList<>(); //nazwa
            ArrayList<Integer> prices = new ArrayList<>(); //ceny
            while(rs.next()){
                //id.add(rs.getInt("id_uslugi_dod"));
                names.add(rs.getString("nazwa"));
                prices.add(rs.getInt("cena"));
            }
            //!!!!!!!!!!!PO DODANIU USLUGI
            /*
            String selectIdAndPrice = "select id_uslugi_dod, cena from uslugi_dod where nazwa = " + wybranaUluga + ";";
            ResultSet rs4 = statement.executeQuery(selectIdAndPrice);
            rs4.next();
            int daysServices = policzyc dni;
            int amount = liczba dni;
            actualPrice += rs4.getInt("id_uslugi_dod") *daysServices * amount ;*/
            //TODO = wybieranie uslug
        }catch (Exception e){
            System.err.println(e.getMessage());
        }
    }
    /*----------------end of block------------------
     *                      6                    */

    /*----------------------7---------------------
     *           bunch of helper  methods            */
    private void setAllVisible() {
        calendar.setVisible(true);
        calendarLabel.setVisible(true);
        checkinButton.setVisible(true);
        checkoutButton.setVisible(true);
        checkinTF.setVisible(true);
        checkoutTF.setVisible(true);
        peopleTextField.setVisible(true);
        selRoomTypeMB.setVisible(true);
        howManyPeopleL.setVisible(true);

    }
    private void enableRegistration(){
        mainReserveB.setVisible(true);
        extraServicesB.setVisible(true);
        oneMoreResB.setVisible(true);
    }

    private void bringToInitialState(){
        calendarLabel.setText("Select check-in date");
        selRoomTypeMB.getItems().removeIf(e -> true);
        selRoomTypeMB.setText("Select room type");
        checkinTF.setText("");
        checkoutTF.setText("");
        peopleTextField.setText("");
        calendar.setValue(null);
        checkinDate = null;
        checkoutDate = null;
    }


    public void peopleInRoomAction(KeyEvent keyEvent) {
        if (keyEvent.getCode().equals(KeyCode.ENTER) || keyEvent.getCode().equals(KeyCode.TAB)) {
            selRoomTypeMB.requestFocus();
            try {
                Statement stmt = Model.connection.createStatement();
                selRoomTypeMB.getItems().removeIf(e -> !e.getText().equals("Doesn't matter"));
                int peopleAmount;
                if(peopleTextField.getText().equals(""))
                    peopleAmount = 1;
                else
                    peopleAmount = Integer.parseInt(peopleTextField.getText());
                String createView = "create or replace view reserved as " +
                        "select typ, id_pokoju " +
                        "from pokoje natural join rezerwacje_pokoje "+
                        "where max_liczba_osob >= " +peopleAmount +
                        " and (('" + checkinTF.getText() + "'::date < data_od and data_od < '" + checkoutTF.getText() + "'::date) or "+
                        " ('"+checkinTF.getText() +"'::date < data_do and data_do < '" + checkoutTF.getText() + "'::date) or " +
                        " (data_od <= '" + checkinTF.getText() + "'::date and '" + checkoutTF.getText() + "'::date <= data_do )) and anulowane_data is null;";
                stmt.executeUpdate(createView);
                StringBuilder select = new StringBuilder("select distinct typ" +
                        " from pokoje "+
                        " where max_liczba_osob >= " +peopleAmount + " and id_pokoju not in " +
                        "(select id_pokoju from reserved)");
                for(Pair<Reservation, List<Services>> pair : reservations)
                {
                    if((checkinDate.isBefore(LocalDate.parse(pair.t.checkinDate)) && checkinDate.isAfter(LocalDate.parse(pair.t.checkoutDate))) ||
                            (checkoutDate.isAfter(LocalDate.parse(pair.t.checkinDate)) && checkoutDate.isBefore(LocalDate.parse(pair.t.checkoutDate))) ||
                            ((checkinDate.isBefore(LocalDate.parse(pair.t.checkinDate)) || checkinDate.isEqual(LocalDate.parse(pair.t.checkinDate)) ) && (checkoutDate.isAfter(LocalDate.parse(pair.t.checkoutDate)) || checkoutDate.isEqual(LocalDate.parse(pair.t.checkoutDate)) )) )
                        select.append(" and id_pokoju <> " + pair.t.idRoom);
                }
                select.append(";");
                String roomType;
                ResultSet rs = stmt.executeQuery(select.toString());
                while(rs.next()){
                    roomType = rs.getString("typ") ;
                    MenuItem nextMI = new MenuItem();
                    nextMI.setText(roomType);
                    nextMI.setOnAction(e -> {
                        selRoomTypeMB.setText(nextMI.getText());
                    });
                    selRoomTypeMB.getItems().add(nextMI);
                }
                rs.close();
            }catch(Exception e){
                e.printStackTrace();
                System.err.println(e.getMessage());
            }
        }
    }
    private void setRoomType(String x){
        roomType = new StringBuilder(x);
    }
    public void selRoomTypeMBaction(ActionEvent actionEvent) {
        enableRegistration();
        //
    }
    private boolean checkData(){
        if(checkinDate == null || checkoutDate == null) return false;
        return true;
    }

    //returns true if data was proper and false otherwise
    private boolean addCurrentState() {
        try {
            Statement statement = Model.connection.createStatement();
            StringBuilder select = new StringBuilder("select id_pokoju from pokoje where typ = '" + selRoomTypeMB.getText()  + "' and id_pokoju not in (select id_pokoju from reserved)");
            for(Pair<Reservation, List<Services>> pair : reservations)
            {
                if((checkinDate.isBefore(LocalDate.parse(pair.t.checkinDate)) && checkinDate.isAfter(LocalDate.parse(pair.t.checkoutDate))) ||
                        (checkoutDate.isAfter(LocalDate.parse(pair.t.checkinDate)) && checkoutDate.isBefore(LocalDate.parse(pair.t.checkoutDate))) ||
                        ((checkinDate.isBefore(LocalDate.parse(pair.t.checkinDate)) || checkinDate.isEqual(LocalDate.parse(pair.t.checkinDate)) ) && (checkoutDate.isAfter(LocalDate.parse(pair.t.checkoutDate)) || checkoutDate.isEqual(LocalDate.parse(pair.t.checkoutDate)) )) )
                    select.append(" and id_pokoju <> " + pair.t.idRoom);
            }
            select.append(" ;");
            ResultSet rs = statement.executeQuery(select.toString());
            rs.next();
            int idRoom = rs.getInt("id_pokoju");
            String selectPrice ="select oblicz_znizke(" + idGast + ", (" + actualPrice +" + cena_podstawowa) * ('" + checkoutTF.getText() + "'::date - '" + checkinTF.getText() + "'::date), '" +checkinTF.getText() + "'::date) as cena from pokoje where id_pokoju = " + idRoom + ";";
            ResultSet rs2 = statement.executeQuery(selectPrice);
            rs2.next();
            int price = rs2.getInt("cena");
            Reservation reservation = new Reservation(checkinTF.getText(), checkoutTF.getText(), Integer.parseInt(peopleTextField.getText()), selRoomTypeMB.getText(), idRoom, price);
            reservations.add(new Pair<Reservation, List<Services>>(reservation, new ArrayList<>()));
            String drop = "drop view if exists reserved";
            statement.executeUpdate(drop);
            // String updateOccupied = "update Occupied set (data_od, data_do, id_pokoju) = ('" + checkinTF.getText()+ "'::date, '" + checkoutTF.getText() + "'::date, "+ idRoom+");";
            // statement.executeUpdate(updateOccupied);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    private void showDateChooser() {
        Stage stage = new Stage();
        stage.setWidth(400);
        stage.setHeight(300);
        String date_from = null;
        final String date_to = null;
        stage.setTitle("Select date from/to");
        StackPane pane = new StackPane();
        TextField dfrom = new TextField();
        TextField dto = new TextField();
        Button submit = new Button("Submit");
        VBox vBox = new VBox();
        vBox.setSpacing(10);
        vBox.getChildren().add(dfrom);
        vBox.getChildren().add(dto);
        pane.getChildren().add(vBox);
        vBox.getChildren().add(submit);
        submit.setOnAction(e -> {
            if(!checkDateFormat(dfrom.getText()) || !checkDateFormat(dfrom.getText())){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error Dialog");
                alert.setHeaderText("Incorrect data!");
                alert.setContentText("Please, enter dates in correct format YYYY-MM-DD.");
                alert.showAndWait();
            }
            else {
                addTableView(dfrom.getText(), dto.getText());
            }
            // stage.close();
        });
        dfrom.setPromptText("Insert date from, format YYYY-MM-DD");
        dto.setPromptText("Insert date to, format YYYY-MM-DD");
        dfrom.setFocusTraversable(false);
        dto.setFocusTraversable(false);
        pane.setAlignment(vBox, Pos.CENTER);
        Scene scene = new Scene(pane);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(this.root.getScene().getWindow());
        stage.setScene(scene);
        stage.showAndWait();

    }
    void  addTableView(String dFrom, String dTO){
        try {
            String selectVisits = "select * from rezerwacje_pokoje natural join rezerwacje_goscie where id_goscia = " + idGast + " and data_od >= '" + dFrom + "'::date and data_do <= '" +dTO +"'::date and anulowane_data is null;";
            Statement statement = Model.connection.createStatement();
            ResultSet rs = statement.executeQuery(selectVisits);
            TableView table = new TableView();
            TableColumn idResTC = new TableColumn("Id reservation");
            idResTC.setResizable(true);
            idResTC.setPrefWidth(150);
            TableColumn fromTC = new TableColumn("Check in");
            fromTC.setResizable(true);
            fromTC.setPrefWidth(140);
            TableColumn toTC = new TableColumn("Check out");
            toTC.setPrefWidth(140);
            TableColumn priceTC = new TableColumn("Price");
            priceTC.setPrefWidth(100);

            ObservableList<ReserveConfirmationController.ReservationTableView> data =
                    FXCollections.observableArrayList();
            while (rs.next()) {
                data.add(new ReserveConfirmationController.ReservationTableView(rs.getString("id_rez_zbiorczej"), rs.getString("data_od"), rs.getString("data_do"), rs.getInt("cena")));
            }
            idResTC.setCellValueFactory(
                    new PropertyValueFactory<>("room"));
            fromTC.setCellValueFactory(
                    new PropertyValueFactory<>("checkIn"));
            toTC.setCellValueFactory(
                    new PropertyValueFactory<>("checkOut"));
            priceTC.setCellValueFactory(
                    new PropertyValueFactory<>("price"));

            table.setItems(data);
            table.getColumns().addAll(idResTC, fromTC, toTC, priceTC);

            VBox vbox = new VBox();
            vbox.setSpacing(5);
            vbox.getChildren().add(table);
            Stage stageVisits = new Stage();
            stageVisits.setTitle("My visits and reservations");
            stageVisits.setWidth(vbox.getWidth());
            stageVisits.setScene(new Scene(vbox));
            stageVisits.show();

        }catch (Exception e){
            e.printStackTrace();
        }

    }
    private void bringToSeeVisitsState(){
        calendarLabel.setText("Select date from/to");
    }

    boolean showTotalConfirmation(){
        try {
            Stage confirmStage = new Stage();
            confirmStage.initModality(Modality.WINDOW_MODAL);
            confirmStage.initOwner(this.root.getScene().getWindow());
            confirmStage.setTitle("Please, confirm all your reservations");
            FXMLLoader resLoader = new FXMLLoader(getClass().getResource("reserveConfirmationForm.fxml"));
            AnchorPane resConfirmRoot = resLoader.load();
            resConfirmController = resLoader.getController();
            resConfirmController.reservationList(reservations);
            resConfirmController.setMainTableView();
            resConfirmController.setTotalCostTF();
            Scene resConfirmScene = new Scene(resConfirmRoot);
            confirmStage.setScene(resConfirmScene);
            resConfirmController.backB.setOnAction(e -> {
                confirmStage.close();

            });
            resConfirmController.confirmB.setOnAction(e -> {
                try{
                    Statement statement = Model.connection.createStatement();
                    String mainReserve = "insert into rezerwacje_goscie values (default, " + idGast + ");";
                    statement.executeUpdate(mainReserve);
                    String selectID = "select id_rez_zbiorczej from rezerwacje_goscie order by 1 desc limit 1;";
                    ResultSet rs = statement.executeQuery(selectID);
                    rs.next();
                    int mainReserveId = rs.getInt("id_rez_zbiorczej");
                    for (Pair<Reservation, List<Services>> pair : reservations) {
                        //insert into
                        String insert = "insert into rezerwacje_pokoje values (" + mainReserveId + ", default, " + pair.t.idRoom + ", '" + pair.t.checkinDate + "'::date, '" + pair.t.checkoutDate + "'::date, " + pair.t.price + ", 'G', " + pair.t.amountOfPeople + ", default);";
                        //System.out.println(insert);
                        statement.executeUpdate(insert);
                        String selectIdOne = "select id_rez_pojedynczej from rezerwacje_pokoje order by 1 desc limit 1;";
                        ResultSet rs3 = statement.executeQuery(selectIdOne);
                        rs3.next();
                        changeConfirmationStatus(ConfirmationStatus.Default);
                    }
                   /*String dropOccupied = "drop view if exists Occupied;";
                    statement.executeUpdate(dropOccupied);*/
                    reservations.clear();
                }catch (Exception e2){
                    System.out.println(e2.getMessage());
                }
                confirmStage.close();
                changeConfirmationStatus(ConfirmationStatus.Back);
                confirmStage.close();
            });
            confirmStage.show();
        }catch(IOException e){
            e.printStackTrace();
        }
        return false;
    }

    private void changeConfirmationStatus(ConfirmationStatus s){
        confirmationStatus = s;
        return;
    }
    /*----------------end of block------------------
     *                      7                     */

}