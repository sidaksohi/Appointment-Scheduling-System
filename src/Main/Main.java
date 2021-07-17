/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import utils.DBConnection;
import utils.DBQuery;

import com.mysql.jdbc.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 *
 * @author Sidak
 *///3 Lambda expressions are labelled and justified in MainScreenController.java, although there are many more lambdas which are not labelled

public class Main extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        
        Parent root = FXMLLoader.load(getClass().getResource("/View_Controller/LoginScreen.fxml"));
        
        Scene scene = new Scene(root);
        
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle("Login");
        stage.sizeToScene();
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws SQLException{
        Connection conn = DBConnection.getConnection(); //Connect to DB
            
        launch(args);
        
        //Set all users to inactive on close
        String insertStatement = "UPDATE user SET active = 0 WHERE userId > 0;";
        DBQuery.setPreprearedStatement(conn, insertStatement); //Create preparedStatment object
        PreparedStatement ps = DBQuery.getPreparedStatement();
        ps.execute(); //Execute
        
        
        DBConnection.closeConnection();
    }
    
}
