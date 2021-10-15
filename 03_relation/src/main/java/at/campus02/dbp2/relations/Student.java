package at.campus02.dbp2.relations;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Student {
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

    //in Fall bon JPA va fefinito altrimenti non va
    public Student() {
    }

    public Student(String name) {
        this.name = name;
    }
}
