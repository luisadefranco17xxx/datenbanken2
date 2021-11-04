package at.campus02.dbp2.relations;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class Animal {
    @Id
    @GeneratedValue
     private Integer id;
    private String name;

    @OneToOne
    private Student owner;

    @ManyToOne
    private Species species;


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

    public Student getOwner() {
        return owner;
    }

    public void setOwner(Student owner) {
        this.owner = owner;
    }

    public Species getSpecies() {
        return species;
    }

    public void setSpecies(Species specie) {
        this.species = species;
    }

    //hashcode e equals non vanno implementati   //puo creare problemi di ricorisione



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Animal animal = (Animal) o;
        return id.equals(animal.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
