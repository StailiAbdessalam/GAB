package com.atm.bankConnect;

import com.atm.bankConnect.controller.Bank;
import com.atm.bankConnect.repository.implementations.jdbc.AccountJDBCRepositoryImplemention;
import com.atm.bankConnect.repository.implementations.jdbc.CardJDBCRepositoryImplementation;
import com.atm.bankConnect.repository.implementations.jdbc.CustomerJDBCRepositoryImplementation;
import com.atm.bankConnect.repository.implementations.jdbc.MovementJDBCRepositoryImplementation;
import com.atm.bankConnect.repository.interfaces.CardRepository;
import com.atm.bankConnect.repository.interfaces.CustomerRepository;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import com.atm.bankConnect.repository.interfaces.AccountRepository;
import com.atm.bankConnect.repository.interfaces.MovementRepository;
import com.atm.bankConnect.service.implementations.jdbc.AccountJDBCServiceImplementation;
import com.atm.bankConnect.service.implementations.jdbc.CardJDBCServiceImplementation;
import com.atm.bankConnect.service.implementations.jdbc.CustomerJDBCServiceImplementation;
import com.atm.bankConnect.service.implementations.jdbc.MovementJDBCServiceImplementation;
import com.atm.bankConnect.service.interfaces.AccountService;
import com.atm.bankConnect.service.interfaces.CardService;
import com.atm.bankConnect.service.interfaces.CustomerService;
import com.atm.bankConnect.service.interfaces.MovementService;

import java.io.IOException;
import java.sql.SQLException;

import static java.util.Objects.requireNonNull;

public class Main extends Application {
    private static Bank bank;

    public static Bank getBank() {
        return bank;
    }

    public static void main(String[] args) throws SQLException {
        CustomerRepository customerRepositoryImplementation = new CustomerJDBCRepositoryImplementation(); // utilizando JDBC
        CustomerService customerServiceImplementation = new CustomerJDBCServiceImplementation(customerRepositoryImplementation); // utilizando JDBC
        CardRepository cardRepositoryImplementation = new CardJDBCRepositoryImplementation(); // utilizando JDBC
        CardService cardServiceImplementation = new CardJDBCServiceImplementation(cardRepositoryImplementation); // utilizando JDBC
        MovementRepository movementRepositoryImplementation = new MovementJDBCRepositoryImplementation(); // utilizando JDBC
        MovementService movementServiceImplementation = new MovementJDBCServiceImplementation(movementRepositoryImplementation); // utilizando JDBC
        AccountRepository accountRepositoryImplementation = new AccountJDBCRepositoryImplemention(); // utilizando JDBC
        AccountService accountServiceImplementation = new AccountJDBCServiceImplementation(customerServiceImplementation, movementServiceImplementation, cardServiceImplementation, accountRepositoryImplementation); // utilizando JDBC

        bank = new Bank(customerServiceImplementation, cardServiceImplementation, movementServiceImplementation, accountServiceImplementation);
        bank.initialMenu();
    }

    public void startSelectedApp(int option) throws SQLException {
        switch (option) {
            case 1 -> launch();
            case 2 -> bank.startAppManagement();
            default -> System.exit(0);
        }
    }

    @Override
    public void start(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(requireNonNull(getClass().getResource("login-view.fxml")));
        Scene scene = new Scene(root);
        stage.getIcons().add(new Image(requireNonNull(getClass().getResourceAsStream("images/logo.png"))));
        stage.setTitle("Rumos Digital Bank ATM");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
        /*stage.setOnCloseRequest(windowEvent -> {
            stage.close();
            bank.initialMenu(); // https://stackoverflow.com/questions/24320014/how-to-call-launch-more-than-once-in-java
        });*/
    }
}
