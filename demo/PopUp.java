package com.example.demo;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class PopUp implements Initializable {

    @FXML
    private TextField BookingID;

    @FXML
    private DatePicker CheckInDate;

    @FXML
    private DatePicker CheckOutDate;

    @FXML
    private TextField ContactNumber;

    @FXML
    private TextField EmailAddress;

    @FXML
    private ComboBox<String> Gender;

    @FXML
    private TextField IC;

    @FXML
    private TextField Name;

    @FXML
    private TextField Room;

    @FXML
    private TextField Status;

    @FXML
    private Button Save;

    private Booking selectedBooking;
    private Stage stage;
    private ObservableList<Booking> bookings;

    public void setSelectedBooking(Booking selectedBooking) {
        this.selectedBooking = selectedBooking;
        populateFields();
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setBookings(ObservableList<Booking> bookings) { // Add this method
        this.bookings = bookings;
    }

    @FXML
    public void initialize(URL url, ResourceBundle resourceBundle) {

        Gender.getItems().addAll("Male", "Female");

        Save.setOnAction(event -> saveBooking());

        Name.textProperty().addListener((observable, oldValue, newValue) -> {
            Save.setDisable(newValue.trim().isEmpty());
        });

        Platform.runLater(() -> Name.requestFocus());

    }
    private void populateFields() {
        Room.setText(selectedBooking.getRoom());
        CheckInDate.setValue(selectedBooking.getDateCheckIn());
        CheckOutDate.setValue(selectedBooking.getDateCheckOut());
        Name.setText(selectedBooking.getName());
        IC.setText(selectedBooking.getIC());
        ContactNumber.setText(selectedBooking.getContact());
        EmailAddress.setText(selectedBooking.getEmail());
        Gender.setValue(selectedBooking.getGender());
        Status.setText(selectedBooking.getStatus());
        BookingID.setText(selectedBooking.getBookingID());
    }

    private void saveBooking() {
        boolean isValid = validateInput();

        if (isValid) {
            Booking modifiedBooking = new Booking(
                    Room.getText(),
                    CheckInDate.getValue(),
                    CheckOutDate.getValue(),
                    Name.getText(),
                    IC.getText(),
                    ContactNumber.getText(),
                    EmailAddress.getText(),
                    Gender.getValue(),
                    Status.getText(),
                    BookingID.getText()
            );

            selectedBooking.setRoom(modifiedBooking.getRoom());
            selectedBooking.setDateCheckIn(modifiedBooking.getDateCheckIn());
            selectedBooking.setDateCheckOut(modifiedBooking.getDateCheckOut());
            selectedBooking.setName(modifiedBooking.getName());
            selectedBooking.setIC(modifiedBooking.getIC());
            selectedBooking.setContact(modifiedBooking.getContact());
            selectedBooking.setEmail(modifiedBooking.getEmail());
            selectedBooking.setGender(modifiedBooking.getGender());
            selectedBooking.setStatus(modifiedBooking.getStatus());
            selectedBooking.setBookingID(modifiedBooking.getBookingID());

            saveBookingDataToFile();

            Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
            successAlert.setTitle("Success");
            successAlert.setHeaderText(null);
            successAlert.setContentText("Booking has been modified.");
            successAlert.showAndWait();

            stage.close();
        } else {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Validation Error");
            errorAlert.setHeaderText(null);
            errorAlert.setContentText("Please enter valid input for all fields.");
            errorAlert.showAndWait();
        }
    }


    public boolean validateInput() {
        boolean isValid = true;

        String nameValue = Name.getText().trim();
        if (nameValue.isEmpty()) {
            isValid = false;
        }

        String icNumberValue = IC.getText().trim();
        if (!icNumberValue.matches("\\d{12}")) {
            isValid = false;
        }

        String contactNumberValue = ContactNumber.getText().trim();
        if (!contactNumberValue.matches("\\d+")) {
            isValid = false;
        }

        String emailAddressValue = EmailAddress.getText().trim();
        if (!emailAddressValue.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            isValid = false;
        }

        String genderValue = Gender.getValue();
        if (genderValue == null || genderValue.isEmpty()) {
            isValid = false;
        }

        LocalDate checkInDate = CheckInDate.getValue();
        if (checkInDate == null) {
            isValid = false;
        }

        LocalDate checkOutDate = CheckOutDate.getValue();
        if (checkOutDate == null) {
            isValid = false;
        }

        if (checkInDate != null && checkOutDate != null) {
            if (checkOutDate.isBefore(checkInDate)) {
                isValid = false;
            }
        }

        String roomValue = Room.getText();
        if (roomValue == null || roomValue.isEmpty()) {
            isValid = false;
        }

        return isValid;
    }


    private void saveBookingDataToFile() {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        Path filePath = Paths.get("C:\\Users\\Asus\\IdeaProjects\\demo\\src\\main\\resources\\com\\info\\bookingDetails.txt");
        try {
            List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
            try (BufferedWriter writer = Files.newBufferedWriter(filePath, StandardCharsets.UTF_8)) {
                for (String line : lines) {
                    String[] parts = line.split(",");
                    String bookingID = parts[9].trim(); // Assuming booking ID is the last column
                    if (bookingID.equals(selectedBooking.getBookingID())) {
                        line = selectedBooking.getRoom() + "," +
                                selectedBooking.getDateCheckIn().format(dateFormatter) + "," +
                                selectedBooking.getDateCheckOut().format(dateFormatter) + "," +
                                selectedBooking.getName() + "," +
                                selectedBooking.getIC() + "," +
                                selectedBooking.getContact() + "," +
                                selectedBooking.getEmail() + "," +
                                selectedBooking.getGender() + "," +
                                selectedBooking.getStatus() + "," +
                                selectedBooking.getBookingID();
                    }
                    writer.write(line);
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }





}
