package com.example.demo;

import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class PaymentController implements Initializable {

    @FXML
    private Pane CardDetails;

    @FXML
    private TextField CardHolderName;

    @FXML
    private TextField CardNumber;

    @FXML
    private PasswordField Cvv;

    @FXML
    private TextField ExpiryDate;

    @FXML
    private Button PayButton;

    @FXML
    private Button PaymentType;
    @FXML
    private Label CardNumberLabel;
    @FXML
    private Label ExpiryDateLabel;
    @FXML
    private Label CvvLabel;
    @FXML
    private Label CardHolderNameLabel;

    private Booking selectedBooking;

    public void setSelectedBooking(Booking selectedBooking) {
        this.selectedBooking = selectedBooking;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        CardNumber.textProperty().addListener((observable, oldValue, newValue) -> {

            String cleanedValue = newValue.replaceAll("\\D", "");

            StringBuilder formattedValue = new StringBuilder();
            for (int i = 0; i < cleanedValue.length(); i++) {
                if (i > 0 && i % 4 == 0) {
                    formattedValue.append(" ");
                }
                formattedValue.append(cleanedValue.charAt(i));
            }

            CardNumber.setText(formattedValue.toString());
        });

        ExpiryDate.textProperty().addListener((observable, oldValue, newValue) -> {

            String cleanedValue = newValue.replaceAll("\\D", "");

            if (cleanedValue.length() >= 2) {
                cleanedValue = cleanedValue.substring(0, 2) + "/" + cleanedValue.substring(2);
            }

            if (cleanedValue.length() > 5) {
                cleanedValue = cleanedValue.substring(0, 5);
            }


            ExpiryDate.setText(cleanedValue);
        });
    }

    @FXML
    private void showFields() {
        CardDetails.setVisible(true);
    }

    public String validateCardNumber(String cardNumber) {
        return cardNumber.matches("\\d{4}( \\d{4}){3}") ? "Valid" : "Invalid";
    }

    public String validateCardHolderName(String cardHolderName) {
        return cardHolderName.matches("^[a-zA-Z\\s-]+$") ? "Valid" : "Invalid";
    }

    public String validateCVV(String cvv) {
        return cvv.matches("\\d{3,4}") ? "Valid" : "Invalid";
    }

    public String validateExpiryDate(String expiryDate) {
        return expiryDate.matches("(0[1-9]|1[0-2])/\\d{2,4}") ? "Valid" : "Invalid";
    }

    public boolean ValidateInput() {
        boolean isValid = true;


        String cardNumberValue = CardNumber.getText().trim();
        String cardNumberLabel = validateCardNumber(cardNumberValue);
        if (!cardNumberLabel.equals("Valid")) {
            CardNumberLabel.setText(cardNumberLabel);
            CardNumberLabel.setVisible(true);
            isValid = false;
        } else {
            CardNumberLabel.setVisible(false);
        }


        String cvvValue = Cvv.getText().trim();
        String cvvLabel = validateCVV(cvvValue);
        if (!cvvLabel.equals("Valid")) {
            CvvLabel.setText(cvvLabel);
            CvvLabel.setVisible(true);
            isValid = false;
        } else {
            CvvLabel.setVisible(false);
        }


        String cardHolderNameValue = CardHolderName.getText().trim();
        String cardHolderNameLabel = validateCardHolderName(cardHolderNameValue);
        if (!cardHolderNameLabel.equals("Valid")) {
            CardHolderNameLabel.setText(cardHolderNameLabel);
            CardHolderNameLabel.setVisible(true);
            isValid = false;
        } else {
            CardHolderNameLabel.setVisible(false);
        }

        String expiryDateValue = ExpiryDate.getText().trim();
        String expiryDateLabel = validateExpiryDate(expiryDateValue);
        if (!expiryDateLabel.equals("Valid")) {
            ExpiryDateLabel.setText(expiryDateLabel);
            ExpiryDateLabel.setVisible(true);
            isValid = false;
        } else {
            ExpiryDateLabel.setVisible(false);
        }

        return isValid;
    }

    @FXML
    private void buttonPay() {
        boolean isValid = ValidateInput();

        if (isValid) {
            openTransactionDetailsPage();

        } else {
            showErrorAlert("Invalid input");
        }
    }

    private void openTransactionDetailsPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("transactionHistory.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.getIcons().add(new Image("C:\\Users\\Asus\\IdeaProjects\\demo\\src\\main\\resources\\com\\img\\LOGO.png"));
            Stage paymentStage = (Stage) PayButton.getScene().getWindow();
            paymentStage.hide();

            stage.show();

            paymentStage.close();
        } catch (IOException e) {
            e.printStackTrace();
            showErrorAlert("Error opening transaction details");
        }
    }


    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

