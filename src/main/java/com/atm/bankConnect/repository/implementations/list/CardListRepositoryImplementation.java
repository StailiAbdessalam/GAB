package com.atm.bankConnect.repository.implementations.list;

import com.atm.bankConnect.repository.interfaces.CardRepository;
import com.atm.bankConnect.model.Account;
import com.atm.bankConnect.model.Card;
import com.atm.bankConnect.model.Customer;

import java.util.ArrayList;

public class CardListRepositoryImplementation implements CardRepository {

    private ArrayList<Card> tableCards = new ArrayList<>();
//    private CustomerRepository customerListRepositoryImplementation = new CustomerListRepositoryImplementation();
    private static int id = 1;

    @Override
    public Card create(Card card) {
        card.setId(++id);
        card.setSerialNumber(String.valueOf(80 + tableCards.size()));
//        debitCard.setPin(String.valueOf((int)(Math.random() * 4)));
        card.setPin("1234");
        tableCards.add(card);
        return card;
    }

    @Override
    public Card create(Card card, boolean isCreditCard) {
        // Used only on JDBC
        return null;
    }

    @Override
    public Card update(Card card) {
        tableCards.set(tableCards.indexOf(card), card);
        return card;
    }

    @Override
    public Card findBySerialNumber(String serialNumber) {
        return tableCards.stream().filter(cardElement -> cardElement.getSerialNumber().equals(serialNumber)).findFirst().orElse(null);
    }

    @Override
    public ArrayList<Card> findAllByAccount(Account account) {
        return null;
    }

    @Override
    public void delete(Card cardOwnedByCustomerToBeDeleted) {
        tableCards.removeIf(cardElement -> cardElement.getSerialNumber().equals(cardOwnedByCustomerToBeDeleted.getSerialNumber()));
    }

    @Override
    public Boolean deleteAllByAccountIdAndCustomerId(int accountId, int customerId) {
//        Used only on JDBC
        return null;
    }

    @Override
    public int countCards(int loggedAccountId, boolean isCreditCard) {
        // Used only on JDBC
        return 0;
    }

    @Override
    public Boolean verifyIfExistsCardsInDebtByAccount(int accountToBeDeletedId) {
        // Used only on JDBC
        return null;
    }

    @Override
    public void deleteAllByAccount(int accountToBeDeletedId) {
//        Used only on JDBC
    }

    @Override
    public ArrayList<Card> loadDatabase(ArrayList<Customer> tableCustomers) {
        // Conta 102
        Card debitCard1 = new Card(true, tableCustomers.get(0),0., 0.);
        create(debitCard1); //Jane Doe
        Card creditCard1 = new Card(true, tableCustomers.get(0),100., 90.);
        create(creditCard1); //Jane Doe
        Card debitCard2 = new Card(false, tableCustomers.get(1),0., 0.);
        create(debitCard2);// John Doe
        Card creditCard2 = new Card(false, tableCustomers.get(1),100., 100.);
        create(creditCard2);// John Doe
        Card debitCard3 = new Card(false, tableCustomers.get(2),0., 0.);
        create(debitCard3);// Rosalvo Doe
        Card debitCard4 = new Card(false, tableCustomers.get(3),0., 0.);
        create(debitCard4);// João Das Couves
        Card debitCard5 = new Card(false, tableCustomers.get(4),0., 0.);
        create(debitCard5);// Aang

        // Conta 101
        Card debitCard6 = new Card(true, tableCustomers.get(8),0., 0.);
        create(debitCard6); // Momo
        Card creditCard7 = new Card(true, tableCustomers.get(3),100., 60.);
        create(creditCard7);// João das Couves

        //Conta 100
        Card debitCard8 = new Card(true, tableCustomers.get(7),0., 0.);
        create(debitCard8); // Gandalf
        Card creditCard9 = new Card(true, tableCustomers.get(7),100., 100.);
        create(creditCard9); // Gandalf

        return tableCards;
    }
}
