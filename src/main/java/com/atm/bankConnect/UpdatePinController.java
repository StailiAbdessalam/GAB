package com.atm.bankConnect;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Duration;
import com.atm.bankConnect.model.Card;
import com.atm.bankConnect.service.interfaces.CardService;

import java.io.IOException;

import static java.util.Objects.requireNonNull;

public class UpdatePinController {
    @FXML
    private TextField textFieldNewPin, textFieldConfirmNewPin;
    @FXML
    private Label labelErrorMessage;
    @FXML
    private PasswordField passwordFieldNewPin, passwordFieldConfirmPin;
    private Stage stage;
    private Scene scene;
    private Parent root;
    private Card loggedCard;


    private CardService cardServiceImplementation;

    public UpdatePinController() {
        cardServiceImplementation = Main.getBank().getCardServiceImplementation();
    }

    public void setLoggedCard(Card loggedCard) {
        this.loggedCard = loggedCard;
    }
    @FXML
    protected void cancel(ActionEvent actionEvent) throws IOException {
        root = FXMLLoader.load(requireNonNull(getClass().getResource("login-view.fxml")));
        stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setTitle("Rumos Digital Bank ATM");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    protected void confirm(ActionEvent actionEvent) throws IOException {
        if (passwordFieldNewPin.getText().equals(passwordFieldConfirmPin.getText())) {
            String newPin = passwordFieldConfirmPin.getText();
            Card loggedCard = cardServiceImplementation.update(newPin, this.loggedCard); // troca o PIN do cartão

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("menu-main-view.fxml"));
            root = fxmlLoader.load();

            MenuMainController menuMainController = fxmlLoader.getController();
            menuMainController.setLoggedCard(loggedCard);
            if (loggedCard.getMonthyPlafond() == 0.) { // Verifica se o cartão é de débito, se for
                menuMainController.getButtonMakeLoan().setVisible(false); // esconde o botão fazer empréstimo
                menuMainController.getButtonPayLoan().setVisible(false); // esconde o botão pagar empréstimo
            } else {
                menuMainController.setLabelMonthlyPlafond(); // mostra o limite mensal do cartão
                menuMainController.setLabelPlafondBalance(); // mostra o saldo disponível do limite do cartão
            }
            menuMainController.setLoggedAccount();
            menuMainController.setWelcome();
            menuMainController.setAccountBalance();

            stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } else {
            passwordFieldNewPin.setText("");
            textFieldNewPin.setText(passwordFieldNewPin.getText());
            passwordFieldConfirmPin.setText("");
            textFieldConfirmNewPin.setText(passwordFieldConfirmPin.getText());
            labelErrorMessage.setText("PIN numbers do not match");
            passwordFieldNewPin.getStyleClass().add("error");
            passwordFieldConfirmPin.getStyleClass().add("error");
            new Timeline(new KeyFrame(Duration.millis(2000), actionEventElement -> {
                labelErrorMessage.setText("");
                passwordFieldNewPin.getStyleClass().remove("error");
                passwordFieldConfirmPin.getStyleClass().remove("error");
            })).play(); // limpa a mensagem após dois segundos
        }
    }

    @FXML
    protected void changeNewPinVisibility() {
        if (passwordFieldNewPin.isVisible()) {
            passwordFieldNewPin.setVisible(false);
            textFieldNewPin.setVisible(true);
//            checkboxNewPin.setGraphic(new ImageView(requireNonNull(getClass().getResource("images/show-small.png")).toExternalForm()));

            textFieldNewPin.setText(passwordFieldNewPin.getText());
        } else {
            passwordFieldNewPin.setVisible(true);
            textFieldNewPin.setVisible(false);
//            checkboxNewPin.setGraphic(new ImageView(requireNonNull(getClass().getResource("images/hide-small.png")).toExternalForm()));

            passwordFieldNewPin.setText(passwordFieldNewPin.getText());
        }
    }

    @FXML
    protected void changeConfirmNewPinVisibility() {
        if (passwordFieldConfirmPin.isVisible()) {
            passwordFieldConfirmPin.setVisible(false);
            textFieldConfirmNewPin.setVisible(true);
//            checkboxConfirmPin.setGraphic(new ImageView(requireNonNull(getClass().getResource("images/show-small.png")).toExternalForm()));

            textFieldConfirmNewPin.setText(passwordFieldConfirmPin.getText());
        } else {
            passwordFieldConfirmPin.setVisible(true);
            textFieldConfirmNewPin.setVisible(false);
//            checkboxConfirmPin.setGraphic(new ImageView(requireNonNull(getClass().getResource("images/hide-small.png")).toExternalForm()));

            passwordFieldConfirmPin.setText(textFieldConfirmNewPin.getText());
        }
    }
}
