

package at.campus02.dbp2.repository;




public class Application {

    public static void log(String msg) {
        System.out.println("Application:  --> " + msg);
    }

    public static void main(String[] args) {

        log("application started");

        //CustomerRepository repository=new JdbcRepository("jdbc:derby:database;create=true");
        CustomerRepository repository=new JpaRepository();
        //il name é database e lo mette nel sistema , se non esiste lo crea

        //CustomerRepository repository=new JpaRepository("jdbc:derby:database;create=true");
        Customer customer=new Customer();
        customer.setEmail("customer1@customer.com");
        customer.setLastname("Customer");
        customer.setFirstname("Luisa");

        Customer customer2=new Customer();
        customer2.setEmail("customer2@customer.com");
        customer2.setLastname("Customer");
        customer2.setFirstname("Luisa");

        //1)create
         repository.create(customer) ;
         log("customer created: "+ customer);
        repository.create(customer2) ;
        log("customer created: "+ customer);

         //2)read
        Customer fromRepository= repository.read(customer.getEmail());
        log("Customer read: " + fromRepository);

        //3) Update
        fromRepository.setFirstname("Maria");
        repository.update(fromRepository);
        Customer updated =repository.read(fromRepository.getEmail());
        log("Customer updated : "+updated);

       //4) Delete
        repository.delete(updated);
        Customer deleted = repository.read(updated.getEmail());
        log("deleted customer: "+ deleted); //soll null sein

    }
}
