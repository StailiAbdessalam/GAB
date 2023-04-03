package com.atm.bankConnect.repository.interfaces;

import com.atm.bankConnect.enums.MovementType;
import com.atm.bankConnect.model.Account;
import com.atm.bankConnect.model.Card;
import com.atm.bankConnect.model.Customer;
import com.atm.bankConnect.model.Movement;

import java.util.ArrayList;
import java.util.List;

public interface AccountRepository {
    Account create(Account account);
    Account findByCode(String code);
    Account update(Account account);
    void addSecondaryHolder(int secondaryHolderId, int loggedAccountId);
    void delete(Account account);
    List<Account> findAll();
    List<Card> findAllDebitCardsByAccount(Account account); // used only on ArrayLists?
    List<Card> findAllCreditCardsByAccount(Account account); // used only on ArrayLists?
    List<Movement> findAllSpecificMovements(MovementType movementType, Account accountToBeDebited);
    Account findByCardSerialNumber(String cardSerialNumber);
    int findAmountOfSecondaryHolders(int loggedAccountId);
    Boolean verifyIfCustomerExistsInLoggedAccount(int customerId, int loggedAccountId);
    Boolean verifyIfCustomerExistsInAnotherAccount(int secondaryHolderId);
    Boolean verifyIfCustomerIsMainHolder(int customerToBeDeletedId, int loggedAccountId);
    Customer getMainHolder(int loggedAccountId);
    List<Customer> getSecondaryHolders(int loggedAccountId);
    void deleteSecondaryHolder(int loggedAccountId, int secondaryHolderId);
    void deleteSecondaryHolders(int accountToBeDeletedId);
    void delete(int accountToBeDeletedId);
    void loadDatabase(ArrayList<Customer> customers, ArrayList<Card> cards, ArrayList<Movement> movements);
}
