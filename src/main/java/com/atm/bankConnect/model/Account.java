package com.atm.bankConnect.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Entity class Account.
 */
public class Account {
    private int id = 1;
    private String code;
    private double balance;
    private Customer mainHolder;
    private List<Customer> secondaryHolders = new ArrayList<>();
    private List<Movement> movements = new ArrayList<>();
    private List<Card> cards = new ArrayList<>();

    public Account() {
    }

    public Account(double balance, Customer mainHolder) {
        this.balance = balance;
        this.mainHolder = mainHolder;
    }

    public Account(int id, String code, double balance) {
        this.id = id;
        this.code = code;
        this.balance = balance;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public Customer getMainHolder() {
        return mainHolder;
    }

    public void setMainHolder(Customer mainHolder) {
        this.mainHolder = mainHolder;
    }

    public List<Customer> getSecondaryHolders() {
        return secondaryHolders;
    }

    public void setSecondaryHolders(ArrayList<Customer> secondaryHolders) {
        this.secondaryHolders = secondaryHolders;
    }

    public List<Movement> getMovements() {
        return movements;
    }

    public void setMovements(ArrayList<Movement> movements) {
        this.movements = movements;
    }

    public List<Card> getCards() {
        return cards;
    }

    public void setCards(List<Card> cards) {
        this.cards = cards;
    }

    @Override
    public String toString() {
        return "| CODE:" + code +
                " | BALANCE: " + balance +
                " | MAIN HOLDER: " + mainHolder +
                " |";
    }
}
