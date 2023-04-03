package com.atm.bankConnect.service.implementations.list;

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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Contains all methods responsible for the businees rules related to accounts.
 */
public class AccountListServiceImplementation implements AccountService {
    private CustomerService customerServiceImplementation;
    private AccountRepository accountRepositoryImplementation;
    private CardService cardServiceImplementation;
    private MovementService movimentServiceImplementation;

    public AccountListServiceImplementation(CustomerService customerServiceImplementation, MovementService movementServiceImplementation, CardService cardServiceImplementation, AccountRepository accountRepositoryImplementation) {
        this.customerServiceImplementation = customerServiceImplementation;
        this.movimentServiceImplementation = movementServiceImplementation;
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
        account.getMovements().add(movimentServiceImplementation.create(account.getBalance(), MovementType.DEPOSIT)); // Adiciona movimento à conta

        return accountRepositoryImplementation.create(account);
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
        List<Customer> secondaryHolders = loggedAccount.getSecondaryHolders();
        if (secondaryHolders.stream().anyMatch(customerElement -> customerElement.getNif().equals(secondaryHolder.getNif())) || loggedAccount.getMainHolder().getNif().equals(secondaryHolder.getNif())) {
            return false;
        }
        secondaryHolders.add(secondaryHolder);
        accountRepositoryImplementation.update(loggedAccount);
        return true;
    }

