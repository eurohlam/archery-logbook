package nz.roag.archerylogbook.db;

import nz.roag.archerylogbook.db.model.Subscriber;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;


@DataJpaTest
class SubscriberRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private SubscriberRepository subscriberRepository;

    @Test
    void subscriberRepositoryTest() {
        var subscriber = new Subscriber();
        subscriber.setAccessKey("testAccessKey");
        subscriber.setSecretKey("testSecret");
        entityManager.persist(subscriber);

        Assertions.assertEquals(1, subscriberRepository.findAll().size());

        var storedSubscriber = subscriberRepository.findById("testAccessKey").get();
        Assertions.assertEquals("testSecret", storedSubscriber.getSecretKey());
    }
}
