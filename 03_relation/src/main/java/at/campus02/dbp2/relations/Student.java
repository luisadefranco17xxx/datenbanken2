package at.campus02.dbp2.relations;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class Student {
    @Id
    @GeneratedValue
    private Integer id;
    private String name;

    @OneToOne (mappedBy="owner", cascade= CascadeType.PERSIST)  // mappedBy :   significa che voglio la colonna solo da animal   (corrisponde a folie)
    private Animal pet;                                         // cascade:     se scrivevo ALL allora a cacellare pet cancellava anche lo studente
                                                                 // con PERSIST cacella solo bestia
    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    //in Fall bon JPA va fefinito altrimenti non va
    public Student() {
    }

    public Student(String name) {
        this.name = name;
    }

    public Animal getPet() {
        return pet;
    }

    public void setPet(Animal pet) {
        this.pet = pet;
    }


    //qui confronto solo ID!!!!!
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return id.equals(student.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
