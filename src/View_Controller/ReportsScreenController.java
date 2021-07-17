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

//Time
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.TimeZone;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * FXML Controller class
 *
 * @author Sidak
 */
public class ReportsScreenController implements Initializable {

    @FXML
    private ToggleGroup reportButtons;
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
    private ChoiceBox<String> customerSelectPicker;
    @FXML
    private ChoiceBox<String> userSelectPicker;
    @FXML
    private ChoiceBox<String> monthPicker;
    @FXML
    private Label timezone;

    /**
     * Initializes the controller class.
     */
    Schedule schedule;
    Connection conn;
    ArrayList<String> allCustomers;
    ArrayList<String> allUsers;
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        conn = DBConnection.getConnection();
        allCustomers = new ArrayList<>();
        allUsers = new ArrayList<>();
    
        //Call select statement to fill customer and user arraylist
        getCustomerUserInfo();
        
        //Set timezone label
        String timezoneText = String.valueOf(TimeZone.getDefault().getDisplayName());
        timezone.setText("Timezone: " + timezoneText);
    }
    
    ReportsScreenController(Schedule schedule){
        this.schedule = schedule;
    }
    
    private void getCustomerUserInfo(){
        //Get all customer names and usernames, add them to their respective arrayList
    try{
        String selectStatement = "SELECT * FROM customer, user";
        DBQuery.setPreprearedStatement(conn, selectStatement); //Create preparedStatment object
        PreparedStatement ps = DBQuery.getPreparedStatement();
        ps.execute(); //Execute
        
        ResultSet rs = ps.getResultSet();
        
        while(rs.next() == true){
            String userName = rs.getString("userName");
            String customerName = rs.getString("customerName");
            
            if(allUsers.contains(userName) == false){
                allUsers.add(userName);
            }
            if(allCustomers.contains(customerName) == false){
                allCustomers.add(customerName);
            }
        }
        
    }catch(SQLException e){
        System.out.println("SQLException: reports addcustomersusers");
        return;
    }    
        
    }//end

    @FXML
    private void back(MouseEvent event) {
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
    private void customerSchedule(MouseEvent event) {
        userSelectPicker.setDisable(true);
        customerSelectPicker.setDisable(false);
        monthPicker.setDisable(true);
        aptTable.setItems(null);
        
        customerSelectPicker.setItems(FXCollections.observableList(allCustomers));
    }

    @FXML
    private void consultantSchedule(MouseEvent event) {
        userSelectPicker.setDisable(false);
        customerSelectPicker.setDisable(true);
        monthPicker.setDisable(true);
        aptTable.setItems(null);
        
        userSelectPicker.setItems(FXCollections.observableList(allUsers));
    }
    
    @FXML
    private void monthSchedule(MouseEvent event) {
        userSelectPicker.setDisable(true);
        customerSelectPicker.setDisable(true);
        monthPicker.setDisable(false);
        aptTable.setItems(null);
        
        
        monthPicker.setItems(FXCollections.observableArrayList("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"));
        
    }


    @FXML
    private void customerSelect(ActionEvent event) {
        //Create temporary arrayList, which resets everytime a new user is selected
        ArrayList<Appointment> customerReport = new ArrayList<>();
        customerReport.clear();
        
        //If the username matches an appointment with the same username, it will be added to the arraylist
        for(int i = 0; i < schedule.getAllAppointments().size() ; i++){
           if((schedule.getAllAppointments()).get(i).getCustomer().equalsIgnoreCase(customerSelectPicker.getValue())==true){
               customerReport.add((schedule.getAllAppointments()).get(i));
            }
        }
        
        //Generate Consultant Appointments Table 
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
        
        aptTable.setItems(FXCollections.observableList(customerReport));
        aptTable.refresh();
    }//end

    @FXML
    private void userSelect(ActionEvent event) {
        //Create temporary arrayList, which resets everytime a new user is selected
        ArrayList<Appointment> userReport = new ArrayList<>();
        userReport.clear();
        
        //If the username matches an appointment with the same username, it will be added to the arraylist
        for(int i = 0; i < schedule.getAllAppointments().size() ; i++){
           if((schedule.getAllAppointments()).get(i).getUser().equalsIgnoreCase(userSelectPicker.getValue())==true){
               userReport.add((schedule.getAllAppointments()).get(i));
            }
        }
        
        //Generate Consultant Appointments Table 
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
        
        aptTable.setItems(FXCollections.observableList(userReport));
        aptTable.refresh();
    }//end

    

    @FXML
    private void monthPickerSelect(ActionEvent event) {
        //Convert selected month into string number value i.e.: July = 07
        int selectedIndex = monthPicker.selectionModelProperty().getValue().getSelectedIndex() + 1;
        String index = null;
        if(selectedIndex < 10){
            index = "0"+String.valueOf(selectedIndex);
        }
        if(selectedIndex >= 10 && selectedIndex < 13){
            index = String.valueOf(selectedIndex);
        }
        
        //Add appointments to temporary arraylist if the months match
        ArrayList<Appointment> monthSearch = new ArrayList<>();
        monthSearch.clear();
        
        for(int i = 0; i < schedule.getAllAppointments().size(); i++){
            String date = (schedule.getAllAppointments()).get(i).getDate();
            String enumDate = String.valueOf(date.charAt(0)) + String.valueOf(date.charAt(1));
                if(enumDate.equalsIgnoreCase(index)==true){
                    monthSearch.add((schedule.getAllAppointments()).get(i));
                }
        }
        
        //Find the # of appointment types this month
        ArrayList<String> monthSearchTypes = new ArrayList<>();
        monthSearchTypes.clear();
        
        for(int i = 0; i < monthSearch.size(); i++){
            if( monthSearchTypes.contains((monthSearch.get(i)).getType())==false)
                monthSearchTypes.add((monthSearch.get(i)).getType());
        }
        
        //Add error if there are no appointments that month
        if(monthSearch.isEmpty()==true){
            alert("Error","No appointments scheduled for "+monthPicker.getValue());
            return;
        }
        
        //If there are appointments in that month, give an alert showing how many different apt types there are that month
        alert("# of Types",monthPicker.getValue()+" has "+(monthSearchTypes.size())+" different type(s) of appointments!");
        
        
        //Generate Consultant Appointments Table 
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
        
        aptTable.setItems(FXCollections.observableList(monthSearch));
        aptTable.refresh();
    }//end
    
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
    
}
