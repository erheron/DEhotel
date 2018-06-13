import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

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


    /*----------------------1-------------------
     *              general fields used        */

    private final String pattern = "yyyy-MM-dd";
    
    //list for reservations

    public <T, U> class Pair<T, U> {
        T t;
        U u;
        Pair(T t, U u){
            this.t = t;
            this.u = u;
        }

    }
    private class reservation{
        String checkinDate, checkoutDate;    
        int amountOfPeople;
        String roomType;
    }
    private class services{
        String services;
        public services(){
            services = null;
        }
    }

    }
    List<Pair<reservation, List<services> > > reservations;

    LocalDate checkinDate, checkoutDate;

    /*----------------end of block------------------
     *                      1                       */

    public void initialize(){
        //show login window
        setConverter();
    }

    public void setConverter(){
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

    }

    public void seeMyVisitsButtonOnAction(ActionEvent actionEvent) {
        //some postgresql stuff
    }

    /*----------------------10---------------------
     *         bunch of checker  methods            */
    private boolean checkDates(){
        return checkinDate.isBefore(checkoutDate);
    }

    /*----------------end of block------------------
     *                      10                      */


    /*----------------------11---------------------
     *           bunch of handler  methods            */
    public void mainReserveBaction(ActionEvent actionEvent) {
        //insert into table all stuff from 'reservations'
        //perform checking if non-empty
    }

    public void oneMoreResBaction(ActionEvent actionEvent) {
        //change state to initial

    }
    /*----------------end of block------------------
     *                      11                    */


}
