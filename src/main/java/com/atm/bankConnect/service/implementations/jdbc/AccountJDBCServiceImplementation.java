package com.atm.bankConnect.service.implementations.jdbc;

import com.atm.bankConnect.enums.MovementType;
import com.atm.bankConnect.enums.ResponseType;
import com.atm.bankConnect.model.Account;
import com.atm.bankConnect.model.Card;
import com.atm.bankConnect.model.Customer;
import com.atm.bankConnect.model.Movement;
import com.atm.bankConnect.repository.interfaces.AccountRepository;
import com.atm.bankConnect.service.interfaces.AccountService;
import com.atm.bankConnect.service.interfaces.CardService;
import com.atm.bankConnect.service.interfaces.CustomerService;
import com.atm.bankConnect.service.interfaces.MovementService;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains all methods responsible for the businees rules related to accounts.
 */
public class AccountJDBCServiceImplementation implements AccountService {
    private CustomerService customerServiceImplementation;
    private AccountRepository accountRepositoryImplementation;
    private CardService cardServiceImplementation;
    private MovementService movementServiceImplementation;

    public AccountJDBCServiceImplementation(CustomerService customerServiceImplementation, MovementService movementServiceImplementation, CardService cardServiceImplementation, AccountRepository accountRepositoryImplementation) {
        this.customerServiceImplementation = customerServiceImplementation;
        this.movementServiceImplementation = movementServiceImplementation;
        this.cardServiceImplementation = cardServiceImplementation;
        this.accountRepositoryImplementation = accountRepositoryImplementation;
    }

    /**
     * Creates a new account.
     *
     * @return the new account created
     */
    @Override
    public Account create(Account account) {
        Account newAccount = accountRepositoryImplementation.create(account);
        newAccount.setMainHolder(account.getMainHolder());

        movementServiceImplementation.create(account.getBalance(), MovementType.DEPOSIT, newAccount);

        return newAccount;
    }

    /**
     * Allows to do several activities and transactions for the logged account.
     */
    @Override
    public Account update(Account account) {
        return accountRepositoryImplementation.create(account);
    }

    /**
     * Verifies if the first deposit into account has a mininum value of 50.
     *
     * @param depositValue value to be deposited
     * @return <ul>
     * <li>true if the <span style="color:#ffb86c; font-style: italic">depositValue</span> is 50 or superior</li>
     * <li>false if the <span style="color:#ffb86c; font-style: italic">depositValue</span> is minor than 50</li>
     * </ul>
     */
    public boolean validateInitialDeposit(double depositValue) {
        return depositValue >= 50.;
    }

    @Override
    public Account getByCode(String code) {
        return accountRepositoryImplementation.findByCode(code);
    }

    @Override
    public boolean addSecondaryHolder(Account loggedAccount, Customer secondaryHolder) {

        if (accountRepositoryImplementation.verifyIfCustomerExistsInLoggedAccount(secondaryHolder.getId(), loggedAccount.getId())) {
            return false;
        }

        accountRepositoryImplementation.addSecondaryHolder(secondaryHolder.getId(), loggedAccount.getId());
        return true;
    }

    @Override
    public boolean deposit(Account destinationAccount, double depositValue, MovementType movementType) {
        destinationAccount.setBalance(destinationAccount.getBalance() + depositValue);
        Movement movement = movementServiceImplementation.create(depositValue, movementType, destinationAccount);
        destinationAccount.getMovements().add(movement);

        return accountRepositoryImplementation.update(destinationAccount) != null;
    }

    @Override
    public ResponseType transfer(Account originAccount, double value, String destinationAccountCode) {
        Account destinationAccount = getByCode(destinationAccountCode); // Procura a conta de destino
        if (destinationAccount == null) { // Se não encontrar
            return ResponseType.INEXISTENT;
        }
        ResponseType responseTypeWithdraw = withdraw(value, originAccount, MovementType.TRANSFER_OUT); // Faz o saque e recebe a resposta

        if (responseTypeWithdraw.equals(ResponseType.SUCCESS)) { // Se o retorno do método de saque (com o valor determinado e o tipo de movimento configurado como transferência de saída, que em tese, é a mesma coisa que o saque) for do tipo enum SUCCESS
            deposit(destinationAccount, value, MovementType.TRANSFER_IN);// Então faz o depósito na conta a ser pesquisada
        }
        return responseTypeWithdraw; // Só pode ser SUCCESS ou INSUFFICIENT_BALANCE
    }

