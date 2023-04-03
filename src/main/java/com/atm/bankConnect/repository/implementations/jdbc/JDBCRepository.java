package com.atm.bankConnect.repository.implementations.jdbc;

import java.sql.*;

abstract class JDBCRepository {
    protected Connection connection;
    protected PreparedStatement preparedStatement;
    protected ResultSet resultSet;
    private final String SCHEMA = "bankConnectGab",
                         DATABASE = "mysql",
                         HOST = "localhost",
                         PORT = "3306",
                         URL = "jdbc:"+ DATABASE + "://" + HOST + ":" + PORT + "/" + SCHEMA,
                         USER = "root",
                         PASSWORD = "";

    protected void openConnection() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        connection = DriverManager.getConnection(URL, USER, PASSWORD);
    }

    protected void closeConnection() throws SQLException {
        if (preparedStatement != null) {
            preparedStatement.close();
        }
        if (connection != null) {
            connection.close();
        }
    }
}
