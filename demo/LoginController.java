package com.example.demo;

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
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class LoginController implements Initializable {
    @FXML
    private Label loginMessage;
    @FXML
    private Button loginButton;
    @FXML
    private TextField usernameTextField;
    @FXML
    private PasswordField passwordPasswordField;
    @FXML
    private Button closeButton;
    @FXML
    private CheckBox ShowPassword;
@FXML
private TextField textfield;
    public void loginButtonAction(ActionEvent e) {

        String username = usernameTextField.getText();
        String password = passwordPasswordField.getText();

        if (!username.isBlank() && !password.isBlank()) {
            if (validate(username, password)) {
                toHomePage();
            } else {
                loginMessage.setText("Invalid username or password");
            }
        } else {
            loginMessage.setText("Please enter username and password");
        }
    }
    private boolean validate(String username, String password) {
        try (BufferedReader reader = new BufferedReader(new FileReader("C:\\Users\\Asus\\IdeaProjects\\demo\\src\\main\\resources\\com\\info\\admin.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                String storedUsername = parts[0];
                String storedPassword = parts[1];

                if (storedUsername.equals(username) && storedPassword.equals(password)) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    public void closeButtonAction(ActionEvent e) {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }

    private void toHomePage() {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("homePage.fxml"));
            Image icon =new Image("C:\\Users\\Asus\\IdeaProjects\\demo\\src\\main\\resources\\com\\img\\LOGO.png");
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.getIcons().add(icon);
            stage.setScene(new Scene(root));
            stage.show();
            stage.setResizable(false);
            Stage currentStage = (Stage) usernameTextField.getScene().getWindow();
            currentStage.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        textfield.setVisible(false);

        ShowPassword.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                passwordPasswordField.setVisible(false);
                textfield.setVisible(true);
                textfield.setText(passwordPasswordField.getText());
            } else {
                passwordPasswordField.setVisible(true);
                textfield.setVisible(false);
                passwordPasswordField.setText(textfield.getText());
            }
        });
    }
}


