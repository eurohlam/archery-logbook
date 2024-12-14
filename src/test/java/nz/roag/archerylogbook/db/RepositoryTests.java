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
class RepositoryTests {

    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private ClubRepository clubRepository;
    @Autowired
    private ArcherRepository archerRepository;
    @Autowired
    private BowRepository bowRepository;
    @Autowired
    private RoundRepository roundRepository;
    @Autowired
    private CompetitionRepository competitionRepository;

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
        ds1.setSight("6");
        ds1.setBowId(storedBow.getId());
        ds1.setIsTested(true);
        storedBow.getDistanceSettingsList().add(ds1);
        var ds2 = new DistanceSettings();
        ds2.setDistance(10);
        ds2.setSight("5");
        ds2.setBowId(storedBow.getId());
        ds2.setIsTested(true);
        storedBow.getDistanceSettingsList().add(ds2);
        var ds3 = new DistanceSettings();
        ds3.setDistance(15);
        ds3.setSight("5");
        ds3.setBowId(storedBow.getId());
        ds3.setIsTested(true);
        storedBow.getDistanceSettingsList().add(ds3);
        entityManager.persist(storedBow);


        var bows = bowRepository.findByArcherId(archerId, Sort.by("name").ascending());
        Assertions.assertEquals("Test bow", bows.get(0).getName());
        Assertions.assertEquals(Bow.Type.RECURVE, bows.get(0).getType());
        Assertions.assertEquals(3, bows.get(0).getDistanceSettingsList().size());

        bows = bowRepository.findByArcherIdAndArchived(archerId, false, Sort.by("name").ascending());
        Assertions.assertEquals("Test bow", bows.get(0).getName());
        Assertions.assertEquals(Bow.Type.RECURVE, bows.get(0).getType());
        Assertions.assertEquals(3, bows.get(0).getDistanceSettingsList().size());

        bows = bowRepository.findByArcherIdAndArchived(archerId, true, Sort.by("name").ascending());
        Assertions.assertEquals(0, bows.size());

        bowRepository.setArchivedForBowId(true, bow.getId());
        bows = bowRepository.findByArcherIdAndArchived(archerId, true, Sort.by("name").ascending());
        Assertions.assertEquals(1, bows.size());

    }

    @Test
    void roundRepositoryTest() {
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
        var storedRound = entityManager.persist(round);

        var end = new End();
        end.setEndNumber((short) 1);
        end.setRoundId(storedRound.getId());
        var storedEnd = entityManager.persist(end);

        var shot = new Shot();
        shot.setShotNumber((short) 1);
        shot.setShotScore((short) 10);
        shot.setEndId(storedEnd.getId());
        end.setShots(List.of(shot));
        round.setEnds(List.of(end));
        entityManager.persist(round);

        var rounds = roundRepository.findByArcherId(archerId, Sort.by("roundDate").descending());
        Assertions.assertEquals("30", rounds.get(0).getDistance());
        Assertions.assertEquals("Nottingham", rounds.get(0).getCity());
        Assertions.assertEquals(1, rounds.get(0).getEndsCount());
        Assertions.assertEquals(10, rounds.get(0).getSum());
        Assertions.assertEquals(10, rounds.get(0).getEnds().get(0).getShots().get(0).getShotScore());

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

        pageableRounds = roundRepository.findByArcherId(archerId, PageRequest.of(2,5, Sort.by("roundDate").ascending()));
        Assertions.assertEquals(0, pageableRounds.getContent().size());

        pageableRounds = roundRepository.findByArcherId(archerId, PageRequest.of(0,5, Sort.by("roundDate").ascending()));
        Assertions.assertEquals("30", pageableRounds.getContent().get(0).getDistance());
        Assertions.assertEquals("Nottingham", pageableRounds.getContent().get(0).getCity());
        Assertions.assertEquals(1, pageableRounds.getContent().get(0).getEndsCount());
        Assertions.assertEquals(10, pageableRounds.getContent().get(0).getSum());
        Assertions.assertEquals(10, pageableRounds.getContent().get(0).getEnds().get(0).getShots().get(0).getShotScore());

        roundRepository.setArchivedForRoundId(true, pageableRounds.getContent().get(0).getId());
        pageableRounds = roundRepository.findByArcherIdAndArchived(archerId, false, PageRequest.of(0,5, Sort.by("roundDate").ascending()));
        Assertions.assertEquals(0, pageableRounds.getContent().size());
    }

    @Test
    void competitionRepositoryTest() {
        var bow = new Bow();
        bow.setName("Test bow");
        bow.setType(Bow.Type.RECURVE);
        bow.setLevel(Bow.Level.INTERMEDIATE);
        bow.setPoundage("44-44");
        bow.setRiserModel("test riser");
        bow.setLimbsModel("test limbs");
        bow.setArcherId(archerId);
        var storedBow = entityManager.persist(bow);

        var competition = new Competition();
        competition.setArcherId(archerId);
        competition.setCompetitionType(Competition.CompetitionType.WA1440);
        competition.setCountry("England");
        competition.setCity("Nottingham");
        var storedCompetition = entityManager.persist(competition);

        var round = new Round();
        round.setArcherId(archerId);
        round.setDistance("30");
        round.setTargetFace(TargetFace.TF_80cm);
        round.setCity("Nottingham");
        round.setRoundDate(new Date());
        round.setCountry("England");
        round.setBowId(storedBow.getId());
        round.setCompetitionId(storedCompetition.getId());
        var storedRound = entityManager.persist(round);

        var end = new End();
        end.setEndNumber((short) 1);
        end.setRoundId(storedRound.getId());
        var storedEnd = entityManager.persist(end);

        var shot = new Shot();
        shot.setShotNumber((short) 1);
        shot.setShotScore((short) 10);
        shot.setEndId(storedEnd.getId());
        end.setShots(List.of(shot));
        round.setEnds(List.of(end));
        entityManager.persist(round);
        competition.setRounds(List.of(round));
        entityManager.persist(competition);

        var competitions = competitionRepository.findByArcherId(archerId, Sort.by("competitionDate").descending());
        Assertions.assertEquals(Competition.CompetitionType.WA1440, competitions.get(0).getCompetitionType());
        Assertions.assertEquals("Nottingham", competitions.get(0).getCity());
        Assertions.assertEquals(1, competitions.get(0).getRounds().size());
        Assertions.assertEquals(1, competitions.get(0).getRounds().get(0).getShotsCount());

        var pageableCompetitions = competitionRepository.findByArcherId(archerId, PageRequest.of(0,5, Sort.by("competitionDate").descending()));
        Assertions.assertEquals(Competition.CompetitionType.WA1440, pageableCompetitions.getContent().get(0).getCompetitionType());
        Assertions.assertEquals("Nottingham", pageableCompetitions.getContent().get(0).getCity());
        Assertions.assertEquals(1, pageableCompetitions.getContent().get(0).getRounds().size());
        Assertions.assertEquals(1, pageableCompetitions.getContent().get(0).getRounds().get(0).getShotsCount());

        pageableCompetitions = competitionRepository.findByArcherIdAndCompetitionType(archerId, Competition.CompetitionType.WA1440, PageRequest.of(0,5, Sort.by("competitionDate").descending()));
        Assertions.assertEquals(Competition.CompetitionType.WA1440, pageableCompetitions.getContent().get(0).getCompetitionType());
        Assertions.assertEquals(1, pageableCompetitions.getContent().get(0).getRounds().size());
    }
}
