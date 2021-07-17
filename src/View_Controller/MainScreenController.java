/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package View_Controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

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
import Model.*;
import java.sql.Timestamp;

//Time
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.TimeZone;
import javafx.scene.control.RadioButton;
import javafx.scene.control.cell.PropertyValueFactory;

//Logger
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;


/**
 * FXML Controller class
 *
 * @author Sidak
 */
public class MainScreenController implements Initializable {

    @FXML
    private ToggleGroup aptViewButtons;
    @FXML
    private TableView<Appointment> aptTable;
    @FXML
    private TableColumn<Appointment, Integer> aptidcol;
    @FXML
    private TableColumn<Appointment, String> customercol;
    @FXML
    private TableColumn<Appointment, String> titlecol;
    @FXML
    private TableColumn<Appointment, String> descriptioncol;
    @FXML
    private TableColumn<Appointment, String> typecol;
    @FXML
    private TableColumn<Appointment, String> datecol;
    @FXML
    private TableColumn<Appointment, String> starttimecol;
    @FXML
    private TableColumn<Appointment, String> endtimecol;
    @FXML
    private Label timezoneLabel;
    
    
    @FXML
    private RadioButton byWeek;
    @FXML
    private RadioButton byMonth;
    @FXML
    private RadioButton viewAllButon;
    @FXML
    private RadioButton byDay;
    
    Connection conn;
    String userName;
    Schedule schedule;
    int userID;
    
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        schedule = new Schedule();
        conn = DBConnection.getConnection();
        
        //Set timezone label
        String timezone = String.valueOf(TimeZone.getDefault().getDisplayName());
        timezoneLabel.setText("Timezone: " + timezone);
        viewAllButon.setSelected(true);
        
        //Generate Appointments Table
        generateAptTable();
        aptidcol.setCellValueFactory(new PropertyValueFactory<>("appointmentID"));
        customercol.setCellValueFactory(new PropertyValueFactory<>("customer"));
        titlecol.setCellValueFactory(new PropertyValueFactory<>("title"));
        descriptioncol.setCellValueFactory(new PropertyValueFactory<>("description"));
        typecol.setCellValueFactory(new PropertyValueFactory<>("type"));
        datecol.setCellValueFactory(new PropertyValueFactory<>("date"));
        starttimecol.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        endtimecol.setCellValueFactory(new PropertyValueFactory<>("endTime"));
        
        aptidcol.setResizable(false);
        customercol.setResizable(false);
        titlecol.setResizable(false);
        descriptioncol.setResizable(false);
        typecol.setResizable(false);
        datecol.setResizable(false);
        starttimecol.setResizable(false);
        endtimecol.setResizable(false);
        
        //Get active user, store in userId, userName
    try{
        String selectStatement = "SELECT userId, userName FROM user WHERE active=1 AND userId > 0;";
        DBQuery.setPreprearedStatement(conn, selectStatement); 
        PreparedStatement getUserID = DBQuery.getPreparedStatement();
        
        getUserID.execute();
        ResultSet getUserIDResult = getUserID.getResultSet();
        while(getUserIDResult.next() == true){
        userID = getUserIDResult.getInt("userId");
        userName = getUserIDResult.getString("userName");
        }
    }catch(SQLException e){ 
        System.out.println("SQLException: mainscreen, getuserid");
        return;        
    }
    
        //Call logger to store username and timestamp after login
        logLogin();
        
        //Check if there is an appointment within 15 minutes, and if there is, display an alert
        System.out.println("Appointment within 15 minutes: "+schedule.getWithinQHourAlert());
        
        //If there is, send an alert to the user who scheduled the appointment
        if(schedule.getWithinQHourAlert()==true){
            System.out.println("Alert userID: "+schedule.getQHourAlertUser());
            System.out.println("Current userID: "+userID);
            System.out.println("Alert text: "+schedule.getQHourAlertText());
        }
        
        if(schedule.getWithinQHourAlert() == true){
            if(userID == schedule.getQHourAlertUser()){
                System.out.println("Alert displayed!");
                alert("!",schedule.getQHourAlertText());
            }
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
        //Lambda expression used here to close alert window after user has read it and clicks "close"
        closeButton.setOnAction(e -> window.close());
        
        VBox layout = new VBox(10);
        layout.getChildren().addAll(label, closeButton);
        layout.setAlignment(Pos.CENTER);
        
        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.show();
    }
    
