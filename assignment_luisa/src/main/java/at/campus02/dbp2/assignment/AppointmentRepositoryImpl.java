package at.campus02.dbp2.assignment;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
        TypedQuery<Appointment> query = manager.createQuery(
                "update Appointment c set c.customer = null  " +
                        "where c.customer.email = :email ",
                       Appointment.class
                                      );

        query.setParameter("email",customer.getEmail());
        query.executeUpdate();
        manager.getTransaction().commit();
        manager.getTransaction().begin();
        //List<Appointment> resultList = query.getResultList();

        //customer.setAppointment(null);//errore    lúnico modo é fare la query
        //customer.getAppointment().setCustomer(null);// `???nessun migliorramnet
       // manager.merge(customer);//.setAppointment(null); //???
        manager.remove(manager.merge(customer));//causa una violazione edl foreign key: perche email é foreign key sulla tabella appointment!!!

        manager.getTransaction().commit();
        //manager.refresh(customer);  //new
        return true;
    }

    @Override
    public boolean create(Provider provider) {
        if (provider==null)  return false;

        Provider providerFromDb=read(provider.getId());

        for (Appointment appointment : provider.getAppointments()) {
            appointment.setProvider(provider);
        }

        if(providerFromDb!=null) {
           /* manager.getTransaction().begin();
            for (Appointment appointment : provider.getAppointments()) {
                appointment.setProvider(provider);
            }
            manager.merge(provider);
            manager.getTransaction().commit();*/
             return false;
        };

        manager.getTransaction().begin();
        for (Appointment appointment : provider.getAppointments()) {   //todo togliere perche cascade lo fa autom.
            appointment.setProvider(provider);
        }
        manager.persist(provider);
        manager.getTransaction().commit();

        manager.refresh(provider); //?
        return true;
    }

    @Override
    public Provider read(Integer id) {
        if(id==null) return null;
        return manager.find(Provider.class, id);
    }

  /*  @Override   //MAINE LOESUNG
    public Provider update(Provider provider) {
        if(provider == null)
            return null;
        if(provider.getId()==null|| read(provider.getId()) == null) {
            throw new IllegalArgumentException("Provider does not exist, cannot update");
        }
        manager.getTransaction().begin();                   //todo    qui bisogna cacellare gli appuntamenti doppamente presenti
        for (Appointment appointment : provider.getAppointments()) {
            appointment.setProvider(provider);
        }
        Provider managed= manager.merge(provider);
        manager.getTransaction().commit();
        manager.refresh(managed); //oder clear?
        //manager.clear();
        return managed;
    }  */

      @Override            //SOLUTIONE PAULINA!!!!!
     public Provider update(Provider provider) {
          if (provider == null) return null;
          if (provider.getId() == null || read(provider.getId()) == null)
             throw new IllegalArgumentException("Provider does not exist, cannot update!");
          manager.getTransaction().begin();
         for (int i = 0; i < provider.getAppointments().size(); i++) {
              for (int j = i + 1; j < provider.getAppointments().size(); j++) {
                  if (provider.getAppointments().get(i) == provider.getAppointments().get(j)) {
                      provider.getAppointments().remove(j);
                  }
              }
          }
          List<Appointment> appointmentList= provider.getAppointments();
          {
              for (int i = 0; i <appointmentList.size(); i++) {
                 // if (appointmentList.get(i).getProvider().getId()!=provider.getId()) {
                      appointmentList.get(i).setProvider(provider);
                      manager.merge(appointmentList.get(i));
                 // }
              }

          }



          Provider updated = manager.merge(provider);
          manager.getTransaction().commit();
          return updated;
      }



    @Override
    public boolean delete(Provider provider) {
        if (provider==null )  return false;
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
        //List<Provider> providers=new ArrayList<>();                      //TODO verbessern.....
        if (type==null || addressPart==null)  return  new ArrayList<>();
        //if (type==null  && addressPart==null)  return  getAllProviders();
        addressPart="%"+addressPart+"%";
        TypedQuery<Provider> query = manager.createQuery(
                "SELECT p FROM Provider p " +
                        "WHERE p.provider = :provider_type " +
                        "and lower(p.address) like lower(:address_Part)",
                Provider.class
        );
        query.setParameter("provider_type",type);
        query.setParameter("address_Part",addressPart);
        return query.getResultList();
    }

    public List<Provider> getAllProviders() {
        TypedQuery<Provider> query = manager.createQuery(
                "SELECT p FROM Provider p " +
                        "ORDER BY p.id",
                Provider.class
        );
        return query.getResultList();
    }

    @Override
    public List<Appointment> findAppointmentsAt(String addressPart) {
        List<Appointment> appointments=new ArrayList<>();               //TODO verbessern.....
        String nullString=null;
        if(addressPart == null)  return appointments;
        addressPart="%"+addressPart+"%";
        TypedQuery<Appointment> query = manager.createQuery(
                "SELECT a FROM Appointment a " +
                        "WHERE lower(a.provider.address) LIKE lower(:address_Part) " +
                       " and a.customer is null ",
                Appointment.class
        );
        query.setParameter("address_Part",addressPart);
        //query.setParameter("null_string",nullString);
        return query.getResultList();


    }

   /* @Override
    public List<Appointment> findAppointments(LocalDateTime from, LocalDateTime to) {
        LocalDateTime lowel,upper;
        if (from==null){
             lowel= LocalDateTime.of(2000,1,1,0,0);
        }else {
            lowel=from;
        }
        if (to==null){
             upper=LocalDateTime.of(3000,1,1,0,0);
        }else {
            upper=to;
        }
        TypedQuery<Appointment> query = manager.createQuery(
                "SELECT a FROM Appointment a " +
                        "WHERE a.date between :time_from " +
                        "and :time_to " +
                        "and a.customer.email is null " +
                        "ORDER by a.date",
                Appointment.class
        );
        query.setParameter("time_from",lowel);
        query.setParameter("time_to",upper );
        return query.getResultList();
    }*/

