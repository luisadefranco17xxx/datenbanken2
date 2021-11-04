package at.campus02.dbps2.relations;

import at.campus02.dbp2.relations.Animal;
import at.campus02.dbp2.relations.Species;
import at.campus02.dbp2.relations.Student;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.hamcrest.collection.IsIterableContainingInAnyOrder;
import org.hamcrest.core.IsNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class OneToManyTest {

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
    public void persistSpeciesWithCascadeStoresAnimalsInDatabase(){                    //cosa succede ad un gooetto , succede anche a tutti i collegata


        Animal bunni=new Animal("Bunni");
        Animal dog=new Animal("Leo");
        Species mammals = new Species("Mammals");

        //referenzen fur FK in DB  metto qualcosa da cascadieren.....
        bunni.setSpecies(mammals);
        dog.setSpecies(mammals);
        //2 fur cascadieren

        mammals.getAnimals().add(bunni);
        mammals.getAnimals().add(dog);
        //3)
        //bunni.setSpecie(mammals);

        //when
        manager.getTransaction().begin();;
        manager.persist(mammals);
        //bunni soll durch cascade mit "Hansi" mitgespeicher werden
        manager.getTransaction().commit();

        manager.clear();
        //then
        Species mammalsFromDB=manager.find(Species.class, mammals.getId());
        assertThat(mammalsFromDB.getAnimals().size(),is(2));
        assertThat(mammalsFromDB.getAnimals(), Matchers.containsInAnyOrder(bunni,dog));

    }
    @Test
    //@Disabled ("Only works without orphanRemoval - enable after setting orphanRemoval to false")
    public void pupdateExampleWithCorrectionReferences(){                    //cosa succede ad un gooetto , succede anche a tutti i collegata
        // -----------------------------------------------------
        // given
        Animal clownfish = new Animal("Nemo");
        Animal squirrel = new Animal("Squirrel");
        Species fish = new Species("Fish");

        // Referenzen für DB
        clownfish.setSpecies(fish);
        // FEHLER -> den wollen wir dann korrigieren
        squirrel.setSpecies(fish);

        // Referenzen fürs CASCADE
        fish.getAnimals().add(clownfish);
        fish.getAnimals().add(squirrel);

        // -> Bild dazu: update_example_values.png

        // Speichern
        manager.getTransaction().begin();
        manager.persist(fish);
        manager.getTransaction().commit();

        manager.clear();

        // -> Bild dazu: update_example_persist.png

        // -----------------------------------------------------
        // when: Korrekturversuch, zum Scheitern verurteilt...
        manager.getTransaction().begin();
        fish.getAnimals().remove(squirrel);
        manager.merge(fish);
        manager.getTransaction().commit();
        manager.clear();

        // -> Bild dazu: update_example_wrong.png

        // -----------------------------------------------------
        // then
        // Squirrel existiert noch in DB
        Animal squirrelFromDb = manager.find(Animal.class, squirrel.getId());
        assertThat(squirrelFromDb, CoreMatchers.is(notNullValue()));

        // Squirrel ist immer noch ein Fisch - wir haben im Speicher die Liste von
        // "Fish" geändert, aber species von Squirrel zeigt nach wie vor auf Fish,
        // auch in der DB.
        assertThat(squirrelFromDb.getSpecies().getId(), is(fish.getId()));

        // auch wenn wir die Liste mittels "refresh" neu einlesen, wird die
        // Referenz von Squirrel auf Fish (DB) neu eingelesen und Squirrel ist
        // wieder in der Liste drin.
        Species mergedFish = manager.merge(fish);
        manager.refresh(mergedFish);
        assertThat(mergedFish.getAnimals().size(), CoreMatchers.is(2));

        // -----------------------------------------------------
        // when: Korrekturversuch, diesmal richtig ...
        manager.getTransaction().begin();
        squirrel.setSpecies(null);
        manager.merge(squirrel);
        manager.getTransaction().commit();
        manager.clear();

        // -> Bild dazu: update_example_correct.png

        // -----------------------------------------------------
        // then
        // Squirrel existiert noch in DB
        squirrelFromDb = manager.find(Animal.class, squirrel.getId());
        assertThat(squirrelFromDb, is(notNullValue()));

        // Squirrel ist kein Fisch mehr
        assertThat(squirrelFromDb.getSpecies(), is(nullValue()));

        // auch wenn wir die Liste mittels "refresh" neu einlesen, ist Squirrel
        // nicht mehr enthalten
        mergedFish = manager.merge(fish);
        manager.refresh(mergedFish);
        assertThat(mergedFish.getAnimals().size(), is(1));
    }

    @Test
    public void orphanRemovalDeletesOrphansFromDatabase() {
        // given
        Animal clownfish = new Animal("Nemo");
        Animal squirrel = new Animal("Squirrel");
        Species fish = new Species("Fish");

        // Referenzen für DB
        clownfish.setSpecies(fish);
        // FEHLER -> den wollen wir dann korrigieren
        squirrel.setSpecies(fish);

        // Referenzen fürs CASCADE
        fish.getAnimals().add(clownfish);
        fish.getAnimals().add(squirrel);

        // Speichern
        manager.getTransaction().begin();
        manager.persist(fish);
        manager.getTransaction().commit();

        manager.clear();

        // when
        manager.getTransaction().begin();
        fish.getAnimals().remove(squirrel);
        manager.merge(fish);
        manager.getTransaction().commit();

        manager.clear();

        // then
        Animal squirrelFromDb = manager.find(Animal.class, squirrel.getId());
        // bei Verwendung von orphanRemoval wird Squirrel aus der DB gelöscht.
        assertThat(squirrelFromDb, CoreMatchers.is(nullValue()));

        Species refreshedFish = manager.merge(fish);
        manager.refresh(refreshedFish);

        assertThat(refreshedFish.getAnimals().size(), CoreMatchers.is(1));
    }

}