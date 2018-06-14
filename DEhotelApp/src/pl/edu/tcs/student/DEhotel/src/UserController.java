import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.util.StringConverter;

import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

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
    /*----------------------1-------------------
     *              general fields used        */

    private final String pattern = "yyyy-MM-dd";
    Connection connection;
    private StringBuilder roomType;
    private int idRoom;
    int idGast;
    //list for reservations

    public class Pair<T, U> {
        T t;
        U u;
        Pair(T t, U u){
            this.t = t;
            this.u = u;
        }
    }
    private class Reservation{
        String checkinDate, checkoutDate;    
        int amountOfPeople;
        String roomType;
        int idRoom;

        Reservation(String indate,String outdate, int i, String type, int id){
            checkinDate = indate;
            checkoutDate = outdate;
            amountOfPeople = i;
            roomType = type;
            idRoom = id;
        }
    }
    private class Services{
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

    private List<Pair<Reservation, List<Services>> > reservations = new ArrayList<>();
    List<Services> curServices;
    Reservation curReservation;
    private LocalDate checkinDate, checkoutDate;

    /*----------------end of block------------------
     *                      1                       */


    /*----------------------2-------------------
     *              working with database        */


    private void getProperRoomsType(){
        //select from table

    }
    /*----------------end of block------------------
     *                      1                       */

    public void initialize(){
        //show login window
        setConverter();
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
        setAllVisible();


    }

    public void seeMyVisitsButtonOnAction(ActionEvent actionEvent) {
        try {
            String selectVisits = "select * from rezerwacje_pokoje natural join rezerwacje_goscie where id_goscia = " + idGast + ";";
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(selectVisits);
            while(rs.next()){
                //TODO = przegladnie rezerwacji
            }
        }catch (Exception e){
            System.err.println(e.getMessage());
        }
    }

    /*----------------------10---------------------
     *         bunch of checker  methods            */
    private boolean checkDates(){
        if(checkinDate == null) return false;
        return checkinDate.isBefore(checkoutDate);
    }

    /*----------------end of block------------------
     *                      10                      */


    /*----------------------11---------------------
     *           bunch of handler  methods            */
    public void mainReserveBaction(ActionEvent actionEvent) {
        addCurrentState();
        try {
            Statement statement = connection.createStatement();
            String mainReserve = "insert into rezerwacje_goscie values (default, "+ idGast + ");";
            statement.executeUpdate(mainReserve);
            String selectID = "select id_rez_zbiorczej from rezerwacje_goscie order by 1 desc limit 1;";
            ResultSet rs = statement.executeQuery(selectID);
            rs.next();
            int mainReserveId = rs.getInt("id_rez_zbiorczej");
            for(Pair<Reservation, List<Services>> pair : reservations){
                //price
                String selectPrice ="select oblicz_znizke(" + idGast + ",cena_podstawowa * ('" + pair.t.checkoutDate + "'::date - '" + pair.t.checkinDate + "'::date)) as cena from pokoje where id_pokoju = " + pair.t.idRoom + ";";
                ResultSet rs2 = statement.executeQuery(selectPrice);
                rs2.next();
                int price = rs2.getInt("cena");
                //insert into
                String insert = "insert into rezerwacje_pokoje values ("+mainReserveId+ ", default, " + pair.t.idRoom +", '"+ pair.t.checkinDate + "'::date, '" + pair.t.checkoutDate +"'::date, " + price + ", 'G', " + pair.t.amountOfPeople + ", default);";
                System.out.println(insert);
                statement.executeUpdate(insert);
                String selectIdOne = "select id_rez_pojedynczej from rezerwacje_pokoje order by 2 desc limit 1;";
                ResultSet rs3 = statement.executeQuery(selectIdOne);
                rs3.next();
                int idOneRes =rs3.getInt("id_rez_pojedynczej") ;
                int priceToUpdate = price;
                //add services
                for(Services service : pair.u){
                        String selectIdAndPrice = "select id_uslugi_dod, cena from uslugi_dod where nazwa = " + service.services + ";";
                        ResultSet rs4 = statement.executeQuery(selectIdAndPrice);
                        rs4.next();
                        String insertServices = "insert into usl_rez values (" + rs4.getInt("id_uslugi_dod")+", "+ idOneRes + ", " + service.number + ", '" +service.dateFrom + "'::date, '"+ service.dateTo + "'::date, default);";
                        statement.executeUpdate(insertServices);
                        int daysServices;
                        String daysServicesSelect = "select '" + service.dateTo + "'::date - '" + service.dateFrom + "'::date as days;";
                        ResultSet rs5 = statement.executeQuery(daysServicesSelect);
                        daysServices = rs5.getInt("days");
                        priceToUpdate += rs4.getInt("cena") * daysServices * service.number;
                }
                String upadte = "update rezerwacje_pokoje set cena = " + priceToUpdate + ";";
                statement.executeUpdate(upadte);
            }
            reservations.clear();
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        //TODO= wyczyscic guziki, bo sie doda kilka razy

    }

    public void oneMoreResBaction(ActionEvent actionEvent) {
        addCurrentState();
        //add current state to "reservation" list
        bringToInitialState();
    }
    public void extraServicesBaction(ActionEvent actionEvent) {
        try{
            Statement statement = connection.createStatement();
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
            //TODO wyswietlanie i wybor uslug, wybor dat i liczby
        }catch (Exception e){
            System.err.println(e.getMessage());
        }
    }
    /*----------------end of block------------------
     *                      11                    */

    /*----------------------12---------------------
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

    }
    private void enableRegistration(){
        mainReserveB.setVisible(true);
        extraServicesB.setVisible(true);
        oneMoreResB.setVisible(true);
    }

    private void bringToInitialState(){
        calendarLabel.setText("Select check-in date");
        checkinTF.setText("");
        checkoutTF.setText("");
        peopleTextField.setText("");
        calendar.setValue(null);
    }


    public void peopleInRoomAction(KeyEvent keyEvent) {
        if (keyEvent.getCode().equals(KeyCode.ENTER) || keyEvent.getCode().equals(KeyCode.TAB)) {
            selRoomTypeMB.requestFocus();
            try {
                Statement stmt = connection.createStatement();
                selRoomTypeMB.getItems().removeIf(e -> !e.getText().equals("Doesn't matter"));
                String select = "select distinct typ, id_pokoju " +
                                "from pokoje" +
                                " where max_liczba_osob >= " + Integer.parseInt(peopleTextField.getText()) +
                                " and not exists (select id_rez_pojedynczej from rezerwacje_pokoje " +
                                    "where (data_od >= '"+ checkinTF.getText() +"'::date and data_od < '"+ checkoutTF.getText() + "'::date) or " +
                                    "(data_do > '" + checkinTF.getText() +"'::date and data_do <= '"+ checkoutTF.getText()+ "'::date ));";
                String roomType;
                ResultSet rs = stmt.executeQuery(select);
                while(rs.next()){
                    roomType = rs.getString("typ") ;
                    idRoom = rs.getInt("id_pokoju");
                    MenuItem nextMI = new MenuItem();
                    nextMI.setText(roomType);
                    nextMI.setOnAction(e -> {
                        selRoomTypeMB.setText(nextMI.getText());
                    });
                    nextMI.setOnAction( e ->{
                        setRoomType(nextMI.getText());
                        System.out.println(nextMI.getText());
                    });
                    selRoomTypeMB.getItems().add(nextMI);
                }
                rs.close();
            }catch(Exception e){
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
            System.out.println(checkinTF.getText() + " " + checkoutTF.getText() + " " + Integer.parseInt(peopleTextField.getText()) + " " + selRoomTypeMB.getAccessibleText() + " " + roomType.toString());
            Reservation reservation = new Reservation(checkinTF.getText(), checkoutTF.getText(), Integer.parseInt(peopleTextField.getText()), roomType.toString(), idRoom);
            reservations.add(new Pair<Reservation, List<Services>>(reservation, new ArrayList<>()));
            return true;
        }catch (Exception e){
            return false;
        }
    }

    /*----------------end of block------------------
     *                      12                    */






}
