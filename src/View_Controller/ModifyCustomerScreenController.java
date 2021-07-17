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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.util.ArrayList;
import javafx.scene.control.TextField;

/**
 * FXML Controller class
 *
 * @author Sidak
 */



public class ModifyCustomerScreenController implements Initializable {

    @FXML
    private ChoiceBox<String> customerList;
    
    @FXML
    private Button selectCustomerButton;
    @FXML
    private Button deleteCustomerButton;
    @FXML
    private TextField nameField;
    @FXML
    private TextField addressField;
    @FXML
    private TextField cityField;
    @FXML
    private TextField countryField;
    @FXML
    private TextField phoneField;
    

    /**
     * Initializes the controller class.
     */
    
    //initialize variables for page
    Connection conn = null;
    ArrayList<String> allCustomers;
    
    //initialize variables for customer
    int addressID = 0;
    int cityID = 0;
    int countryID = 0;
    int customerID = 0;
    
 
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        conn = DBConnection.getConnection();
        allCustomers = new ArrayList<>();
        String firstCustomer;
        
        //Disable buttons until customer is selected
        deleteCustomerButton.setDisable(true);
        nameField.setDisable(true);
        addressField.setDisable(true);
        cityField.setDisable(true);
        countryField.setDisable(true);
        phoneField.setDisable(true);
        
        
        
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
        
    }//end
    
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
    private void selectCustomer(MouseEvent event) {
        //Enable buttons since customer is selected and we have ID's
        deleteCustomerButton.setDisable(false);
        selectCustomerButton.setDisable(true);
        nameField.setDisable(false);
        addressField.setDisable(false);
        cityField.setDisable(false);
        countryField.setDisable(false);
        phoneField.setDisable(false);
    
        
    
        //Fill name and get addressID
    try{
        String selectStatement = "SELECT * FROM customer WHERE customerName = ?";
        
        DBQuery.setPreprearedStatement(conn, selectStatement); //Create preparedStatment object
        PreparedStatement ps = DBQuery.getPreparedStatement();
        
        String customerName = customerList.getValue();
        ps.setString(1, customerName);
        
        ps.execute(); //Execute statement
        
        ResultSet rs = ps.getResultSet();
        
        
        while(rs.next() == true){
            addressID = rs.getInt("addressId");
            customerID = rs.getInt("customerId");
        }
        nameField.setText(customerName);
    }catch(SQLException e){ 
        System.out.println("SQLException: modifycustomer selectcustomerName");
        return;
    }
    
        //Fill phone/address and get city id
    try{
        String selectStatement = "SELECT * FROM address WHERE addressId = ?";
        
        DBQuery.setPreprearedStatement(conn, selectStatement); //Create preparedStatment object
        PreparedStatement ps = DBQuery.getPreparedStatement();

        ps.setInt(1, addressID);
        
        ps.execute(); //Execute statement
        
        ResultSet rs = ps.getResultSet();
        
        
        while(rs.next() == true){
            String address = rs.getString("address");
            addressField.setText(address);
            String phone = rs.getString("phone");
            phoneField.setText(phone);
            cityID = rs.getInt("addressId");
        }
    }catch(SQLException e){ 
        System.out.println("SQLException: modifycustomer selectaddress");
        return;
    }
    
        //Fill city and get countryID
    try{
        String selectStatement = "SELECT * FROM city WHERE cityId = ?";
        
        DBQuery.setPreprearedStatement(conn, selectStatement); //Create preparedStatment object
        PreparedStatement ps = DBQuery.getPreparedStatement();

        ps.setInt(1, cityID);
        
        ps.execute(); //Execute statement
        
        ResultSet rs = ps.getResultSet();
        
        
        while(rs.next() == true){
            String city = rs.getString("city");
            cityField.setText(city);
            countryID = rs.getInt("countryId");
        }
    }catch(SQLException e){ 
        System.out.println("SQLException: modifycustomer selectcity");
        return;
    }
    
        //Fill country
    try{
        String selectStatement = "SELECT * FROM country WHERE countryId = ?";
        
        DBQuery.setPreprearedStatement(conn, selectStatement); //Create preparedStatment object
        PreparedStatement ps = DBQuery.getPreparedStatement();

        ps.setInt(1, countryID);
        
        ps.execute(); //Execute statement
        
        ResultSet rs = ps.getResultSet();
        
        
        while(rs.next() == true){
            String country = rs.getString("country");
            countryField.setText(country);
        }
    }catch(SQLException e){ 
        System.out.println("SQLException: modifycustomer selectcountry");
        return;
    }
        
    
    }//end

    @FXML
    private void deleteCustomer(MouseEvent event) throws IOException{
        
        //Delete from customer first
        String deleteStatement = "DELETE FROM customer WHERE customerId=?";
    try{
        DBQuery.setPreprearedStatement(conn, deleteStatement); //Create preparedStatment object
        
        PreparedStatement ps = DBQuery.getPreparedStatement();
        
        //Keymap
        ps.setInt(1, customerID);
        
        ps.execute(); //Execute
    }catch(SQLException e){
        System.out.println("SQLException: modifycustomer deletecustomer");
        return;
    }    
    
        //Delete from address
        String deleteStatement2 = "DELETE FROM address WHERE addressId=?";
    try{
        DBQuery.setPreprearedStatement(conn, deleteStatement2); //Create preparedStatment object
        
        PreparedStatement ps = DBQuery.getPreparedStatement();
        
        //Keymap
        ps.setInt(1, addressID);
        
        ps.execute(); //Execute
    }catch(SQLException e){
        System.out.println("SQLException: modifycustomer deleteaddress");
        return;
    }
    
        //Delete from city
        String deleteStatement3 = "DELETE FROM city WHERE cityId=?";
    try{
        DBQuery.setPreprearedStatement(conn, deleteStatement3); //Create preparedStatment object
        
        PreparedStatement ps = DBQuery.getPreparedStatement();
        
        //Keymap
        ps.setInt(1, cityID);
        
        ps.execute(); //Execute
    }catch(SQLException e){
        System.out.println("SQLException: modifycustomer deletecity");
        return;
    }    
    
        //Delete from country, done
        String deleteStatement4 = "DELETE FROM country WHERE countryId=?";
    try{
        DBQuery.setPreprearedStatement(conn, deleteStatement4); //Create preparedStatment object
        
        PreparedStatement ps = DBQuery.getPreparedStatement();
        
        //Keymap
        ps.setInt(1, countryID);
        
        ps.execute(); //Execute
    }catch(SQLException e){
        System.out.println("SQLException: modifycustomer deletecountry");
        return;
    }  
        //Print message and call class to close window and open main
        System.out.println("Customer deleted!");
        deleteDone(event);
    
    }//end
    
    private void deleteDone(MouseEvent event) throws IOException{
       ((Stage)(((Button)event.getSource()).getScene().getWindow())).close();
        openMain();
    }
    

    @FXML
    private void cancel(MouseEvent event) throws IOException{
        cancelConfirmation("","Cancel modifying a customer?", event);
    }
    
    private void cancelConfirmation(String title, String message, MouseEvent event) throws IOException{
        
        Stage window = new Stage();
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
       //Add error if a customer is not selected
    if(deleteCustomerButton.isDisabled() == true){
        alert("Error", "No customer selected!");
        return;
    }
       //Add error if any of the fields are blank
    if(nameField.getText().isEmpty() == true || addressField.getText().isEmpty() == true || cityField.getText().isEmpty() == true || countryField.getText().isEmpty() == true || 
            phoneField.getText().isEmpty() == true){
        alert("Error","All fields must be filled out");
        return;
    }

    //Add error if customer name already exists, other than the current one
    ArrayList<String> allCustomers = new ArrayList<>();
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
    
    int x = 0;
    for(int i=0; i<allCustomers.size(); i++){
        if(nameField.getText().equalsIgnoreCase(allCustomers.get(i)) == true){
            x++;
            if(x == 2){
                alert("Error", "Customer name already exists, please make name unique");
                return;
            }
        }
    }
    //Find userName, store in userName
        String userName = null;
    try{
        String selectStatement = "SELECT userName FROM user WHERE active=1 AND userId > 0;";
        DBQuery.setPreprearedStatement(conn, selectStatement); 
        PreparedStatement getUserID = DBQuery.getPreparedStatement();
        
        getUserID.execute();
        ResultSet getUserIDResult = getUserID.getResultSet();
        while(getUserIDResult.next() == true){
        userName = getUserIDResult.getString("userName");
        }
    }catch(SQLException e){ 
        System.out.println("SQLException: modifycustomer, getuserIduserName");
        return;        
    }
    
    //Update customer using customerID
    try{
        String updateStatement = "UPDATE customer SET customerName=?, lastUpdateBy=? WHERE customerId=?";
        
        DBQuery.setPreprearedStatement(conn, updateStatement); //Create preparedStatment object
        
        PreparedStatement ps = DBQuery.getPreparedStatement();
        
        String customerName = nameField.getText();
        
        //Keymap
        ps.setString(1, customerName);
        ps.setString(2, userName);
        ps.setInt(3, customerID);
        
        ps.execute(); //Execute
    }catch(SQLException e){ 
        System.out.println("SQLException: modifycustomer updatecustomer");
        return;
    }
    
    //Update address/phone using addressID
    try{
        String updateStatement = "UPDATE address SET address=?, phone=?, lastUpdateBy=? WHERE addressId=?";
        
        DBQuery.setPreprearedStatement(conn, updateStatement); //Create preparedStatment object
        
        PreparedStatement ps = DBQuery.getPreparedStatement();
        
        String address = addressField.getText();
        String phone = phoneField.getText();
        
        //Keymap
        ps.setString(1, address);
        ps.setString(2, phone);
        ps.setString(3, userName);
        ps.setInt(4, addressID);
        
        
        ps.execute(); //Execute
    }catch(SQLException e){ 
        System.out.println("SQLException: modifycustomer updateaddress");
        return;
    }
    //Update city using cityID
    try{
        String updateStatement = "UPDATE city SET city=?, lastUpdateBy=? WHERE cityId=?";
        
        DBQuery.setPreprearedStatement(conn, updateStatement); //Create preparedStatment object
        
        PreparedStatement ps = DBQuery.getPreparedStatement();
        
        String city = cityField.getText();
        
        //Keymap
        ps.setString(1, city);
        ps.setString(2, userName);
        ps.setInt(3, cityID);
        
        
        ps.execute(); //Execute
    }catch(SQLException e){ 
        System.out.println("SQLException: modifycustomer updatecity");
        return;
    }
    //Update country using countryID
    try{
        String updateStatement = "UPDATE country SET country=?, lastUpdateBy=? WHERE countryId=?";
        
        DBQuery.setPreprearedStatement(conn, updateStatement); //Create preparedStatment object
        
        PreparedStatement ps = DBQuery.getPreparedStatement();
        
        String country = countryField.getText();
        
        //Keymap
        ps.setString(1, country);
        ps.setString(2, userName);
        ps.setInt(3, countryID);
        
        
        ps.execute(); //Execute
    }catch(SQLException e){ 
        System.out.println("SQLException: modifycustomer updatecountry");
        return;
    }
        //Close window
        ((Stage)(((Button)event.getSource()).getScene().getWindow())).close();
        System.out.println("Customer updated!");
    
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
    
}
