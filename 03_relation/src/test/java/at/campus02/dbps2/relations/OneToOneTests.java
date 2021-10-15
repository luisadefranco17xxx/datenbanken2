package at.campus02.dbps2.relations;

import at.campus02.dbp2.relations.Animal;
import at.campus02.dbp2.relations.Student;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class OneToOneTests {

    @Test
    public void justATest(){
        EntityManagerFactory factory= Persistence.createEntityManagerFactory("persistenceUnitName");
        EntityManager manager=factory.createEntityManager();
        Student student=new Student("Hansy");
        Animal animal = new Animal("Gatto");

        manager.getTransaction().begin();
        manager.persist(student);
        manager.getTransaction().commit();

        manager.getTransaction().begin();
        manager.persist(animal);
        manager.getTransaction().commit();
    }
}
