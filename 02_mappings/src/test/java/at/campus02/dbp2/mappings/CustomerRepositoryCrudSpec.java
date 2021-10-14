package at.campus02.dbp2.mappings;      //devo avere lo stesso package dove codice da testare

import at.campus02.dbp2.mappings.AccountType;
import at.campus02.dbp2.mappings.Customer;
import at.campus02.dbp2.mappings.CustomerRepository;
import at.campus02.dbp2.mappings.CustomerRepositoryJpa;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.time.LocalDate;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;


public class CustomerRepositoryCrudSpec {

    //#region test data
    private final String firstname = "Firstname";
    private final String lastname = "Lastname";
    private final LocalDate registeredSince = LocalDate.of(2021,10,1);
    private final AccountType accountType = AccountType.BASIC;

    private Customer initDefaultCustomer() {
        Customer customer = new Customer();
        customer.setFirstname(firstname);
        customer.setLastname(lastname);
        customer.setAccountType(accountType);
        customer.setRegisteredSince(registeredSince);
        //LocalDate registeredSince = LocalDate.now();
        return  customer;
    }
    //#endregion / tear down

    //#region setup

    private CustomerRepository repository;
    private EntityManagerFactory factory;
    private EntityManager manager;

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

    //#region CRUD
    @Test
    public void createNullAsCustomerReturnFalse(){
        //given
        boolean result=  repository.create(null);  //prima era null
        //then
        Assertions.assertFalse(result);
    }

    @Test
    public void createPersistCustomerInDatabaseUndReturnsTrue(){
        //given
        Customer toCreate=initDefaultCustomer();;

        //when
        boolean result=  repository.create(toCreate);

        //then
        assertTrue(result);
        //Kontrolle aus der Datenbank
        Customer fromDb = manager.find(Customer.class, toCreate.getId()); //appena oggetto toCreate é correttamente creato viene aggiunto automaticamente in ij!!!
        assertEquals(firstname,fromDb.getFirstname());
        assertEquals(lastname,fromDb.getLastname());
        assertEquals(accountType,fromDb.getAccountType());
        assertEquals(registeredSince,fromDb.getRegisteredSince());
     }

     @Test
    public void createExistingCustomerReturnsFalse(){
         //given

         Customer toCreate =initDefaultCustomer();

         manager.getTransaction().begin();;
         manager.persist(toCreate);
         manager.getTransaction().commit();

         //when
         boolean result=  repository.create(toCreate);   //lo metto per la seconda volta in Db
         //then
         Assertions.assertFalse(result);
     }

    //#endregion

    //#region CRUD: read
    @Test
    public void readFindsCustomerInDatabase(){
        Customer existing =initDefaultCustomer();

        manager.getTransaction().begin();;
        manager.persist(existing);
        manager.getTransaction().commit();

        //when
        Customer fromRepository=  repository.read(existing.getId());   //lo metto per la seconda volta in Db
        //then
        assertEquals(firstname,fromRepository.getFirstname());
        assertEquals(lastname,fromRepository.getLastname());
        assertEquals(accountType,fromRepository.getAccountType());
        assertEquals(registeredSince,fromRepository.getRegisteredSince());
    }

    @Test
    public void readWithNotExistingIdReturnsNull(){
        Customer fromRepository=repository.read(-1);
            Assertions.assertNull(fromRepository);
    }


    @Test
    public void readWithNullAsIdReturnsNull(){
        Customer fromRepository =repository.read(null);
        Assertions.assertNull(fromRepository);
    }
    //#end region

    //#region CRUD: update
    @Test
    public void updateChangesAttributesInDatabase(){
        Customer existing =initDefaultCustomer();

        manager.getTransaction().begin();;
        manager.persist(existing);
        manager.getTransaction().commit();
        String changedFirstName ="changedFistName";
        String changedLastName="changedLastName";
        AccountType changedAcc=AccountType.PREMIUM;
        LocalDate changedregisteredSince = LocalDate.of(2021,10,14);


        existing.setFirstname(changedFirstName);
        existing.setLastname(changedLastName);
        existing.setAccountType(changedAcc);
        existing.setRegisteredSince(changedregisteredSince);
        Customer updated =repository.update(existing);
        //then
        assertEquals(changedFirstName,updated.getFirstname());
        assertEquals(changedLastName,updated.getLastname());
        assertEquals(changedAcc,updated.getAccountType());
        assertEquals(changedregisteredSince,updated.getRegisteredSince());

        //also check values form database
        //cleas cache to ensure reading from DB again
        manager.clear();
        Customer fromDb =manager.find(Customer.class,updated.getId());
        assertEquals(changedFirstName,fromDb.getFirstname());
        assertEquals(changedLastName,fromDb.getLastname());
        assertEquals(changedAcc,fromDb.getAccountType());
        assertEquals(changedregisteredSince,fromDb.getRegisteredSince());

    }

    @Test
    public void updatedNotExistingCustomerThrowsIllegalArgumentExeption(){
        Customer notExisting=initDefaultCustomer();
        //When
        assertThrows(IllegalArgumentException.class, ()-> repository.update(notExisting));

    }

    @Test
    public void updateWithNullAsCustomerReturnNull(){
        //when
        Customer updated=repository.update(null);
        assertNull(updated);
    }
    //end region

    //#region CRUD: delete
    @Test
    public void deleteRemoveCutomerFromDatabaseAndReturndTrue(){
        Customer existing =initDefaultCustomer();

        manager.getTransaction().begin();;
        manager.persist(existing);
        manager.getTransaction().commit();

        //when
        boolean result= repository.delete(existing);
        assertTrue(result);
        manager.clear();
        Customer hopefullyDeleted= manager.find(Customer.class,existing.getId());
        assertNull(hopefullyDeleted);

    }

    @Test
    public void  deleteNotExixstingCustomerThrowsIllegalArgumentExeption(){
        Customer notExisting =initDefaultCustomer();
        //when
        assertThrows(IllegalArgumentException.class, ()->repository.delete((notExisting)));
    }

    @Test
    public void deleteNullAsCustomerReturnFalse(){

        boolean result= repository.delete(null);
        assertFalse(result);
    }
    //end region
}
