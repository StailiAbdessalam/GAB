package com.atm.bankConnect.repository.implementations.jdbc;

import com.atm.bankConnect.repository.interfaces.CustomerRepository;
import com.atm.bankConnect.model.Customer;

import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

public class CustomerJDBCRepositoryImplementation extends JDBCRepository implements CustomerRepository {
    @Override
    public Customer create(Customer customer) {
        try {
            openConnection();

            preparedStatement = connection.prepareStatement("INSERT INTO customers VALUES (null, ?, ?, ?, ?, ?, ?, ?, ?)");

            preparedStatement.setString(1, customer.getNif());
            preparedStatement.setString(2, customer.getName());
            preparedStatement.setString(3, customer.getPassword());
            preparedStatement.setString(4, customer.getPhone());
            preparedStatement.setString(5, customer.getMobile());
            preparedStatement.setString(6, customer.getEmail());
            preparedStatement.setString(7, customer.getProfession());
            preparedStatement.setDate(8, Date.valueOf(customer.getBirthDate()));

            preparedStatement.execute();
        } catch (SQLException sqlException) {
            System.err.println("Error on CustomerJDBCRepositoryImplementation.create() " + sqlException.getMessage());
            return null;
        } catch (ClassNotFoundException classNotFoundException) {
            System.err.println("Error opening database connection" + classNotFoundException.getMessage());
        } finally {
            try {
                closeConnection();
            } catch (SQLException sqlException) {
                System.err.println("Error closing database connection " + sqlException.getMessage());
            }
        }
        return findByNif(customer.getNif());
    }

    @Override
    public Customer findByNif(String nif) {
        Customer customerToBeFound = null;

        try {
            openConnection();

            preparedStatement = connection.prepareStatement("SELECT * FROM customers WHERE nif = " + nif + ";");
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                customerToBeFound = new Customer(
                        resultSet.getInt("id"),
                        resultSet.getString("nif"),
                        resultSet.getString("name"),
                        resultSet.getString("password"),
                        resultSet.getString("phone"),
                        resultSet.getString("mobile"),
                        resultSet.getString("mobile"),
                        resultSet.getString("email"),
                        resultSet.getDate("birthdate").toLocalDate());
            }
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
        return customerToBeFound;
    }

    @Override
    public Customer findById(int id) {
        Customer customerToBeFound = null;

        try {
            openConnection();

            preparedStatement = connection.prepareStatement("SELECT * FROM customers WHERE id = " + id + ";");
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                customerToBeFound = new Customer(
                        resultSet.getInt("id"),
                        resultSet.getString("nif"),
                        resultSet.getString("name"),
                        resultSet.getString("password"),
                        resultSet.getString("phone"),
                        resultSet.getString("mobile"),
                        resultSet.getString("mobile"),
                        resultSet.getString("email"),
                        resultSet.getDate("birthdate").toLocalDate());
            }
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
        return customerToBeFound;
    }

    @Override
    public Customer update(Customer customer) {
        try {
            openConnection();

            preparedStatement = connection.prepareStatement("UPDATE customers SET nif = ?, " +
                    "name = ?, " +
                    "password = ?, " +
                    "phone = ?, " +
                    "mobile = ?, " +
                    "email = ?, " +
                    "profession = ?, " +
                    "birthdate = ?");

            preparedStatement.setString(1, customer.getNif());
            preparedStatement.setString(2, customer.getName());
            preparedStatement.setString(3, customer.getPassword());
            preparedStatement.setString(4, customer.getPhone());
            preparedStatement.setString(5, customer.getMobile());
            preparedStatement.setString(6, customer.getEmail());
            preparedStatement.setString(7, customer.getProfession());
            preparedStatement.setDate(8, Date.valueOf(customer.getBirthDate()));

            preparedStatement.executeUpdate();
        } catch (SQLException sqlException) {
            System.err.println("Error on CustomerJDBCRepositoryImplementation.create() " + sqlException.getMessage());
            return null;
        } catch (ClassNotFoundException classNotFoundException) {
            System.err.println("Error opening database connection" + classNotFoundException.getMessage());
        } finally {
            try {
                closeConnection();
            } catch (SQLException sqlException) {
                System.err.println("Error closing database connection " + sqlException.getMessage());
            }
        }
        return findByNif(customer.getNif());
    }

    @Override
    public void delete(Customer customer) {
        try {
            openConnection();

            preparedStatement = connection.prepareStatement("DELETE FROM customers WHERE id = " + customer.getId() + ";");
            preparedStatement.executeUpdate();
            preparedStatement.clearParameters();

        } catch (SQLException sqlException) {
            System.err.println("Error on CustomerJDBCRepositoryImplementation.delete() " + sqlException.getMessage());
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
    public List<Customer> findAll() {
        return null;
    }

    @Override
    public boolean verifyIfNifAlreadyExists(String nif) {
        return false;
    }

    @Override
    public List<Customer> loadDatabase() {
        return null;
    }
}
