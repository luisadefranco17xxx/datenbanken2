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

    private Customer initDefaultCustomer() {
        Customer toCreate = new Customer();
        AccountType accountType = AccountType.BASIC;
        //LocalDate registeredSince = LocalDate.now();


        toCreate.setFirstname(firstname);
        toCreate.setLastname(lastname);
        toCreate.setAccountType(accountType);
        toCreate.setRegisteredSince(registeredSince);
        return  toCreate;
    }
    //#endregion / tear down

    //#region setup

    CustomerRepository repository;
    EntityManagerFactory factory;
    EntityManager manager;

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
        //EntityManagerFactory factory= Persistence.createEntityManagerFactory("persistenceUnitName");
        //CustomerRepository repository=new CustomerRepositoryJpa(factory);
        Customer toCreate=initDefaultCustomer();
        AccountType accountType = AccountType.BASIC;
        LocalDate registeredSince = LocalDate.now();

        /*toCreate.setFirstname(firstname);
        toCreate.setLastname(lastname);
        toCreate.setAccountType(accountType);
        toCreate.setRegisteredSince(registeredSince);*/
        //when
        boolean result=  repository.create(toCreate);
        //then
        assertTrue(result);
        //Kontrolle aus der Datenbank
        //EntityManager manager=factory.createEntityManager();   //con questo leggo veramente databanek
        //manager
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


        AccountType accountType = AccountType.BASIC;
        LocalDate registeredSince = LocalDate.now();
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
        String changeFirstNAme ="changeFist";
        String changedLastNAme="changedLast";
        AccountType changedAcc=AccountType.PREMIUM;
        LocalDate changedregisteredSince = LocalDate.of(2021,10,14);


        existing.setFirstname(changeFirstNAme);
        existing.setLastname(changedLastNAme);
        existing.setAccountType(changedAcc);
        existing.setRegisteredSince(registeredSince);
        Customer updated =repository.update(existing);
        //then
        assertEquals(changeFirstNAme,updated.getFirstname());
        assertEquals(changedLastNAme,updated.getLastname());
        assertEquals(changedAcc,updated.getAccountType());
        assertEquals(changedregisteredSince,updated.getRegisteredSince());

        //also check values form database
        //cleas cache to ensure reading from DB again
        manager.clear();
        Customer fromDb =manager.find(Customer.class,updated.getId());
        assertEquals(changeFirstNAme,fromDb.getFirstname());
        assertEquals(changedLastNAme,fromDb.getLastname());
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
