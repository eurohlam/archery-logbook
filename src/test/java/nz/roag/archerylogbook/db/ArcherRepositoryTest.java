package nz.roag.archerylogbook.db;

import nz.roag.archerylogbook.db.model.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;


@DataJpaTest
class ArcherRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private ClubRepository clubRepository;
    @Autowired
    private ArcherRepository archerRepository;

    private long clubId;
    private final long archerId = 1L;

    @BeforeEach
    void beforeEach() {
        var club = new Club();
        club.setName("Test Club");
        club.setCountry("England");
        var storedClub = entityManager.persist(club);
        clubId = storedClub.getId();

        var archer = new Archer();
        archer.setId(archerId);
        archer.setFirstName("Robin");
        archer.setLastName("Hood");
        archer.setEmail("robin@hood.arch");
        archer.setClubId(storedClub.getId());
        entityManager.persist(archer);
    }

    @AfterEach
    void afterEach() {
        archerRepository.deleteById(archerId);
        clubRepository.deleteById(clubId);
    }

    @Test
    void clubRepositoryTest() {
        var club = clubRepository.findFirstByName("Test Club");
        Assertions.assertEquals("England", club.getCountry());
    }

    @Test
    void findByIdTest() {
        var archer = archerRepository.findById(archerId).get();
        Assertions.assertEquals("Robin", archer.getFirstName());
        Assertions.assertEquals("robin@hood.arch", archer.getEmail());
    }

    @Test
    void findByClubIdOrderByLastNameAscTest() {
        var archers = archerRepository.findByClubIdOrderByLastNameAsc(clubId, PageRequest.of(0,5, Sort.by("lastName").ascending()));
        Assertions.assertEquals("Hood", archers.getContent().get(0).getLastName());
    }

    @Test
    void findByArchivedTest() {
        var archers = archerRepository.findByArchived(false, PageRequest.of(0,5, Sort.by("lastName").ascending()));
        Assertions.assertEquals("Hood", archers.getContent().get(0).getLastName());
    }

}
