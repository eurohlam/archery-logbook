package nz.roag.archerylogbook.db;

import nz.roag.archerylogbook.db.model.Archer;
import nz.roag.archerylogbook.db.model.Bow;
import nz.roag.archerylogbook.db.model.Club;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Sort;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class RepositoryTests {

    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private ClubRepository clubRepository;
    @Autowired
    private ArcherRepository archerRepository;
    @Autowired
    private BowRepository bowRepository;

    private long clubId;
    private final long archerId = 1L;

    @BeforeEach
    void beforeEach() {
        var club = new Club();
        club.setName("Test Club");
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
    void archerRepositoryTest() {

        var archer = archerRepository.findById(archerId).get();

        Assertions.assertEquals("Robin", archer.getFirstName());
        Assertions.assertEquals("robin@hood.arch", archer.getEmail());


        var archers = archerRepository.findByClubIdOrderByLastNameAsc(clubId);
        Assertions.assertEquals("Hood", archers.get(0).getLastName());
    }

    @Test
    void bowRepositoryTest() {
        var bow = new Bow();
        bow.setName("Test bow");
        bow.setType(Bow.Type.RECURVE);
        bow.setLevel(Bow.Level.INTERMEDIATE);
        bow.setPoundage("44-44");
        bow.setRiserModel("test riser");
        bow.setLimbsModel("test limbs");
        bow.setArcherId(archerId);
        entityManager.persist(bow);

        var bows = bowRepository.findByArcherId(archerId, Sort.by("name").ascending());

        Assertions.assertEquals("Test bow", bows.get(0).getName());
        Assertions.assertEquals(Bow.Type.RECURVE, bows.get(0).getType());
    }

}
