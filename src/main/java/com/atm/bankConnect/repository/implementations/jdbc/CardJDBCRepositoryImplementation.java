package com.atm.bankConnect.repository.implementations.jdbc;

import com.atm.bankConnect.repository.interfaces.CardRepository;
import com.atm.bankConnect.Main;
import com.atm.bankConnect.model.Account;
import com.atm.bankConnect.model.Card;
import com.atm.bankConnect.model.Customer;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CardJDBCRepositoryImplementation extends JDBCRepository implements CardRepository {

    @Override
    public Card create(Card card, boolean isCreditCard) {
//        debitCard.setPin(String.valueOf((int)(Math.random() * 4)));
        try {
            openConnection();

            double monthlyPlafond = isCreditCard ? 100. : 0.;
            preparedStatement = connection.prepareStatement("SELECT EXISTS(SELECT * FROM cards WHERE accounts_id = " + card.getAccount().getId() + " AND customers_id = " + card.getCardHolder().getId() + " AND monthly_plafond = " + monthlyPlafond + ");"); // verfica se este usuário já tem um cartão de crédito
            resultSet = preparedStatement.executeQuery();
            preparedStatement.clearParameters();

            boolean cardExists = false;
            while (resultSet.next()) {
                cardExists = resultSet.getBoolean(1);
            }

            if (cardExists) {
                return null;
            }
            preparedStatement = connection.prepareStatement("INSERT INTO cards(pin, is_virgin, monthly_plafond, plafond_balance, customers_id, accounts_id) VALUES (?, ?, ?, ?, ?, ?);");

            preparedStatement.setString(1, card.getPin());
            preparedStatement.setBoolean(2, card.isVirgin());
            preparedStatement.setDouble(3, card.getMonthyPlafond());
            preparedStatement.setDouble(4, card.getPlafondBalance());
            preparedStatement.setInt(5, card.getCardHolder().getId());
            preparedStatement.setInt(6, card.getAccount().getId());

            preparedStatement.execute();
            preparedStatement.clearParameters();

            preparedStatement = connection.prepareStatement("SELECT MAX(id) FROM cards");
            resultSet = preparedStatement.executeQuery();

            int lastId = 0;
            while (resultSet.next()) {
                lastId = resultSet.getInt(1);
            }
            preparedStatement.clearParameters();

            int serialNumber = lastId + 800;

            preparedStatement = connection.prepareStatement("UPDATE cards SET serial_number = ? WHERE id = " + lastId + ";");
            preparedStatement.setInt(1, serialNumber);
            preparedStatement.executeUpdate();
            preparedStatement.clearParameters();

            return findBySerialNumber(String.valueOf(serialNumber));
        } catch (SQLException sqlException) {
            System.err.println("Error on CardJDBCRepositoryImplementation.create() " + sqlException.getMessage());
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
        return null;
    }

    @Override
    public Card create(Card card) {
        return null;
    }

    @Override
    public Card update(Card card) {
        try {
            openConnection();

            preparedStatement = connection.prepareStatement("UPDATE cards SET plafond_balance = ?, pin = ?, is_virgin = false WHERE id = " + card.getId() + ";");
            preparedStatement.setDouble(1, card.getPlafondBalance());
            preparedStatement.setString(2, card.getPin());

            preparedStatement.executeUpdate();
            preparedStatement.clearParameters();
        } catch (SQLException sqlException) {
            System.err.println("Error on CardJDBCRepositoryImplementation.update() " + sqlException.getMessage());
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
        return findBySerialNumber(card.getSerialNumber());
    }

    @Override
    public Card findBySerialNumber(String serialNumber) {
        Card cardToBeFound = null;
        try {
            openConnection();

            preparedStatement = connection.prepareStatement("SELECT * FROM cards WHERE serial_number = " + serialNumber + ";");
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                cardToBeFound = new Card(
                        resultSet.getInt("id"),
                        resultSet.getString("serial_number"),
                        resultSet.getString("pin"),
                        resultSet.getBoolean("is_virgin"),
                        resultSet.getDouble("monthly_plafond"),
                        resultSet.getDouble("plafond_balance")
                );

                // Used in Java FX
                Customer cardOwner = Main.getBank().getCustomerServiceImplementation().getById(resultSet.getInt("customers_id"));
                cardToBeFound.setCardHolder(cardOwner);
            }
        } catch (SQLException sqlException) {
            System.err.println("Error on CardJDBCRepositoryImplementation.findBySerialNumber() " + sqlException.getMessage());
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
        return cardToBeFound;
    }

    @Override
    public List<Card> findAllByAccount(Account account) {
        List<Card> accountCards = new ArrayList<>();

        try {
            openConnection();

            preparedStatement = connection.prepareStatement("SELECT * FROM cards WHERE accounts_id = " + account.getId() + ";");
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Card card = new Card();
                card.setId(resultSet.getInt("id"));
                card.setSerialNumber(String.valueOf(resultSet.getInt("serial_number")));
                card.setVirgin(resultSet.getBoolean("is_virgin"));
                card.setMonthyPlafond(resultSet.getDouble("monthly_plafond"));
                card.setPlafondBalance(resultSet.getDouble("plafond_balance"));

                Customer cardOwner = Main.getBank().getCustomerServiceImplementation().getById(resultSet.getInt("customers_id"));
                card.setCardHolder(cardOwner);

                accountCards.add(card);
            }
        } catch (SQLException sqlException) {
            System.err.println("Error on CardJDBCRepositoryImplementation.findAllByAccount() " + sqlException.getMessage());
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
        return accountCards;
    }

    @Override
    public void delete(Card cardOwnedByCustomerToBeDeleted) {
//        Used only on Lists
    }

    @Override
    public Boolean deleteAllByAccountIdAndCustomerId(int accountId, int customerId) {
        try {
            openConnection();

            preparedStatement = connection.prepareStatement("SELECT * FROM cards WHERE accounts_id = " + accountId + " AND customers_id = " + customerId + ";");
            resultSet = preparedStatement.executeQuery();
            preparedStatement.clearParameters();

            List<Card> cards = new ArrayList<>();
            Card card;
            if (resultSet != null) {
                card = new Card();
                while (resultSet.next()) {
                    card.setId(resultSet.getInt("id"));
                    card.setSerialNumber(String.valueOf(resultSet.getInt("serial_number")));
                    card.setVirgin(resultSet.getBoolean("is_virgin"));
                    card.setMonthyPlafond(resultSet.getDouble("monthly_plafond"));
                    card.setPlafondBalance(resultSet.getDouble("plafond_balance"));

                    cards.add(card);
                }

                if (cards.stream().anyMatch(cardElement -> cardElement.getMonthyPlafond() > cardElement.getPlafondBalance())) {
                    return false;
                }

                for (Card cardElement : cards) {
                    preparedStatement = connection.prepareStatement("DELETE FROM cards WHERE id = " + cardElement.getId() + ";");
                    preparedStatement.executeUpdate();
                    preparedStatement.clearParameters();
                }

                return true;
            }
        } catch (SQLException sqlException) {
            System.err.println("Error on CardJDBCRepositoryImplementation.delete() " + sqlException.getMessage());
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
        return false;
    }

    @Override
    public int countCards(int loggedAccountId, boolean isCreditCard) {
        int amountOfDebitCards = 0;

        try {
            openConnection();

            preparedStatement = isCreditCard ? connection.prepareStatement("SELECT COUNT(id) FROM cards WHERE accounts_id = " + loggedAccountId + " AND monthly_plafond = 100.;") : connection.prepareStatement("SELECT COUNT(id) FROM cards WHERE accounts_id = " + loggedAccountId + " AND monthly_plafond = 0.;");
            resultSet = preparedStatement.executeQuery();
            preparedStatement.clearParameters();

            while (resultSet.next()) {
                amountOfDebitCards = resultSet.getInt(1);
            }
        } catch (SQLException sqlException) {
            System.err.println("Error on CardJDBCRepositoryImplementation.countCards() " + sqlException.getMessage());
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
        return amountOfDebitCards;
    }

    @Override
    public Boolean verifyIfExistsCardsInDebtByAccount(int accountToBeDeletedId) {
        boolean cardExists = false;
        try {
            openConnection();

            preparedStatement = connection.prepareStatement("SELECT EXISTS(SELECT * FROM cards WHERE accounts_id = " + accountToBeDeletedId + " AND monthly_plafond > plafond_balance);");
            resultSet = preparedStatement.executeQuery();
            preparedStatement.clearParameters();

            while (resultSet.next()) {
                cardExists = resultSet.getBoolean(1);
            }
        } catch (SQLException sqlException) {
            System.err.println("Error on CardJDBCRepositoryImplementation.verifyIfExistsCardsInDebtByAccount() " + sqlException.getMessage());
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
        return cardExists;
    }

    @Override
    public void deleteAllByAccount(int accountToBeDeletedId) {
        try {
            openConnection();

            preparedStatement = connection.prepareStatement("DELETE FROM cards WHERE accounts_id = " + accountToBeDeletedId + ";");
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
    public List<Card> loadDatabase(ArrayList<Customer> tableCustomers) {
        // used only on Lists
        return null;
    }
}
