/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import java.util.ArrayList;
import javafx.collections.*;

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
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.TimeZone;
import javafx.scene.control.RadioButton;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 *
 * @author Sidak
 */
public class Schedule {
    private ArrayList<Appointment> allAppointments;
    private ArrayList<Appointment> allAppointmentsByMonth;
    private ArrayList<Appointment> allAppointmentsByWeek;
    private ArrayList<Appointment> allAppointmentsByDay;
    private ArrayList<String> allUsers;
    private boolean withinQHourAlert = false;
    private String qHourAlertText;
    private int qHourAlertUser;
    
    public Schedule(){
        allAppointments = new ArrayList<>();
        allAppointmentsByMonth = new ArrayList<>();
        allAppointmentsByWeek = new ArrayList<>();
        allAppointmentsByDay = new ArrayList<>();
        allUsers = new ArrayList<>();     
    }
    
    public void addAppointment(Appointment apt){
        if(apt != null){
        this.allAppointments.add(apt);
        
        if(apt.isWithinMonth() == true){
            this.allAppointmentsByMonth.add(apt);
        }
        if(apt.isWithinWeek() == true){
            this.allAppointmentsByWeek.add(apt);
        }
        if(apt.isWithinDay() == true){
            this.allAppointmentsByDay.add(apt);
        }
        if(apt.isWithinQHour() == true){
            this.withinQHourAlert = true;
            this.qHourAlertText = "You have an appointment at "+apt.getStartTime()+" with "+apt.getCustomer()+"!";
            this.qHourAlertUser = apt.getUserID();
        }
        }//end of if not null
    }
    
    public void updateAppointment(int index, Appointment apt){
        this.allAppointments.set(index, apt);
    }
    
    public void deleteAppointment(Appointment apt){
        this.allAppointments.remove(apt);
    }
    
    public void deleteUser(String user){
        this.allUsers.remove(user);
    }
    
    public ObservableList<Appointment> getAllAppointments(){
        return FXCollections.observableList(allAppointments);
    }
    
    public ObservableList<String> getAllUsers(){
        return FXCollections.observableList(allUsers);
    }

    public ObservableList<Appointment> getAllAppointmentsByMonth() {
        return FXCollections.observableList(allAppointmentsByMonth);
    }

    public ObservableList<Appointment> getAllAppointmentsByWeek() {
        return FXCollections.observableList(allAppointmentsByWeek);
    }

    public ObservableList<Appointment> getAllAppointmentsByDay() {
        return FXCollections.observableList(allAppointmentsByDay);
    }
    
    public boolean getWithinQHourAlert(){
        return this.withinQHourAlert;
    }
    
    public String getQHourAlertText(){
        return this.qHourAlertText;
    }
    
    public int getQHourAlertUser(){
        return this.qHourAlertUser;
    }
    
    
    
}
