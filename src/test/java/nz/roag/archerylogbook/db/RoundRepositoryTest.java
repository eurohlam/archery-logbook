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

import java.util.Date;
import java.util.List;

@DataJpaTest
public class RoundRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private ClubRepository clubRepository;
    @Autowired
    private ArcherRepository archerRepository;
    @Autowired
    private RoundRepository roundRepository;

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

        var bow = new Bow();
        bow.setName("Test bow");
        bow.setType(Bow.Type.RECURVE);
        bow.setLevel(Bow.Level.INTERMEDIATE);
        bow.setPoundage("44-44");
        bow.setRiserModel("test riser");
        bow.setLimbsModel("test limbs");
        bow.setArcherId(archerId);
        var storedBow = entityManager.persist(bow);

        var round = new Round();
        round.setArcherId(archerId);
        round.setDistance("30");
        round.setTargetFace(TargetFace.TF_80cm);
        round.setCity("Nottingham");
        round.setRoundDate(new Date());
        round.setCountry("England");
        round.setBowId(storedBow.getId());

        var end = new End();
        end.setEndNumber((short) 1);

        var shot = new Shot();
        shot.setShotNumber((short) 1);
        shot.setShotScore((short) 10);
        end.setShots(List.of(shot));
        round.setEnds(List.of(end));
        entityManager.persist(round);
    }

    @AfterEach
    void afterEach() {
        archerRepository.deleteById(archerId);
        clubRepository.deleteById(clubId);
    }

    @Test
    void findByArcherIdTest() {
        var rounds = roundRepository.findByArcherId(archerId, Sort.by("roundDate").descending());
        Assertions.assertEquals("30", rounds.get(0).getDistance());
        Assertions.assertEquals("Nottingham", rounds.get(0).getCity());
        Assertions.assertEquals(1, rounds.get(0).getEndsCount());
        Assertions.assertEquals(10, rounds.get(0).getSum());
        Assertions.assertEquals(10, rounds.get(0).getEnds().get(0).getShots().get(0).getShotScore());

        var pageableRounds = roundRepository.findByArcherId(archerId, PageRequest.of(2,5, Sort.by("roundDate").ascending()));
        Assertions.assertEquals(0, pageableRounds.getContent().size());

        pageableRounds = roundRepository.findByArcherId(archerId, PageRequest.of(0,5, Sort.by("roundDate").ascending()));
        Assertions.assertEquals("30", pageableRounds.getContent().get(0).getDistance());
        Assertions.assertEquals("Nottingham", pageableRounds.getContent().get(0).getCity());
        Assertions.assertEquals(1, pageableRounds.getContent().get(0).getEndsCount());
        Assertions.assertEquals(10, pageableRounds.getContent().get(0).getSum());
        Assertions.assertEquals(10, pageableRounds.getContent().get(0).getEnds().get(0).getShots().get(0).getShotScore());
    }

    @Test
    void findByArcherIdAndArchivedTest() {
        var pageableRounds = roundRepository.findByArcherIdAndArchived(archerId, false, PageRequest.of(0,5, Sort.by("roundDate").ascending()));
        Assertions.assertEquals("30", pageableRounds.getContent().get(0).getDistance());
        Assertions.assertEquals("Nottingham", pageableRounds.getContent().get(0).getCity());
        Assertions.assertEquals(1, pageableRounds.getContent().get(0).getEndsCount());
        Assertions.assertEquals(10, pageableRounds.getContent().get(0).getSum());
        Assertions.assertEquals(10, pageableRounds.getContent().get(0).getEnds().get(0).getShots().get(0).getShotScore());

        pageableRounds = roundRepository.findByArcherIdAndArchived(archerId,true,  PageRequest.of(2,5, Sort.by("roundDate").ascending()));
        Assertions.assertEquals(0, pageableRounds.getContent().size());

        pageableRounds = roundRepository.findByArcherIdAndArchived(archerId, true, PageRequest.of(0,5, Sort.by("roundDate").ascending()));
        Assertions.assertEquals(0, pageableRounds.getContent().size());
    }

    @Test
    void setArchivedForRoundIdTest() {
        var pageableRounds = roundRepository.findByArcherIdAndArchived(archerId, false, PageRequest.of(0,5, Sort.by("roundDate").ascending()));
        roundRepository.setArchivedForRoundId(true, pageableRounds.getContent().get(0).getId());
        pageableRounds = roundRepository.findByArcherIdAndArchived(archerId, false, PageRequest.of(0,5, Sort.by("roundDate").ascending()));
        Assertions.assertEquals(0, pageableRounds.getContent().size());
    }

    @Test
    void findByArcherIdAndDistanceAndArchivedTest() {
        var pageableRounds = roundRepository.findByArcherIdAndDistanceAndArchived(archerId, "30",false, PageRequest.of(0,5, Sort.by("roundDate").ascending()));
        Assertions.assertEquals(1, pageableRounds.getContent().size());
    }

    @Test
    void getBestRoundsByDistanceForArcherIdTest() {
        Assertions.assertEquals(1, roundRepository.getTotalRoundsByArcherId(archerId));
        Assertions.assertEquals(1, roundRepository.getTotalRoundsLastMonthByArcherId(archerId));

        var rounds = roundRepository.getBestRoundsByDistanceForArcherId(archerId);
        Assertions.assertEquals("30", rounds.get(0).getDistance());

    }

    @Test
    void getTotalRoundsByArcherIdTest() {
        Assertions.assertEquals(1, roundRepository.getTotalRoundsByArcherId(archerId));
    }

    @Test
    void getTotalRoundsLastMonthByArcherIdTest() {
        Assertions.assertEquals(1, roundRepository.getTotalRoundsLastMonthByArcherId(archerId));

    }
}