    private void generateAptTable(){
      //Select all appointments
        String selectStatement = "SELECT appointmentId, a.userId, customerName, userName, title, description, type, start, end FROM appointment a, customer b, user c WHERE a.customerId=b.customerId AND a.userId=c.userId";
    try{    
        DBQuery.setPreprearedStatement(conn, selectStatement); //Create preparedStatment object
        PreparedStatement ps = DBQuery.getPreparedStatement();
        ps.execute(); //Execute statement
        
        ResultSet rs = ps.getResultSet();
        
        while(rs.next() == true){
            int appointmentID = rs.getInt("appointmentId");
            int uID = rs.getInt("userId");
            String customer = rs.getString("customerName");
            String user = rs.getString("userName");
            String title = rs.getString("title");
            String description = rs.getString("description");
            String type = rs.getString("type");
            String date = String.valueOf(rs.getDate("start").toLocalDate());
            String start = String.valueOf(rs.getTime("start").toLocalTime());
            String end = String.valueOf(rs.getTime("end").toLocalTime());
            
            schedule.addAppointment(new Appointment(appointmentID, uID, customer, user, title, description, type, date, start, end, schedule));
            
            
        }
    }catch(SQLException e){
        System.out.println("SQLException: mainscreen selectappointments");
        return;
    }catch(Exception e){
        System.out.println("Exception " +e+": mainscreen addappointments");
        return;
    }
        aptTable.setItems(schedule.getAllAppointments());
        aptTable.refresh();
    
    
    }//end
    
