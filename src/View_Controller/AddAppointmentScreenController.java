/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package View_Controller;

import Model.*;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
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
import javafx.scene.control.DateCell;
import javafx.scene.control.TextField;

/**
 * FXML Controller class
 *
 * @author Sidak
 */
public class AddAppointmentScreenController implements Initializable {

    @FXML
    private ChoiceBox<String> customerList;
    @FXML
    private TextField titleField;
    @FXML
    private TextField descriptionField;
    @FXML
    private ChoiceBox<String> typeList;
    @FXML
    private DatePicker datePicker;
    @FXML
    private ChoiceBox<String> startList;
    @FXML
    private ChoiceBox<String> endList;

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
    
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        endList.setDisable(true);
        conn = DBConnection.getConnection();
        allCustomers = new ArrayList<>();
        String firstCustomer; 
        
        //LAMBDA expression: Disable all previous days in Datepicker
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
        
        initializeStartTimes();
        
        //Create Duplicate of displayStartTimes, other than the last value
        for(int i = 0; i < displayStartTimes.size() - 1; i++){
            displayStartTimesMinus1.add(displayStartTimes.get(i));
        }
        
        startList.setItems(FXCollections.observableList(displayStartTimesMinus1));
        
    }
    
    private void initializeStartTimes(){
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
    private void cancel(MouseEvent event) throws IOException{
        cancelConfirmation("","Cancel adding an appointment?", event);
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
    private void save(MouseEvent event) throws IOException{
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
            
            String aptStartZDT1 = dateApt+"T"+startApt+"Z";
            String aptEndZDT1 = dateApt+"T"+endApt+"Z";
            
            ZonedDateTime aptStartParsed = ZonedDateTime.parse(aptStartZDT1);
            ZonedDateTime aptEndParsed = ZonedDateTime.parse(aptEndZDT1);
            
            aptStartZDT.add(aptStartParsed);
            aptEndZDT.add(aptEndParsed);
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
        
        //Find customerID, store in customerID
        int customerID = 0;
    try{
        String selectStatement3 = "SELECT customerId FROM customer WHERE customerName=? AND customerId > 0;";
        DBQuery.setPreprearedStatement(conn, selectStatement3); 
        PreparedStatement getCustomerID = DBQuery.getPreparedStatement();
        
        String customerName = customerList.getValue();
        
        //Keymap
        getCustomerID.setString(1, customerName);
        
        getCustomerID.execute();
        ResultSet getCustomerIDResult = getCustomerID.getResultSet();
        while(getCustomerIDResult.next() == true){
        customerID = getCustomerIDResult.getInt("customerId");
        }
    }catch(SQLException f){ 
        System.out.println("SQLException: add appointment, getcustomerId");
        return;        
    }      
        
        //Find userID & userName, store in userID and userName
        int userID = 0;
        String userName = null;
    try{
        String selectStatement2 = "SELECT userId, userName FROM user WHERE active=1 AND userId > 0;";
        DBQuery.setPreprearedStatement(conn, selectStatement2); 
        PreparedStatement getUserID = DBQuery.getPreparedStatement();
        
        getUserID.execute();
        ResultSet getUserIDResult = getUserID.getResultSet();
        while(getUserIDResult.next() == true){
        userID = getUserIDResult.getInt("userId");
        userName = getUserIDResult.getString("userName");
        }
    }catch(SQLException f){ 
        System.out.println("SQLException: add appointment, getuserIduserName");
        return;        
    }
    
    //Insert Appointment
        String insertStatement = "INSERT INTO appointment(customerId, userId, title, description, location, contact, type"
                + ", url, start, end, createDate, createdBy, lastUpdateBy) VALUES (?, ?, ?, ?, 'unused', 'unused', ?, 'unused', ?, ?, NOW(), ?, ?)";   
    try{    
        DBQuery.setPreprearedStatement(conn, insertStatement); //Create preparedStatment object to insert country       
        PreparedStatement ps = DBQuery.getPreparedStatement();
        
        //customerId already exists
        //userId already exits
        String title = titleField.getText();
        String description = descriptionField.getText();
        String type = typeList.getValue();
        //start already exists
        //end already exists
        //userName already exists
        //userName already exists
        
        //Keymap
        ps.setInt(1, customerID);
        ps.setInt(2, userID);
        ps.setString(3, title);
        ps.setString(4, description);
        ps.setString(5, type);
        ps.setString(6, startTimeSQL);
        ps.setString(7, endTimeSQL);
        ps.setString(8, userName);
        ps.setString(9, userName);
        
        
        ps.execute(); //Execute
        
    }catch(SQLException e){
        System.out.println("SQLException: add appointment, insert appointment");
        return;
    }
    
        //Close window
        ((Stage)(((Button)event.getSource()).getScene().getWindow())).close();
        System.out.println("Appointment Added!");
    
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
