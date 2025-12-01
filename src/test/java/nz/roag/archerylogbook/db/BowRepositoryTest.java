package nz.roag.archerylogbook.db;

import nz.roag.archerylogbook.db.model.Archer;
import nz.roag.archerylogbook.db.model.Bow;
import nz.roag.archerylogbook.db.model.Club;
import nz.roag.archerylogbook.db.model.DistanceSettings;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Sort;

@DataJpaTest
public class BowRepositoryTest {


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
    private long bowId;

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

        var bow = new Bow();
        bow.setName("Test bow");
        bow.setType(Bow.Type.RECURVE);
        bow.setLevel(Bow.Level.INTERMEDIATE);
        bow.setPoundage("44-44");
        bow.setRiserModel("test riser");
        bow.setLimbsModel("test limbs");
        bow.setArcherId(archerId);
        var storedBow = entityManager.persist(bow);
        bowId = storedBow.getId();

        var ds1 = new DistanceSettings();
        ds1.setDistance(20);
        ds1.setSight("6");
        ds1.setIsTested(true);
        storedBow.getDistanceSettingsList().add(ds1);
        var ds2 = new DistanceSettings();
        ds2.setDistance(10);
        ds2.setSight("5");
        ds2.setIsTested(true);
        storedBow.getDistanceSettingsList().add(ds2);
        var ds3 = new DistanceSettings();
        ds3.setDistance(15);
        ds3.setSight("5");
        ds3.setIsTested(true);
        storedBow.getDistanceSettingsList().add(ds3);
        entityManager.persist(storedBow);
    }

    @AfterEach
    void afterEach() {
        archerRepository.deleteById(archerId);
        clubRepository.deleteById(clubId);
    }

    @Test
    void findByArcherIdTest() {
        var bows = bowRepository.findByArcherId(archerId, Sort.by("name").ascending());
        Assertions.assertEquals("Test bow", bows.get(0).getName());
        Assertions.assertEquals(Bow.Type.RECURVE, bows.get(0).getType());
        Assertions.assertEquals(3, bows.get(0).getDistanceSettingsList().size());
    }

    @Test
    void findByArcherIdAndArchivedTest() {
        var bows = bowRepository.findByArcherIdAndArchived(archerId, false, Sort.by("name").ascending());
        Assertions.assertEquals("Test bow", bows.get(0).getName());
        Assertions.assertEquals(Bow.Type.RECURVE, bows.get(0).getType());
        Assertions.assertEquals(3, bows.get(0).getDistanceSettingsList().size());

        bows = bowRepository.findByArcherIdAndArchived(archerId, true, Sort.by("name").ascending());
        Assertions.assertEquals(0, bows.size());
    }

    @Test
    void setArchivedForBowIdTest() {
        bowRepository.setArchivedForBowId(true, bowId);
        var bows = bowRepository.findByArcherIdAndArchived(archerId, true, Sort.by("name").ascending());
        Assertions.assertEquals(1, bows.size());

        bows = bowRepository.findByArcherIdAndArchived(archerId, false, Sort.by("name").ascending());
        Assertions.assertEquals(0, bows.size());
    }
}
