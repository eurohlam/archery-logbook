package nz.roag.archerylogbook.db;

import nz.roag.archerylogbook.db.model.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Sort;

import java.util.Date;
import java.util.List;
import java.util.Set;


@DataJpaTest
public class RepositoryTests {

    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private ClubRepository clubRepository;
    @Autowired
    private ArcherRepository archerRepository;
    @Autowired
    private BowRepository bowRepository;
    @Autowired
    private ScoreRepository scoreRepository;

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
        var storedBow = entityManager.persist(bow);

        var ds1 = new DistanceSettings();
        ds1.setDistance(20);
        ds1.setSight(6);
        ds1.setBowId(storedBow.getId());
        ds1.setTested(true);
        storedBow.getDistanceSettingsList().add(ds1);
        var ds2 = new DistanceSettings();
        ds2.setDistance(10);
        ds2.setSight(5);
        ds2.setBowId(storedBow.getId());
        ds2.setTested(true);
        storedBow.getDistanceSettingsList().add(ds2);
        var ds3 = new DistanceSettings();
        ds3.setDistance(15);
        ds3.setSight(5);
        ds3.setBowId(storedBow.getId());
        ds3.setTested(true);
        storedBow.getDistanceSettingsList().add(ds3);
        entityManager.persist(storedBow);


        var bows = bowRepository.findByArcherId(archerId, Sort.by("name").ascending());
        Assertions.assertEquals("Test bow", bows.get(0).getName());
        Assertions.assertEquals(Bow.Type.RECURVE, bows.get(0).getType());
        Assertions.assertEquals(3, bows.get(0).getDistanceSettingsList().size());
    }

    @Test
    void scoreRepositoryTest() {
        var score = new Score();
        score.setArcherId(archerId);
        score.setMatch("30");
        score.setCity("Nottingham");
        score.setScoreDate(new Date());
        score.setCountry("England");
        var storedScore = entityManager.persist(score);

        var end = new End();
        end.setEndNumber((short) 1);
        end.setScoreId(storedScore.getId());
        var storedEnd = entityManager.persist(end);

        var round = new Round();
        round.setRoundNumber((short) 1);
        round.setRoundScore((short) 10);
        round.setEndId(storedEnd.getId());
        end.setRounds(List.of(round));
        score.setEnds(List.of(end));
        entityManager.persist(score);

        var scores = scoreRepository.findByArcherId(archerId, Sort.by("scoreDate").descending());
        Assertions.assertEquals("30", scores.get(0).getMatch());
        Assertions.assertEquals("Nottingham", scores.get(0).getCity());
        Assertions.assertEquals(1, scores.get(0).getEndsCount());
        Assertions.assertEquals(10, scores.get(0).getSum());
        Assertions.assertEquals(10, scores.get(0).getEnds().get(0).getRounds().get(0).getRoundScore());
    }
}
