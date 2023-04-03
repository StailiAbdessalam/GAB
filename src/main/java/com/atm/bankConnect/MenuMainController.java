package com.atm.bankConnect;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import com.atm.bankConnect.model.Account;
import com.atm.bankConnect.model.Card;
import com.atm.bankConnect.service.interfaces.AccountService;

import java.io.IOException;
import java.text.DecimalFormat;

import static java.util.Objects.requireNonNull;

/**
 * Controller of the main menu screen.
 */
public class MenuMainController {
    @FXML
    private Button buttonMakeLoan, buttonPayLoan;
    @FXML
    private Label labelWelcome, labelBalance, labelMonthlyPlafond, labelPlafondBalance;

    private Stage stage;
    private Scene scene;
    private Parent root;
    private Account loggedAccount;
    private Card loggedCard;
    private AccountService accountServiceImplementation;

    public MenuMainController() {
        accountServiceImplementation = Main.getBank().getAccountServiceImplementation();
    }

    public Button getButtonMakeLoan() {
        return buttonMakeLoan;
    }

    public Button getButtonPayLoan() {
        return buttonPayLoan;
    }

    public void setLoggedCard(Card loggedCard) {
        this.loggedCard = loggedCard;
    }

    public void setLoggedAccount() {
        this.loggedAccount = accountServiceImplementation.getAccountByCardSerialNumber(loggedCard.getSerialNumber());
    }

    public void setWelcome() {
        labelWelcome.setText("Welcome " + loggedCard.getCardHolder().getName());
    }

    public void setAccountBalance() {
        labelBalance.setText("Account balance: " + new DecimalFormat("0.00").format(loggedAccount.getBalance()) + "€");
    }

    public void setLabelMonthlyPlafond() {
        labelMonthlyPlafond.setText("Credit card monthly plafond: " + new DecimalFormat("0.00").format(loggedCard.getMonthyPlafond()) + "€");
    }

    public void setLabelPlafondBalance() {
        labelPlafondBalance.setText("Credit card available plafond: " + new DecimalFormat("0.00").format(loggedCard.getPlafondBalance()) + "€");
    }

    /**
     * Creates and changes focus to a new screen to make the deposit operation.
     *
     * @param actionEvent contains the method to get the instance of the screen
     * @throws IOException in case of any type of inputing error
     */
    @FXML
    protected void deposit(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("deposit-view.fxml"));
        root = fxmlLoader.load();

        // Aqui eu passo as informações de conta e cartão, existentes em MenuMainController e passando-as para DepositController
        DepositController depositController = fxmlLoader.getController();
        depositController.setLoggedCard(loggedCard);
        depositController.setLoggedAccount(loggedAccount);

        stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Creates and changes focus to a new screen to make the withdrawal operation.
     *
     * @param actionEvent contains the method to get the instance of the screen
     * @throws IOException in case of any type of inputing error
     */
    @FXML
    protected void withdraw(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("withdraw-view.fxml"));
        root = fxmlLoader.load();

        // Aqui eu passo as informações de conta e cartão, existentes em MenuMainController e passando-as para DepositController
        WithdrawController withdrawController = fxmlLoader.getController();
        withdrawController.setLoggedCard(loggedCard);
        withdrawController.setLoggedAccount(loggedAccount);

        stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Creates and changes focus to a new screen to make the transfer operation.
     *
     * @param actionEvent contains the method to get the instance of the screen
     * @throws IOException in case of any type of inputing error
     */
    @FXML
    protected void transfer(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("transfer-view.fxml"));
        root = fxmlLoader.load();

        // Aqui eu passo as informações de conta e cartão, existentes em MenuMainController e passando-as para DepositController
        TransferController transferController = fxmlLoader.getController();
        transferController.setLoggedCard(loggedCard);
        transferController.setLoggedAccount(loggedAccount);

        stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Creates and changes focus to a new screen to make show movements operation.
     *
     * @param actionEvent contains the method to get the instance of the screen
     * @throws IOException in case of any type of inputing error
     */
    @FXML
    protected void movements(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("movements-view.fxml"));
        root = fxmlLoader.load();

        // Aqui eu passo as informações de conta e cartão, existentes em MenuMainController e passando-as para DepositController
        MovementsController movementsController = fxmlLoader.getController();
        movementsController.setLoggedCard(loggedCard);
        movementsController.setLoggedAccount(loggedAccount);
        movementsController.loadColumns();
        movementsController.showAll();

        stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Creates and changes focus to a new screen to make loan operation.
     *
     * @param actionEvent contains the method to get the instance of the screen
     * @throws IOException in case of any type of inputing error
     */
    @FXML
    protected void makeLoan(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("make-loan-view.fxml"));
        root = fxmlLoader.load();

        // Aqui eu passo as informações de conta e cartão, existentes em MenuMainController e passando-as para DepositController
        MakeLoanController makeLoanController = fxmlLoader.getController();
        makeLoanController.setLoggedCard(loggedCard);
        makeLoanController.setLoggedAccount(loggedAccount);

        stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    
    /**
     * Creates and changes focus to a new screen to pay loan operation.
     *
     * @param actionEvent contains the method to get the instance of the screen
     * @throws IOException in case of any type of inputing error
     */
    @FXML
    protected void payLoan(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("pay-loan-view.fxml"));
        root = fxmlLoader.load();

        // Aqui eu passo as informações de conta e cartão, existentes em MenuMainController e passando-as para DepositController
        PayLoanController payLoanController = fxmlLoader.getController();
        payLoanController.setLoggedCard(loggedCard);
        payLoanController.setLoggedAccount(loggedAccount);

        stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    protected void logout(ActionEvent actionEvent) throws IOException {
        root = FXMLLoader.load(requireNonNull(getClass().getResource("login-view.fxml")));
        stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setTitle("Rumos Digital Bank ATM");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }
}
