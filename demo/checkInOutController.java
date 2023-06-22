package com.example.demo;

import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.*;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class checkInOutController implements Initializable {

    @FXML
    private TableView<Booking> tableView;
    @FXML
    private TableColumn<Booking, String> roomColumn;
    @FXML
    private TableColumn<Booking, LocalDate> dateCheckInColumn;
    @FXML
    private TableColumn<Booking, LocalDate> dateCheckOutColumn;
    @FXML
    private TableColumn<Booking, String> nameColumn;
    @FXML
    private TableColumn<Booking, String> icColumn;
    @FXML
    private TableColumn<Booking, String> contactColumn;
    @FXML
    private TableColumn<Booking, String> emailColumn;
    @FXML
    private TableColumn<Booking, String> genderColumn;
    @FXML
    private TableColumn<Booking, String> statusColumn;
    @FXML
    private Button buttonBack;
    @FXML
    private Button buttonRefresh;
    @FXML
    private TextField fieldSearch;
    private ObservableList<Booking> bookings;
    @FXML
    private Button buttonCheckIn;
    @FXML
    private Button buttonCheckOut;

    private receiptController receiptController;
    private PaymentController paymentController;

    public void setPaymentController(PaymentController paymentController) {
        this.paymentController = paymentController;
    }

    private void passSelectedBookingToPaymentController(Booking selectedItem) {
        paymentController.setSelectedBooking(selectedItem);
    }

    public void initialize(URL url, ResourceBundle resourceBundle) {

        roomColumn.setCellValueFactory(new PropertyValueFactory<Booking, String>("Room"));
        dateCheckInColumn.setCellValueFactory(new PropertyValueFactory<Booking, LocalDate>("DateCheckIn"));
        dateCheckOutColumn.setCellValueFactory(new PropertyValueFactory<Booking, LocalDate>("DateCheckOut"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<Booking, String>("Name"));
        icColumn.setCellValueFactory(new PropertyValueFactory<Booking, String>("IC"));
        contactColumn.setCellValueFactory(new PropertyValueFactory<Booking, String>("Contact"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<Booking, String>("Email"));
        genderColumn.setCellValueFactory(new PropertyValueFactory<Booking, String>("Gender"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<Booking, String>("Status"));

        fieldSearch.textProperty().addListener((observable, oldValue, newValue) -> search(newValue));
        receiptNumberCounter = readLastReceiptNumberFromFile();

        fetchData();
        search("");

        bookings = FXCollections.observableArrayList(readBookingDetailsFromFile("bookingDetails.txt"));
        tableView.setItems(bookings);

    }

    private void checkIn() {
        Booking selectedBooking = tableView.getSelectionModel().getSelectedItem();

        if (selectedBooking != null) {
            if (selectedBooking.getStatus().equals("wait-Check-In")) {
                updateBookingStatus(selectedBooking, "Check-In");
                writeBookingDetailsToFile("C:\\Users\\Asus\\IdeaProjects\\demo\\src\\main\\resources\\com\\info\\bookingDetails.txt", bookings);

                bookings.clear();

                bookings.addAll(readBookingDetailsFromFile("bookingDetails.txt"));
                tableView.setItems(bookings);

                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                successAlert.setTitle("Success");
                successAlert.setHeaderText(null);
                successAlert.setContentText("Check In Successful!");
                successAlert.showAndWait();
            } else if (selectedBooking.getStatus().equals("Check-In")) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Warning");
                alert.setHeaderText(null);
                alert.setContentText("This booking is already checked in.");
                alert.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Warning");
                alert.setHeaderText(null);
                alert.setContentText("This booking is not eligible for check-in.");
                alert.showAndWait();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setHeaderText(null);
            alert.setContentText("Please select a booking to check in.");
            alert.showAndWait();
        }
    }

    private void updateBookingStatus(Booking selectedBooking, String status) {
        selectedBooking.setStatus(status);
        tableView.refresh();

        try {
            File file = new File("C:\\Users\\Asus\\IdeaProjects\\demo\\src\\main\\resources\\com\\info\\bookingDetails.txt");
            List<String> lines = new ArrayList<>();
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] bookingData = line.split(",");
                String room = bookingData[0].trim();

                if (room.equals(selectedBooking.getRoom())) {

                    bookingData[7] = status;
                    line = String.join(",", bookingData);
                }

                lines.add(line);
            }
            reader.close();

            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            for (String updatedLine : lines) {
                writer.write(updatedLine);
                writer.newLine();
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private ObservableList<Booking> readBookingDetailsFromFile(String fileName) {

        fileName = "C:\\Users\\Asus\\IdeaProjects\\demo\\src\\main\\resources\\com\\info\\bookingDetails.txt";
        ObservableList<Booking> bookingList = FXCollections.observableArrayList();

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] bookingData = line.split(",");

                String room = bookingData[0].trim();
                LocalDate dateCheckIn = LocalDate.parse(bookingData[1].trim(), DateTimeFormatter.ofPattern("dd-MM-yyyy"));
                LocalDate dateCheckOut = LocalDate.parse(bookingData[2].trim(), DateTimeFormatter.ofPattern("dd-MM-yyyy"));
                String name = bookingData[3].trim();
                String ic = bookingData[4].trim();
                String contact = bookingData[5].trim();
                String email = bookingData[6].trim();
                String gender = bookingData[7].trim();
                String status = bookingData[8].trim();
                String bookingID = bookingData[9].trim();

                Booking booking = new Booking(room, dateCheckIn, dateCheckOut, name, ic, contact, email, gender, status, bookingID);
                bookingList.add(booking);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bookingList;
    }

    private void writeBookingDetailsToFile(String fileName, ObservableList<Booking> bookings) {
        fileName = "C:\\Users\\Asus\\IdeaProjects\\demo\\src\\main\\resources\\com\\info\\bookingDetails.txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, false))) {
            for (Booking booking : bookings) {
                String line = booking.getRoom() + "," +
                        booking.getDateCheckIn().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) + "," +
                        booking.getDateCheckOut().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) + "," +
                        booking.getName() + "," +
                        booking.getIC() + "," +
                        booking.getContact() + "," +
                        booking.getEmail() + "," +
                        booking.getGender() + "," +
                        booking.getStatus() + "," +
                        booking.getBookingID();

                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void checkOutButton() {
        Booking selectedItem = tableView.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            LocalDate checkInDate = selectedItem.getDateCheckIn();
            LocalDate checkOutDate = selectedItem.getDateCheckOut();

            if (selectedItem.getStatus().equals("Check-In")) {

                selectedItem.setStatus("Check-Out");

                writeBookingDetailsToFile("C:\\Users\\Asus\\IdeaProjects\\demo\\src\\main\\resources\\com\\info\\bookingDetails.txt", bookings);

                tableView.refresh();

                long dayOfStay = ChronoUnit.DAYS.between(checkInDate, checkOutDate);

                double chargePerDay = 350.00;
                double serviceTaxRate = 0.10;
                double tourismTaxPerDay = 10.00;

                double totalRoomCharges = chargePerDay * dayOfStay;
                double afterTax = totalRoomCharges + (totalRoomCharges * serviceTaxRate);
                double tourismTax = tourismTaxPerDay * dayOfStay;
                double totalAmount = afterTax + tourismTax;

                paymentController = new PaymentController();

                paymentController.setSelectedBooking(selectedItem);

                setPaymentController(paymentController);
                passSelectedBookingToPaymentController(selectedItem);

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Check-Out Successful");
                alert.setHeaderText(null);
                alert.setContentText("The booking has been successfully checked out.\n\n" +
                        "Number of days stayed: " + dayOfStay + "Days"  + "\n" +
                        "Total Room Charges: " + "RM" + totalRoomCharges + "\n" +
                        "After Tax: " +  "RM" + afterTax + "\n" +
                        "Tourism Tax: " + "RM" + tourismTax + "\n" +
                        "Total Amount: " + "RM" + totalAmount);

                writeTransactionDetailsToFile("C:\\Users\\Asus\\IdeaProjects\\demo\\src\\main\\resources\\" +
                                "com\\info\\transactionHistory.txt", selectedItem, totalAmount);
                alert.showAndWait();
                openPaymentPage(selectedItem);

            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Warning");
                alert.setHeaderText(null);
                alert.setContentText("This booking is not eligible for Check Out.");
                alert.showAndWait();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Please select a booking to Check Out.");
            alert.showAndWait();
        }
    }


    private void openPaymentPage(Booking selectedItem) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("payment.fxml"));
            Parent root = loader.load();
            PaymentController paymentController = loader.getController();

            paymentController.setSelectedBooking(selectedItem);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.getIcons().add(new Image("C:\\Users\\Asus\\IdeaProjects\\demo\\src\\main\\resources\\com\\img\\LOGO.png"));

            showLoadingMessage();

            PauseTransition pause = new PauseTransition(Duration.seconds(3));
            pause.setOnFinished(event -> {

                Stage currentStage = (Stage) buttonCheckOut.getScene().getWindow();
                currentStage.close();

                stage.show();
            });

            pause.play();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setReceiptController(receiptController receiptController) {
        this.receiptController = receiptController;
    }

    private static int receiptNumberCounter = 1;

    private int readLastReceiptNumberFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader("C:\\Users\\Asus\\IdeaProjects\\demo\\src\\main\\resources\\com\\info\\transactionHistory.txt"))) {
            String line;
            int lastReceiptNumber = 0;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 2) {
                    lastReceiptNumber = Integer.parseInt(parts[1].trim().substring(1));
                }
            }
            return lastReceiptNumber;
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }

        return 1;
    }

    private synchronized String generateReceiptNumber() {
        receiptNumberCounter++;
        String formattedNumber = String.format("%04d", receiptNumberCounter);
        String receiptNumber = "R" + formattedNumber;
        return receiptNumber;
    }

    private void writeTransactionDetailsToFile(String filePath, Booking booking, double totalAmount) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath, true))) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime transactionDate = LocalDateTime.now();

            receiptController receiptController = new receiptController();
            checkInOutController checkInOutController = new checkInOutController();
            checkInOutController.setReceiptController(receiptController);

            String receiptNumber = generateReceiptNumber();

            String amountString = Double.toString(totalAmount);

            writer.println(booking.getBookingID() + "," +
                    receiptNumber + "," +
                    booking.getName() + "," +
                    amountString + "," +
                    transactionDate.format(formatter));

            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    public void checkInButton() {
        checkIn();
    }
    private void fetchData() {
        List<Booking> bookingList = new ArrayList<>();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        try (BufferedReader reader = new BufferedReader(new FileReader("C:\\Users\\Asus\\IdeaProjects\\demo\\src\\main\\resources\\com\\info\\bookingDetails.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                String room = data[0];
                LocalDate dateCheckIn = LocalDate.parse(data[1], dateFormatter);
                LocalDate dateCheckOut = LocalDate.parse(data[2], dateFormatter);
                String name = data[3];
                String ic = data[4];
                String contact = data[5];
                String email = data[6];
                String gender = data[7];
                String status = data[8];
                String bookingID = data[9];
                Booking booking = new Booking(room, dateCheckIn, dateCheckOut, name, ic, contact, email, gender, status, bookingID);
                bookingList.add(booking);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        bookings = FXCollections.observableArrayList(bookingList);
    }

    public void search(String searchTerm) {
        String bookingID = fieldSearch.getText().trim();

        ObservableList<Booking> filteredList = FXCollections.observableArrayList();
        for (Booking booking : bookings) {
            if (booking.getBookingID().contains(bookingID)) {
                filteredList.add(booking);
            }
        }
        tableView.setItems(filteredList);
    }

    private void showLoadingMessage() {
        Alert loadingAlert = new Alert(Alert.AlertType.INFORMATION);
        loadingAlert.setTitle("Loading");
        loadingAlert.setHeaderText(null);
        loadingAlert.setContentText("You will be taken to the payment page...");
        loadingAlert.show();
    }

    @FXML
    public void refresh() {
        fetchData();
        tableView.setItems(bookings);
    }

    @FXML
    public void backToScene() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("homePage.fxml"));
            Parent root = loader.load();
            Scene scene = buttonBack.getScene();
            scene.setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}