    private void logLogin(){
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        // Convert timestamp to instant
        Instant instant = timestamp.toInstant();
        String timestampUTC = String.valueOf(instant);
        
        String logInput = "USER: " + userName + " OPENED MAIN SCREEN AT " + timestampUTC;
         
        //Write to file
        FileWriter fw = null;
        BufferedWriter bw = null;
        PrintWriter out = null;
        try {
        fw = new FileWriter("aptUserTrackerLog.txt", true);
        bw = new BufferedWriter(fw);
        out = new PrintWriter(bw);
        out.println(logInput);
        out.close();

        System.out.println("User logged at C:/Users/Sidak/Desktop/aptUserTrackerLog.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void logOut(MouseEvent event) throws IOException{
        logOutConfirmation("","Log out?", event);
    }
    
    private void logOutConfirmation(String title, String message, MouseEvent event) throws IOException{
        
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(title);
        window.setMinWidth(350);
        
        Label label = new Label();
        label.setText(message);
        Button closeButton = new Button("OK");
        Button cancelButton = new Button("Cancel");
        
        //Lambda expression used here to call the logOutOk function when the user says "Ok" to logging out
        //It passes the current window and mouseevent so that logOutOk can use them to close the current window
        closeButton.setOnAction(e -> logOutOk(window, event));
        
        //Another Lambda expression used here to close the alert pop up if user decides not to log out
        //It does this but calling window.close(), where without a lambda it would use more code
        //I use these types of lambda expressions all throughout my program, all for calling certain methods in a more efficient way
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
    
    private void logOutOk(Stage window, MouseEvent event){
        window.close();
        ((Stage)(((Button)event.getSource()).getScene().getWindow())).close();
        try{
            openLogin();
        }catch(Exception e){
            
        }
    }
    
    private void openLogin() throws IOException{
        Parent root = FXMLLoader.load(getClass().getResource("/View_Controller/LoginScreen.fxml"));
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle("Login");
        stage.sizeToScene();
        stage.show();
    try{    
        //Set all users to inactive on close
        String insertStatement = "UPDATE user SET active = 0 WHERE userId > 0;";
        DBQuery.setPreprearedStatement(conn, insertStatement); //Create preparedStatment object
        PreparedStatement ps = DBQuery.getPreparedStatement();
        ps.execute(); //Execute
    }catch(SQLException e){
            System.out.println("SQLException: log out");
            } 
    }


    @FXML
    private void viewAll(MouseEvent event) {
        aptTable.setItems(schedule.getAllAppointments());
        aptTable.refresh();
    }


    @FXML
    private void addCustomer(MouseEvent event) throws IOException{
        Parent root = FXMLLoader.load(getClass().getResource("/View_Controller/AddCustomerScreen.fxml"));
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle("Add Customer");
        stage.sizeToScene();
        stage.show();
        
        ((Stage)(((Button)event.getSource()).getScene().getWindow())).close();
    }

    @FXML
    private void modifyCustomer(MouseEvent event) throws IOException{
        Parent root = FXMLLoader.load(getClass().getResource("/View_Controller/ModifyCustomerScreen.fxml"));
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle("Modify Customer");
        stage.sizeToScene();
        stage.show();
        
        ((Stage)(((Button)event.getSource()).getScene().getWindow())).close();
    }

    @FXML
    private void addAppointment(MouseEvent event) throws IOException{
        Parent root = FXMLLoader.load(getClass().getResource("/View_Controller/AddAppointmentScreen.fxml"));
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle("Add Appointment");
        stage.sizeToScene();
        stage.show();
        
        ((Stage)(((Button)event.getSource()).getScene().getWindow())).close();
    }

    @FXML
    private void modifyAppointment(MouseEvent event) throws IOException{
        //Add error if an appointment is not selected
        if(aptTable.getSelectionModel().getSelectedItem() == null){
            alert("Error","An appointment must be selected to modify!");
            return;
        }
        
        //Send selected appointment fields through constructor and open modify screen
        int appointmentID;
        String customerName;
        String title;
        String description;
        String type;
        String startTime;
        String endTime;
        String date;
        
        appointmentID = aptTable.getSelectionModel().getSelectedItem().getAppointmentID();
        customerName = aptTable.getSelectionModel().getSelectedItem().getCustomer();
        title = aptTable.getSelectionModel().getSelectedItem().getTitle();
        description = aptTable.getSelectionModel().getSelectedItem().getDescription();
        type = aptTable.getSelectionModel().getSelectedItem().getType();
        startTime = aptTable.getSelectionModel().getSelectedItem().getStartTime();
        endTime = aptTable.getSelectionModel().getSelectedItem().getEndTime();
        date = aptTable.getSelectionModel().getSelectedItem().getDate();
        
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/View_Controller/ModifyAppointmentScreen.fxml"));
        
        View_Controller.ModifyAppointmentScreenController controller = new View_Controller.ModifyAppointmentScreenController(appointmentID, customerName, title, description, type, startTime, endTime, date);
        loader.setController(controller);
        
        Parent root = loader.load();
        
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle("Modify Appointment");
        stage.sizeToScene();
        stage.show();
        
        //Close main screen
        ((Stage)(((Button)event.getSource()).getScene().getWindow())).close();
    }

    @FXML
    private void reports(MouseEvent event) throws IOException {
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/View_Controller/ReportsScreen.fxml"));
        
        View_Controller.ReportsScreenController controller = new View_Controller.ReportsScreenController(schedule);
        loader.setController(controller);
        
        Parent root = loader.load();
        
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle("Reports");
        stage.sizeToScene();
        stage.show();
        
        //Close main screen
        ((Stage)(((Button)event.getSource()).getScene().getWindow())).close();
    }

    @FXML
    private void viewToday(MouseEvent event) {
        if(schedule.getAllAppointmentsByDay().isEmpty() == true){
            alert("","No appointments today!");
            return;
        }
        aptTable.setItems(schedule.getAllAppointmentsByDay());
        aptTable.refresh();
    }

    @FXML
    private void viewWeek(MouseEvent event) {
        if(schedule.getAllAppointmentsByWeek().isEmpty() == true){
            alert("","No appointments this week!");
            return;
        }
        aptTable.setItems(schedule.getAllAppointmentsByWeek());
        aptTable.refresh();
    }

    @FXML
    private void viewMonth(MouseEvent event) {
        if(schedule.getAllAppointmentsByWeek().isEmpty() == true){
            alert("","No appointments this month!");
            return;
        }
        aptTable.setItems(schedule.getAllAppointmentsByMonth());
        aptTable.refresh();
    }
    
}
