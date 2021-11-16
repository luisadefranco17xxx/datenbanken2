package at.campus02.dbp2.assignment;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
public class Provider {

    @Id
    @GeneratedValue
    private Integer id;
    @Column(nullable = false) //??
    private ProviderType provider;
    private String address;
    //@OneToMany(mappedBy="provider", cascade= {CascadeType.PERSIST,CascadeType.MERGE ,CascadeType.MERGE, CascadeType.REMOVE}, orphanRemoval =false)      //cascade all by delete(se muore provider sono cancellati appuntmenti)
   // @OneToMany(mappedBy="provider", cascade=CascadeType.ALL, orphanRemoval =false)
    @OneToMany(mappedBy="provider", cascade=CascadeType.PERSIST, orphanRemoval =true)
    private final List<Appointment> appointments=new ArrayList<>();

    public Provider() {
    }

    public Integer getId() {
        return id;
    }

    public ProviderType getType() {
        return provider;
    }

    public void setType(ProviderType type) {
        this.provider=type;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address=address;
    }

    public List<Appointment> getAppointments() {

        return appointments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Provider provider = (Provider) o;
        return Objects.equals(getId(), provider.getId()) && getType() == provider.getType() && Objects.equals(getAddress(), provider.getAddress());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getType(), getAddress());
    }
}
