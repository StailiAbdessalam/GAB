package com.atm.bankConnect.repository.implementations.jdbc;

import com.atm.bankConnect.enums.MovementType;
import com.atm.bankConnect.model.Movement;
import com.atm.bankConnect.repository.interfaces.MovementRepository;

import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MovementJDBCRepositoryImplementation extends JDBCRepository implements MovementRepository {
    @Override
    public Movement create(Movement movement) {
        try {
            openConnection();

            preparedStatement = connection.prepareStatement("INSERT INTO movements VALUES (null, ?, ?, ?, ?)");
            preparedStatement.setString(1, movement.getType().toString());
            preparedStatement.setDate(2, Date.valueOf(LocalDate.now()));
            preparedStatement.setDouble(3, movement.getValue());
            preparedStatement.setInt(4, movement.getAccount().getId());

            preparedStatement.execute();
        } catch (SQLException sqlException) {
            System.err.println("Error on CustomerJDBCRepositoryImplementation.create() " + sqlException.getMessage());
            return null;
        } catch (ClassNotFoundException classNotFoundException) {
            System.err.println("Error opening database connection " + classNotFoundException.getMessage());
        } finally {
            try {
                closeConnection();
            } catch (SQLException sqlException) {
                System.err.println("Error closing database connection " + sqlException.getMessage());
            }
        }

        return movement;
    }

    @Override
    public List<Movement> findAll() {
//        Used only on Lists
        return null;
    }

    @Override
    public List<Movement> findAll(int accountId) {
        List<Movement> accountMovements = new ArrayList<>();

        try {
            openConnection();

            preparedStatement = connection.prepareStatement("SELECT * FROM movements WHERE accounts_id = " + accountId + ";");
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Movement movement = new Movement();
                movement.setId(resultSet.getInt("id"));
                movement.setType(MovementType.valueOf(resultSet.getString("type")));
                movement.setDate(resultSet.getDate("date").toLocalDate());
                movement.setValue(resultSet.getDouble("value"));

                accountMovements.add(movement);
            }
        } catch (SQLException sqlException) {
            System.err.println("Error on AccountJDBCRepositoryImplementation.getAllMovements() " + sqlException.getMessage());
            return null;
        } catch (ClassNotFoundException classNotFoundException) {
            System.err.println("Error opening database connection " + classNotFoundException.getMessage());
        } finally {
            try {
                closeConnection();
            } catch (SQLException sqlException) {
                System.err.println("Error closing database connection " + sqlException.getMessage());
            }
        }
        return accountMovements;
    }

    @Override
    public List<Movement> loadDatabase() {
        return null;
    }

    @Override
    public void deleteAll(int accountToBeDeletedId) {
        try {
            openConnection();

            preparedStatement = connection.prepareStatement("DELETE FROM movements WHERE accounts_id = " + accountToBeDeletedId + ";");
            preparedStatement.executeUpdate();
            preparedStatement.clearParameters();

        } catch (SQLException sqlException) {
            System.err.println("Error on MovementJDBCRepositoryImplementation.deleteAll() " + sqlException.getMessage());
        } catch (ClassNotFoundException classNotFoundException) {
            System.err.println("Error opening database connection " + classNotFoundException.getMessage());
        } finally {
            try {
                closeConnection();
            } catch (SQLException sqlException) {
                System.err.println("Error closing database connection " + sqlException.getMessage());
            }
        }
    }

    @Override
    public double sumAllTodayWithdrawMovements(int accountIdThatOwnsThisMovement) {
        double sumWithdrawToday = 0.;
        try {
            openConnection();

            preparedStatement = connection.prepareStatement("SELECT SUM(value) FROM movements WHERE accounts_id = " + accountIdThatOwnsThisMovement + " AND date = CURDATE() AND type = '" + MovementType.WITHDRAW + "';");
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                sumWithdrawToday = resultSet.getDouble(1);
            }
        } catch (SQLException sqlException) {
            System.err.println("Error on MovementJDBCRepositoryImplementation.sumAllTodayWithdrawMovements() " + sqlException.getMessage());
            return 0.;
        } catch (ClassNotFoundException classNotFoundException) {
            System.err.println("Error opening database connection " + classNotFoundException.getMessage());
            return 0.;
        } finally {
            try {
                closeConnection();
            } catch (SQLException sqlException) {
                System.err.println("Error closing database connection " + sqlException.getMessage());
            }
        }
        return sumWithdrawToday;
    }
}
