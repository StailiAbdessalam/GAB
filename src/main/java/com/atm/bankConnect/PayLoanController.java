package com.atm.bankConnect;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import com.atm.bankConnect.model.Account;
import com.atm.bankConnect.model.Card;
import com.atm.bankConnect.service.interfaces.CardService;

import java.io.IOException;

public class PayLoanController {
    @FXML
    private TextField textFieldValue;
    private CardService cardServiceImplementation = Main.getBank().getCardServiceImplementation();

    private Stage stage;
    private Scene scene;
    private Parent root;
    private Card loggedCard;
    private Account loggedAccount;

    public void setLoggedCard(Card loggedCard) {
        this.loggedCard = loggedCard;
    }

    public void setLoggedAccount(Account loggedAccount) {
        this.loggedAccount = loggedAccount;
    }

    @FXML
    protected void payLoan(ActionEvent actionEvent) throws IOException {
        Alert alert;
        if (cardServiceImplementation.payLoan(loggedCard, Double.parseDouble(textFieldValue.getText()))) {
            alert = generateAlert(Alert.AlertType.INFORMATION, null, "Loan successfully perfomed");
        } else {
            alert = generateAlert(Alert.AlertType.ERROR, "Error", "Value to be paid exceeds value in debt");
        }
        loadMainScreen(actionEvent);
        alert.showAndWait();
    }

    private Alert generateAlert(Alert.AlertType alertType, String header, String content) {
        Alert alert;
        alert = new Alert(alertType);
        alert.setTitle("Make Loan ATM");
        alert.setHeaderText(header);
        alert.setContentText(content);
        return alert;
    }

    @FXML
    protected void cancel(ActionEvent actionEvent) throws IOException {
        loadMainScreen(actionEvent);
    }

    private void loadMainScreen(ActionEvent actionEvent) throws IOException {
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
    }

    @FXML
    protected void formatTextFieldToNumbersOnly() {
        textFieldValue.textProperty().addListener((observableValue, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                textFieldValue.setText(newValue.replaceAll("\\D", ""));
            }
        });
    }
}
