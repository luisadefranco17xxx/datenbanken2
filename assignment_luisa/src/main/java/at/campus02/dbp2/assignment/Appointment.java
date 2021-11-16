package at.campus02.dbp2.assignment;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
public class Appointment {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne //cancellare?? doppione qui sbagliato
    private Customer customer;
    @ManyToOne(cascade = CascadeType.MERGE)      //Paulina(prima manyToOne vuoto)
    private Provider provider;

    private LocalDateTime date;

    public Appointment() {
    }

    public Integer getId() {
        return id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Provider getProvider() {
        return provider;
    }

    public void setProvider(Provider provider) {
        this.provider = provider;
    }

    public LocalDateTime getTime() {
        return date;
    }

    public void setTime(LocalDateTime time) {
        this.date = time;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Appointment that = (Appointment) o;
        return Objects.equals(getId(), that.getId()) && Objects.equals(getTime(), that.getTime()) && Objects.equals(getCustomer(), that.getCustomer()) && Objects.equals(getProvider(), that.getProvider());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getTime(), getCustomer(), getProvider());
    }


}
