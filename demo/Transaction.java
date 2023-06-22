package com.example.demo;

import java.time.LocalDateTime;

public class Transaction {
    private String bookingID;
    private double amount;
    private LocalDateTime dateTime;
    private String receiptNumber;
    private String Name;
    public Transaction(String bookingID, String receiptNumber, String Name, double amount, LocalDateTime dateTime) {
        this.bookingID = bookingID;
        this.receiptNumber = receiptNumber;
        this.Name = Name;
        this.amount = amount;
        this.dateTime = dateTime;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public String getReceiptNumber() {
        return receiptNumber;
    }

    public void setReceiptNumber(String receiptNumber) {
        this.receiptNumber = receiptNumber;
    }


    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getBookingID() {
        return bookingID;
    }

    public void setBookingID(String bookingID) {
        this.bookingID = bookingID;
    }


    public void setBooking(Booking booking) {
    }
}
