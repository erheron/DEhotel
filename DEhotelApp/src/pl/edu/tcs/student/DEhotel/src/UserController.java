import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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

        Reservation(String indate,String outdate, int i, String type){
            checkinDate = indate;
            checkoutDate = outdate;
            amountOfPeople = i;
            roomType = type;
        }
    }
    private class Services{
        String services;
        public Services(){
            services = null;
        }
        public Services(String s){
            services = s;
        }
    }

    private List<Pair<Reservation, List<Services>> > reservations;
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
        checkinDate = calendar.getValue();
        if(checkinDate == null || checkinDate.isBefore(LocalDate.now())) return;
        checkinTF.setText(calendar.getConverter().toString(calendar.getValue()));
        calendarLabel.setText("Select check-out date");


    }

    public void checkoutButtonOnAction(ActionEvent actionEvent) {
        checkoutDate = calendar.getValue();
        if(checkDates()) {
            checkoutTF.setText(calendar.getConverter().toString(calendar.getValue()));
            peopleTextField.setEditable(true);
        }
    }

    public void reserveButtonOnAction(ActionEvent actionEvent) {
        setAllVisible();


    }

    public void seeMyVisitsButtonOnAction(ActionEvent actionEvent) {
        //some postgresql stuff
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
        //TODO= insert into table all stuff from 'reservations'
        //TODO= and perform checking if non-empty

    }

    public void oneMoreResBaction(ActionEvent actionEvent) {
        //add current state to "reservation" list
        bringToInitialState();
    }
    public void extraServicesBaction(ActionEvent actionEvent) {
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
        if(keyEvent.getCode().equals(KeyCode.ENTER) || keyEvent.getCode().equals(KeyCode.TAB))
            selRoomTypeMB.requestFocus();
    }
    public void selRoomTypeMBaction(ActionEvent actionEvent) {
        enableRegistration();
        //
    }

    private boolean addCurrentState(){
        try{
            curReservation = new Reservation(checkinDate.toString(), checkoutDate.toString(), Integer.parseInt(peopleTextField.getText()), "coś do zrobienia");
        }catch(Exception e){
            return false;
        }
        return true;

    /*----------------end of block------------------
     *                      12                    */






}
