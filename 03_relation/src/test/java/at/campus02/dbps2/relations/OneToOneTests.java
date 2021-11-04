package at.campus02.dbps2.relations;

import at.campus02.dbp2.relations.Animal;
import at.campus02.dbp2.relations.Species;
import at.campus02.dbp2.relations.Student;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class OneToOneTests {
    private EntityManagerFactory factory;
    private EntityManager manager;


    @BeforeEach
    public void setup(){
         factory= Persistence.createEntityManagerFactory("persistenceUnitName");
         manager=factory.createEntityManager();
    }

    @AfterEach
    public void teardown(){
        if(manager.isOpen()) manager.close();

        if(factory.isOpen())  factory.close();

    }

    @Test
    public void persistAnimalAndStudentStoreRelationInDatabaset(){
        // given

        Student student=new Student("Hansy");
        Animal animal = new Animal("Gatto");

        //in Speicher selber um die Refarenzen kümmern
        student.setPet(animal);             //(ho creato collegamento)
        animal.setOwner(student);   //dipende quale CASCADE ho usato, altrimenti fare solo refresh

        //when
        manager.getTransaction().begin();
        manager.persist(student);
        manager.persist(animal);
        manager.getTransaction().commit();

        manager.clear();//? qui crea nuove referenze

        Animal gattoFromDatabase=manager.find(Animal.class, animal.getId());
        assertThat(gattoFromDatabase.getOwner(), is(student));


        Student ownweFromDb=manager.find(Student.class,student.getId());
        assertThat(ownweFromDb.getPet(), is(animal));
    }

    @Test
    public void persistWithCascade(){                    //cosa succede ad un gooetto , succede anche a tutti i collegata
        Student hansi=new Student("Hansi");
        Animal bunni=new Animal("Bunni");

        //1)  Owner setzten  solo cosi si completa il collegamento
        bunni.setOwner(hansi);
        //2) Pet setzen damit Cascade functioniert  (beide verbindungen)
        hansi.setPet(bunni);

        //when
        manager.getTransaction().begin();;
        manager.persist(hansi);
        //bunni soll durch cascade mit "Hansi" mitgespeicher werden
        manager.getTransaction().commit();

        manager.clear();
        //then
        Animal bunnyFromDb=manager.find(Animal.class, bunni.getId());
        assertThat(bunnyFromDb.getOwner(),is(hansi));

        Student hansiFromDb=manager.find(Student.class,hansi.getId());
        assertThat(hansiFromDb.getPet(), is(bunni));

    }

    @Test
    public void refreshClosedReferencesNotHAndledInMemoryRefresch(){

        Student hansi=new Student("Hansy");
        Animal bunni = new Animal("Bunni");
        //1)  Owner setzten  solo cosi si completa il collegamento
        bunni.setOwner(hansi);
        //2) Pet setzen damit Cascade functioniert  (beide verbindungen)
        //hansi.setPet(bunni);      //questo non lo daccio

        //when
        manager.getTransaction().begin();
        manager.persist(bunni);      //hansi non é nel db
        //nachdem am hansy kein pet gesetzt ist, reicht es nicht hansy allein zu persistiern (Cascade kann nicht greigfer)
        // d.h. wir müsser beide Entitiie persistieren (Reiefolge innerhalb die transation ist egal)
        manager.persist(hansi);
        manager.getTransaction().commit();

        manager.clear();


        //then
        //1) referenz von Animal auf Student ist gesetzt
        Animal bunniFromDb =manager.find(Animal.class, bunni.getId());
        assertThat(bunniFromDb.getOwner(), is(hansi));

        //2)ohne refresh wird die Referenz von "Hanzy" auf "bunni" nicht geschlosse
        //Leverl 1 chache leert bei Relationen    (leggi commento professore)
        Student hansyFromDb= manager.find(Student.class, hansi.getId());
        assertThat(hansyFromDb.getPet(),is(IsNull.nullValue()));  //bunni non cé

        //3)"refresch" erzwing a

        manager.refresh(hansyFromDb); //per aver stato databank....in questo caso funziona
        assertThat(hansyFromDb.getPet(), is(bunni));   //adesso non é piu null
    }


    @Test
    public void persistWithCascadeSpecie(){                    //cosa succede ad un gooetto , succede anche a tutti i collegata
        Student hansi=new Student("Hansi");
        Animal bunni=new Animal("Bunni");
        Species specieBunni = new Species("roditores");

        //1)  Owner setzten  solo cosi si completa il collegamento
        bunni.setOwner(hansi);
        //2) Pet setzen damit Cascade functioniert  (beide verbindungen)
        hansi.setPet(bunni);
        //3)
        bunni.setSpecies(specieBunni);

        //when
        manager.getTransaction().begin();;
        manager.persist(hansi);
        //bunni soll durch cascade mit "Hansi" mitgespeicher werden
        manager.getTransaction().commit();

        manager.clear();
        //then
        Animal bunnyFromDb=manager.find(Animal.class, bunni.getId());
        assertThat(bunnyFromDb.getOwner(),is(hansi));

        Student hansiFromDb=manager.find(Student.class,hansi.getId());
        assertThat(hansiFromDb.getPet(), is(bunni));

        bunnyFromDb=manager.find(Animal.class, bunni.getId());
        assertThat(bunnyFromDb.getSpecies(),is(specieBunni));

    }
}
