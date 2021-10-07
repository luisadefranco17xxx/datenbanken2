package at.campus02.dbp2.repository;

import java.util.HashMap;
import java.util.Map;

public class InMemoryRepository implements  CustomerRepository{

    private final static Map<String, Customer> storage= new HashMap<>();
    // static se vogliama usarla in tanti non va bene (allora solo oprivat)



    @Override
    public void create(Customer customer) {
        storage.put(customer.getEmail(), customer);
    }

    @Override
    public Customer read(String email) {
        Customer customer=storage.get(email);
        return customer;
    }

    @Override
    public void update(Customer customer) {
       storage.replace(customer.getEmail(), customer);
    }

    @Override
    public void delete(Customer customer) {
       storage.remove(customer.getEmail());
    }
}
