package com.atm.bankConnect.repository.implementations.list;

import com.atm.bankConnect.repository.interfaces.CustomerRepository;
import com.atm.bankConnect.model.Customer;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Database layer.<br>
 * <em>Implementation by <code>ArrayList</code></em>
 */
public class CustomerListRepositoryImplementation implements CustomerRepository {
    private List<Customer> tableCustomers = new ArrayList<>();
    private static int id = 1;

    public CustomerListRepositoryImplementation() {

    }

    /**
     * Adds the given <code>Customer</code> instance to the customers list
     *
     * @param customer instance to be added
     */
    @Override
    public Customer create(Customer customer) {
        customer.setId(++id);
        tableCustomers.add(customer);

        return customer;
    }

    /**
     * Finds and specific customer in database.
     *
     * @param nif the customer's identifier
     * @return <ul>
     * <li>the <code>Customer</code> that owns the NIF number</li>
     * <li>null if the NIF is filled with 0 and the operation is cancelled</li>
     * </ul>
     */
    @Override
    public Customer findByNif(String nif) {
        return tableCustomers.stream().filter(customerElement -> customerElement.getNif().equals(nif)).findFirst().orElse(null);
    }

    @Override
    public Customer findById(int id) {
        return null;
    }

    @Override
    public Customer update(Customer customer) {
        tableCustomers.set(tableCustomers.indexOf(customer), customer);

        return customer;
    }

    /**
     * Deletes a customer in database.
     *
     * @param customer the customer object instance to be deleted
     */
    @Override
    public void delete(Customer customer) {
        tableCustomers.removeIf(customerElement -> customerElement.getNif().equals(customer.getNif()));
    }

    /**
     * Finds all customers registered in the database.
     *
     * @return the whole customer's list
     */
    @Override
    public List<Customer> findAll() {
        return tableCustomers;
    }

    @Override
    public boolean verifyIfNifAlreadyExists(String nif) {
        return tableCustomers.stream().anyMatch(customerElement -> customerElement.getNif().equals(nif));
    }

    /**
     * Generates initial data to fill the customer HashSet that's serves as database.
     */
    public List<Customer> loadDatabase() {
        Customer customer1 = new Customer("987456321", "Jane Doe", "123456", "321654987", "99885544", "someone@email.com", "Lawyer", LocalDate.of(1983, 2, 24));
        create(customer1);
        Customer customer2 = new Customer("123456789", "John Doe", "654321", "321644481", "99221166", "anything@email.com", "Pilot", LocalDate.of(1973, 12, 12));
        create(customer2);
        Customer customer3 = new Customer("132456789", "Rosalvo Doe", "123654", "325554937", "99887766", "something@email.com", "Firefighter", LocalDate.of(1985, 8, 2));
        create(customer3);
        Customer customer4 = new Customer("369258147", "João das Couves", "526341", "222111333", "951951951", "cabbages@email.pt", "seller", LocalDate.of(1972, 1, 22));
        create(customer4);
        Customer customer5 = new Customer("145784244", "Aang", "540022", "250140320", "981258457", "air@avatar.com", "avatar", LocalDate.of(2005, 1, 22));
        create(customer5);
        Customer customer6 = new Customer("144774144", "Korra", "541166", "335478852", "999258741", "water@avatar.com", "avatar", LocalDate.of(2011, 1, 22));
        create(customer6);
        Customer customer7 = new Customer("245774144", "James Bond", "541166", "335478852", "999258741", "bond@007.com", "spy", LocalDate.of(1983, 7, 13));
        create(customer7);
        Customer customer8 = new Customer("145774144", "Gandalf", "541166", "335478852", "999258741", "mage@rings.com", "mage", LocalDate.of(1983, 7, 13));
        create(customer8);
        Customer customer9 = new Customer("345774144", "Momo", "541166", "335478852", "999258741", "momo@love.com", "muito linda de cachinho", LocalDate.of(1983, 2, 24));
        create(customer9);
        Customer customer10 = new Customer("245774144", "Tony Stark", "541166", "335478852", "999258741", "tony@marvel.com", "businessman", LocalDate.of(1923, 7, 15));
        create(customer10);

        return tableCustomers;
    }
}
