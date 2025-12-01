package nz.roag.archerylogbook.rest;

import jakarta.transaction.Transactional;
import nz.roag.archerylogbook.db.ArcherRepository;
import nz.roag.archerylogbook.db.EndRepository;
import nz.roag.archerylogbook.db.ShotRepository;
import nz.roag.archerylogbook.db.RoundRepository;
import nz.roag.archerylogbook.db.model.Archer;
import nz.roag.archerylogbook.db.model.Round;
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
