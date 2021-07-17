/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import com.mysql.jdbc.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author Sidak
 */
public class DBConnection {
    //URL Parts
    private static final String protocol = "jdbc";
    private static final String vendorName = ":mysql:";
    private static final String ipAddress = "//3.227.166.251/U06nH7";
    
    //JDBC URL
    private static final String jdbcURL = protocol + vendorName + ipAddress;
    
    //Driver & Connection Interface Reference 
    private static final String MYSQLJDBCDRIVER = "com.mysql.jdbc.Driver";
    private static Connection conn = null;
    
    //Username & Password
    private static final String username = "U06nH7";
    private static final String password = "53688817159";
    
    public static Connection getConnection(){
        try{
        Class.forName(MYSQLJDBCDRIVER);
        conn = (Connection) DriverManager.getConnection(jdbcURL, username, password);
        //System.out.println("Connection Successful!");
        }
        catch(ClassNotFoundException e){
            System.out.println(e.getMessage());
        }
        catch(SQLException e){
            System.out.println(e.getMessage());
        }
        
        return conn;
    }
    
    public static void closeConnection(){
        try{
        conn.close();
        //System.out.println("Connection Closed.");
        }
        catch(SQLException e){
            System.out.println("Error: "+ e.getMessage());
        }
    }
    
}
