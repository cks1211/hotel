package com.example.demo;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class TransactionHistoryController implements Initializable {

    @FXML
    private Button buttonBack;
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
    private TableColumn<Booking, Void> receiptColumn;
    private ObservableList<Booking> updatedBookingList;


    @Override
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
        receiptColumn.setCellFactory(createButtonCellFactory());

        ObservableList<Booking> bookingList = FXCollections.observableArrayList();
        tableView.setItems(bookingList);

        ObservableList<Booking> bookingDetailsList = readBookingDetailsFile("bookingDetails.txt");

        ObservableList<Transaction> transactionHistoryList = readTransactionHistoryFile("transactionHistory.txt", bookingDetailsList);

        List<Booking> matchingRecords = findMatchingRecords(bookingDetailsList, transactionHistoryList);

        tableView.setItems(FXCollections.observableArrayList(matchingRecords));

        updateTableView();
        refresh();
    }

    private Callback<TableColumn<Booking, Void>, TableCell<Booking, Void>> createButtonCellFactory() {
        return new Callback<TableColumn<Booking, Void>, TableCell<Booking, Void>>() {
            @Override
            public TableCell<Booking, Void> call(TableColumn<Booking, Void> param) {
                return new TableCell<Booking, Void>() {
                    private final Button viewReceiptButton = new Button("View");

                    {
                        viewReceiptButton.setOnAction(event -> {
                            Booking booking = getTableRow().getItem();
                            if (booking != null) {
                                openReceiptPopup(booking, booking.getReceiptNumber());
                            }
                        });

                        viewReceiptButton.setAlignment(Pos.CENTER);
                        setGraphic(viewReceiptButton);
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            Booking booking = getTableRow().getItem();
                            if (booking != null) {
                                setGraphic(viewReceiptButton);
                                viewReceiptButton.setOnAction(event -> {
                                    String receiptNumber = getReceiptNumberFromTransactionHistory();
                                    openReceiptPopup(booking, receiptNumber);
                                });
                            } else {
                                setGraphic(null);
                            }
                        }
                    }
                };
            }
        };
    }

    private void openReceiptPopup(Booking booking, String receiptNumber) {
        Booking  selectedBooking = tableView.getSelectionModel().getSelectedItem();

        if (selectedBooking != null) {
            try {

                FXMLLoader loader = new FXMLLoader(getClass().getResource("receipt.fxml"));
                Parent root = loader.load();

                receiptController receiptController = loader.getController();

                receiptController.setBooking(selectedBooking);

                receiptController.setReceiptNumber(receiptNumber);

                receiptController.initialize();

                Stage stage = new Stage();
                stage.getIcons().add(new Image("C:\\Users\\Asus\\IdeaProjects\\demo\\src\\main\\resources\\com\\img\\LOGO.png"));
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String getReceiptNumberFromTransactionHistory() {
        String receiptFilePath = "C:\\Users\\Asus\\IdeaProjects\\demo\\src\\main\\resources\\com\\info\\transactionHistory.txt";

        try (BufferedReader reader = new BufferedReader(new FileReader(receiptFilePath))) {
            String line = reader.readLine();
            if (line != null) {
                String[] data = line.split(",");
                if (data.length >= 2) {
                    return data[1].trim();
                } else {
                    System.out.println("Invalid line format in the file: " + line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }

    private void updateTableView() {

        ObservableList<Booking> bookingDetailsList = readBookingDetailsFile("bookingDetails.txt");

        ObservableList<Transaction> transactionHistoryList = readTransactionHistoryFile("transactionHistory.txt", bookingDetailsList);

        List<Booking> matchingRecords = findMatchingRecords(bookingDetailsList, transactionHistoryList);

        updatedBookingList = FXCollections.observableArrayList(matchingRecords);

        tableView.setItems(updatedBookingList);
    }

    private ObservableList<Booking> readBookingDetailsFile(String fileName) {
        fileName = "C:\\Users\\Asus\\IdeaProjects\\demo\\src\\main\\resources\\com\\info\\bookingDetails.txt";
        ObservableList<Booking> bookingDetailsList = FXCollections.observableArrayList();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 10) {
                    String room = parts[0];
                    LocalDate dateCheckIn = LocalDate.parse(parts[1], dateFormatter);
                    LocalDate dateCheckOut = LocalDate.parse(parts[2], dateFormatter);
                    String name = parts[3];
                    String ic = parts[4];
                    String contact = parts[5];
                    String email = parts[6];
                    String gender = parts[7];
                    String status = parts[8];
                    String bookingID = parts[9];

                    bookingDetailsList.add(new Booking(room, dateCheckIn, dateCheckOut, name, ic, contact, email, gender, status, bookingID));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bookingDetailsList;
    }
    private ObservableList<Transaction> readTransactionHistoryFile(String fileName, ObservableList<Booking> bookingList) {
        fileName = "C:\\Users\\Asus\\IdeaProjects\\demo\\src\\main\\resources\\com\\info\\transactionHistory.txt";
        ObservableList<Transaction> transactionHistoryList = FXCollections.observableArrayList();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 5) {
                    String receiptNumber = parts[0].trim();
                    String bookingID = parts[1].trim();
                    String customerName = parts[2].trim();
                    double amount = Double.parseDouble(parts[3].trim());
                    LocalDateTime dateTime = LocalDateTime.parse(parts[4].trim(), formatter);

                    Booking booking = bookingList.stream()
                            .filter(b -> b.getBookingID().equals(bookingID))
                            .findFirst()
                            .orElse(null);

                    if (booking != null) {
                        booking.setReceiptNumber(receiptNumber);
                    }

                    Transaction transaction = new Transaction(receiptNumber, bookingID, customerName, amount, dateTime);
                    transaction.setBooking(booking);
                    transactionHistoryList.add(transaction);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return transactionHistoryList;
    }

    private List<Booking> findMatchingRecords(List<Booking> bookingDetailsList, List<Transaction> transactionHistoryList) {
        List<Booking> matchingRecords = new ArrayList<>();

        for (Transaction transaction : transactionHistoryList) {
            String bookingID = transaction.getBookingID();
            Booking matchingBooking = bookingDetailsList.stream()
                    .filter(booking -> booking.getBookingID().equals(bookingID))
                    .findFirst()
                    .orElse(null);

            if (matchingBooking != null) {
                matchingRecords.add(matchingBooking);
            }
        }

        return matchingRecords;
    }
    @FXML
    public void refresh() {
        updateTableView();
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
