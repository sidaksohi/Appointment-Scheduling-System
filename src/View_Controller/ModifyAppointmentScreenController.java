/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package View_Controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

//Imports
import utils.DBConnection;
import utils.DBQuery;
import javafx.collections.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import java.io.IOException;
import utils.*;
import com.mysql.jdbc.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.TimeZone;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

/**
 * FXML Controller class
 *
 * @author Sidak
 */
public class ModifyAppointmentScreenController implements Initializable {

    @FXML
    private ChoiceBox<String> customerList;
    @FXML
    private TextField titleField;
    @FXML
    private TextField descriptionField;
    @FXML
    private ChoiceBox<String> typeList;
    @FXML
    private ChoiceBox<String> startList;
    @FXML
    private ChoiceBox<String> endList;
    @FXML
    private DatePicker datePicker;

    /**
     * Initializes the controller class.
     */
    Connection conn;
    
    ArrayList<String> allCustomers;
    ArrayList<String> displayStartTimes = new ArrayList<>();
    ArrayList<String> displayStartTimesMinus1 = new ArrayList<>();
    ArrayList<String> startTimesUTC = new ArrayList<>();
        
    int startIndex = 0;
    int endIndex = 0;
    
    int appointmentID;
    int customerID;
    String userName;
    String customerName;
    String title;
    String description;
    String type;
    String startTime;
    String endTime;
    String date;
    
    String utcString;
    
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        endList.setDisable(true);
        conn = DBConnection.getConnection();
        allCustomers = new ArrayList<>();
        String firstCustomer; 
        
        //Disable all previous days in Datepicker
        datePicker.setDayCellFactory(picker -> new DateCell() {
        public void updateItem(LocalDate date, boolean empty) {
            super.updateItem(date, empty);
            LocalDate today = LocalDate.now();

            setDisable(empty || date.compareTo(today) < 0 );
        }});
        
        //Add all customer names to an ArrayList of Strings
    try{
        String selectStatement = "SELECT customerName FROM customer";
        
        DBQuery.setPreprearedStatement(conn, selectStatement); //Create preparedStatment object
        PreparedStatement ps = DBQuery.getPreparedStatement();
        ps.execute(); //Execute statement
        
        ResultSet rs = ps.getResultSet();
        
        
        while(rs.next() == true){
            String name = rs.getString("customerName");
            allCustomers.add(name);
        }
    }catch(SQLException e){ System.out.println("SQLException: modifycustomer selectallcustomers");}
        
        //Add the array of strings to the choiceBox
        customerList.setItems(FXCollections.observableList(allCustomers));
        firstCustomer = allCustomers.get(0);
        customerList.setValue(firstCustomer);
        
        //Add values to type picker
        typeList.setItems(FXCollections.observableArrayList("Beginner", "Intermediate", "Advanced"));
        
        //Set values
        customerList.setValue(customerName);
        titleField.setText(title);
        descriptionField.setText(description);
        typeList.setValue(type);
        
        //Initialize times
        initializeStartTimes();
        
        //Create Duplicate of displayStartTimes, other than the last value
        for(int i = 0; i < displayStartTimes.size() - 1; i++){
            displayStartTimesMinus1.add(displayStartTimes.get(i));
        }
        