    @Override
    public ResponseType withdraw(double value, Account accountToBeDebited, MovementType movementType) {
        if (movementType.equals(MovementType.WITHDRAW)) { // Se o tipo de movimentação for saque (pode ser TRANFER_OUT ou WITHDRAW)
            double amountWithdrawToday = movementServiceImplementation.getSumAllTodayWithdrawMovements(accountToBeDebited.getId());

            if (amountWithdrawToday >= 500.) { // Se o valor do saque diário for maior ou igual a 500
                return ResponseType.WITHDRAW_OVERFLOW; // Não é possível realizar a operação
            }
        }
        if (accountToBeDebited.getBalance() < value) { // Se o saldo da conta for infasrior ao valor do saquue
            return ResponseType.INSUFFICIENT_BALANCE; // Não é possível realizar a operação
        }
        accountToBeDebited.setBalance(accountToBeDebited.getBalance() - value); // atualiza o saldo da conta no objeto
        accountRepositoryImplementation.update(accountToBeDebited); // envia o objeto modificado para a base de dados para ser atualizado
        accountToBeDebited.getMovements().add(movementServiceImplementation.create(value, movementType, accountToBeDebited)); // adiciona um movimento à conta do mesmo tipo passado por parâmetro (TRANSFER_OUT ou  WITHDRAW)
        return ResponseType.SUCCESS; // Operação realizada com sucesso
    }

    @Override
    public boolean deleteSecondaryHolder(Account loggedAccount, Customer secondaryHolder) {
        // Passo 1: verificar se o cliente tem cartões de crédito, se tiver, se ao menos um deles tiver dívida. Caso não não tenha, o método retorna true e os cartões são deletados.
        if (Boolean.FALSE.equals(cardServiceImplementation.deleteAllByAccountIdAndCustomerId(loggedAccount.getId(), secondaryHolder.getId()))) {
            return false;
        }
        // Passo 2: remove o registro de customer id e account id da tabela customers_accounts
        accountRepositoryImplementation.deleteSecondaryHolder(loggedAccount.getId(), secondaryHolder.getId());

        // Passo 3: Verifica se este cliente é titular em outra conta
        if (Boolean.FALSE.equals(accountRepositoryImplementation.verifyIfCustomerExistsInAnotherAccount(secondaryHolder.getId()))) {
            // Se não for, deleta-o da tabela de customers
            customerServiceImplementation.delete(secondaryHolder);
        }
        return true;
    }

    @Override
    public Card addDebitCard(Account loggedAccount, Customer cardHolder) {
        List<Card> debitCards = getDebitCards(loggedAccount);

        return getCard(cardHolder, debitCards, false, loggedAccount);
    }

    private Card getCard(Customer cardHolder, List<Card> cards, boolean isCreditCard, Account loggedAccount) {
        if (existsThisTypeCardForThisHolder(cardHolder, cards)) {

            return null;
        } else {
            Card card = cardServiceImplementation.create(cardHolder, isCreditCard);
            loggedAccount.getCards().add(card);
            accountRepositoryImplementation.update(loggedAccount);

            return card;
        }
    }

    @Override
    public Card addCreditCard(Account loggedAccount, Customer cardHolder) {
        List<Card> creditCards = getCreditCards(loggedAccount);

        return getCard(cardHolder, creditCards, true, loggedAccount);
    }

    @Override
    public List<Card> getDebitCards(Account loggedAccount) {
        return accountRepositoryImplementation.findAllDebitCardsByAccount(loggedAccount);
    }

    @Override
    public ArrayList<Card> getCreditCards(Account loggedAccount) {
        return (ArrayList<Card>) accountRepositoryImplementation.findAllCreditCardsByAccount(loggedAccount);
    }

    @Override
    public Customer getCustomerByNif(String nif, Account loggedAccount) {
        Customer customer;
        if (loggedAccount.getMainHolder().getNif().equals(nif)) {
            customer = loggedAccount.getMainHolder();
        } else {
            customer = loggedAccount.getSecondaryHolders().stream().filter(customerElement -> customerElement.getNif().equals(nif)).findFirst().orElse(null);
        }

        return customer;
    }

