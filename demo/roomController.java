package com.example.demo;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class roomController implements Initializable {

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
    private TableColumn<Booking, String> bookingIDColumn;
    @FXML
    private Button buttonBack;
    @FXML
    private Button Refresh;
    @FXML
    private TextField Search;
    private ObservableList<Booking> bookings;
    @FXML
    private Button buttonDelete;
    @FXML
    private Button buttonModify;




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
        bookingIDColumn.setCellValueFactory(new PropertyValueFactory<Booking, String>("BookingID"));

        ObservableList<Booking> bookingList = FXCollections.observableArrayList();
        tableView.setItems(bookingList);

        Search.textProperty().addListener((observable, oldValue, newValue) -> search(newValue));
        search("");
        fetchData();
        refresh();
    }

    private void fetchData() {
        bookings = FXCollections.observableArrayList();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        try (BufferedReader reader = new BufferedReader(new FileReader("C:\\Users\\Asus\\IdeaProjects\\demo\\src\\main\\resources\\com\\info\\bookingDetails.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length == 10) {
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
                    bookings.add(booking);
            }
        }
    } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getRoomStatus(String roomNumber) {
        try (BufferedReader reader = new BufferedReader(new FileReader("C:\\Users\\Asus\\IdeaProjects\\demo\\src\\main\\resources\\com\\info\\rooms.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 2 && data[0].equals(roomNumber)) {
                    return data[1];
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }

    @FXML
    public void bookingModify(ActionEvent event) {
        Booking selectedBooking = tableView.getSelectionModel().getSelectedItem();
        if (selectedBooking != null) {
            try {

                FXMLLoader loader = new FXMLLoader(getClass().getResource("PopUp.fxml"));
                Parent root = loader.load();

                Stage popupStage = new Stage();
                popupStage.setTitle("Modify Booking");
                popupStage.initModality(Modality.APPLICATION_MODAL);
                popupStage.setResizable(false);
                Scene scene = new Scene(root);
                popupStage.setScene(scene);

                PopUp popupController = loader.getController();
                popupController.setSelectedBooking(selectedBooking);
                popupController.setStage(popupStage);

                popupStage.showAndWait();

                tableView.refresh();

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Booking Selected");
            alert.setHeaderText(null);
            alert.setContentText("Please select a booking to modify.");
            alert.showAndWait();
        }
    }

    @FXML
    public void deleteSelectedBooking(ActionEvent event) {
        Booking selectedBooking = tableView.getSelectionModel().getSelectedItem();
        if (selectedBooking != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete Booking");
            alert.setHeaderText("Confirmation");
            alert.setContentText("Are you sure you want to delete this booking?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                bookings.remove(selectedBooking);
                saveDataToFile();

                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                successAlert.setTitle("Success");
                successAlert.setHeaderText(null);
                successAlert.setContentText("Delete successful");
                successAlert.showAndWait();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Booking Selected");
            alert.setHeaderText(null);
            alert.setContentText("Please select a booking to delete.");
            alert.showAndWait();
        }
    }

    private void saveRoomDataToFile() {
        try {
            Path filePath = Paths.get("C:\\Users\\Asus\\IdeaProjects\\demo\\src\\main\\resources\\com\\info\\rooms.txt");
            List<String> lines = Files.readAllLines(filePath);

            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                String[] data = line.split(",");

                String roomNumber = data[0];
                String status = getRoomStatus(roomNumber);
                lines.set(i, roomNumber + "," + status);
            }
            Files.write(filePath, lines);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveBookingDataToFile() {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        Path filePath = Paths.get("C:\\Users\\Asus\\IdeaProjects\\demo\\src\\main\\resources\\com\\info\\bookingDetails.txt");
        try (BufferedWriter writer = Files.newBufferedWriter(filePath, StandardCharsets.UTF_8)) {
            for (Booking booking : bookings) {
                String line = booking.getRoom() + "," +
                        booking.getDateCheckIn().format(dateFormatter) + "," +
                        booking.getDateCheckOut().format(dateFormatter) + "," +
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

    private void saveDataToFile() {
        saveRoomDataToFile();
        saveBookingDataToFile();
    }

    public void search(String searchTerm) {

        String roomNumber = Search.getText();

        if (bookings == null || bookings.isEmpty()) {
            tableView.setItems(FXCollections.emptyObservableList());
            return;
        }

        ObservableList<Booking> filteredList = FXCollections.observableArrayList();
        for (Booking booking : bookings) {
            if (booking.getRoom().toLowerCase().contains(roomNumber)) {
                filteredList.add(booking);
            }
        }
        tableView.setItems(filteredList);
    }

    @FXML
    public void refresh() {
        fetchData();
        tableView.setItems(bookings);
    }

    @FXML
    public void backToScene() {
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