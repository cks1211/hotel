package com.example.demo;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.*;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;


public class BookingController implements Initializable {

    @FXML
    private DatePicker fromDatePicker;
    @FXML
    private DatePicker toDatePicker;
    @FXML
    private TextField name;
    @FXML
    private TextField icNumber;
    @FXML
    private TextField contactNumber;
    @FXML
    private TextField emailAddress;
    @FXML
    private ChoiceBox<String> gender;
    @FXML
    private ChoiceBox<String> room;
    private final String[] rooms = {"1001", "1002", "1003", "1004", "1005",
            "1006", "1007", "1008", "1009", "1010",
            "2001", "2002", "2003", "2004", "2005",
            "2006", "2007", "2008", "2009", "2010"};
    @FXML
    private Button buttonBack;
    @FXML
    private Label labelEmail;
    @FXML
    private Label labelContact;
    @FXML
    private Label labelIC;
    @FXML
    private Label labelName;
    @FXML
    private Label labelGender;
    @FXML
    private Button book;
    @FXML
    private Label fromLabel;
    @FXML
    private Label toLabel;
    @FXML
    private Label roomLabel;
    @FXML
    private TextField daysOfStay;
    private final String filename = "C:\\Users\\Asus\\IdeaProjects\\demo\\src\\main\\resources\\com\\info\\rooms.txt";
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
    private ObservableList<Booking> bookings;
    private List<String> availableRooms = new ArrayList<>();
    private List<Room> Room;

    private AtomicInteger bookingCounter = new AtomicInteger(0);

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        gender.getItems().addAll("Male", "Female");


        availableRooms = getAllRooms();

        fromDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            calculateDaysOfStay();
            updateAvailableRooms();
        });

        toDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            calculateDaysOfStay();
            updateAvailableRooms();
        });

        roomColumn.setCellValueFactory(new PropertyValueFactory<Booking, String>("Room"));
        dateCheckInColumn.setCellValueFactory(new PropertyValueFactory<Booking, LocalDate>("DateCheckIn"));
        dateCheckOutColumn.setCellValueFactory(new PropertyValueFactory<Booking, LocalDate>("DateCheckOut"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<Booking, String>("Name"));
        icColumn.setCellValueFactory(new PropertyValueFactory<Booking, String>("IC"));
        contactColumn.setCellValueFactory(new PropertyValueFactory<Booking, String>("Contact"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<Booking, String>("Email"));
        genderColumn.setCellValueFactory(new PropertyValueFactory<Booking, String>("Gender"));

        bookings = FXCollections.observableArrayList();
        tableView.setItems(bookings);
    }

    public boolean validateInput() {
        boolean isValid = true;

        String nameValue = name.getText().trim();
        if (nameValue.isEmpty()) {
            labelName.setText("Name is required");
            labelName.setVisible(true);
            isValid = false;
        } else {
            labelName.setVisible(false);
        }

        String icNumberValue = icNumber.getText().trim();
        if (!icNumberValue.matches("\\d{12}")) {
            labelIC.setText("Invalid IC number");
            labelIC.setVisible(true);
            isValid = false;
        } else {
            labelIC.setVisible(false);
        }

        String contactNumberValue = contactNumber.getText().trim();
        if (!contactNumberValue.matches("\\d+")) {
            labelContact.setText("Invalid contact number");
            labelContact.setVisible(true);
            isValid = false;
        } else {
            labelContact.setVisible(false);
        }

        String emailAddressValue = emailAddress.getText().trim();
        if (!emailAddressValue.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            labelEmail.setText("Invalid email address");
            labelEmail.setVisible(true);
            isValid = false;
        } else {
            labelEmail.setVisible(false);
        }


        String genderValue = gender.getValue();
        if (genderValue == null || genderValue.isEmpty()) {
            labelGender.setText("Gender is required");
            labelGender.setVisible(true);
            isValid = false;
        } else {
            labelGender.setVisible(false);
        }

        LocalDate checkInDate = fromDatePicker.getValue();
        if (checkInDate == null) {
            fromLabel.setText("Check-in date is required");
            fromLabel.setVisible(true);
            isValid = false;
        } else {
            fromLabel.setVisible(false);
        }

        LocalDate checkOutDate = toDatePicker.getValue();
        if (checkOutDate == null) {
            toLabel.setText("Check-out date is required");
            toLabel.setVisible(true);
            isValid = false;
        } else {
            toLabel.setVisible(false);
        }

        if (checkInDate != null && checkOutDate != null) {
            if (checkOutDate.isBefore(checkInDate)) {
                toLabel.setText("Check-out date must be after check-in date");
                toLabel.setVisible(true);
                isValid = false;
            } else {
                toLabel.setVisible(false);
            }
        }

        String roomValue = room.getValue();
        if (roomValue == null || roomValue.isEmpty()) {

            roomLabel.setText("Room selection is required");
            roomLabel.setVisible(true);
            isValid = false;

        } else {
            roomLabel.setVisible(false);
        }

        return isValid;
    }

    @FXML
    private void updateAvailableRooms() {
        LocalDate checkInDate = fromDatePicker.getValue();
        LocalDate checkOutDate = toDatePicker.getValue();

        if (checkInDate == null || checkOutDate == null) {
            return;
        }

        availableRooms = getAllRooms();
        availableRooms.clear();

        for (String room : rooms) {
            availableRooms.add(room);
        }

        try (BufferedReader reader = new BufferedReader(new FileReader("C:\\Users\\Asus\\IdeaProjects\\demo\\src\\main\\resources\\com\\info\\bookingDetails.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                String[] bookingData = line.split(",");

                if (bookingData.length >= 3) {
                    String bookedRoom = bookingData[0];
                    String bookedCheckInDateStr = bookingData[1];
                    String bookedCheckOutDateStr = bookingData[2];

                    LocalDate bookedCheckInDate = null;
                    LocalDate bookedCheckOutDate = null;

                    if (bookedCheckInDateStr != null && !bookedCheckInDateStr.equals("null")) {
                        bookedCheckInDate = LocalDate.parse(bookedCheckInDateStr, formatter);
                    }

                    if (bookedCheckOutDateStr != null && !bookedCheckOutDateStr.equals("null")) {
                        bookedCheckOutDate = LocalDate.parse(bookedCheckOutDateStr, formatter);
                    }

                    if (checkInDate.isBefore(bookedCheckOutDate) && checkOutDate.isAfter(bookedCheckInDate)) {
                        availableRooms.remove(bookedRoom);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        updateComboBox();
    }

    private List<String> getAllRooms() {
        List<String> allRooms = new ArrayList<>();

        for (String room : rooms) {
            allRooms.add(room);
        }

        return allRooms;
    }

    private void saveInformationToFile() {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String checkInDate = fromDatePicker.getValue().format(formatter);
        String checkOutDate = toDatePicker.getValue().format(formatter);
        String selectedRoom = room.getValue();
        String enteredName = name.getText();
        String enteredIC = icNumber.getText();
        String enteredContact = contactNumber.getText();
        String enteredEmail = emailAddress.getText();
        String selectedGender = gender.getValue();
        String status = "wait-Check-In";
        String bookingID = generateBookingID();

        String bookingInfo = selectedRoom + "," + checkInDate + "," + checkOutDate + "," + enteredName + "," + enteredIC + "," +
                enteredContact + "," + enteredEmail + "," + selectedGender + "," + status + "," + bookingID;;

        try (FileWriter writer = new FileWriter("src\\main\\resources\\com\\info\\bookingDetails.txt", true);
             BufferedWriter bufferedWriter = new BufferedWriter(writer)) {
            bufferedWriter.write(bookingInfo);
            bufferedWriter.newLine();
            bufferedWriter.flush();

            updateRoomStatus(selectedRoom, "unavailable");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateRoomStatus(String roomNumber, String newStatus) {
        try {
            List<String> lines = Files.readAllLines(Paths.get(filename));
            for (int i = 0; i < lines.size(); i++) {
                String[] roomInfo = lines.get(i).split(",");
                if (roomInfo.length < 2) {
                    lines.get(i);
                    continue;
                }

                String number = roomInfo[0];
                String currentStatus = roomInfo[1];

                if (number.equals(roomNumber)) {
                    if (currentStatus.equals("Occupied")) {
                        return;
                    }

                    lines.set(i, number + "," + newStatus);
                    Files.write(Paths.get(filename), lines);
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void updateComboBox() {
        ObservableList<String> roomItems = FXCollections.observableArrayList(availableRooms);
        room.setItems(roomItems);
    }

    private void calculateDaysOfStay() {
        LocalDate checkInDate = fromDatePicker.getValue();
        LocalDate checkOutDate = toDatePicker.getValue();

        if (checkInDate != null && checkOutDate != null) {
            long days = ChronoUnit.DAYS.between(checkInDate, checkOutDate);
            daysOfStay.setText(String.valueOf(days));
        } else {
            daysOfStay.setText("");
        }
    }

    @FXML
    public void buttonBook() {
        if (validateInput()) {
            String selectedRoom = room.getValue();

            if (selectedRoom != null) {
                LocalDate checkInDate = fromDatePicker.getValue();
                LocalDate checkOutDate = toDatePicker.getValue();
                String enteredName = name.getText();
                String enteredIC = icNumber.getText();
                String enteredContact = contactNumber.getText();
                String enteredEmail = emailAddress.getText();
                String selectedGender = gender.getValue();
                String status = "wait-Check-In";
                String bookingID = generateBookingID();


                Booking booking = new Booking(selectedRoom, checkInDate, checkOutDate, enteredName,
                        enteredIC, enteredContact, enteredEmail, selectedGender, status,bookingID);
                bookings.add(booking);
                tableView.setItems(bookings);

                saveInformationToFile();
                clearFields();
                showSuccessAlert("Your booking has been successful!");
            } else {
                showErrorAlert("Room required to be select.");
            }
        }
    }

    private String generateBookingID() {
        char prefix = 'S';
        int numericValue = retrieveLastNumericValue() + 1;
        String numericString = String.format("%02d", numericValue);
        return prefix + numericString;
    }

    private int retrieveLastNumericValue() {
        int lastNumericValue = 0;

        try {
            List<String> lines = Files.readAllLines(Path.of("C:\\Users\\Asus\\IdeaProjects\\demo\\src\\main\\resources\\com\\info\\bookingDetails.txt"));
            if (!lines.isEmpty()) {
                String lastLine = lines.get(lines.size() - 1);
                String[] values = lastLine.split(",");

                if (values.length >= 1) {
                    String bookingID = values[values.length - 1].trim();
                    lastNumericValue = Integer.parseInt(bookingID.substring(1));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();

        }

        return lastNumericValue;
    }
    private void clearFields() {
        fromDatePicker.setValue(null);
        toDatePicker.setValue(null);
        name.clear();
        icNumber.clear();
        contactNumber.clear();
        emailAddress.clear();
        gender.getSelectionModel().clearSelection();
        room.getSelectionModel().clearSelection();
    }

    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccessAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    public void backToPreviousScene() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("homepage.fxml"));
            Parent root = loader.load();
            Scene scene = buttonBack.getScene();
            scene.setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}