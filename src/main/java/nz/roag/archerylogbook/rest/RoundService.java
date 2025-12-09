package nz.roag.archerylogbook.rest;

import jakarta.transaction.Transactional;
import nz.roag.archerylogbook.db.ArcherRepository;
import nz.roag.archerylogbook.db.EndRepository;
import nz.roag.archerylogbook.db.ShotRepository;
import nz.roag.archerylogbook.db.RoundRepository;
import nz.roag.archerylogbook.db.model.Archer;
import nz.roag.archerylogbook.db.model.Round;
import nz.roag.archerylogbook.db.model.TargetFace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class RoundService {

    private final Logger logger = LoggerFactory.getLogger(RoundService.class);

    @Autowired
    private RoundRepository roundRepository;

    @Autowired
    private EndRepository endRepository;

    @Autowired
    private ShotRepository shotRepository;

    @Autowired
    private ArcherRepository archerRepository;

    public Page<Round> listAllRounds(long archerId, int page, int size) throws NoSuchElementException {
        logger.debug("Getting rounds for archerId {}; page {} size {}", archerId, page, size);
        var archer = archerRepository.findById(archerId).orElseThrow(() -> new NoSuchElementException("Archer not found. archerId=" + archerId));
        return roundRepository.findByArcherIdAndArchived(archer.getId(), false, PageRequest.of(page, size, Sort.by("roundDate").descending()));
    }

    /*** Filtering methods ***/

    public Page<Round> listRoundsWithFilters(long archerId, int page, int size, Long bowId, String distance, TargetFace targetFace, Boolean archived) throws NoSuchElementException {
        logger.debug("Getting rounds for archerId {} with filters: bowId={}, distance={}, targetFace={}, archived={}; page {} size {}", 
                archerId, bowId, distance, targetFace, archived, page, size);
        // Validate archer exists
        archerRepository.findById(archerId).orElseThrow(() -> new NoSuchElementException("Archer not found. archerId=" + archerId));
        
        var pageable = PageRequest.of(page, size, Sort.by("roundDate").descending());
        boolean isArchived = archived != null ? archived : false;
        
        // Determine which repository method to call based on provided filters
        if (bowId != null && distance != null && targetFace != null) {
            // All three filters
            return roundRepository.findByArcherIdAndBowIdAndDistanceAndTargetFaceAndArchived(
                    archerId, bowId, distance, targetFace, isArchived, pageable);
        } else if (bowId != null && distance != null) {
            // Bow + Distance
            return roundRepository.findByArcherIdAndBowIdAndDistanceAndArchived(
                    archerId, bowId, distance, isArchived, pageable);
        } else if (bowId != null && targetFace != null) {
            // Bow + TargetFace
            return roundRepository.findByArcherIdAndBowIdAndTargetFaceAndArchived(
                    archerId, bowId, targetFace, isArchived, pageable);
        } else if (distance != null && targetFace != null) {
            // Distance + TargetFace
            return roundRepository.findByArcherIdAndDistanceAndTargetFaceAndArchived(
                    archerId, distance, targetFace, isArchived, pageable);
        } else if (bowId != null) {
            // Bow only
            return roundRepository.findByArcherIdAndBowIdAndArchived(
                    archerId, bowId, isArchived, pageable);
        } else if (distance != null) {
            // Distance only
            return roundRepository.findByArcherIdAndDistanceAndArchived(
                    archerId, distance, isArchived, pageable);
        } else if (targetFace != null) {
            // TargetFace only
            return roundRepository.findByArcherIdAndTargetFaceAndArchived(
                    archerId, targetFace, isArchived, pageable);
        } else {
            // No filters, use archived flag only
            return roundRepository.findByArcherIdAndArchived(archerId, isArchived, pageable);
        }
    }

    @Transactional
    public void addRound(long archerId, Round round) throws NoSuchElementException, IllegalArgumentException {
        logger.info("Adding a new round {} for archerId {}", round, archerId);
        Archer archer = archerRepository.findById(archerId).orElseThrow(() -> new NoSuchElementException("Archer not found. archerId=" + archerId));
        if (archer.getBowList().stream().noneMatch(bow -> bow.getId() == round.getBowId())) {
            throw new IllegalArgumentException("Bow with bowId=" + round.getBowId() + " does not belong to archer with archerId=" + archerId);
        }
        if (round.getEndsCount() == 0) {
            throw new IllegalArgumentException("Round has to have at least one end");
        }
        if (round.getEnds().stream().anyMatch(e -> e.getShotsCount()==0)) {
            throw new IllegalArgumentException("End has to have at least one shot");
        }
        round.setArcherId(archer.getId());
        roundRepository.save(round);
    }

    public Round getRound(long id) throws NoSuchElementException {
        logger.debug("Getting round by id {}", id);
        return roundRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Round not found. roundId=" + id));
    }

    @Transactional
    public void deleteRound(long id) {
        logger.warn("Deleting round by id {}", id);
        roundRepository.setArchivedForRoundId(true, id);
    }

    /*** Dashboard statistics ***/

    public List<Round> listBestRounds(long archerId) throws NoSuchElementException {
        logger.debug("Getting best rounds for archerId {}", archerId);
        var archer = archerRepository.findById(archerId).orElseThrow(() -> new NoSuchElementException("Archer not found. archerId=" + archerId));
        return roundRepository.getBestRoundsByDistanceForArcherId(archerId);
    }

    public int getTotalRounds(long archerId) throws NoSuchElementException {
        logger.debug("Getting total number of rounds for archerId {}", archerId);
        var archer = archerRepository.findById(archerId).orElseThrow(() -> new NoSuchElementException("Archer not found. archerId=" + archerId));
        return roundRepository.getTotalRoundsByArcherId(archerId);
    }

    public int getTotalLastMonthRounds(long archerId) throws NoSuchElementException {
        logger.debug("Getting total number of rounds for last month for archerId {}", archerId);
        var archer = archerRepository.findById(archerId).orElseThrow(() -> new NoSuchElementException("Archer not found. archerId=" + archerId));
        return roundRepository.getTotalRoundsLastMonthByArcherId(archerId);
    }

}
