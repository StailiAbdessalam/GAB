package com.atm.bankConnect.repository.implementations.jdbc;

import com.atm.bankConnect.enums.MovementType;
import com.atm.bankConnect.model.Account;
import com.atm.bankConnect.model.Card;
import com.atm.bankConnect.model.Customer;
import com.atm.bankConnect.model.Movement;
import com.atm.bankConnect.repository.interfaces.AccountRepository;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AccountJDBCRepositoryImplemention extends JDBCRepository implements AccountRepository {

    @Override
    public Account create(Account account) {
        try {
            openConnection();
            preparedStatement = connection.prepareStatement("INSERT INTO accounts (balance, customers_id) VALUES (?, ?)"); // supostamente funcionaria para tudo
            preparedStatement.setDouble(1, account.getBalance());
            preparedStatement.setInt(2, account.getMainHolder().getId());

            preparedStatement.execute();
            preparedStatement.clearParameters();

            preparedStatement = connection.prepareStatement("SELECT MAX(id) FROM accounts");
            resultSet = preparedStatement.executeQuery();

            int lastId = 0;
            while (resultSet.next()) {
                lastId = resultSet.getInt(1);
            }
            preparedStatement.clearParameters();

            int code = lastId + 100;
//            preparedStatement = connection.prepareStatement("SELECT code FROM accounts WHERE id = " + lastId + ";"); // Isso era para usar o trigger
            preparedStatement = connection.prepareStatement("UPDATE accounts SET code = ? WHERE id = " + lastId + ";");
            preparedStatement.setInt(1, code);
            preparedStatement.executeUpdate();

            return findByCode(String.valueOf(code));

        } catch (ClassNotFoundException classNotFoundException) {
            System.err.println("Error opening database connection" + classNotFoundException.getMessage());
        } catch (SQLException sqlException) {
            System.err.println("Error on AccountJDBCRepositoryImplemention.create() " + sqlException.getMessage());
        }
        return null;
    }

    @Override
    public Account findByCode(String code) {
        Account accountToBeFound = null;
        int idCustomer;
        try {
            openConnection();

            preparedStatement = connection.prepareStatement("SELECT * FROM accounts WHERE code = " + code + ";");
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                accountToBeFound = new Account(
                        resultSet.getInt("id"),
                        resultSet.getString("code"),
                        resultSet.getDouble("balance"));
                idCustomer = resultSet.getInt("customers_id");
            }
        } catch (SQLException sqlException) {
            System.err.println("Error on AccountJDBCRepositoryImplementation.findByCode() " + sqlException.getMessage());
            return null;
        } catch (ClassNotFoundException classNotFoundException) {
            System.err.println("Error opening database connection " + classNotFoundException.getMessage());
            return null;
        } finally {
            try {
                closeConnection();
            } catch (SQLException sqlException) {
                System.err.println("Error closing database connection " + sqlException.getMessage());
            }
        }
        return accountToBeFound;
    }

    @Override
    public Account update(Account account) {
        try {
            openConnection();

            preparedStatement = connection.prepareStatement("UPDATE accounts SET balance = ? WHERE code = " + account.getCode() + ";");
            preparedStatement.setDouble(1, account.getBalance());

            preparedStatement.executeUpdate();
        } catch (SQLException sqlException) {
            System.err.println("Error on AccountJDBCRepositoryImplementation.update() " + sqlException.getMessage());
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
        return findByCode(account.getCode());
    }

    @Override
    public void addSecondaryHolder(int secondaryHolderId, int loggedAccountId) {
        try {
            openConnection();

            preparedStatement = connection.prepareStatement("INSERT INTO customers_accounts (customers_id, accounts_id) VALUES (?, ?)");

            preparedStatement.setInt(1, secondaryHolderId);
            preparedStatement.setInt(2, loggedAccountId);

            preparedStatement.execute();
        } catch (SQLException sqlException) {
            System.err.println("Error on AccountJDBCRepositoryImplementation.addSecondaryHolder() " + sqlException.getMessage());
        } catch (ClassNotFoundException classNotFoundException) {
            System.err.println("Error opening database connection" + classNotFoundException.getMessage());
        } finally {
            try {
                closeConnection();
            } catch (SQLException sqlException) {
                System.err.println("Error closing database connection " + sqlException.getMessage());
            }
        }
    }

    @Override
    public void delete(Account account) {
//        Used only on Lists
    }

    @Override
    public List<Account> findAll() {
        return null;
    }

    @Override
    public List<Card> findAllDebitCardsByAccount(Account account) {

        return null;
    }

    @Override
    public List<Card> findAllCreditCardsByAccount(Account account) {
        return null;
    }

    @Override
    public List<Movement> findAllSpecificMovements(MovementType movementType, Account accountToBeDebited) {
        return null;
    }

    @Override
    public Account findByCardSerialNumber(String cardSerialNumber) {
        Account accountToBeFound = null;

        try {
            openConnection();

            preparedStatement = connection.prepareStatement("SELECT * FROM accounts WHERE id = (SELECT accounts_id FROM cards WHERE serial_number = " + cardSerialNumber + ");");
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                accountToBeFound = new Account();
                accountToBeFound.setId(resultSet.getInt("id"));
                accountToBeFound.setCode(resultSet.getString("code"));
                accountToBeFound.setBalance(resultSet.getDouble("balance"));
            }
        } catch (SQLException sqlException) {
            System.err.println("Error on AccountJDBCRepositoryImplementation.findByCardSerialNumber() " + sqlException.getMessage());
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
        return accountToBeFound;
    }

    @Override
    public int findAmountOfSecondaryHolders(int loggedAccountId) {
        int amountOfSecondaryHolders = 0;
        try {
            openConnection();

            preparedStatement = connection.prepareStatement("SELECT COUNT(customers_id) FROM customers_accounts WHERE accounts_id = " + loggedAccountId + ";");
            resultSet = preparedStatement.executeQuery();
            preparedStatement.clearParameters();

            while (resultSet.next()) {
                amountOfSecondaryHolders = resultSet.getInt(1);
            }
        } catch (SQLException sqlException) {
            System.err.println("Error on AccountJDBCRepositoryImplementation.findAmountOfSecondaryHolders() " + sqlException.getMessage());
            return 0;
        } catch (ClassNotFoundException classNotFoundException) {
            System.err.println("Error opening database connection " + classNotFoundException.getMessage());
            return 0;
        } finally {
            try {
                closeConnection();
            } catch (SQLException sqlException) {
                System.err.println("Error closing database connection " + sqlException.getMessage());
            }
        }
        return amountOfSecondaryHolders;
    }

    @Override
    public Boolean verifyIfCustomerExistsInLoggedAccount(int customerId, int loggedAccountId) {
        Boolean existsCustomerInAccount = null;
        try {
            openConnection();

            // Verifica se o cliente é um main holder
            preparedStatement = connection.prepareStatement("SELECT EXISTS(SELECT * FROM accounts WHERE customers_id = " + customerId + " AND id = " + loggedAccountId + ");");
            resultSet = preparedStatement.executeQuery();
            preparedStatement.clearParameters();

            while (resultSet.next()) {
                existsCustomerInAccount = resultSet.getBoolean(1);
            }

            if (Boolean.FALSE.equals(existsCustomerInAccount)) {
                // Verificar se o cliente existe como secondary holder
                preparedStatement = connection.prepareStatement("SELECT EXISTS(SELECT * FROM customers_accounts WHERE customers_id = " + customerId + " AND accounts_id = " + loggedAccountId + ");");
                resultSet = preparedStatement.executeQuery();
            }

            while (resultSet.next()) {
                existsCustomerInAccount = resultSet.getBoolean(1);
            }
        } catch (SQLException sqlException) {
            System.err.println("Error on AccountJDBCRepositoryImplementation.verifyIfCustomerExistsInLoggedAccount() " + sqlException.getMessage());
            return null;
        } catch (ClassNotFoundException classNotFoundException) {
            System.err.println("Error opening database connection " + classNotFoundException.getMessage());
            return null;
        } finally {
            try {
                closeConnection();
            } catch (SQLException sqlException) {
                System.err.println("Error closing database connection " + sqlException.getMessage());
            }
        }
        return existsCustomerInAccount;
    }

    @Override
    public Boolean verifyIfCustomerExistsInAnotherAccount(int secondaryHolderId) {
        Boolean existsCustomerInAccount = null;
        try {
            openConnection();

            // Verifica se o cliente é um main holder
            preparedStatement = connection.prepareStatement("SELECT EXISTS(SELECT * FROM accounts WHERE customers_id = " + secondaryHolderId + ");");
            resultSet = preparedStatement.executeQuery();
            preparedStatement.clearParameters();

            while (resultSet.next()) {
                existsCustomerInAccount = resultSet.getBoolean(1);
            }

            if (Boolean.FALSE.equals(existsCustomerInAccount)) {
                // Verificar se o cliente existe como secondary holder
                preparedStatement = connection.prepareStatement("SELECT EXISTS(SELECT * FROM customers_accounts WHERE customers_id = " + secondaryHolderId + ");");
                resultSet = preparedStatement.executeQuery();
            }

            while (resultSet.next()) {
                existsCustomerInAccount = resultSet.getBoolean(1);
            }
        } catch (SQLException sqlException) {
            System.err.println("Error on AccountJDBCRepositoryImplementation.verifyIfCustomerExistsInAnotherAccount() " + sqlException.getMessage());
            return null;
        } catch (ClassNotFoundException classNotFoundException) {
            System.err.println("Error opening database connection " + classNotFoundException.getMessage());
            return null;
        } finally {
            try {
                closeConnection();
            } catch (SQLException sqlException) {
                System.err.println("Error closing database connection " + sqlException.getMessage());
            }
        }
        return existsCustomerInAccount;
    }

    @Override
    public Boolean verifyIfCustomerIsMainHolder(int customerToBeDeletedId, int loggedAccountId) {
        Boolean existsCustomerInAccount = null;
        try {
            openConnection();

            // Verifica se o cliente é um main holder
            preparedStatement = connection.prepareStatement("SELECT EXISTS(SELECT * FROM accounts WHERE customers_id = " + customerToBeDeletedId + " AND id = " + loggedAccountId + ");");
            resultSet = preparedStatement.executeQuery();
            preparedStatement.clearParameters();

            while (resultSet.next()) {
                existsCustomerInAccount = resultSet.getBoolean(1);
            }
        } catch (SQLException sqlException) {
            System.err.println("Error on AccountJDBCRepositoryImplementation.verifyIfCustomerIsMainHolder() " + sqlException.getMessage());
            return null;
        } catch (ClassNotFoundException classNotFoundException) {
            System.err.println("Error opening database connection " + classNotFoundException.getMessage());
            return null;
        } finally {
            try {
                closeConnection();
            } catch (SQLException sqlException) {
                System.err.println("Error closing database connection " + sqlException.getMessage());
            }
        }
        return existsCustomerInAccount;
    }

    @Override
    public Customer getMainHolder(int loggedAccountId) {
        Customer customerToBeFound = null;

        try {
            openConnection();

            preparedStatement = connection.prepareStatement("SELECT * FROM customers WHERE id = (SELECT customers_id FROM accounts WHERE id = " + loggedAccountId + ");");
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
            System.err.println("Error on AccountJDBCRepositoryImplementation.getMainHolder() " + sqlException.getMessage());
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
    public List<Customer> getSecondaryHolders(int loggedAccountId) {
        List<Customer> customersToBeFound = new ArrayList<>();

        try {
            openConnection();

            preparedStatement = connection.prepareStatement("SELECT * FROM customers WHERE id IN (SELECT customers_id FROM customers_accounts WHERE accounts_id = " + loggedAccountId + ");");
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Customer customer = new Customer(
                        resultSet.getInt("id"),
                        resultSet.getString("nif"),
                        resultSet.getString("name"),
                        resultSet.getString("password"),
                        resultSet.getString("phone"),
                        resultSet.getString("mobile"),
                        resultSet.getString("mobile"),
                        resultSet.getString("email"),
                        resultSet.getDate("birthdate").toLocalDate());

                customersToBeFound.add(customer);
            }
        } catch (SQLException sqlException) {
            System.err.println("Error on AccountJDBCRepositoryImplementation.getSecondaryHolders() " + sqlException.getMessage());
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
        return customersToBeFound;
    }

    @Override
    public void deleteSecondaryHolder(int loggedAccountId, int secondaryHolderId) {
        try {
            openConnection();

            preparedStatement = connection.prepareStatement("DELETE FROM customers_accounts WHERE accounts_id = " + loggedAccountId + " AND customers_id = " + secondaryHolderId + ";");
            preparedStatement.executeUpdate();
            preparedStatement.clearParameters();

        } catch (SQLException sqlException) {
            System.err.println("Error on AccountJDBCRepositoryImplementation.deleteSecondaryHolder() " + sqlException.getMessage());
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
    public void deleteSecondaryHolders(int accountToBeDeletedId) {
        try {
            openConnection();

            preparedStatement = connection.prepareStatement("DELETE FROM customers_accounts WHERE accounts_id = " + accountToBeDeletedId + ";");
            preparedStatement.executeUpdate();
            preparedStatement.clearParameters();

        } catch (SQLException sqlException) {
            System.err.println("Error on AccountJDBCRepositoryImplementation.deleteSecondaryHolders() " + sqlException.getMessage());
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
    public void delete(int accountToBeDeletedId) {
        try {
            openConnection();

            preparedStatement = connection.prepareStatement("DELETE FROM accounts WHERE id = " + accountToBeDeletedId + ";");
            preparedStatement.executeUpdate();
            preparedStatement.clearParameters();

        } catch (SQLException sqlException) {
            System.err.println("Error on AccountJDBCRepositoryImplementation.delete() " + sqlException.getMessage());
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
    public void loadDatabase(ArrayList<Customer> customers, ArrayList<Card> cards, ArrayList<Movement> movements) {
        // Used only on Lists
    }
}
