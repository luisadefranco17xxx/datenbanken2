package at.campus02.dbp2.mappings;      //devo avere lo stesso package dove codice da testare

import at.campus02.dbp2.mappings.AccountType;
import at.campus02.dbp2.mappings.Customer;
import at.campus02.dbp2.mappings.CustomerRepository;
import at.campus02.dbp2.mappings.CustomerRepositoryJpa;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.time.LocalDate;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class CustomerRepositoryCrudSpec {

    @Test
    public void createNullAsCustomerReturnFalse(){
        //given
        EntityManagerFactory factory= Persistence.createEntityManagerFactory("persistenceUnitName");
        CustomerRepository repository=new CustomerRepositoryJpa(factory);

        boolean result=  repository.create(null);  //prima era null
        //then
        Assertions.assertFalse(result);
    }

    @Test
    public void createPersistCustomerInDatabaseUndReturnsTrue(){
        //given
        EntityManagerFactory factory= Persistence.createEntityManagerFactory("persistenceUnitName");
        CustomerRepository repository=new CustomerRepositoryJpa(factory);
        Customer toCreate =new Customer();
        String firstname ="Firstname";
        String lastname ="Lastname";
        AccountType accountType = AccountType.BASIC;
        LocalDate registeredSince = LocalDate.now();

        toCreate.setFirstname(firstname);
        toCreate.setLastname(lastname);
        toCreate.setAccountType(accountType);
        toCreate.setRegisteredSince(registeredSince);
        //when
        boolean result=  repository.create(toCreate);
        //then
        assertTrue(result);
        //Kontrolle aus der Datenbank
        EntityManager manager=factory.createEntityManager();   //con questo leggo veramente databanek
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
         EntityManagerFactory factory= Persistence.createEntityManagerFactory("persistenceUnitName");
         CustomerRepository repository=new CustomerRepositoryJpa(factory);
         Customer toCreate =new Customer();
         String firstname ="Firstname";
         String lastname ="Lastname";
         AccountType accountType = AccountType.BASIC;
         LocalDate registeredSince = LocalDate.now();

         toCreate.setFirstname(firstname);
         toCreate.setLastname(lastname);
         toCreate.setAccountType(accountType);
         toCreate.setRegisteredSince(registeredSince);

         EntityManager manager=factory.createEntityManager();
         manager.getTransaction().begin();;
         manager.persist(toCreate);
         manager.getTransaction().commit();

         //when
         boolean result=  repository.create(toCreate);   //lo metto per la seconda volta in Db
         //then
         Assertions.assertFalse(result);
     }


}
