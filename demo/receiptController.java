package com.example.demo;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

public class receiptController {

    @FXML
    private Label ChargePerDayField;

    @FXML
    private Label CustomerContactField;

    @FXML
    private Label CustomerEmailField;

    @FXML
    private Label CustomerICField;

    @FXML
    private Label CustomerNameField;

    @FXML
    private Label DatePaymentField;

    @FXML
    private Label DayOfStayField;

    @FXML
    private Label ReceiptNoField;

    @FXML
    private Label RoomField;

    @FXML
    private Label TaxField;

    @FXML
    private Label TotalAmountField;

    @FXML
    private Label TourismTaxField;

    @FXML
    private Label BookingIDField;


    private Booking booking;

    public void setBooking(Booking booking) {
        this.booking = booking;
    }

    @FXML
    public void initialize() {
        if (booking != null) {
            readFileAndPopulateFields();
        } else {

        }
    }
    @FXML
    private void readFileAndPopulateFields() {
        String filePath = "C:\\Users\\Asus\\IdeaProjects\\demo\\src\\main\\resources\\com\\info\\bookingDetails.txt";

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean bookingFound = false;

            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");

                if (data.length == 10 && data[9].trim().equals(booking.getBookingID())) {
                    String firstColumn = data[0].trim();
                    String secondColumn = data[1].trim();
                    String thirdColumn = data[2].trim();
                    String fourthColumn = data[3].trim();
                    String fifthColumn = data[4].trim();
                    String sixthColumn = data[5].trim();
                    String seventhColumn = data[6].trim();
                    String tenthColumn = data[9].trim();

                    RoomField.setText(firstColumn);
                    CustomerNameField.setText(fourthColumn);
                    CustomerICField.setText(fifthColumn);
                    CustomerContactField.setText(sixthColumn);
                    CustomerEmailField.setText(seventhColumn);
                    BookingIDField.setText(tenthColumn);

                    String receiptNumber = readReceiptNumberFromFile();
                    ReceiptNoField.setText(receiptNumber);

                    // Perform price calculations
                    LocalDate currentDate = LocalDate.now();
                    DatePaymentField.setText(currentDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));

                    LocalDate checkInDate = LocalDate.parse(secondColumn, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
                    LocalDate checkOutDate = LocalDate.parse(thirdColumn, DateTimeFormatter.ofPattern("dd-MM-yyyy"));

                    long dayOfStay = getDayOfStay(checkInDate, checkOutDate);

                    double ChargePerDay = 350.00;
                    double serviceTaxRate = 0.10;
                    double tourismTaxPerDay = 10.00;

                    double totalRoomCharges = calculateTotalRoomCharges(ChargePerDay, dayOfStay);
                    double Tax = calculateTax(totalRoomCharges, serviceTaxRate);
                    double tourismTax = calculateTourismTax(tourismTaxPerDay, dayOfStay);
                    double totalAmount = calculateTotalAmount(totalRoomCharges, Tax, tourismTax);

                    DayOfStayField.setText(dayOfStay + "Days");
                    ChargePerDayField.setText("RM " + totalRoomCharges);
                    TaxField.setText("RM " + Tax);
                    TourismTaxField.setText("RM " + tourismTax);;
                    TotalAmountField.setText("RM " + totalAmount);

                    bookingFound = true;
                    break;

                }
            }
            if (!bookingFound) {
                    System.out.println("Booking ID not found: " + booking.getBookingID());
                }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String readReceiptNumberFromFile() {
        String receiptFilePath = "C:\\Users\\Asus\\IdeaProjects\\demo\\src\\main\\resources\\com\\info\\transactionHistory.txt";
        String bookingID = booking.getBookingID();
        String receiptNumber = "";

        try (BufferedReader reader = new BufferedReader(new FileReader(receiptFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 2 && data[0].equals(bookingID)) {
                    receiptNumber = data[1].trim();
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return receiptNumber;
    }

    public void setReceiptNumber(String receiptNumber) {
        ReceiptNoField.setText(receiptNumber);
    }

    private long getDayOfStay(LocalDate checkInDate, LocalDate checkOutDate) {
        return ChronoUnit.DAYS.between(checkInDate, checkOutDate);
    }

    private double calculateTotalRoomCharges(double chargePerDay, long dayOfStay) {
        return chargePerDay * dayOfStay;
    }

    private double calculateTax(double totalRoomCharges, double serviceTaxRate) {
        return totalRoomCharges * serviceTaxRate;
    }

    private double calculateTourismTax(double tourismTaxPerDay, long dayOfStay) {
        return tourismTaxPerDay * dayOfStay;
    }

    private double calculateTotalAmount(double totalRoomCharges, double Tax, double tourismTax) {
        return totalRoomCharges + Tax + tourismTax;
    }

}
