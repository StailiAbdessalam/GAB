module com.atm.bankConnect {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires mysql.connector.j;

    exports com.atm.bankConnect;
    exports com.atm.bankConnect.model; // Necessário para exibir os registros da tabela

    opens com.atm.bankConnect to javafx.fxml;
    opens com.atm.bankConnect.model to javafx.fxml; // Necessário para exibir os registros da tabela
}