//paulina
@Override
public List<Appointment> findAppointments(LocalDateTime from, LocalDateTime to) {
    LocalDateTime startCentury = LocalDateTime.of(2000, 1, 1, 0, 0);
    LocalDateTime endCentury = LocalDateTime.of(3000, 1, 1, 0, 0);
    //Named Query
    if (from == null && to == null) {            //TOGLIERE

          TypedQuery<Appointment> query = manager.createQuery(
                "select a from Appointment a " +
                        "where a.customer is null " +
                        "and a.date > :startCentury " +
                        "and a.date < :endCentury ",
                Appointment.class
        );
        query.setParameter("startCentury", startCentury).setParameter("endCentury", endCentury);
        return query.getResultList();
    }
    if (from == null) {
           TypedQuery<Appointment> query = manager.createQuery(
                "select a from Appointment a " +
                        "where a.customer is null " +
                        "and a.date > :startCentury " +
                        "and a.date < :to ",
                Appointment.class
        );
        query.setParameter("startCentury", startCentury).setParameter("to", to);
        return query.getResultList();
    }

    if (to == null) {
        TypedQuery<Appointment> query = manager.createQuery(
                "select a from Appointment a " +
                        "where a.customer is null " +
                        "and a.date > :from " +
                        "and a.date < :endCentury ",
                Appointment.class
        );
        query.setParameter("from", from).setParameter("endCentury", endCentury);
        return query.getResultList();
    }
    TypedQuery<Appointment> query = manager.createQuery(
            "select a from Appointment a " +
                    "where a.customer is null " +
                    "and a.date > :from " +
                    "and a.date < :to ",
            Appointment.class
    );
    query.setParameter("from", from).setParameter("to", to);
    return query.getResultList();
}
    //
