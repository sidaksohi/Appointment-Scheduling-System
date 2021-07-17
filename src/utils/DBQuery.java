/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import com.mysql.jdbc.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;

/**
 *
 * @author Sidak
 */
public class DBQuery {
    private static PreparedStatement statement;
    
    //Create Statement Object
    public static void setPreprearedStatement(Connection conn, String sqlStatement) throws SQLException{
        statement = conn.prepareStatement(sqlStatement);
    }
    
    //Get Statement Object
    public static PreparedStatement getPreparedStatement() {
        return statement;
    }
}
