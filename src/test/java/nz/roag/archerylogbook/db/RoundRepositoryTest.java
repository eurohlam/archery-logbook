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

import java.time.LocalDateTime;
import java.time.ZoneOffset;
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
    private long bowId1;
    private long bowId2;

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

        var bow1 = new Bow();
        bow1.setName("Test bow 1");
        bow1.setType(Bow.Type.RECURVE);
        bow1.setLevel(Bow.Level.INTERMEDIATE);
        bow1.setPoundage("44-44");
        bow1.setRiserModel("test riser");
        bow1.setLimbsModel("test limbs");
        bow1.setArcherId(archerId);
        var storedBow1 = entityManager.persist(bow1);
        bowId1 = storedBow1.getId();

        var bow2 = new Bow();
        bow2.setName("Test bow 2");
        bow2.setType(Bow.Type.COMPOUND);
        bow2.setLevel(Bow.Level.ADVANCED);
        bow2.setPoundage("50-50");
        bow2.setArcherId(archerId);
        var storedBow2 = entityManager.persist(bow2);
        bowId2 = storedBow2.getId();

        // Round 1: bow1, distance 30, TF_80cm, not archived
        var round1 = new Round();
        round1.setArcherId(archerId);
        round1.setDistance("30");
        round1.setTargetFace(TargetFace.TF_80cm);
        round1.setCity("Nottingham");
        round1.setRoundDate(Date.from(LocalDateTime.now().minusHours(1).toInstant(ZoneOffset.UTC)));
        round1.setCountry("England");
        round1.setBowId(bowId1);
        round1.setArchived(false);
        var end1 = new End();
        end1.setEndNumber((short) 1);
        var shot1 = new Shot();
        shot1.setShotNumber((short) 1);
        shot1.setShotScore((short) 10);
        end1.setShots(List.of(shot1));
        round1.setEnds(List.of(end1));
        entityManager.persist(round1);

        // Round 2: bow1, distance 50, TF_122cm, not archived
        var round2 = new Round();
        round2.setArcherId(archerId);
        round2.setDistance("50");
        round2.setTargetFace(TargetFace.TF_122cm);
        round2.setCity("London");
        round2.setRoundDate(Date.from(LocalDateTime.now().minusHours(2).toInstant(ZoneOffset.UTC)));
        round2.setCountry("England");
        round2.setBowId(bowId1);
        round2.setArchived(false);
        var end2 = new End();
        end2.setEndNumber((short) 1);
        var shot2 = new Shot();
        shot2.setShotNumber((short) 1);
        shot2.setShotScore((short) 9);
        end2.setShots(List.of(shot2));
        round2.setEnds(List.of(end2));
        entityManager.persist(round2);

        // Round 3: bow2, distance 30, TF_80cm, not archived
        var round3 = new Round();
        round3.setArcherId(archerId);
        round3.setDistance("30");
        round3.setTargetFace(TargetFace.TF_80cm);
        round3.setCity("Manchester");
        round3.setRoundDate(Date.from(LocalDateTime.now().minusHours(3).toInstant(ZoneOffset.UTC)));
        round3.setCountry("England");
        round3.setBowId(bowId2);
        round3.setArchived(false);
        var end3 = new End();
        end3.setEndNumber((short) 1);
        var shot3 = new Shot();
        shot3.setShotNumber((short) 1);
        shot3.setShotScore((short) 8);
        end3.setShots(List.of(shot3));
        round3.setEnds(List.of(end3));
        entityManager.persist(round3);

        // Round 4: bow2, distance 50, TF_122cm, archived
        var round4 = new Round();
        round4.setArcherId(archerId);
        round4.setDistance("50");
        round4.setTargetFace(TargetFace.TF_122cm);
        round4.setCity("Birmingham");
        round4.setRoundDate(Date.from(LocalDateTime.now().minusHours(4).toInstant(ZoneOffset.UTC)));
        round4.setCountry("England");
        round4.setBowId(bowId2);
        round4.setArchived(true);
        var end4 = new End();
        end4.setEndNumber((short) 1);
        var shot4 = new Shot();
        shot4.setShotNumber((short) 1);
        shot4.setShotScore((short) 7);
        end4.setShots(List.of(shot4));
        round4.setEnds(List.of(end4));
        entityManager.persist(round4);
    }

    @AfterEach
    void afterEach() {
        archerRepository.deleteById(archerId);
        clubRepository.deleteById(clubId);
    }

    @Test
    void findByArcherIdTest() {
        var rounds = roundRepository.findByArcherId(archerId, Sort.by("roundDate").descending());
        Assertions.assertEquals(4, rounds.size()); // All 4 rounds

        var pageableRounds = roundRepository.findByArcherId(archerId, PageRequest.of(2, 5, Sort.by("roundDate").ascending()));
        Assertions.assertEquals(0, pageableRounds.getContent().size());

        pageableRounds = roundRepository.findByArcherId(archerId, PageRequest.of(0, 5, Sort.by("roundDate").ascending()));
        Assertions.assertEquals(4, pageableRounds.getContent().size());
        // Verify first round (Nottingham)
        var nottinghamRound = pageableRounds.getContent().stream()
                .filter(r -> r.getCity().equals("Nottingham"))
                .findFirst()
                .orElse(null);
        Assertions.assertNotNull(nottinghamRound);
        Assertions.assertEquals("30", nottinghamRound.getDistance());
        Assertions.assertEquals(1, nottinghamRound.getEndsCount());
        Assertions.assertEquals(10, nottinghamRound.getSum());
    }

    @Test
    void findByArcherIdAndArchivedTest() {
        var pageableRounds = roundRepository.findByArcherIdAndArchived(archerId, false, PageRequest.of(0, 5, Sort.by("roundDate").ascending()));
        Assertions.assertEquals(3, pageableRounds.getContent().size()); // 3 non-archived rounds
        Assertions.assertTrue(pageableRounds.getContent().stream().allMatch(r -> !r.getArchived()));

        pageableRounds = roundRepository.findByArcherIdAndArchived(archerId, true, PageRequest.of(2, 5, Sort.by("roundDate").ascending()));
        Assertions.assertEquals(0, pageableRounds.getContent().size());

        pageableRounds = roundRepository.findByArcherIdAndArchived(archerId, true, PageRequest.of(0, 5, Sort.by("roundDate").ascending()));
        Assertions.assertEquals(1, pageableRounds.getContent().size()); // 1 archived round
        Assertions.assertTrue(pageableRounds.getContent().stream().allMatch(r -> r.getArchived()));
    }

    @Test
    void setArchivedForRoundIdTest() {
        var pageableRounds = roundRepository.findByArcherIdAndArchived(archerId, false, PageRequest.of(0, 5, Sort.by("roundDate").ascending()));
        Assertions.assertEquals(3, pageableRounds.getContent().size()); // Initially 3 non-archived rounds
        roundRepository.setArchivedForRoundId(true, pageableRounds.getContent().get(0).getId());
        pageableRounds = roundRepository.findByArcherIdAndArchived(archerId, false, PageRequest.of(0, 5, Sort.by("roundDate").ascending()));
        Assertions.assertEquals(2, pageableRounds.getContent().size()); // Now 2 non-archived rounds
    }

    @Test
    void findByArcherIdAndDistanceAndArchivedTest() {
        var pageableRounds = roundRepository.findByArcherIdAndDistanceAndArchived(archerId, "30", false, PageRequest.of(0, 5, Sort.by("roundDate").ascending()));
        Assertions.assertEquals(2, pageableRounds.getContent().size()); // Two rounds with distance 30, both not archived
        Assertions.assertTrue(pageableRounds.getContent().stream().allMatch(r -> r.getDistance().equals("30") && !r.getArchived()));

        pageableRounds = roundRepository.findByArcherIdAndDistanceAndArchived(archerId, "30", true, PageRequest.of(0, 5, Sort.by("roundDate").ascending()));
        Assertions.assertEquals(0, pageableRounds.getContent().size());

        pageableRounds = roundRepository.findByArcherIdAndDistanceAndArchived(archerId, "50", false, PageRequest.of(0, 5, Sort.by("roundDate").ascending()));
        Assertions.assertEquals(1, pageableRounds.getContent().size()); // One round with distance 50, not archived

        pageableRounds = roundRepository.findByArcherIdAndDistanceAndArchived(archerId, "50", true, PageRequest.of(0, 5, Sort.by("roundDate").ascending()));
        Assertions.assertEquals(1, pageableRounds.getContent().size()); // One round with distance 50, archived
    }

    @Test
    void getBestRoundsByDistanceForArcherIdTest() {
        var rounds = roundRepository.getBestRoundsByDistanceForArcherId(archerId); // Only non-archived rounds
        Assertions.assertEquals(2, rounds.size());
    }

    @Test
    void getTotalRoundsByArcherIdTest() {
        Assertions.assertEquals(3, roundRepository.getTotalRoundsByArcherId(archerId)); // Only non-archived rounds
    }

    @Test
    void getTotalRoundsLastMonthByArcherIdTest() {
        Assertions.assertEquals(3, roundRepository.getTotalRoundsLastMonthByArcherId(archerId)); // Only non-archived rounds
    }

    /*** Tests for filtering by bow, targetFace and combinations ***/

    @Test
    void findByArcherIdAndBowIdTest() {
        var pageableRounds = roundRepository.findByArcherIdAndBowId(archerId, bowId1, PageRequest.of(0, 10, Sort.by("roundDate").ascending()));
        Assertions.assertEquals(2, pageableRounds.getContent().size());
        Assertions.assertTrue(pageableRounds.getContent().stream().allMatch(r -> r.getBowId() == bowId1));

        pageableRounds = roundRepository.findByArcherIdAndBowId(archerId, bowId2, PageRequest.of(0, 10, Sort.by("roundDate").ascending()));
        Assertions.assertEquals(2, pageableRounds.getContent().size());
        Assertions.assertTrue(pageableRounds.getContent().stream().allMatch(r -> r.getBowId() == bowId2));
    }

    @Test
    void findByArcherIdAndBowIdAndArchivedTest() {
        var pageableRounds = roundRepository.findByArcherIdAndBowIdAndArchived(archerId, bowId1, false, PageRequest.of(0, 10, Sort.by("roundDate").ascending()));
        Assertions.assertEquals(2, pageableRounds.getContent().size());
        Assertions.assertTrue(pageableRounds.getContent().stream().allMatch(r -> r.getBowId() == bowId1 && !r.getArchived()));

        pageableRounds = roundRepository.findByArcherIdAndBowIdAndArchived(archerId, bowId1, true, PageRequest.of(0, 10, Sort.by("roundDate").ascending()));
        Assertions.assertEquals(0, pageableRounds.getContent().size());

        pageableRounds = roundRepository.findByArcherIdAndBowIdAndArchived(archerId, bowId2, false, PageRequest.of(0, 10, Sort.by("roundDate").ascending()));
        Assertions.assertEquals(1, pageableRounds.getContent().size());
        Assertions.assertEquals("30", pageableRounds.getContent().get(0).getDistance());

        pageableRounds = roundRepository.findByArcherIdAndBowIdAndArchived(archerId, bowId2, true, PageRequest.of(0, 10, Sort.by("roundDate").ascending()));
        Assertions.assertEquals(1, pageableRounds.getContent().size());
        Assertions.assertEquals("50", pageableRounds.getContent().get(0).getDistance());
    }

    @Test
    void findByArcherIdAndTargetFaceTest() {
        var pageableRounds = roundRepository.findByArcherIdAndTargetFace(archerId, TargetFace.TF_80cm, PageRequest.of(0, 10, Sort.by("roundDate").ascending()));
        Assertions.assertEquals(2, pageableRounds.getContent().size());
        Assertions.assertTrue(pageableRounds.getContent().stream().allMatch(r -> r.getTargetFace() == TargetFace.TF_80cm));

        pageableRounds = roundRepository.findByArcherIdAndTargetFace(archerId, TargetFace.TF_122cm, PageRequest.of(0, 10, Sort.by("roundDate").ascending()));
        Assertions.assertEquals(2, pageableRounds.getContent().size());
        Assertions.assertTrue(pageableRounds.getContent().stream().allMatch(r -> r.getTargetFace() == TargetFace.TF_122cm));
    }

    @Test
    void findByArcherIdAndTargetFaceAndArchivedTest() {
        var pageableRounds = roundRepository.findByArcherIdAndTargetFaceAndArchived(archerId, TargetFace.TF_80cm, false, PageRequest.of(0, 10, Sort.by("roundDate").ascending()));
        Assertions.assertEquals(2, pageableRounds.getContent().size());
        Assertions.assertTrue(pageableRounds.getContent().stream().allMatch(r -> r.getTargetFace() == TargetFace.TF_80cm && !r.getArchived()));

        pageableRounds = roundRepository.findByArcherIdAndTargetFaceAndArchived(archerId, TargetFace.TF_80cm, true, PageRequest.of(0, 10, Sort.by("roundDate").ascending()));
        Assertions.assertEquals(0, pageableRounds.getContent().size());

        pageableRounds = roundRepository.findByArcherIdAndTargetFaceAndArchived(archerId, TargetFace.TF_122cm, false, PageRequest.of(0, 10, Sort.by("roundDate").ascending()));
        Assertions.assertEquals(1, pageableRounds.getContent().size());
        Assertions.assertEquals("50", pageableRounds.getContent().get(0).getDistance());

        pageableRounds = roundRepository.findByArcherIdAndTargetFaceAndArchived(archerId, TargetFace.TF_122cm, true, PageRequest.of(0, 10, Sort.by("roundDate").ascending()));
        Assertions.assertEquals(1, pageableRounds.getContent().size());
        Assertions.assertEquals("50", pageableRounds.getContent().get(0).getDistance());
    }

    @Test
    void findByArcherIdAndBowIdAndDistanceTest() {
        var pageableRounds = roundRepository.findByArcherIdAndBowIdAndDistance(archerId, bowId1, "30", PageRequest.of(0, 10, Sort.by("roundDate").ascending()));
        Assertions.assertEquals(1, pageableRounds.getContent().size());
        Assertions.assertEquals("30", pageableRounds.getContent().get(0).getDistance());
        Assertions.assertEquals(bowId1, pageableRounds.getContent().get(0).getBowId());

        pageableRounds = roundRepository.findByArcherIdAndBowIdAndDistance(archerId, bowId1, "50", PageRequest.of(0, 10, Sort.by("roundDate").ascending()));
        Assertions.assertEquals(1, pageableRounds.getContent().size());
        Assertions.assertEquals("50", pageableRounds.getContent().get(0).getDistance());
        Assertions.assertEquals(bowId1, pageableRounds.getContent().get(0).getBowId());

        pageableRounds = roundRepository.findByArcherIdAndBowIdAndDistance(archerId, bowId2, "30", PageRequest.of(0, 10, Sort.by("roundDate").ascending()));
        Assertions.assertEquals(1, pageableRounds.getContent().size());
        Assertions.assertEquals("30", pageableRounds.getContent().get(0).getDistance());
        Assertions.assertEquals(bowId2, pageableRounds.getContent().get(0).getBowId());
    }

    @Test
    void findByArcherIdAndBowIdAndDistanceAndArchivedTest() {
        var pageableRounds = roundRepository.findByArcherIdAndBowIdAndDistanceAndArchived(archerId, bowId1, "30", false, PageRequest.of(0, 10, Sort.by("roundDate").ascending()));
        Assertions.assertEquals(1, pageableRounds.getContent().size());
        Assertions.assertEquals("30", pageableRounds.getContent().get(0).getDistance());
        Assertions.assertEquals(bowId1, pageableRounds.getContent().get(0).getBowId());
        Assertions.assertFalse(pageableRounds.getContent().get(0).getArchived());

        pageableRounds = roundRepository.findByArcherIdAndBowIdAndDistanceAndArchived(archerId, bowId1, "30", true, PageRequest.of(0, 10, Sort.by("roundDate").ascending()));
        Assertions.assertEquals(0, pageableRounds.getContent().size());

        pageableRounds = roundRepository.findByArcherIdAndBowIdAndDistanceAndArchived(archerId, bowId2, "50", false, PageRequest.of(0, 10, Sort.by("roundDate").ascending()));
        Assertions.assertEquals(0, pageableRounds.getContent().size());

        pageableRounds = roundRepository.findByArcherIdAndBowIdAndDistanceAndArchived(archerId, bowId2, "50", true, PageRequest.of(0, 10, Sort.by("roundDate").ascending()));
        Assertions.assertEquals(1, pageableRounds.getContent().size());
        Assertions.assertEquals("50", pageableRounds.getContent().get(0).getDistance());
        Assertions.assertEquals(bowId2, pageableRounds.getContent().get(0).getBowId());
        Assertions.assertTrue(pageableRounds.getContent().get(0).getArchived());
    }

    @Test
    void findByArcherIdAndBowIdAndTargetFaceTest() {
        var pageableRounds = roundRepository.findByArcherIdAndBowIdAndTargetFace(archerId, bowId1, TargetFace.TF_80cm, PageRequest.of(0, 10, Sort.by("roundDate").ascending()));
        Assertions.assertEquals(1, pageableRounds.getContent().size());
        Assertions.assertEquals(TargetFace.TF_80cm, pageableRounds.getContent().get(0).getTargetFace());
        Assertions.assertEquals(bowId1, pageableRounds.getContent().get(0).getBowId());

        pageableRounds = roundRepository.findByArcherIdAndBowIdAndTargetFace(archerId, bowId1, TargetFace.TF_122cm, PageRequest.of(0, 10, Sort.by("roundDate").ascending()));
        Assertions.assertEquals(1, pageableRounds.getContent().size());
        Assertions.assertEquals(TargetFace.TF_122cm, pageableRounds.getContent().get(0).getTargetFace());
        Assertions.assertEquals(bowId1, pageableRounds.getContent().get(0).getBowId());

        pageableRounds = roundRepository.findByArcherIdAndBowIdAndTargetFace(archerId, bowId2, TargetFace.TF_80cm, PageRequest.of(0, 10, Sort.by("roundDate").ascending()));
        Assertions.assertEquals(1, pageableRounds.getContent().size());
        Assertions.assertEquals(TargetFace.TF_80cm, pageableRounds.getContent().get(0).getTargetFace());
        Assertions.assertEquals(bowId2, pageableRounds.getContent().get(0).getBowId());
    }

    @Test
    void findByArcherIdAndBowIdAndTargetFaceAndArchivedTest() {
        var pageableRounds = roundRepository.findByArcherIdAndBowIdAndTargetFaceAndArchived(archerId, bowId1, TargetFace.TF_80cm, false, PageRequest.of(0, 10, Sort.by("roundDate").ascending()));
        Assertions.assertEquals(1, pageableRounds.getContent().size());
        Assertions.assertEquals(TargetFace.TF_80cm, pageableRounds.getContent().get(0).getTargetFace());
        Assertions.assertEquals(bowId1, pageableRounds.getContent().get(0).getBowId());
        Assertions.assertFalse(pageableRounds.getContent().get(0).getArchived());

        pageableRounds = roundRepository.findByArcherIdAndBowIdAndTargetFaceAndArchived(archerId, bowId1, TargetFace.TF_80cm, true, PageRequest.of(0, 10, Sort.by("roundDate").ascending()));
        Assertions.assertEquals(0, pageableRounds.getContent().size());

        pageableRounds = roundRepository.findByArcherIdAndBowIdAndTargetFaceAndArchived(archerId, bowId2, TargetFace.TF_122cm, false, PageRequest.of(0, 10, Sort.by("roundDate").ascending()));
        Assertions.assertEquals(0, pageableRounds.getContent().size());

        pageableRounds = roundRepository.findByArcherIdAndBowIdAndTargetFaceAndArchived(archerId, bowId2, TargetFace.TF_122cm, true, PageRequest.of(0, 10, Sort.by("roundDate").ascending()));
        Assertions.assertEquals(1, pageableRounds.getContent().size());
        Assertions.assertEquals(TargetFace.TF_122cm, pageableRounds.getContent().get(0).getTargetFace());
        Assertions.assertEquals(bowId2, pageableRounds.getContent().get(0).getBowId());
        Assertions.assertTrue(pageableRounds.getContent().get(0).getArchived());
    }

    @Test
    void findByArcherIdAndDistanceAndTargetFaceTest() {
        var pageableRounds = roundRepository.findByArcherIdAndDistanceAndTargetFace(archerId, "30", TargetFace.TF_80cm, PageRequest.of(0, 10, Sort.by("roundDate").ascending()));
        Assertions.assertEquals(2, pageableRounds.getContent().size());
        Assertions.assertTrue(pageableRounds.getContent().stream().allMatch(r -> r.getDistance().equals("30") && r.getTargetFace() == TargetFace.TF_80cm));

        pageableRounds = roundRepository.findByArcherIdAndDistanceAndTargetFace(archerId, "50", TargetFace.TF_122cm, PageRequest.of(0, 10, Sort.by("roundDate").ascending()));
        Assertions.assertEquals(2, pageableRounds.getContent().size());
        Assertions.assertTrue(pageableRounds.getContent().stream().allMatch(r -> r.getDistance().equals("50") && r.getTargetFace() == TargetFace.TF_122cm));

        pageableRounds = roundRepository.findByArcherIdAndDistanceAndTargetFace(archerId, "30", TargetFace.TF_122cm, PageRequest.of(0, 10, Sort.by("roundDate").ascending()));
        Assertions.assertEquals(0, pageableRounds.getContent().size());
    }

    @Test
    void findByArcherIdAndDistanceAndTargetFaceAndArchivedTest() {
        var pageableRounds = roundRepository.findByArcherIdAndDistanceAndTargetFaceAndArchived(archerId, "30", TargetFace.TF_80cm, false, PageRequest.of(0, 10, Sort.by("roundDate").ascending()));
        Assertions.assertEquals(2, pageableRounds.getContent().size());
        Assertions.assertTrue(pageableRounds.getContent().stream().allMatch(r -> r.getDistance().equals("30") && r.getTargetFace() == TargetFace.TF_80cm && !r.getArchived()));

        pageableRounds = roundRepository.findByArcherIdAndDistanceAndTargetFaceAndArchived(archerId, "30", TargetFace.TF_80cm, true, PageRequest.of(0, 10, Sort.by("roundDate").ascending()));
        Assertions.assertEquals(0, pageableRounds.getContent().size());

        pageableRounds = roundRepository.findByArcherIdAndDistanceAndTargetFaceAndArchived(archerId, "50", TargetFace.TF_122cm, false, PageRequest.of(0, 10, Sort.by("roundDate").ascending()));
        Assertions.assertEquals(1, pageableRounds.getContent().size());
        Assertions.assertEquals("50", pageableRounds.getContent().get(0).getDistance());
        Assertions.assertEquals(TargetFace.TF_122cm, pageableRounds.getContent().get(0).getTargetFace());
        Assertions.assertFalse(pageableRounds.getContent().get(0).getArchived());

        pageableRounds = roundRepository.findByArcherIdAndDistanceAndTargetFaceAndArchived(archerId, "50", TargetFace.TF_122cm, true, PageRequest.of(0, 10, Sort.by("roundDate").ascending()));
        Assertions.assertEquals(1, pageableRounds.getContent().size());
        Assertions.assertEquals("50", pageableRounds.getContent().get(0).getDistance());
        Assertions.assertEquals(TargetFace.TF_122cm, pageableRounds.getContent().get(0).getTargetFace());
        Assertions.assertTrue(pageableRounds.getContent().get(0).getArchived());
    }

    @Test
    void findByArcherIdAndBowIdAndDistanceAndTargetFaceTest() {
        var pageableRounds = roundRepository.findByArcherIdAndBowIdAndDistanceAndTargetFace(archerId, bowId1, "30", TargetFace.TF_80cm, PageRequest.of(0, 10, Sort.by("roundDate").ascending()));
        Assertions.assertEquals(1, pageableRounds.getContent().size());
        Assertions.assertEquals("30", pageableRounds.getContent().get(0).getDistance());
        Assertions.assertEquals(TargetFace.TF_80cm, pageableRounds.getContent().get(0).getTargetFace());
        Assertions.assertEquals(bowId1, pageableRounds.getContent().get(0).getBowId());

        pageableRounds = roundRepository.findByArcherIdAndBowIdAndDistanceAndTargetFace(archerId, bowId1, "50", TargetFace.TF_122cm, PageRequest.of(0, 10, Sort.by("roundDate").ascending()));
        Assertions.assertEquals(1, pageableRounds.getContent().size());
        Assertions.assertEquals("50", pageableRounds.getContent().get(0).getDistance());
        Assertions.assertEquals(TargetFace.TF_122cm, pageableRounds.getContent().get(0).getTargetFace());
        Assertions.assertEquals(bowId1, pageableRounds.getContent().get(0).getBowId());

        pageableRounds = roundRepository.findByArcherIdAndBowIdAndDistanceAndTargetFace(archerId, bowId2, "30", TargetFace.TF_80cm, PageRequest.of(0, 10, Sort.by("roundDate").ascending()));
        Assertions.assertEquals(1, pageableRounds.getContent().size());
        Assertions.assertEquals("30", pageableRounds.getContent().get(0).getDistance());
        Assertions.assertEquals(TargetFace.TF_80cm, pageableRounds.getContent().get(0).getTargetFace());
        Assertions.assertEquals(bowId2, pageableRounds.getContent().get(0).getBowId());

        pageableRounds = roundRepository.findByArcherIdAndBowIdAndDistanceAndTargetFace(archerId, bowId2, "50", TargetFace.TF_122cm, PageRequest.of(0, 10, Sort.by("roundDate").ascending()));
        Assertions.assertEquals(1, pageableRounds.getContent().size());
        Assertions.assertEquals("50", pageableRounds.getContent().get(0).getDistance());
        Assertions.assertEquals(TargetFace.TF_122cm, pageableRounds.getContent().get(0).getTargetFace());
        Assertions.assertEquals(bowId2, pageableRounds.getContent().get(0).getBowId());

        // Test non-matching combination
        pageableRounds = roundRepository.findByArcherIdAndBowIdAndDistanceAndTargetFace(archerId, bowId1, "30", TargetFace.TF_122cm, PageRequest.of(0, 10, Sort.by("roundDate").ascending()));
        Assertions.assertEquals(0, pageableRounds.getContent().size());
    }

    @Test
    void findByArcherIdAndBowIdAndDistanceAndTargetFaceAndArchivedTest() {
        var pageableRounds = roundRepository.findByArcherIdAndBowIdAndDistanceAndTargetFaceAndArchived(archerId, bowId1, "30", TargetFace.TF_80cm, false, PageRequest.of(0, 10, Sort.by("roundDate").ascending()));
        Assertions.assertEquals(1, pageableRounds.getContent().size());
        Assertions.assertEquals("30", pageableRounds.getContent().get(0).getDistance());
        Assertions.assertEquals(TargetFace.TF_80cm, pageableRounds.getContent().get(0).getTargetFace());
        Assertions.assertEquals(bowId1, pageableRounds.getContent().get(0).getBowId());
        Assertions.assertFalse(pageableRounds.getContent().get(0).getArchived());

        pageableRounds = roundRepository.findByArcherIdAndBowIdAndDistanceAndTargetFaceAndArchived(archerId, bowId1, "30", TargetFace.TF_80cm, true, PageRequest.of(0, 10, Sort.by("roundDate").ascending()));
        Assertions.assertEquals(0, pageableRounds.getContent().size());

        pageableRounds = roundRepository.findByArcherIdAndBowIdAndDistanceAndTargetFaceAndArchived(archerId, bowId2, "50", TargetFace.TF_122cm, false, PageRequest.of(0, 10, Sort.by("roundDate").ascending()));
        Assertions.assertEquals(0, pageableRounds.getContent().size());

        pageableRounds = roundRepository.findByArcherIdAndBowIdAndDistanceAndTargetFaceAndArchived(archerId, bowId2, "50", TargetFace.TF_122cm, true, PageRequest.of(0, 10, Sort.by("roundDate").ascending()));
        Assertions.assertEquals(1, pageableRounds.getContent().size());
        Assertions.assertEquals("50", pageableRounds.getContent().get(0).getDistance());
        Assertions.assertEquals(TargetFace.TF_122cm, pageableRounds.getContent().get(0).getTargetFace());
        Assertions.assertEquals(bowId2, pageableRounds.getContent().get(0).getBowId());
        Assertions.assertTrue(pageableRounds.getContent().get(0).getArchived());
    }
}