/*
    @Override
    public List<Appointment> findAppointments(LocalDateTime from, LocalDateTime to) {
        LocalDateTime lowel,upper;
        if (from==null){
            lowel= LocalDateTime.of(2000,1,1,0,0);
        }else {
            lowel=from;
        }
        if (to==null){
            upper=LocalDateTime.of(3000,1,1,0,0);
        }else {
            upper=to;
        }
        TypedQuery<Appointment> query = manager.createQuery(
                "SELECT a FROM Appointment a " +
                        "WHERE a.customer = '' " +               //NON FUNZIONA
                        " and a.date between :time_from and :time_to ",
                       //"where a.customer.email is NULL or  a.customer.email = '' ",
                Appointment.class
        );
        query.setParameter("time_from",lowel);
        query.setParameter("time_to",upper );
        return query.getResultList();
    }
*/
    @Override
    public List<Appointment> getAppointmentsFor(Customer customer) {
        List<Appointment> appointments;// =new ArrayList<>();
        if(customer==null) return  new ArrayList<>();
        if(read(customer.getEmail())==null)  return  new ArrayList<>();
        String customerName= customer.getLastname();
        TypedQuery<Appointment> query = manager.createQuery(
                "SELECT a FROM Appointment a " +
                        "WHERE a.customer = :customer " +
                        "ORDER by a.customer.lastname",
                Appointment.class
        );

        query.setParameter("customer",customer);
        appointments= query.getResultList();
        return appointments;
    }

    @Override
    public boolean reserve(Appointment appointment, Customer customer) {
        if (customer == null || customer.getEmail() == null || appointment == null) return false;
        if (read(customer.getEmail()) == null ) return false;

        /*List<Appointment> appointments = findAppointments(null,null);  //solo lista per provider, non per tutti i provider
        boolean foundAppoint=false;
        for (Appointment a : appointments) {
            while(!foundAppoint) {
                if (a.getId() == appointment.getId() || !foundAppoint) {
                    if (a.getCustomer().getEmail() != customer.getEmail()) {
                        foundAppoint = true;
                    }
                }
            }
        }*/
        TypedQuery<Appointment> query = manager.createQuery(            //CambiaRE   i nomi
                "select c from Appointment c " +
                        "where c.id = :id ",
                Appointment.class
        );
        query.setParameter("id", appointment.getId());
        List<Appointment> appointments = query.getResultList();

        if (appointments.isEmpty()) {
            return false;
        }
        if (appointment.getCustomer() != null) {
            return false;
        } else {
            manager.getTransaction().begin();
            appointment.setCustomer(customer);
            manager.merge(appointment);
            manager.getTransaction().commit();
           // manager.refresh(managed);
            return true;
        } //else return false;
    }

    @Override
    public boolean cancel(Appointment appointment, Customer customer) {

        if (  customer==null || customer.getEmail()==null ||
            appointment == null  || appointment.getCustomer()!=customer) {
            return false;
        }
        if (appointment.getCustomer()==null) {    //questo non ho capito
            return false;
        }
           manager.getTransaction().begin(); //??
                    TypedQuery<Appointment> query = manager.createQuery(            //CambiaRE   i nomi
                "select c from Appointment c " +
                        "where c.id = :id ",
                            Appointment.class
                 );
             query.setParameter("id", appointment.getId());
              List<Appointment> appointments = query.getResultList();

           if(!appointments.isEmpty()){
              // manager.getTransaction().begin();
               appointment.setCustomer(null);

              // customer.setAppointment(null);
               manager.merge(appointment);
               manager.getTransaction().commit();

           //}


           // manager.getTransaction().begin();
            //customer.setAppointment(null);
            //manager.merge(appointment);
            //manager.getTransaction().commit();
            return true;
        }
           return false;

        }

    @Override
    public void close() {
         if(manager!=null && manager.isOpen()){
            manager.close();
        }
    }

}

