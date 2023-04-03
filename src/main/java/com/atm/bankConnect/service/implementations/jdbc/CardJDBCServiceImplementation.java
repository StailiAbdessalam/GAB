package com.atm.bankConnect.service.implementations.jdbc;

import com.atm.bankConnect.model.Account;
import com.atm.bankConnect.model.Card;
import com.atm.bankConnect.model.Customer;
import com.atm.bankConnect.repository.interfaces.CardRepository;
import com.atm.bankConnect.service.interfaces.CardService;

import java.util.ArrayList;
import java.util.List;

public class CardJDBCServiceImplementation implements CardService {

    private CardRepository cardRepositoryImplementation;

    public CardJDBCServiceImplementation(CardRepository cardRepositoryImplementation) {
        this.cardRepositoryImplementation  = cardRepositoryImplementation;
    }

    @Override
    public Card create(Customer cardHolder, boolean isCreditCard) {
        // Used only on Lists
        return null;
    }

    @Override
    public Card create(Customer cardHolder, boolean isCreditCard, Account loggedAccount) {
        Card card = new Card();
        card.setCardHolder(cardHolder);
        card.setVirgin(true);
        card.setPin("1234");
        card.setAccount(loggedAccount);
        if (isCreditCard) {
            card.setMonthyPlafond(100.);
            card.setPlafondBalance(100.);
        }
        return cardRepositoryImplementation.create(card, isCreditCard);
    }

    @Override
    public Card update(String pin, Card card) {
        card.setPin(pin);
        return cardRepositoryImplementation.update(card);
    }

    @Override
    public Card getBySerialNumber(String serialNumber) {
        return cardRepositoryImplementation.findBySerialNumber(serialNumber);
    }

    @Override
    public List<Card> getAllByAccount(Account account) {
        return cardRepositoryImplementation.findAllByAccount(account);
    }

    @Override
    public void delete(Card cardOwnedByCustomerToBeDeleted) {
//        Used only on Lists
    }

    @Override
    public Boolean deleteAllByAccountIdAndCustomerId(int accountId, int customerId) {
        return cardRepositoryImplementation.deleteAllByAccountIdAndCustomerId(accountId, customerId);
    }

    @Override
    public boolean payLoan(Card card, double value) {
        if (card.getPlafondBalance() + value > card.getMonthyPlafond()) { // Se o valor a ser pago é maior que o valor em dívida
            return false;
        }

        card.setPlafondBalance(card.getPlafondBalance() + value); // atualiza o saldo do plafond do cartão com o valor pago
        cardRepositoryImplementation.update(card); // atualiza o plafond balance do cartão na base de dados
        return true;
    }

    @Override
    public boolean makeLoan(Card card, double value) {
        if (card.getPlafondBalance() < value) { // Se não houver plafond disponível
            return false;
        }

        card.setPlafondBalance(card.getPlafondBalance() - value); // atualiza o saldo plafond do cartão com o valor sacado
        cardRepositoryImplementation.update(card); // atualiza o plafond balance do cartão na base de dados
        return true;
    }

    @Override
    public int getAmountOfCards(int loggedAccountId, boolean isCreditcard) {
        return cardRepositoryImplementation.countCards(loggedAccountId, isCreditcard);
    }

    @Override
    public Boolean verifyIfExistsCardsInDebtByAccount(int accountToBeDeletedId) {
        return cardRepositoryImplementation.verifyIfExistsCardsInDebtByAccount(accountToBeDeletedId);
    }

    @Override
    public void deleteAllByAccount(int accountToBeDeletedId) {
        cardRepositoryImplementation.deleteAllByAccount(accountToBeDeletedId);
    }

    @Override
    public List<Card> loadDatabase(ArrayList<Customer> customers) {
        return cardRepositoryImplementation.loadDatabase(customers);
    }
}
