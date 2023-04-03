package com.atm.bankConnect;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import com.atm.bankConnect.enums.MovementType;
import com.atm.bankConnect.model.Account;
import com.atm.bankConnect.model.Card;
import com.atm.bankConnect.model.Movement;
import com.atm.bankConnect.service.interfaces.MovementService;

import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MovementsController {
    public ObservableList<Movement> movements;
    @FXML
    private TableView<Movement> tableViewMovements;
    @FXML
    private TableColumn<Movement, LocalDate> tableColumnDate;
    @FXML
    private TableColumn<Movement, MovementType> tableColumnType;
    @FXML
    private TableColumn<Movement, Double> tableColumnValue;
    private MovementService movementServiceImplementation = Main.getBank().getMovementServiceImplementation();
    private Stage stage;
    private Scene scene;
    private Parent root;
    private Card loggedCard;
    private Account loggedAccount;
    private List<Movement> allMovements;

    public void setLoggedCard(Card loggedCard) {
        this.loggedCard = loggedCard;
    }

    public void setLoggedAccount(Account loggedAccount) {
        this.loggedAccount = loggedAccount;
    }

    @FXML
    protected void showOnlyDeposits() {
        ArrayList<Movement> depositsArrayList = getMovements().stream().filter(movementElement -> movementElement.getType().equals(MovementType.DEPOSIT)).collect(Collectors.toCollection(ArrayList::new));

        ObservableList<Movement> depositsObservableList = FXCollections.observableList(depositsArrayList);

        tableViewMovements.setItems(depositsObservableList);
    }

    @FXML
    protected void showOnlyTransfers() {
        ArrayList<Movement> depositsArrayList = getMovements().stream().filter(movementElement -> movementElement.getType().equals(MovementType.TRANSFER_IN) || movementElement.getType().equals(MovementType.TRANSFER_OUT)).collect(Collectors.toCollection(ArrayList::new));

        ObservableList<Movement> depositsObservableList = FXCollections.observableList(depositsArrayList);

        tableViewMovements.setItems(depositsObservableList);
    }

    @FXML
    protected void showOnlyWithdraws() {
        ArrayList<Movement> depositsArrayList = getMovements().stream().filter(movementElement -> movementElement.getType().equals(MovementType.WITHDRAW)).collect(Collectors.toCollection(ArrayList::new));

        ObservableList<Movement> depositsObservableList = FXCollections.observableList(depositsArrayList);

        tableViewMovements.setItems(depositsObservableList);
    }

    @FXML
    protected void showAll() {
        ObservableList<Movement> movementObservableList = getMovements();

        generateFormattedCells();

        tableViewMovements.setItems(movementObservableList);
    }

    private ObservableList<Movement> getMovements() {
        allMovements = movementServiceImplementation.getAll(loggedAccount.getId());

        if (allMovements != null) {
            movements = FXCollections.observableList(allMovements);
        }
        return movements;
    }

    public void loadColumns() {
        tableColumnDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        tableColumnType.setCellValueFactory(new PropertyValueFactory<>("type"));
        tableColumnValue.setCellValueFactory(new PropertyValueFactory<>("value"));
    }

    private void generateFormattedCells() {
        tableColumnDate.setCellFactory(tableColumnValue -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                }
            }
        });
        tableColumnType.setCellFactory(tableColumnValue -> new TableCell<>() {
            @Override
            protected void updateItem(MovementType movementType, boolean empty) {
                super.updateItem(movementType, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(movementType.getVALUE());
                }
            }
        });
        tableColumnValue.setCellFactory(tableColumnValue -> new TableCell<>() {
            @Override
            protected void updateItem(Double value, boolean empty) {
                super.updateItem(value, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(new DecimalFormat("0.00").format(value) + "€");
                }
            }
        });
    }

    @FXML
    protected void back(ActionEvent actionEvent) throws IOException {
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
}
