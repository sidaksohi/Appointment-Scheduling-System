/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package View_Controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import utils.DBConnection;

//Imports
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
import java.util.ArrayList;

/**
 * FXML Controller class
 *
 * @author Sidak
 */
public class AddCustomerScreenController implements Initializable {

    @FXML
    private TextField nameFIeld;
    @FXML
    private TextField addressField;
    @FXML
    private TextField cityField;
    @FXML
    private TextField countryField;
    @FXML
    private TextField phoneField;
    
    Connection conn;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        conn = DBConnection.getConnection();
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
    private void cancelCustomer(MouseEvent event) throws IOException{
    cancelConfirmation("","Cancel adding a customer?", event);
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
    private void saveCustomer(MouseEvent event) throws IOException{
    //Add error if any of the fields are blank
    if(nameFIeld.getText().isEmpty() == true || addressField.getText().isEmpty() == true || cityField.getText().isEmpty() == true || countryField.getText().isEmpty() == true || 
            phoneField.getText().isEmpty() == true){
        alert("Error","All fields must be filled out");
        return;
    }

    //Add error if customer name already exists
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
    }catch(SQLException e){ System.out.println("SQLException: addcustomer selectallcustomers");}
    
    for(int i=0; i<allCustomers.size(); i++){
        if(nameFIeld.getText().equalsIgnoreCase(allCustomers.get(i)) == true){
            alert("Error", "Customer name already exists, please make name unique");
            return;         
        }
    }    
        
        
    //initialize variables    
    String user = null;
    int countryID = 0;
    int cityID = 0;
    int addressID = 0;
    
    //Get Username of Active User, store in user
    try{
        String selectStatement = "SELECT * FROM user WHERE active=1 AND userId > 0;";
        DBQuery.setPreprearedStatement(conn, selectStatement); 
        PreparedStatement getUser = DBQuery.getPreparedStatement();
        getUser.execute();
        ResultSet getUserResult = getUser.getResultSet();
        while(getUserResult.next() == true){
        user = getUserResult.getString("userName");
        }
    }catch(SQLException e){ 
        System.out.println("SQLException: add customer, getuser");
        return;        
    }
    
    
    //Insert Country    
        String insertStatement = "INSERT INTO country(country, createDate, createdBy, lastUpdateBy) VALUES (?, NOW(), ?, ?)";   
    try{    
        DBQuery.setPreprearedStatement(conn, insertStatement); //Create preparedStatment object to insert country       
        PreparedStatement ps = DBQuery.getPreparedStatement();
        String country = countryField.getText();
        String createdBy = user;
        String lastUpdateBy = user;
        
        //Keymap
        ps.setString(1, country);
        ps.setString(2, createdBy);
        ps.setString(3, lastUpdateBy);
        
        
        ps.execute(); //Execute
        
    }catch(SQLException e){
        System.out.println("SQLException: add customer, insert country");
        return;
    }
    
    //Get CountryID, store in countryID
    try{
        String selectStatement = "SELECT * FROM country WHERE country=? AND countryId > 0;";
        DBQuery.setPreprearedStatement(conn, selectStatement); 
        PreparedStatement getCountryID = DBQuery.getPreparedStatement();
        
        String country = countryField.getText();
        
        //Keymap
        getCountryID.setString(1, country);
        
        getCountryID.execute();
        ResultSet getCountryIDResult = getCountryID.getResultSet();
        while(getCountryIDResult.next() == true){
        countryID = getCountryIDResult.getInt("countryId");
        }
    }catch(SQLException e){ 
        System.out.println("SQLException: add customer, getcountryId");
        return;        
    }
    
    //Insert City    
        String insertStatement2 = "INSERT INTO city(city, countryId, createDate, createdBy, lastUpdateBy) VALUES (?, ?, NOW(), ?, ?)";   
    try{    
        DBQuery.setPreprearedStatement(conn, insertStatement2); //Create preparedStatment object to insert country       
        PreparedStatement ps = DBQuery.getPreparedStatement();
        
        String city = cityField.getText();
        //int CountryID already set
        String createdBy = user;
        String lastUpdateBy = user;
        
        //Keymap
        ps.setString(1, city);
        ps.setInt(2, countryID);
        ps.setString(3, createdBy);
        ps.setString(4, lastUpdateBy);
        
        
        ps.execute(); //Execute
        
    }catch(SQLException e){
        System.out.println("SQLException: add customer, insert city");
        return;
    }
    
    //Get cityId, store in cityID
    try{
        String selectStatement = "SELECT * FROM city WHERE city=? AND cityId > 0;";
        DBQuery.setPreprearedStatement(conn, selectStatement); 
        PreparedStatement getCityID = DBQuery.getPreparedStatement();
        
        String city = cityField.getText();
        
        //Keymap
        getCityID.setString(1, city);
        
        getCityID.execute();
        ResultSet getCityIDResult = getCityID.getResultSet();
        while(getCityIDResult.next() == true){
        cityID = getCityIDResult.getInt("cityId");
        }
    }catch(SQLException e){ 
        System.out.println("SQLException: add customer, getcityId");
        return;        
    }
    
    //Insert Address    
        String insertStatement3 = "INSERT INTO address(address, address2, cityId, postalCode, phone, createDate, createdBy, lastUpdateBy)"
                                + "VALUES (?, 'unused', ?, 'unused', ?, NOW(), ?, ?)";   
    try{    
        DBQuery.setPreprearedStatement(conn, insertStatement3); //Create preparedStatment object to insert country       
        PreparedStatement ps = DBQuery.getPreparedStatement();
        
        String address = addressField.getText();
        //int CityID already set
        String phone = phoneField.getText();
        String createdBy = user;
        String lastUpdateBy = user;
        
        //Keymap
        ps.setString(1, address);
        ps.setInt(2, cityID);
        ps.setString(3, phone);
        ps.setString(4, createdBy);
        ps.setString(5, lastUpdateBy);
        
        
        ps.execute(); //Execute
        
    }catch(SQLException e){
        System.out.println("SQLException: add customer, insert address");
        return;
    }
    
    //Get addressID, store in addressID
    try{
        String selectStatement = "SELECT * FROM address WHERE phone=? AND addressId > 0;";
        DBQuery.setPreprearedStatement(conn, selectStatement); 
        PreparedStatement getAddressID = DBQuery.getPreparedStatement();
        
        String phone = phoneField.getText();
        
        //Keymap
        getAddressID.setString(1, phone);
        
        getAddressID.execute();
        ResultSet getAddressIDResult = getAddressID.getResultSet();
        while(getAddressIDResult.next() == true){
        addressID = getAddressIDResult.getInt("addressId");
        }
    }catch(SQLException e){ 
        System.out.println("SQLException: add customer, getaddressId");
        return;        
    }
    
    //Insert Customer  
        String insertStatement4 = "INSERT INTO customer(customerName, addressId, active, createDate, createdBy, lastUpdateBy)"
                                + "VALUES (?, ?, 0, NOW(), ?, ?)";   
    try{    
        DBQuery.setPreprearedStatement(conn, insertStatement4); //Create preparedStatment object to insert country       
        PreparedStatement ps = DBQuery.getPreparedStatement();
        
        String customerName = nameFIeld.getText();
        //int addressID already set
        //int active set to 0 by default
        String createdBy = user;
        String lastUpdateBy = user;
        
        //Keymap
        ps.setString(1, customerName);
        ps.setInt(2, addressID);
        ps.setString(3, createdBy);
        ps.setString(4, lastUpdateBy);
        
        
        ps.execute(); //Execute
        
    }catch(SQLException e){
        System.out.println("SQLException: add customer, insert customer");
    }
    
    //Close window
    ((Stage)(((Button)event.getSource()).getScene().getWindow())).close();
    System.out.println("Customer added!");
    
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
