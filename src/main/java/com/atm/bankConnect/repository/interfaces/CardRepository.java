package com.atm.bankConnect.repository.interfaces;

import com.atm.bankConnect.model.Account;
import com.atm.bankConnect.model.Card;
import com.atm.bankConnect.model.Customer;

import java.util.ArrayList;
import java.util.List;

public interface CardRepository {
    Card create(Card card);
    Card create(Card card, boolean isCreditCard);
    Card update(Card card);
    Card findBySerialNumber(String serialNumber);
    List<Card> findAllByAccount(Account account);
    void delete(Card cardOwnedByCustomerToBeDeleted);
    Boolean deleteAllByAccountIdAndCustomerId(int accountId, int customerId);
    int countCards(int loggedAccountId, boolean isCreditCard);
    Boolean verifyIfExistsCardsInDebtByAccount(int accountToBeDeletedId);
    void deleteAllByAccount(int accountToBeDeletedId);
    List<Card> loadDatabase(ArrayList<Customer> customers);
}
