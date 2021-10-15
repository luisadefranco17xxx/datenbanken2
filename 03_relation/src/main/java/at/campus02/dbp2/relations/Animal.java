package at.campus02.dbp2.relations;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Animal {
    @Id
    @GeneratedValue
     private Integer id;

    private String name;


    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
   //in JPA obligatorio fare contruttore
    public Animal() {
    }

    public Animal(String name) {
        this.name = name;
    }
}