        startList.setItems(FXCollections.observableList(displayStartTimesMinus1));
        
    }
    
    ModifyAppointmentScreenController(int appointmentID, String customerName, String title, String description, String type, String startTime, String endTime, String date){
        this.appointmentID = appointmentID;
        this.customerName = customerName;
        this.title = title;
        this.description = description;
        this.type = type;
        this.startTime = startTime;
        this.endTime = endTime;
        this.date = date;
        
    }
    
    private void initializeStartTimes(){
        //Create localZDT of current apt time and date
        String timeZoneID = String.valueOf(TimeZone.getDefault().getID());
        
        // 07/22/2020T02:00 AM [America/Los Angeles]
        DateTimeFormatter f = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a z");
        DateTimeFormatter g = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        ZonedDateTime localZDT = ZonedDateTime.parse(date+" "+startTime+" "+timeZoneID, f);

        //Now utcString is a String with the same SQL value of the current Apt, useful for checking overlaps compared to ALL OTHER appointments
        localZDT = localZDT.withZoneSameInstant(ZoneId.of("UTC"));
        utcString = localZDT.format(g);
        
        
        //Create arraylist of UTC startTimse
        String startTime;
        for(int i = 9; i < 18; i++){
            if(i == 9 ){
                startTime = ("2020-07-20T0" + i + ":00:00.000Z");
                startTimesUTC.add(startTime);
            }
            else if (i > 9){
                startTime = ("2020-07-20T" + i + ":00:00.000Z");
                startTimesUTC.add(startTime);
            }
        }
        
        //Pass arraylist through a method which will convert them to localtime
        timeToLocalZDT(startTimesUTC);
    }
    
    public void timeToLocalZDT(ArrayList<String> startTimesUTC){
            ArrayList<ZonedDateTime> localZDTs = new ArrayList<>();
            
        for(int i = 0; i < startTimesUTC.size() ; i++){
            String string = startTimesUTC.get(i);
            ZonedDateTime utcZDT = ZonedDateTime.parse(string);
            ZonedDateTime localZDT = utcZDT.withZoneSameInstant(ZoneId.systemDefault());
            localZDTs.add(localZDT);
        }
        
        localZDTFormatter(localZDTs);
        
    }
    
    public void localZDTFormatter(ArrayList<ZonedDateTime> localZDTs){
            DateTimeFormatter g = DateTimeFormatter.ofPattern("hh:mm a z");
            for(int i = 0; i < localZDTs.size() ; i++){
                displayStartTimes.add(String.valueOf(localZDTs.get(i).format(g)));
            }
        }
    
    private void alert(String title, String message){
        Stage window = new Stage();
        
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(title);
        window.setMinWidth(300);
        
        Label label = new Label();
        label.setText(message);
        Button closeButton = new Button("Close");
        closeButton.setOnAction(e -> window.close());
        
        VBox layout = new VBox(10);
        layout.getChildren().addAll(label, closeButton);
        layout.setAlignment(Pos.CENTER);
        
        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.show();
    }


    @FXML
    private void cancel(MouseEvent event) throws IOException {
        cancelConfirmation("","Cancel modifying appointment?", event);
    }
    
    private void cancelConfirmation(String title, String message, MouseEvent event) throws IOException{Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(title);
        window.setMinWidth(350);
        
        Label label = new Label();
        label.setText(message);
        Button closeButton = new Button("OK");
        Button cancelButton = new Button("Cancel");
        
        closeButton.setOnAction(e -> cancelOk(window, event));
        
        cancelButton.setOnAction(e -> window.close());
        
        
        VBox layout = new VBox(10);
        HBox buttons = new HBox(10);
        buttons.getChildren().addAll(closeButton, cancelButton);
        buttons.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(label, buttons);
        layout.setAlignment(Pos.CENTER);
        
        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.show();
    }
    
    private void cancelOk(Stage window, MouseEvent event){
        window.close();
        ((Stage)(((Button)event.getSource()).getScene().getWindow())).close();
        try{
            openMain();
        }catch(Exception e){
            
        }
    }
    
    private void openMain() throws IOException{
        Parent root = FXMLLoader.load(getClass().getResource("/View_Controller/MainScreen.fxml"));
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle("Appointment Schedule");
        stage.sizeToScene();
        stage.show();
    }

    @FXML
    private void delete(MouseEvent event) throws IOException {
        //Delete appointment
        String deleteStatement = "DELETE FROM appointment WHERE appointmentId=?";
    try{
        DBQuery.setPreprearedStatement(conn, deleteStatement); //Create preparedStatment object
        
        PreparedStatement ps = DBQuery.getPreparedStatement();
        
        //Keymap
        ps.setInt(1, appointmentID);
        
        ps.execute(); //Execute
    }catch(SQLException e){
        System.out.println("SQLException: modifyappointment deleteappointment");
        return;
    }    
    
        //Print message and call class to close window and open main
        System.out.println("Appointment deleted!");
        deleteDone(event);
    
    }//end
    
    private void deleteDone(MouseEvent event) throws IOException{
       ((Stage)(((Button)event.getSource()).getScene().getWindow())).close();
        openMain();
    }
    

    @FXML
    private void save(MouseEvent event) throws IOException {
        //Add error if any fields are blank
        if(titleField.getText().isEmpty()==true || descriptionField.getText().isEmpty()==true){
            alert("Error","Fields cannot be empty");
            return;
        }
        if (datePicker.getValue() == null || startList.getValue() == null || endList.getValue() == null){
            alert("Error", "A date, start time, and end time must be selected!");
            return;
        }
        
        //Get start and end
        //Convert selected date, starttime, endtime to SQL format
        String startTimeUTC = UTCFormatter(startTimesUTC.get(startIndex));
        String endTimeUTC = UTCFormatter(startTimesUTC.get(endIndex));
        String localDate = String.valueOf(datePicker.getValue());
        String startTimeSQL = localDate + " " + startTimeUTC;
        String endTimeSQL = localDate + " " + endTimeUTC;
        
        //Add error if the appointment is overlapping with another appointment
        //2020-07-20T09:00:00.000Z
        //Get selected start and end ZDTs
        String startString = localDate + "T" + startTimeUTC + ".000Z";
        ZonedDateTime startZDT = ZonedDateTime.parse(startString);
        
        String endString = localDate + "T" + endTimeUTC + "Z";
        ZonedDateTime endZDT = ZonedDateTime.parse(endString);
        
        //Create Array to store all appointment start and end times in ZDT
        ArrayList<ZonedDateTime> aptStartZDT = new ArrayList<>();
        aptStartZDT.clear();
        ArrayList<ZonedDateTime> aptEndZDT = new ArrayList<>();
        aptEndZDT.clear();
        
        //Select all appointments, parse them into ZDTs, and add them all into startZDT arraylist and endZDT arraylist
        String selectStatement = "SELECT * FROM appointment";
    try{    
        DBQuery.setPreprearedStatement(conn, selectStatement); //Create preparedStatment object
        PreparedStatement ps = DBQuery.getPreparedStatement();
        ps.execute(); //Execute statement
        ResultSet rs = ps.getResultSet();
        
        while(rs.next() == true){
            String dateApt = String.valueOf(rs.getDate("start").toLocalDate());
            String startApt = String.valueOf(rs.getTime("start").toLocalTime());
            String endApt = String.valueOf(rs.getTime("end").toLocalTime());
            
         //Only add the ZDTs which do not equal the sent in ZDT
         //startTime, endTime, and date are sent in, convert them to a UTC ZDT and if they are equal then DO NOT add
         String checkSame = dateApt + " " + startApt;
         
         if(checkSame.equalsIgnoreCase(utcString)==false){   
             
            String aptStartZDT1 = dateApt+"T"+startApt+"Z";
            String aptEndZDT1 = dateApt+"T"+endApt+"Z";
            
            ZonedDateTime aptStartParsed = ZonedDateTime.parse(aptStartZDT1);
            ZonedDateTime aptEndParsed = ZonedDateTime.parse(aptEndZDT1);
            
            aptStartZDT.add(aptStartParsed);
            aptEndZDT.add(aptEndParsed);
         }
        }
    }catch(SQLException e){
        System.out.println("SQLException: addappointment selectappointments");
        return;
    }
        //See if the current Start and End ZDTs overlap with ANY other appointment Start and End ZDTs
        //startZDT endZDT = selected ZDTs
        //aptStartZDT.get(i) aptEndZDT.get(i) = APT ZDTs
        
        //Logic for finding overlap
        
        for(int i=0; i < aptEndZDT.size(); i++){                                    //if both are positive or both are negative then it doesnt overlap, if one is pos and one is neg it overlaps
            long check1 = Duration.between(startZDT, aptEndZDT.get(i)).toHours(); //5amstart, 8am end ++ //5amstart, 4am end -- //5amstart, 7amend ++ //5am start, 6am end ++  
            long check2 = Duration.between(endZDT, aptStartZDT.get(i)).toHours(); //6am end, 7am start ++ //6am end, 3am start -- //6am end, 4am start -- //6am end, 5am start --
            if(check1 < 0 && check2 > 0){
                alert("Error","Selected times and date overlaps with another appointment!");
                return;
            }
            if(check1 > 0 && check2 < 0){
                alert("Error","Selected times and date overlaps with another appointment!");
                return;
            }
        }
        
        //Get customerID of selected customer, store in customerID
    try{
        String selectStatement3 = "SELECT customerId FROM customer WHERE customerName=? AND customerId > 0;";
        DBQuery.setPreprearedStatement(conn, selectStatement3); 
        PreparedStatement getCustomerID = DBQuery.getPreparedStatement();
        
        getCustomerID.setString(1, customerList.getValue());
        
        getCustomerID.execute();
        ResultSet getCustomerIDResult = getCustomerID.getResultSet();
        while(getCustomerIDResult.next() == true){
        customerID = getCustomerIDResult.getInt("customerId");
        }
    }catch(SQLException e){ 
        System.out.println("SQLException: add appointment, getuserIduserName");
        return;        
    }
        //Get userName, store in userName
    try{
        String selectStatement4 = "SELECT userName FROM user WHERE active=1 AND userId > 0;";
        DBQuery.setPreprearedStatement(conn, selectStatement4); 
        PreparedStatement getUserID = DBQuery.getPreparedStatement();
        
        getUserID.execute();
        ResultSet getUserIDResult = getUserID.getResultSet();
        while(getUserIDResult.next() == true){
        userName = getUserIDResult.getString("userName");
        }
    }catch(SQLException e){ 
        System.out.println("SQLException: modifyappointment, getuserIduserName");
        return;        
    }
    
    
        //Update appointment
    try{
        String updateStatement = "UPDATE appointment SET customerId=?, title=?, description=?, type=?, start=?, end=?, lastUpdateBy=? WHERE appointmentId=?";
        
        DBQuery.setPreprearedStatement(conn, updateStatement); //Create preparedStatment object
        
        PreparedStatement ps = DBQuery.getPreparedStatement();
        
        //Keymap
        ps.setInt(1, customerID);
        ps.setString(2, titleField.getText());
        ps.setString(3, descriptionField.getText());
        ps.setString(4, typeList.getValue());
        ps.setString(5, startTimeSQL);
        ps.setString(6, endTimeSQL);
        ps.setString(7, userName);
        ps.setInt(8, appointmentID);
        
        ps.execute(); //Execute
    }catch(SQLException e){ 
        System.out.println("SQLException: modifyappointment updateappointment");
        return;
    }
    
        //Close window
        ((Stage)(((Button)event.getSource()).getScene().getWindow())).close();
        System.out.println("Appointment updated!");
    
        //Open Main
        Parent root = FXMLLoader.load(getClass().getResource("/View_Controller/MainScreen.fxml"));
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle("Appointment Schedule");
        stage.sizeToScene();
        stage.show();
            
            
            
            
    }//end
    
    private String UTCFormatter(String string){
        //2020-07-20T09:00:00.000Z
        ZonedDateTime utcZDT = ZonedDateTime.parse(string);
        DateTimeFormatter g = DateTimeFormatter.ofPattern("HH:mm:ss");
        return String.valueOf(utcZDT.format(g));
    }

    @FXML
    private void startListSelect(ActionEvent event) {
        endList.setDisable(false);
        startList.setDisable(true);
        
        //Create instance of displayedstarttimes
        ArrayList<String> endTimeTemp = new ArrayList<>();
        for(int i = 0; i < displayStartTimes.size(); i++){
            endTimeTemp.add(displayStartTimes.get(i));
        }
        
        //Find the index of the selected start time
        for(int i = 0; i < displayStartTimesMinus1.size() ; i++){
            if (startList.getValue().equalsIgnoreCase(displayStartTimesMinus1.get(i)) == true){
                startIndex = i;
            }
        }
        
        for(int i=0; i<=startIndex; i++){
            endTimeTemp.remove(0);
        }
        
        endList.setItems(FXCollections.observableList(endTimeTemp));
    }

    @FXML
    private void endListSelect(ActionEvent event) {
        //Find the index of the selected end time, in relation to the index of displayStartTimes
        for(int i = 1; i < displayStartTimes.size() ; i++){
            if (endList.getValue().equalsIgnoreCase(displayStartTimes.get(i)) == true){
                endIndex = i;
            }
        }
    }
    
}
