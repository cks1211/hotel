package com.example.demo;

import java.time.LocalDate;
import java.time.LocalDateTime;


public class Booking {
    private String Room;
    private LocalDate DateCheckIn;
    private LocalDate DateCheckOut;
    private String Name;
    private String Email;
    private String Contact;
    private String IC;
    private String Gender;
    private boolean checkedIn;
    private String status;
    private String bookingID;
    private String receiptNumber;


    public Booking(String Room, LocalDate CheckInDate, LocalDate CheckOutDate, String Name, String Ic, String Contact,
                   String Email, String Gender, String Status, String BookingID) {
        this.Room = Room;
        this.DateCheckIn = CheckInDate;
        this.DateCheckOut = CheckOutDate;
        this.Name = Name;
        this.IC = Ic;
        this.Contact = Contact;
        this.Email = Email;
        this.Gender = Gender;
        this.status = Status;
        this.bookingID = BookingID;
    }

    public String getRoom() {
        return Room;
    }

    public void setRoom(String room) {
        Room = room;
    }

    public LocalDate getDateCheckIn() {
        return DateCheckIn;
    }

    public void setDateCheckIn(LocalDate dateCheckIn) {
        DateCheckIn = dateCheckIn;
    }

    public LocalDate getDateCheckOut() {
        return DateCheckOut;
    }

    public void setDateCheckOut(LocalDate dateCheckOut) {
        DateCheckOut = dateCheckOut;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getContact() {
        return Contact;
    }

    public void setContact(String contact) {
        Contact = contact;
    }

    public String getIC() {
        return IC;
    }

    public void setIC(String IC) {
        this.IC = IC;
    }

    public String getGender() {
        return Gender;
    }

    public void setGender(String gender) {
        Gender = gender;
    }

    public boolean isCheckedIn() {
        return checkedIn;
    }

    public void setCheckedIn(boolean checkedIn) {
        this.checkedIn = checkedIn;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBookingID() {
        return bookingID;
    }

    public void setBookingID(String bookingID) {
        this.bookingID = bookingID;
    }

    public String getReceiptNumber() {
        return receiptNumber;
    }

    public void setReceiptNumber(String receiptNumber) {
        this.receiptNumber = receiptNumber;
    }
}

