package at.campus02.dbp2.mappings;

import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.collection.IsIterableContainingInOrder;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;


import static org.hamcrest.MatcherAssert.assertThat;

public class CustomerRepositoryQuerySpec {
    private EntityManagerFactory factory;
    private EntityManager manager;
    private CustomerRepository repository;

    private Customer customer1;
    private Customer customer2;
    private Customer customer3;
    private Customer customer4;
    private Customer customer5;
    private Customer customer6;
    private Customer customer7;

private void setupCommonTestDate(){
    customer1 = new Customer();
    customer1.setFirstname("Albert");
    customer1.setLastname("Aarhus");
    customer1.setAccountType(AccountType.BASIC);
    customer1.setRegisteredSince(LocalDate.of(2021, 1, 1));

    customer2 = new Customer();
    customer2.setFirstname("Bernadette");
    customer2.setLastname("Brandtner");
    customer2.setAccountType(AccountType.PREMIUM);
    customer2.setRegisteredSince(LocalDate.of(2021, 2, 2));

    customer3 = new Customer();
    customer3.setFirstname("Charlie");
    customer3.setLastname("Chandler");
    customer3.setAccountType(AccountType.PREMIUM);
    customer3.setRegisteredSince(LocalDate.of(2021, 3, 3));

    customer4 = new Customer();
    customer4.setFirstname("Dorli");
    customer4.setLastname("Dornacher");
    customer4.setAccountType(AccountType.BASIC);
    customer4.setRegisteredSince(LocalDate.of(2021, 4, 4));

    customer5 = new Customer();
    customer5.setFirstname("Emil");
    customer5.setLastname("Eberhard");
    customer5.setAccountType(AccountType.PREMIUM);
    customer5.setRegisteredSince(LocalDate.of(2021, 5, 5));

    customer6 = new Customer();
    customer6.setFirstname("Charlotte");
    customer6.setLastname("Eberstolz");
    customer6.setAccountType(AccountType.BASIC);
    customer6.setRegisteredSince(LocalDate.of(2021, 6, 6));

    customer7 = new Customer();
    customer7.setFirstname("Bernhards");
    customer7.setLastname("Hornbacher");
    customer7.setAccountType(AccountType.BASIC);
    customer7.setRegisteredSince(LocalDate.of(2021, 7, 7));


    manager.getTransaction().begin();
    manager.persist(customer5);
    manager.persist(customer4);
    manager.persist(customer1);
    manager.persist(customer2);
    manager.persist(customer3);
    manager.persist(customer7);
    manager.persist(customer6);
    manager.getTransaction().commit();
}

    @BeforeEach   //questa annotazione viene eseguita alínizio di ogni tests
    public  void beforeEach(){
        factory= Persistence.createEntityManagerFactory("persistenceUnitName");
        repository=new CustomerRepositoryJpa(factory);
        manager=factory.createEntityManager();
    }
    @AfterEach
    public void afterEach(){
        if(manager.isOpen())
            manager.close();
        if (factory.isOpen())
            factory.close();
    }

    @Test
    public void returnAllCustomersFromIdSortedByRegistrationDay(){
        //given
        setupCommonTestDate();
        //when
        List<Customer> sortedCustomer =repository.getAllCustomers();

        //then
        /*Assertions.assertIterableEquals(
                Arrays.asList(customer1,customer2,customer3,customer4,customer5,customer6,customer7),
                sortedCustomer                         //confronta le due liste prendendo gli elemente a copppia uno da sopra e uno da sotto
        );*/
        assertThat(sortedCustomer, IsIterableContainingInOrder.contains(customer1, customer2, customer3, customer4, customer5, customer6, customer7));
              //InAnyOrder  é l´equivalente se non mi interessa ordine
}

    @Test
    public void  getAllCustomerOnEmptyDatabasReturnsEmptyList(){
        List<Customer> sortedCustomer =repository.getAllCustomers();
        Assertions.assertTrue(sortedCustomer.isEmpty());


        assertThat(sortedCustomer, CoreMatchers.is(Matchers.empty()));
    }

    @Test
    public void findByAccountTypeReturnsMatchingCustomers(){
        setupCommonTestDate();
        /*List<Customer> basic =repository.findByAccountType(AccountType.BASIC);
        List<Customer> premium=repository.findByAccountType(AccountType.PREMIUM);

        List<Customer>  expectedBasic =Arrays.asList(customer1,customer4,customer5,customer6,customer7);
        List<Customer>  expectedPremium =Arrays.asList(customer2,customer3);

        Assertions.assertTrue(expectedBasic.size()== basic.size()
                    && expectedBasic.containsAll(basic)
                    && basic.containsAll(expectedBasic));

        Assertions.assertTrue(expectedPremium.size()== premium.size()
                    && expectedPremium.containsAll(premium)
                    && premium.containsAll(expectedPremium));

        assertThat(basic,  Matchers.containsInAnyOrder(customer1,customer4,customer5,customer7));
*/

        List<Customer> basic = repository.findByAccountType(AccountType.BASIC);
        List<Customer> premium = repository.findByAccountType(AccountType.PREMIUM);

        // then
        assertThat(basic, Matchers.containsInAnyOrder(customer1, customer4, customer6, customer7));
        assertThat(premium, Matchers.containsInAnyOrder(customer2, customer3, customer5));
    }


    @Test
    public void findByAccountTypeNullReturnsEmptyList(){

        setupCommonTestDate();
        List<Customer> result=repository.findByAccountType(null);
        MatcherAssert.assertThat(result, Is.is(Matchers.empty()));

    }
    //region Query
    @Test
    public void findByLastnameReturnsCaseSensitiveMatchingCustomers(){
        setupCommonTestDate();
        List<Customer> matching =repository.findByLastnamePart("orn");

        assertThat(matching, Matchers.contains(customer4,customer7));

    }

    @Test
    public void findByLastnameReturnsCaseInsensitiveMatchingCustomers(){
        setupCommonTestDate();
        List<Customer> matching =repository.findByLastname("eBEr");

        assertThat(matching, Matchers.contains(customer5,customer6));

    }

    @Test
    public  void  WithNullOrEmptyStringReturnsEmptyList(){
        setupCommonTestDate();
        List<Customer> matching =repository.findByLastname("");
        assertThat(matching, Is.is(Matchers.empty()));
        matching =repository.findByLastname(null);
        assertThat(matching, Is.is(Matchers.empty()));
    }


    //#region Query: findAllRegisteredAfter

    @Test
    public void findAllRegistrdAfrter(){
        /*setupCommonTestDate();
        List<Customer> matching =repository.findAllRegisteredAfter(LocalDate.of(2021,4,4));
        assertThat(matching,Matchers.containsInAnyOrder(customer4,customer5,customer6,customer7));

        matching =repository.findAllRegisteredAfter(LocalDate.of(2021,4,4));
        assertThat(matching,Matchers.containsInAnyOrder(customer5,customer6,customer7));
        */
        // given
        setupCommonTestDate();

        // when
        List<Customer> matching = repository.findAllRegisteredAfter(LocalDate.of(2021, 4, 4));

        // then
        assertThat(matching, Matchers.containsInAnyOrder(customer5, customer6, customer7));

        // and when
        matching = repository.findAllRegisteredAfter(LocalDate.of(2021, 4, 3));

        // then
        assertThat(matching, Matchers.containsInAnyOrder(customer4, customer5, customer6, customer7));


    }
    //#end region
}
