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
public class CompetitionRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private ClubRepository clubRepository;
    @Autowired
    private ArcherRepository archerRepository;
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
        competition.setRounds(List.of(round));
        entityManager.persist(competition);

    }

    @AfterEach
    void afterEach() {
        archerRepository.deleteById(archerId);
        clubRepository.deleteById(clubId);
    }

    @Test
    void findByArcherIdTest() {
        var competitions = competitionRepository.findByArcherId(archerId, Sort.by("competitionDate").descending());
        Assertions.assertEquals(Competition.CompetitionType.WA1440, competitions.get(0).getCompetitionType());
        Assertions.assertEquals("Nottingham", competitions.get(0).getCity());
        Assertions.assertEquals(1, competitions.get(0).getRounds().size());
        Assertions.assertEquals(1, competitions.get(0).getRounds().get(0).getShotsCount());
    }

    @Test
    void findByArcherIdAndArchivedTest() {
        var pageableCompetitions = competitionRepository.findByArcherIdAndArchived(archerId, false,
                PageRequest.of(0,5, Sort.by("competitionDate").descending()));
        Assertions.assertEquals(Competition.CompetitionType.WA1440, pageableCompetitions.getContent().get(0).getCompetitionType());
        Assertions.assertEquals("Nottingham", pageableCompetitions.getContent().get(0).getCity());
        Assertions.assertEquals(1, pageableCompetitions.getContent().get(0).getRounds().size());
        Assertions.assertEquals(1, pageableCompetitions.getContent().get(0).getRounds().get(0).getShotsCount());

    }

    @Test
    void findByArcherIdAndCompetitionTypeAndArchivedTest() {
        var pageableCompetitions = competitionRepository.findByArcherIdAndCompetitionTypeAndArchived(archerId,
                Competition.CompetitionType.WA1440, false,
                PageRequest.of(0,5, Sort.by("competitionDate").descending()));
        Assertions.assertEquals(Competition.CompetitionType.WA1440, pageableCompetitions.getContent().get(0).getCompetitionType());
        Assertions.assertEquals(1, pageableCompetitions.getContent().get(0).getRounds().size());
    }
}
