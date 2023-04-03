package com.atm.bankConnect.repository.implementations.list;

import com.atm.bankConnect.enums.MovementType;
import com.atm.bankConnect.model.Account;
import com.atm.bankConnect.model.Card;
import com.atm.bankConnect.model.Customer;
import com.atm.bankConnect.model.Movement;
import com.atm.bankConnect.repository.interfaces.AccountRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class AccountListRepositoryImplementation implements AccountRepository {

    private static int id = 1;
    private List<Account> tableAccounts = new ArrayList<>();

    public AccountListRepositoryImplementation() {
    }

    /**
     * Adds the given <code>Account</code> instance to the accounts list
     *
     * @param account instance to be added
     */
    @Override
    public Account create(Account account) {
        account.setId(++id);
        account.setCode(String.valueOf(100 + tableAccounts.size()));
        tableAccounts.add(account);

        return account;
    }

    @Override
    public List<Account> findAll() {
        return tableAccounts;
    }

    @Override
    public List<Card> findAllDebitCardsByAccount(Account account) {
        ArrayList<Card> debitCards = new ArrayList<>();
        account.getCards().forEach(cardElement -> {
            if (cardElement.getMonthyPlafond() == 0.) {
                debitCards.add(cardElement);
            }
        });
        return debitCards;
    }

    @Override
    public List<Card> findAllCreditCardsByAccount(Account account) {
        List<Card> creditCards = new ArrayList<>();
        account.getCards().forEach(cardElement -> {
            if (cardElement.getMonthyPlafond() > 0.) {
                creditCards.add(cardElement);
            }
        });
        return creditCards;
    }

    /**
     * Finds a specific account given a code.
     *
     * @param code account identifier
     * @return account that have that identifier
     */
    @Override
    public Account findByCode(String code) {
        return tableAccounts.stream().filter(accountElement -> accountElement.getCode().equals(code)).findFirst().orElse(null);
    }

    @Override
    public Account update(Account account) {
        tableAccounts.set(tableAccounts.indexOf(account), account);

        return account;
    }

    @Override
    public void addSecondaryHolder(int secondaryHolderId, int loggedAccountId) {
        // Used only on JDBC
    }

    @Override
    public void delete(Account account) {
        tableAccounts.removeIf(accountElement -> accountElement.getCode().equals(account.getCode()));
    }


    @Override
    public List<Movement> findAllSpecificMovements(MovementType movementType, Account accountToBeDebited) {
        /*ArrayList<Movement> specificMovements = new ArrayList<>();
        accountToBeDebited.getMovements().forEach(movementElement -> {
            if (movementElement.getType().equals(movementType)) {
                specificMovements.add(movementElement);
            }
        });*/
        return accountToBeDebited.getMovements().stream().filter(movementElement -> movementElement.getType().equals(movementType)).collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public Account findByCardSerialNumber(String cardSerialNumber) {
        /*for (Account accountElement : tableAccounts) { // procuro dentro da minha tabela contas
            for (Card cardElement : accountElement.getCards()) { // procuro dentro da minha lista de cartões
                if (cardElement.getSerialNumber().equals(cardSerialNumber)) { // Se algum número de série de algum desses cartões for igual ao que eu passei por argumento
                    return accountElement; // retorno a conta
                }
            }
        }
        return null; // Caso não ache nenhum cartão com o número de série fornecido*/

        return tableAccounts.stream().filter(accountElement -> accountElement.getCards().stream().anyMatch(cardElement -> cardElement.getSerialNumber().equals(cardSerialNumber))).findFirst().orElse(null);
    }

    @Override
    public int findAmountOfSecondaryHolders(int loggedAccountId) {
        // Used only on JDBC
        return 0;
    }

    @Override
    public Boolean verifyIfCustomerExistsInLoggedAccount(int customerId, int loggedAccountId) {
        // Used only on JDBC
        return false;
    }

    @Override
    public Boolean verifyIfCustomerExistsInAnotherAccount(int secondaryHolderId) {
        // Used only on JDBC
        return null;
    }

    @Override
    public Boolean verifyIfCustomerIsMainHolder(int customerToBeDeletedId, int loggedAccountId) {
        //Used only on JDBC
        return null;
    }

    @Override
    public Customer getMainHolder(int loggedAccountId) {
        //Used only on JDBC
        return null;
    }

    @Override
    public List<Customer> getSecondaryHolders(int loggedAccountId) {
        //Used only on JDBC
        return null;
    }

    @Override
    public void deleteSecondaryHolder(int loggedAccountId, int secondaryHolderId) {
        // Used only on JDBC
    }

    @Override
    public void deleteSecondaryHolders(int accountToBeDeletedId) {
//        Used only on JDBC
    }

    @Override
    public void delete(int accountToBeDeletedId) {
//        Used only on JDBC
    }

    /**
     * Generates initial data to fill the account HashSet that's serves as database.
     */
    @Override
    public void loadDatabase(ArrayList<Customer> tableCustomers, ArrayList<Card> tableCards, ArrayList<Movement> tableMovements) {
        /*Conta 100
         * Main holder: Gandalf -  cartão de débito: SIM | Cartão de crédito: SIM (Plafond: 100 | Saldo plafond: 100)
         *                         isVirgin: true        | isVirgin: true
         *                         S/N: 89               | S/N: 90
         * Pode fazer saques: SIM */
        Account account100 = new Account(50., tableCustomers.get(7)); //Adiciona o titular principal
        account100.getCards().addAll(Arrays.asList(tableCards.get(9), tableCards.get(10))); // Adiciona cartões de débito e crédito
        account100.getMovements().addAll(Arrays.asList(tableMovements.get(6), tableMovements.get(7), tableMovements.get(8), tableMovements.get(9))); // Adiciona todos os movimentos da respetiva conta
        create(account100);

        /*Conta 101
         * Main Holder: Momo - cartão de débito: SIM | Cartão de crédito: NÃO
         *                         isVirgin: true    | isVirgin: --
         *                         S/N: 87           | S/N: --
         * Pode fazer saques: NÃO
         * Titulares secundários:
         * João das Couves - cartão de débito: NÃO | Cartão de crédito: SIM (Plafond: 100 | Saldo plafond: 60)
         *                         isVirgin: --       | isVirgin: true
         *                         S/N: --               | S/N: 88*/
        Account account101 = new Account(800., tableCustomers.get(8));
        account101.getSecondaryHolders().add(tableCustomers.get(3)); // Adiciona titulares secundários
        account101.getCards().addAll(Arrays.asList(tableCards.get(7), tableCards.get(8)));// Adiciona cartões de débito e crédito
        account101.getMovements().addAll(Arrays.asList(tableMovements.get(3), tableMovements.get(4), tableMovements.get(5))); // Adiciona movimentos
        create(account101);

        /*Conta 102
         * Main Holder: John Doe - cartão de débito: SIM | Cartão de crédito: SIM (Plafond: 100 | Saldo plafond: 100)
         *                         isVirgin: falso       | isVirgin: falso
         *                         S/N: 82               | S/N: 83
         * Pode fazer saques: SIM
         * Titulares secundários:
         * João das Couves - cartão de débito: sim | Cartão de crédito: NÃO
         *                   isVirgin: true       | --
         *                           S/N: 85              | --
         * jane Doe - cartão de débito: SIM | Cartão de crédito: SIM (Plafond: 100 | Saldo plafond: 90)
         *                   isVirgin: true       | isVirgin: true
         *                           S/N: 80              | S/N: 81
         * Rosalvo Doe - cartão de débito: SIM | Cartão de crédito: NÃO
         *                   isVirgin: true       | --
         *                            S/N: 84              | --
         * Aang - cartão de débito: SIM | Cartão de crédito: NÃO
         *                   isVirgin: true       | --
         *                           S/N: 86              | -- */
        Account account102 = new Account(115., tableCustomers.get(1));
        account102.getSecondaryHolders().addAll(Arrays.asList(tableCustomers.get(0), tableCustomers.get(2), tableCustomers.get(3), tableCustomers.get(4)));
        account102.getCards().addAll(Arrays.asList(tableCards.get(0), tableCards.get(1), tableCards.get(2), tableCards.get(3), tableCards.get(4), tableCards.get(5), tableCards.get(6)));
        account102.getMovements().addAll(Arrays.asList(tableMovements.get(0), tableMovements.get(1), tableMovements.get(2)));
        create(account102);
    }
}
