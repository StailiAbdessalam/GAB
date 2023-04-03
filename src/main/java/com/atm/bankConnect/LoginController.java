package com.atm.bankConnect;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Duration;
import com.atm.bankConnect.model.Card;
import com.atm.bankConnect.service.interfaces.CardService;

import java.io.IOException;
import java.net.URL;

public class LoginController {
    @FXML
    private CheckBox checkboxShowPin;
    @FXML
    private TextField textFieldCardSerialnumber, textFieldCardPin;
    @FXML
    private PasswordField passwordFieldCardPin;
    @FXML
    private Label labelErrorMessage;

    private Stage stage;
    private Scene scene;
    private Parent root;
    private CardService cardServiceImplementation;


    public LoginController() {
        cardServiceImplementation = Main.getBank().getCardServiceImplementation();
    }

    @FXML
    protected void login(ActionEvent actionEvent) throws IOException {
        Card card = cardServiceImplementation.getBySerialNumber(textFieldCardSerialnumber.getText());
        if (card == null) {
            textFieldCardSerialnumber.setText(""); // limpa o campo do serial number
            passwordFieldCardPin.setText(""); // limpa o campo do PIN
            labelErrorMessage.setLayoutX(124);
            labelErrorMessage.setText("Invalid Serial Number"); // Exibe mensagem de erro
            textFieldCardSerialnumber.getStyleClass().add("error"); // adiciona a classe CSS error
            new Timeline(new KeyFrame(Duration.millis(2000), actionEventElement -> {
                labelErrorMessage.setText("");
                textFieldCardSerialnumber.getStyleClass().remove("error"); // remove a classe CSS error
            })).play(); // limpa a mensagem após dois segundos
        } else {
            if (card.getPin().equals(passwordFieldCardPin.getText())) { // confere se este serial number existe
                URL url;
                if (card.isVirgin()) {
                    url = getClass().getResource("update-pin-view.fxml");
                    // Carrega a informações para abrir uma nova tela
                    FXMLLoader fxmlLoader = new FXMLLoader(url);
                    root = fxmlLoader.load();

                    // Pegando todas as informações que eu preciso passar para a página que será carregada
                    UpdatePinController updatePinController = fxmlLoader.getController();
                    updatePinController.setLoggedCard(card);
                } else {
                    url = getClass().getResource("menu-main-view.fxml");
                    // Carrega a informações para abrir uma nova tela
                    FXMLLoader fxmlLoader = new FXMLLoader(url);
                    root = fxmlLoader.load();

                    // Pegando todas as informações que eu preciso passar para a página que será carregada
                    MenuMainController menuMainController = fxmlLoader.getController();
                    menuMainController.setLoggedCard(card);
                    if (card.getMonthyPlafond() == 0.) { // Verifica se o cartão é de débito, se for
                        menuMainController.getButtonMakeLoan().setVisible(false); // esconde o botão fazer empréstimo
                        menuMainController.getButtonPayLoan().setVisible(false); // esconde o botão pagar empréstimo
                    } else {
                        menuMainController.setLabelMonthlyPlafond(); // mostra o limite mensal do cartão
                        menuMainController.setLabelPlafondBalance(); // mostra o saldo disponível do limite do cartão
                    }
                    menuMainController.setLoggedAccount();
                    menuMainController.setWelcome(); // mostra uma mensagem de boas-vindas com o nome do dono do cartão
                    menuMainController.setAccountBalance(); // mostra o saldo atual da conta
                }

                stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
                scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
            } else {
                textFieldCardSerialnumber.setText(""); // limpa o campo do serial number
                passwordFieldCardPin.setText(""); // limpa o campo do PIN
                labelErrorMessage.setLayoutX(152);
                labelErrorMessage.setText("Invalid PIN"); // Exibe mensagem de erro
                passwordFieldCardPin.getStyleClass().add("error"); // adiciona a classe error
                new Timeline(new KeyFrame(Duration.millis(2000), actionEventElement -> {
                    labelErrorMessage.setText("");
                    passwordFieldCardPin.getStyleClass().remove("error"); // remove a classe error
                })).play(); // limpa a mensagem após dois segundos
            }
        }
    }

    @FXML
    protected void changeTextFieldVisibility() {
        if (passwordFieldCardPin.isVisible()) {
            passwordFieldCardPin.setVisible(false);
            textFieldCardPin.setVisible(true);
//            checkboxShowPin.setGraphic(new ImageView(Objects.requireNonNull(getClass().getResource("images/show-small.png")).toExternalForm()));

            textFieldCardPin.setText(passwordFieldCardPin.getText());
        } else {
            passwordFieldCardPin.setVisible(true);
            textFieldCardPin.setVisible(false);
//            checkboxShowPin.setGraphic(new ImageView(Objects.requireNonNull(getClass().getResource("images/hide-small.png")).toExternalForm()));

            passwordFieldCardPin.setText(textFieldCardPin.getText());
        }
    }
}
