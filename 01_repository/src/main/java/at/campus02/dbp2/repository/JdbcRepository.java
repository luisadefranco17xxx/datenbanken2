

package at.campus02.dbp2.repository;

import java.sql.*;

public class JdbcRepository implements  CustomerRepository{

    private Connection connection;


    public JdbcRepository(String jdbcUrl) {
        try {
            connection = DriverManager.getConnection(jdbcUrl);
            ensureTable();  //erzeugen die tabelle wenn nicht existiert
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            throw  new IllegalStateException("no database connection: "+throwables);
        }
    }

    private void ensureTable() throws SQLException {
        //carichaimo tutte le tabelle che si chiamano CUSTOMER
        boolean tableExist=connection.getMetaData().getTables(null, null, "CUSTOMER", null).next();
        //next() serve per vedere se é veramente una tabella con qualcosa dentro
        if(!tableExist) {
            PreparedStatement statement = connection.prepareStatement(

                     "create table CUSTOMER (" +
                            "email varchar(50) primary key, " +
                            "lastname varchar(50), " +
                            "firstname varchar(50))"
            );
            statement.execute();
        };

    }

    @Override
    public void create(Customer customer) {
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "insert into CUSTOMER values (?,?,?)"
            );
            statement.setString(1, customer.getEmail());
            statement.setString(2, customer.getLastname());
            statement.setString(3, customer.getFirstname());
            statement.executeUpdate();
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
    }

    @Override
    public Customer read(String email) {
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "select * from CUSTOMER where EMAIL =?"
            );
            statement.setString(1, email);
            ResultSet rs=statement.executeQuery();
            if(rs.next()){
                Customer fromDB =new Customer();
                fromDB.setEmail(rs.getString(1));
                fromDB.setLastname(rs.getString(2));
                fromDB.setFirstname(rs.getString(3));
                return fromDB;
            } else  {
                return null;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Could not read customer",e);
        }
    }

    @Override
    public void update(Customer customer) {
        try {
            PreparedStatement statement=connection.prepareStatement(
                 "update CUSTOMER "+
                         "set lastname= ?, firstname=?"+
                         "where email=?"
                  );
            statement.setString(1, customer.getLastname());
            statement.setString(2, customer.getFirstname());
            statement.setString(3, customer.getEmail());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Could not update customer",e);
        }
    }

    @Override
    public void delete(Customer customer) {
        try {
            PreparedStatement statement=connection.prepareStatement(
                    "delete from CUSTOMER "+
                            "where email=?"
            );
            statement.setString(1, customer.getEmail());

            statement.execute();
        } catch (SQLException e) {
            throw new IllegalStateException("Could not delete the customer",e);
        }
    }
}
