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
import java.time.Duration;

//Time
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.TimeZone;
import javafx.scene.control.RadioButton;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 *
 * @author Sidak
 */
public class Appointment {
    private int appointmentID;
    private int userID;
    private String customer;
    private String user;
    private String title;
    private String description;
    private String type;
    private String date;
    private String startTime;
    private String endTime;
    private String utcAptZDT;
    private boolean withinMonth = false;
    private boolean withinWeek = false;
    private boolean withinDay = false;
    private boolean withinQHour = false;
    
    
    public Appointment(int appointmentID, int userID, String customer, String user, String title, String description, String type, String date, String startTime, String endTime, Schedule schedule){
        setTitle(title);
        setDescription(description);
        setType(type);
        setAppointmentID(appointmentID);
        setUserID(userID);
        setUser(user);
        setCustomer(customer);
        utcToLocalStart(date, startTime);
        utcToLocalEnd(date, endTime);
        stringsToZDT();
    }

    public int getAppointmentID() {
        return appointmentID;
    }

    public void setAppointmentID(int appointmentID) {
        this.appointmentID = appointmentID;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    
    //Convert UTC time to localtime for startTime, also set Date
    public void utcToLocalStart(String date, String startTime){
        String utc = date+"T"+startTime+":00.000Z";
        utcAptZDT = utc;
        //Pass arraylist through a method which will convert them to localtime
        timeToLocalZDT(utc);
    }
    
    public void timeToLocalZDT(String string){
            ZonedDateTime utcZDT = ZonedDateTime.parse(string);
            ZonedDateTime localZDT = utcZDT.withZoneSameInstant(ZoneId.systemDefault());
        
        localZDTFormatter(localZDT);
        
    }
    
    public void localZDTFormatter(ZonedDateTime localZDT){
            DateTimeFormatter g = DateTimeFormatter.ofPattern("hh:mm a");
            DateTimeFormatter f = DateTimeFormatter.ofPattern("MM/dd/yyyy");
            String localTime = String.valueOf(localZDT.format(g));
            String localDate = String.valueOf(localZDT.format(f));
            this.startTime = localTime;
            this.date = localDate;
        }
    
    public String getStartTime(){
        return this.startTime;
    }

    public String getDate() {
        return date;
    }
    
    
    
    //Convert UTC time to localtime for endTime
    public void utcToLocalEnd(String date, String endTime){
        String utc = date+"T"+endTime+":00.000Z";
        
        //Pass arraylist through a method which will convert them to localtime
        timeToLocalZDTEnd(utc);
    }
    
    public void timeToLocalZDTEnd(String string){
            ZonedDateTime utcZDT = ZonedDateTime.parse(string);
            ZonedDateTime localZDT = utcZDT.withZoneSameInstant(ZoneId.systemDefault());
        
        localZDTFormatterEnd(localZDT);
        
    }
    
    public void localZDTFormatterEnd(ZonedDateTime localZDT){
            DateTimeFormatter g = DateTimeFormatter.ofPattern("hh:mm a");
            String localTime = String.valueOf(localZDT.format(g));
            this.endTime = localTime;
           
        }
        
    public String getEndTime(){
        return this.endTime;
    }    
     
    public void stringsToZDT(){
        //Get current time in UTC
        OffsetDateTime utcNow = OffsetDateTime.now(ZoneOffset.UTC);
        String currentUTC = String.valueOf(utcNow);
        
        //Parse both current time and apt time into a utc ZDT
        ZonedDateTime aptZDT = ZonedDateTime.parse(utcAptZDT);
        ZonedDateTime currentZDT = ZonedDateTime.parse(currentUTC);
        
        
        //Check distance between
        checkDuration(aptZDT, currentZDT);
    }
    
    public void checkDuration(ZonedDateTime a, ZonedDateTime b){
        System.out.println("AptId# "+this.appointmentID);
        long hoursBetween = Duration.between(b, a).toHours();
        long minutesBetween = Duration.between(b, a).toMinutes();
        
        System.out.println("Hours btwn: "+hoursBetween);
        System.out.println("Minutes btwn: "+minutesBetween);
        
        if(minutesBetween <= 15 && minutesBetween > 0){
            this.withinQHour = true;
        }
        
        if (hoursBetween <= 744 && hoursBetween > 0){
            this.withinMonth = true;
            if(hoursBetween <= 168){
                this.withinWeek = true;
                if(hoursBetween <= 24){
                    this.withinDay = true;
                }
            }
        }
    }//end
       


    public boolean isWithinMonth() {
        return withinMonth;
    }

    public boolean isWithinWeek() {
        return withinWeek;
    }

    public boolean isWithinDay() {
        return withinDay;
    }

    public boolean isWithinQHour() {
        return withinQHour;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    
    
    
    
    
}//end of class