    @Override
    public Boolean isMainHolder(Customer customerToBeDeleted, Account loggedAccount) {
        return accountRepositoryImplementation.verifyIfCustomerIsMainHolder(customerToBeDeleted.getId(), loggedAccount.getId());
    }

    @Override
    public Card getCardBySerialNumberOnCurrentAccount(Account loggedAccount, String cardSerialNumber) {
        return loggedAccount.getCards().stream().filter(cardElement -> cardElement.getSerialNumber().equals(cardSerialNumber)).findFirst().orElse(null);
    }

    @Override
    public Account getAccountByCardSerialNumber(String cardSerialNumber) {
        return accountRepositoryImplementation.findByCardSerialNumber(cardSerialNumber);
    }

    @Override
    public Boolean verifyIfCustomerExistsInLoggedAccount(int customerId, int loggedAccountId) {
        return accountRepositoryImplementation.verifyIfCustomerExistsInLoggedAccount(customerId, loggedAccountId);
    }

    @Override
    public Customer getMainHolder(int loggedAccountId) {
        return accountRepositoryImplementation.getMainHolder(loggedAccountId);
    }

    @Override
    public List<Customer> getSecondaryHolders(int loggedAccountId) {
        return accountRepositoryImplementation.getSecondaryHolders(loggedAccountId);
    }

    private boolean existsThisTypeCardForThisHolder(Customer cardHolder, List<Card> cards) {
        boolean exists = false;
        if (cards.size() > 0) { // Se a conta já tiver o tipo de cartão.
            for (Card cardElement : cards) { // Ver se quem pediu este tipo de cartão já tem um
                if (cardElement.getCardHolder().getNif().equals(cardHolder.getNif())) { // se tiver
                    exists = true;
                    break;
                }
            }
        }

        return exists;
    }

    @Override
    public void loadDatabase(List<Customer> customers, List<Card> cards, List<Movement> movements) {
        accountRepositoryImplementation.loadDatabase((ArrayList<Customer>) customers, (ArrayList<Card>) cards, (ArrayList<Movement>) movements);
    }

    @Override
    public int getAmountOfSecondaryHolders(Account loggedAccount) {
        return accountRepositoryImplementation.findAmountOfSecondaryHolders(loggedAccount.getId());
    }

    @Override
    public int getAmountOfDebitCards(Account loggedAccount) {
        // used only on Lists
        return 0;
    }

    @Override
    public int getAmountOfCreditCards(Account loggedAccount) {
        return accountRepositoryImplementation.findAllCreditCardsByAccount(loggedAccount).size();
    }

    @Override
    public ResponseType delete(Account accountToBeDeleted) {
        if (accountToBeDeleted.getBalance() > 0.) { // Caso o saldo da conta não esteja zerado
            return ResponseType.BALANCE_BIGGER_THAN_ZERO;
        }

        if (Boolean.TRUE.equals(cardServiceImplementation.verifyIfExistsCardsInDebtByAccount(accountToBeDeleted.getId()))) { // na conta, percorre a lista de cartões e busca todos os cartões de crédito que tenham dívida, caso exista algum
            return ResponseType.THERE_ARE_DEBTS; // Há cartões de crédito em débito
        }

        List<Customer> allCustomersInAccountToBeDeleted = new ArrayList<>(accountRepositoryImplementation.getSecondaryHolders(accountToBeDeleted.getId()));
        allCustomersInAccountToBeDeleted.add(accountRepositoryImplementation.getMainHolder(accountToBeDeleted.getId()));

        accountRepositoryImplementation.deleteSecondaryHolders(accountToBeDeleted.getId()); // apaga todos os titulares secundários da conta
        movementServiceImplementation.deleteAll(accountToBeDeleted.getId()); // apaga todos os movimentos da conta
        cardServiceImplementation.deleteAllByAccount(accountToBeDeleted.getId()); // apaga todos os cartões vinculados àquela conta

        accountRepositoryImplementation.delete(accountToBeDeleted.getId()); // apaga a conta

        allCustomersInAccountToBeDeleted.forEach(customerElement -> {
            if (Boolean.FALSE.equals(accountRepositoryImplementation.verifyIfCustomerExistsInAnotherAccount(customerElement.getId()))) {
                customerServiceImplementation.delete(customerElement);
            }
        });

        return ResponseType.SUCCESS;
    }
}
