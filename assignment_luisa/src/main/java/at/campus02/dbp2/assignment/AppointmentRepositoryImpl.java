package at.campus02.dbp2.assignment;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class AppointmentRepositoryImpl implements  AppointmentRepository{

    private EntityManager manager;

    public AppointmentRepositoryImpl(EntityManagerFactory factory) {
        manager = factory.createEntityManager();
    }


    @Override
    public boolean create(Customer customer) {
        if (customer==null)  return false;
        if(customer.getEmail()==null)  return false;
        Customer customerFromDb=read(customer.getEmail());
        if(customerFromDb!=null) return false;
        manager.getTransaction().begin();
        manager.persist(customer);
        manager.getTransaction().commit();
        return true;
    }

    @Override
    public Customer read(String email) {
        if(email==null) return null;
        return manager.find(Customer.class, email);
    }

    @Override
    public Customer update(Customer customer) {
        if(customer == null)
            return null;
        if(customer.getEmail()==null) return  null;
        if(read(customer.getEmail()) == null) {
            throw new IllegalArgumentException("Customer does not exist, cannot update");
        }
        manager.getTransaction().begin();
        Customer managed= manager.merge(customer);
        manager.getTransaction().commit();

        return managed;
    }

    @Override
    public boolean delete(Customer customer) {
        if (customer==null || customer.getEmail()==null)  return false;
        if(read(customer.getEmail()) == null) {
            throw new IllegalArgumentException("Customer does not exist, cannot update");
        }
        manager.getTransaction().begin();
        manager.remove(manager.merge(customer));
        manager.getTransaction().commit();
        return true;
    }

    @Override
    public boolean create(Provider provider) {
        if (provider==null)  return false;
        if(provider.getId()==null)  return false;  //dazu
        Provider providerFromDb=read(provider.getId());
        if(providerFromDb!=null) return false;
        manager.getTransaction().begin();
        manager.persist(provider);
        manager.getTransaction().commit();
        return true;
    }

    @Override
    public Provider read(Integer id) {
        if(id==null) return null;
        return manager.find(Provider.class, id);
    }

    @Override
    public Provider update(Provider provider) {
        if(provider == null)
            return null;
        if(provider.getId()==null|| read(provider.getId()) == null) {
            throw new IllegalArgumentException("Provider does not exist, cannot update");
        }
        manager.getTransaction().begin();
        Provider managed= manager.merge(provider);
        manager.getTransaction().commit();

        return managed;
    }

    @Override
    public boolean delete(Provider provider) {
        if (provider==null || provider.getId()==null)  return false;
        if(provider.getId()==null|| read(provider.getId()) == null) {
            throw new IllegalArgumentException("Customer does not exist, cannot update");
        }
        manager.getTransaction().begin();
        manager.remove(manager.merge(provider));
        manager.getTransaction().commit();
        return true;
    }

    @Override
    public List<Customer> findCustomersBy(String lastname, String firstname) {
        if(lastname==null && firstname==null)  return getAllCustomers();
        if(lastname==null) {
           throw new IllegalArgumentException("Customer has not lastname, cannot find");
        }
        if(firstname==null) return findCustomersByLastname(lastname);
        else{
                TypedQuery<Customer> query = manager.createQuery(
                        "SELECT c FROM Customer c " +
                                "WHERE lower(c.lastname) LIKE lower(:LastName) " +
                                "and lower(c.firstname) LIKE lower(:FirstName) " +
                                "ORDER by c.lastname",
                        Customer.class
                );
                query.setParameter("LastName",lastname);
                query.setParameter("FirstName",firstname);
                return query.getResultList();
        }
    }

    public List<Customer> getAllCustomers() {
        TypedQuery<Customer> query = manager.createQuery(
                "SELECT c FROM Customer c " +
                        "ORDER BY c.lastname",
                Customer.class
        );
        return query.getResultList();
    }

    public List<Customer> findCustomersByLastname(String lastname) {
        if(lastname==null )  return getAllCustomers();
        if(lastname==null) {
            throw new IllegalArgumentException("Customer has not lastname, cannot find");
        }
        TypedQuery<Customer> query = manager.createQuery(
                "SELECT c FROM Customer c " +
                        "WHERE lower(c.lastname) LIKE lower(:LastName) " +
                        "ORDER by c.lastname",
                Customer.class
        );
        query.setParameter("LastName",lastname);
        return query.getResultList();
    }


    @Override
    public List<Provider> findProvidersBy(ProviderType type, String addressPart) {
        addressPart="%"+addressPart+"%";
        TypedQuery<Provider> query = manager.createQuery(
                "SELECT p FROM Provider p " +
                        "WHERE p.provider = :provider_type " +
                        "and p.address=: address_Part",
                Provider.class
        );
        query.setParameter("provider_type",type);
        query.setParameter("address_Part",addressPart);
        return query.getResultList();
    }

    @Override
    public List<Appointment> findAppointmentsAt(String addressPart) {
        addressPart="%"+addressPart+"%";
        TypedQuery<Appointment> query = manager.createQuery(
                "SELECT a FROM Appointment a " +
                        "WHERE lower(a.provider.address) LIKE lower(:address_Part) ",
                Appointment.class
        );
        query.setParameter("address_Part",addressPart);
        return query.getResultList();


    }

    @Override
    public List<Appointment> findAppointments(LocalDateTime from, LocalDateTime to) {
        LocalDateTime lowel;
        if (from==null){
             lowel= LocalDateTime.of(2000,1,1,0,0);
        }else {
            lowel=from;
        }
        //todo etwqa fur lower und upper
        LocalDateTime upper=LocalDateTime.of(3000,1,1,0,0);
        TypedQuery<Appointment> query = manager.createQuery(
                "SELECT a FROM Appointment a " +
                        "WHERE a.date between :time_from " +
                        "and :time_to " +
                        "ORDER by a.date",
                Appointment.class
        );
        query.setParameter("time_from",from);
        query.setParameter("time_to",to);
        return query.getResultList();
    }

    @Override
    public List<Appointment> getAppointmentsFor(Customer customer) {
        String customerName= customer.getLastname();
        TypedQuery<Appointment> query = manager.createQuery(
                "SELECT a FROM Appointment a " +
                        "WHERE lower(a.customer.lastname) like lower(:customer_name) " +
                        "ORDER by a.customer.lastname",
                Appointment.class
        );

        query.setParameter("customer_name",customerName);
        return query.getResultList();
    }

    @Override
    public boolean reserve(Appointment appointment, Customer customer) {
        return false;
    }

    @Override
    public boolean cancel(Appointment appointment, Customer customer) {
        return false;
    }

    @Override
    public void close() {

    }
}
