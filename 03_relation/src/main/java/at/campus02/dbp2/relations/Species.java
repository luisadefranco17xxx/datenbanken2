package at.campus02.dbp2.relations;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
public class Species {
    @Id
    @GeneratedValue
    private Integer id;
    private String name;
                                       //(mappedBy="specie", cascade=CascadeType.ALL, orphanRemoval =false)    prima era cosi // va cambiato per i vari test
    @OneToMany  (mappedBy="species", cascade=CascadeType.ALL, orphanRemoval =false)                   //= {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<Animal> animals=new ArrayList<>();

    public Species() {
    }

    public Species(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }


    public List<Animal> getAnimals() {
        return animals;
    }

    public void setAnimals(List<Animal> animals) {
        this.animals = animals;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Species species = (Species) o;
        return id.equals(species.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
