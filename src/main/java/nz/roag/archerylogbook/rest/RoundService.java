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

import java.util.Collections;
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
    public void addRound(long archerId, Round round) throws NoSuchElementException {
        logger.info("Adding a new round {} for archerId {}", round, archerId);
        Archer archer = archerRepository.findById(archerId).orElseThrow(() -> new NoSuchElementException("Archer not found. archerId=" + archerId));
        round.setArcherId(archer.getId());
        var ends = round.getEnds();
        round.setEnds(Collections.emptyList());
        Round persistedRound = roundRepository.save(round);
        for (var end: ends) {
            end.setRoundId(persistedRound.getId());
            var shots = end.getShots();
            end.setShots(Collections.emptyList());
            var persistedEnd = endRepository.save(end);
            for (var shot: shots) {
                shot.setEndId(persistedEnd.getId());
                shotRepository.save(shot);
            }
        }
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
}
