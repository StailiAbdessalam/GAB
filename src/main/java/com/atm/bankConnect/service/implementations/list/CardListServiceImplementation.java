package com.atm.bankConnect.service.implementations.list;

import com.atm.bankConnect.model.Account;
import com.atm.bankConnect.repository.interfaces.CardRepository;
import com.atm.bankConnect.model.Card;
import com.atm.bankConnect.model.Customer;
import com.atm.bankConnect.service.interfaces.CardService;

import java.util.ArrayList;
import java.util.List;

public class CardListServiceImplementation implements CardService {

    private CardRepository cardRepositoryImplementation;

    public CardListServiceImplementation(CardRepository cardRepositoryImplementation) {
        this.cardRepositoryImplementation  = cardRepositoryImplementation;
    }

    @Override
    public Card create(Customer cardHolder, boolean isCreditCard) {
        Card card = new Card();
        card.setCardHolder(cardHolder);
        card.setVirgin(true);
        if (isCreditCard) {
            card.setMonthyPlafond(100.);
            card.setPlafondBalance(100.);
        } else {
            card.setMonthyPlafond(0.);
            card.setPlafondBalance(0.);
        }
        return cardRepositoryImplementation.create(card);
    }

    @Override
    public Card create(Customer cardHolder, boolean isCreditCard, Account loggedAccount) {
        // Used only on JDBC
        return null;
    }

    @Override
    public Card update(String pin, Card card) {
        card.setPin(pin);
        card.setVirgin(false);
        return cardRepositoryImplementation.update(card);
    }

    @Override
    public Card getBySerialNumber(String serialNumber) {
        return cardRepositoryImplementation.findBySerialNumber(serialNumber);
    }

    @Override
    public List<Card> getAllByAccount(Account account) {
        return null;
    }

    @Override
    public void delete(Card cardOwnedByCustomerToBeDeleted) {
        cardRepositoryImplementation.delete(cardOwnedByCustomerToBeDeleted);
    }

    @Override
    public Boolean deleteAllByAccountIdAndCustomerId(int accountId, int customerId) {
        //Used only on JDBC
        return null;
    }

    @Override
    public boolean payLoan(Card card, double value) {
        if (card.getPlafondBalance() + value > card.getMonthyPlafond()) { // Se o valor a ser pago é maior que o valor em dívida
            return false;
        }

        card.setPlafondBalance(card.getPlafondBalance() + value); // atualiza o saldo do plafond do cartão com o valor pago
        cardRepositoryImplementation.update(card); // atualiza o cartão na base de dados
        return true;
    }

    @Override
    public boolean makeLoan(Card card, double value) {
        if (card.getPlafondBalance() < value) { // Se não houver plafond disponível
            return false;
        }

        card.setPlafondBalance(card.getPlafondBalance() - value); // atualiza o saldo plafond do cartão com o valor sacado
        cardRepositoryImplementation.update(card);
        return true;
    }

    @Override
    public int getAmountOfCards(int loggedAccountId, boolean isCreditCard) {
        // Used only on JDBC
        return 0;
    }

    @Override
    public Boolean verifyIfExistsCardsInDebtByAccount(int accountToBeDeletedId) {
        //Used only on JDBC
        return null;
    }

    @Override
    public void deleteAllByAccount(int accountToBeDeletedId) {
//        Used only on JDBC
    }

    @Override
    public List<Card> loadDatabase(ArrayList<Customer> customers) {
        return cardRepositoryImplementation.loadDatabase(customers);
    }
}
