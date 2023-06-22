package com.example.demo;


import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

public class homePageController {
    @FXML
    private Button buttonLogOut;
    @FXML
    private Button buttonCheckInOut;
    @FXML
    private Button buttonBooking;
    @FXML
    private Button buttonRoom;
    @FXML
    private Button buttonTransactionHistory;

    public void logOutButton() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Log Out");
        alert.setHeaderText("Confirmation");
        alert.setContentText("Do you sure you want to log out?");

        ButtonType yesButton = new ButtonType("Yes", ButtonBar.ButtonData.YES);
        ButtonType noButton = new ButtonType("No", ButtonBar.ButtonData.NO);
        alert.getButtonTypes().setAll(yesButton, noButton);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == yesButton) {

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
                Parent root = loader.load();
                Scene scene = new Scene(root);

                Stage stage = (Stage) buttonLogOut.getScene().getWindow();
                stage.setScene(scene);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void checkInOutButton() {
        switchScene(buttonCheckInOut, "checkIn.fxml");
    }

    public void bookingButton() {
        switchScene(buttonBooking, "booking.fxml");
    }

    public void roomButton() {
        switchScene(buttonRoom, "room.fxml");
    }

    public void transactionHistoryButton() {
        switchScene(buttonTransactionHistory, "transactionHistory.fxml");
    }

    public void switchScene(Button button, String resource) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(resource));
            Parent root = loader.load();
            Scene scene = button.getScene();
            scene.setRoot(root);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

