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
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

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

//Time
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Locale;



/**
 * FXML Controller class
 *
 * @author Sidak
 */
public class LoginScreenController implements Initializable {

    @FXML
    private Label title;
    @FXML
    private Label username;
    @FXML
    private TextField usernameField;
    @FXML
    private Label password;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label language;
    @FXML
    private ChoiceBox<String> languagePicker;
    @FXML
    private Button loginButton;
    @FXML
    private Button exitButton;
    
    Connection conn = null;
    boolean localeLanguage;
    
    Locale userLocale;
    ResourceBundle r;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //Get user Locale and then automatically translate the page to user language on startup
        userLocale = Locale.getDefault();
        //Locale testLocale = new Locale("hi","US");
        //Locale testLocale = new Locale("es","US");
        r = ResourceBundle.getBundle("View_Controller/Bundle", userLocale);
        
        languagePicker.setItems(FXCollections.observableArrayList("English", "Hindi"));
        //languagePicker.setValue("English");
        
        conn = DBConnection.getConnection();
        
        
        
        title.setText(r.getString("titleLocale"));
        username.setText(r.getString("userNameLocale"));
        password.setText(r.getString("passwordLocale"));
        language.setText(r.getString("languageLocale"));
        loginButton.setText(r.getString("loginButtonLocale"));
        exitButton.setText(r.getString("exitButtonLocale"));
        
        System.out.println("Title screen set to user default language: "+userLocale.getDisplayLanguage());
        localeLanguage = true;
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
    private void changeLanguage(ActionEvent event) {
        localeLanguage = false;
        
        if (languagePicker.getValue() == "Hindi"){
            title.setText("अपॉइंटमेंट शेड्यूलर");
            username.setText("नाम");
            password.setText("कुंजिका");
            language.setText("भाषा");
            loginButton.setText("लॉगिन");
            exitButton.setText("प्रस्थान");
        }
        else {
            title.setText("Appointment Scheduler");
            username.setText("Username");
            password.setText("Password");
            language.setText("Language");
            loginButton.setText("Login");
            exitButton.setText("Exit");
        }
    }
    
    @FXML
    private void login(MouseEvent event) throws IOException {
        if(usernameField.getText().isEmpty() == true || passwordField.getText().isEmpty() == true){
            
            if(localeLanguage==true){
                alert(r.getString("errorLocale"),r.getString("emptyFieldLocale"));
                return;
            }
            
            if(languagePicker.getValue().equalsIgnoreCase("Hindi")==true){
                alert("त्रुटि","उपयोगकर्ता नाम और पासवर्ड खाली नहीं हो सकता");
                return;
            }
            alert("Error","Username and Password Field cannot be empty");
            return;
        }
        
    try{
        String selectStatement = "SELECT userName, password FROM user WHERE userName=? AND password=?";
        
        DBQuery.setPreprearedStatement(conn, selectStatement); //Create preparedStatment object
        PreparedStatement ps = DBQuery.getPreparedStatement();
        
        String userName = usernameField.getText();
        String pass = passwordField.getText();
        
        ps.setString(1, userName);
        ps.setString(2, pass);
        
        ps.execute(); //Execute statement
        
        ResultSet rs = ps.getResultSet();
        if(rs.next()==true){
            System.out.println("Login Successful");
            userName = rs.getString("userName");
            pass = rs.getString("password");
            
            //Mark logged in user as active
            String insertStatement = "UPDATE user SET active = 1 WHERE userId > 0 AND userName=? AND password=?;";
            DBQuery.setPreprearedStatement(conn, insertStatement); //Create preparedStatment object
            PreparedStatement ps2 = DBQuery.getPreparedStatement();
            ps2.setString(1, userName);
            ps2.setString(2, pass);
            ps2.execute(); //Execute
            
        }else{
            if(localeLanguage == true){
                alert(r.getString("errorLocale"),r.getString("incorrectLoginLocale"));
                return;
            }
            
            if(languagePicker.getValue().equalsIgnoreCase("Hindi")==true){
                alert("त्रुटि","उपयोगकर्ता नाम और पासवर्ड खाली नहीं हो सकता");
                return;
            }
            alert("Error","Incorrect username or password");
            return;
        }
        
    }
    catch(SQLException e){System.out.println("SQLException: login");}
    
        Parent root = FXMLLoader.load(getClass().getResource("/View_Controller/MainScreen.fxml"));
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle("Appointment Schedule");
        stage.sizeToScene();
        stage.show();
        
        ((Stage)(((Button)event.getSource()).getScene().getWindow())).close();
    }

    @FXML private void exit(MouseEvent event){
    
        
        exitConfirmation("", "Are you sure you want to exit?");
    }
    
    private void exitConfirmation(String title, String message){
        
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(title);
        window.setMinWidth(350);
        
        Label label = new Label();
        label.setText(message);
        Button closeButton = new Button("OK");
        Button cancelButton = new Button("Cancel");
        closeButton.setOnAction(e -> Platform.exit());
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
    
}