    @Override
    public boolean deposit(Account destinationAccount, double depositValue, MovementType movementType) {
        destinationAccount.setBalance(destinationAccount.getBalance() + depositValue);
        destinationAccount.getMovements().add(movimentServiceImplementation.create(depositValue, movementType));

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
            ArrayList<Movement> withdraws = (ArrayList<Movement>) accountRepositoryImplementation.findAllSpecificMovements(MovementType.WITHDRAW, accountToBeDebited); // busca todos os movimentos do tipo sque feitos pelo cliente
            LocalDate today = LocalDate.now();
            double amountWithdrawToday = 0;
            for (Movement withdrawElement : withdraws) { // Percorrer a lista de movimentos, cujo tipo é saque
                if (withdrawElement.getDate().isEqual(today)) { // Se a data do sque for igual à data de hoje
                    amountWithdrawToday += withdrawElement.getValue(); // Incrementa do valor de saque diário
                }
            }
            if (amountWithdrawToday >= 500.) { // Se o valor do saque diário for maior ou igual a 500
                return ResponseType.WITHDRAW_OVERFLOW; // Não é possível realizar a operação
            }
        }
        if (accountToBeDebited.getBalance() < value) { // Se o saldo da conta for infasrior ao valor do saquue
            return ResponseType.INSUFFICIENT_BALANCE; // Não é possível realizar a operação
        }
        accountToBeDebited.setBalance(accountToBeDebited.getBalance() - value); // atualiza o saldo da conta
        accountToBeDebited.getMovements().add(movimentServiceImplementation.create(value, movementType)); // adiciona um movimento à conta do mesmo tipo passado por parâmetro (TRANSFER_OUT ou  WITHDRAW)
        return ResponseType.SUCCESS; // Operação realizada com sucesso
    }

    @Override
    public boolean deleteSecondaryHolder(Account loggedAccount, Customer secondaryHolder) {
        Card creditCardOwnedByCustomerToBeDeleted = null, debitCardOwnedByCustomerToBeDeleted = null;

        for (Card cardElement : accountRepositoryImplementation.findAllCreditCardsByAccount(loggedAccount)) { // busca todos os cartões de crédito da conta logada
            if (cardElement.getCardHolder().getNif().equals(secondaryHolder.getNif())) { // Se o nif do dono do cartão for igual no NIF do titular que dever ser removido
                creditCardOwnedByCustomerToBeDeleted = cardElement;
            }
        }

        if (creditCardOwnedByCustomerToBeDeleted != null && creditCardOwnedByCustomerToBeDeleted.getPlafondBalance() < creditCardOwnedByCustomerToBeDeleted.getMonthyPlafond()) { //Se esse titular secundário tiver cartão de crédito e não tiver dívida no mesmo
            return false;
        }

        for (Card cardElement : accountRepositoryImplementation.findAllDebitCardsByAccount(loggedAccount)) { // busca todos os cartões de débito da conta logada
            if (cardElement.getCardHolder().getNif().equals(secondaryHolder.getNif())) { // Se o nif do dono do cartão for igual no NIF do titular que dever ser removido
                debitCardOwnedByCustomerToBeDeleted = cardElement;
            }
        }

        loggedAccount.getSecondaryHolders().removeIf(customerElement -> customerElement.getNif().equals(secondaryHolder.getNif())); // Excluir o cliente secundário antes de verificar se ele existe em alguma outra conta
        loggedAccount.getCards().removeIf(cardElement -> cardElement.getCardHolder().getNif().equals(secondaryHolder.getNif())); // Excluir todos os cartões daquele cliente na conta logada

        accountRepositoryImplementation.update(loggedAccount); // atualiza a situação dessa conta na base de dados

        if (debitCardOwnedByCustomerToBeDeleted != null) {
            cardServiceImplementation.delete(debitCardOwnedByCustomerToBeDeleted); // deleta o cartão de débito da base de dados
        }

        if (creditCardOwnedByCustomerToBeDeleted != null) {
            cardServiceImplementation.delete(creditCardOwnedByCustomerToBeDeleted); // deleta o cartão de crédito da base de dados
        }

        int timesOfCustomerFound = 0;
        // Verificar se o cliente possui alguma outra conta no banco
        List<Account> allAccounts = accountRepositoryImplementation.findAll();
        for (Account accountElement : allAccounts) { // Percorrer a lista de contas
            if (accountElement.getMainHolder().getNif().equals(secondaryHolder.getNif())) { // Se encontrar o referido cliente como cliente principal de outra conta
                timesOfCustomerFound++; // acrescenta 1 ao contador
            }
            for (Customer customerElement : accountElement.getSecondaryHolders()) { // dentro do objeto conta, percorre a lista de clientes secundários
                if (customerElement.getNif().equals(secondaryHolder.getNif())) { // Se encontrar o referido cliente como cliente secundário de outra conta
                    timesOfCustomerFound++; // acrescenta 1 ao contador
                }
            }
        }
        // Só remove da lista principal se não achar em alguma outra conta
        if (timesOfCustomerFound == 0) {
            customerServiceImplementation.delete(secondaryHolder);
        }

        return true;
    }

    @Override
    public Card addDebitCard(Account loggedAccount, Customer cardHolder) {
        ArrayList<Card> debitCards = getDebitCards(loggedAccount);

        return getCard(cardHolder, debitCards, false, loggedAccount);
    }

    private Card getCard(Customer cardHolder, ArrayList<Card> cards, boolean isCreditCard, Account loggedAccount) {
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
        ArrayList<Card> creditCards = getCreditCards(loggedAccount);

        return getCard(cardHolder, creditCards, true, loggedAccount);
    }

    @Override
    public ArrayList<Card> getDebitCards(Account loggedAccount) {
        return (ArrayList<Card>) accountRepositoryImplementation.findAllDebitCardsByAccount(loggedAccount);
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
        return loggedAccount.getMainHolder().getNif().equals(customerToBeDeleted.getNif());
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
        // Used only on JDBC
        return false;
    }

    @Override
    public Customer getMainHolder(int loggedAccountId) {
        // Used only on JDBC
        return null;
    }

    @Override
    public List<Customer> getSecondaryHolders(int loggedAccountId) {
        // Used only on JDBC
        return null;
    }

    private boolean existsThisTypeCardForThisHolder(Customer cardHolder, ArrayList<Card> cards) {
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
        return loggedAccount.getSecondaryHolders().size();
    }

    @Override
    public int getAmountOfCreditCards(Account loggedAccount) {
        return accountRepositoryImplementation.findAllCreditCardsByAccount(loggedAccount).size();
    }

    @Override
    public int getAmountOfDebitCards(Account loggedAccount) {
        return accountRepositoryImplementation.findAllDebitCardsByAccount(loggedAccount).size();
    }

    @Override
    public ResponseType delete(Account accountToBeDeleted) {
        if (accountToBeDeleted.getCards().stream().filter(cardElement -> cardElement.getMonthyPlafond() > 0. && cardElement.getPlafondBalance() < cardElement.getMonthyPlafond()).collect(Collectors.toCollection(ArrayList::new)).size() > 0) { // na conta, percorre a lista de cartões e busca todos os cartões de crédito que tenham dívida, caso exista algum
            return ResponseType.THERE_ARE_DEBTS; // Há cartões de crédito em débito
        }
        if (accountToBeDeleted.getBalance() > 0.) { // Caso o saldo da conta não esteja zerado
            return ResponseType.BALANCE_BIGGER_THAN_ZERO;
        }

        List<Customer> allCustomersInAccountToBeDeleted = new ArrayList<>(accountToBeDeleted.getSecondaryHolders());
        allCustomersInAccountToBeDeleted.add(accountToBeDeleted.getMainHolder());

        accountRepositoryImplementation.delete(accountToBeDeleted);

        HashSet<Customer> customersThatAreInAnotherAccount = new HashSet<>();
        List<Account> allAccounts = accountRepositoryImplementation.findAll();

        for (Account anotherAccount : allAccounts) {
            for (Customer customerElement : allCustomersInAccountToBeDeleted) {
                if (anotherAccount.getMainHolder().getNif().equals(customerElement.getNif())) {
                    customersThatAreInAnotherAccount.add(customerElement);
                    break;
                }
                for (Customer secondaryHolderInAnotherAccount : anotherAccount.getSecondaryHolders()) {
                    if (secondaryHolderInAnotherAccount.getNif().equals(customerElement.getNif())) {
                        customersThatAreInAnotherAccount.add(customerElement);
                        break;
                    }
                }
            }
        }
        boolean hasAnotherAccount = false;
        for (Customer customerElement : allCustomersInAccountToBeDeleted) {
            for (Customer customerThatAreInAnotherAccount : customersThatAreInAnotherAccount) {
                if (customerElement.getNif().equals(customerThatAreInAnotherAccount.getNif())) {
                    hasAnotherAccount = true;
                }
            }
            if (!hasAnotherAccount) {
                customerServiceImplementation.delete(customerElement);
            }
            hasAnotherAccount = false;
        }

        return ResponseType.SUCCESS;
    }
}
