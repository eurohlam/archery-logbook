package nz.roag.archerylogbook.rest;

import jakarta.transaction.Transactional;
import nz.roag.archerylogbook.db.ArcherRepository;
import nz.roag.archerylogbook.db.EndRepository;
import nz.roag.archerylogbook.db.RoundRepository;
import nz.roag.archerylogbook.db.ScoreRepository;
import nz.roag.archerylogbook.db.model.Archer;
import nz.roag.archerylogbook.db.model.Score;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ScoreService {

    private final Logger logger = LoggerFactory.getLogger(ScoreService.class);

    @Autowired
    private ScoreRepository scoreRepository;

    @Autowired
    private EndRepository endRepository;

    @Autowired
    private RoundRepository roundRepository;

    @Autowired
    private ArcherRepository archerRepository;

    public List<Score> listAllScores(long archerId) throws NoSuchElementException {
        logger.debug("Getting scores for archerId {}", archerId);
        var archer = archerRepository.findById(archerId).orElseThrow(() -> new NoSuchElementException("Archer not found. archerId=" + archerId));
        return scoreRepository.findByArcherId(archer.getId(), Sort.by("scoreDate").descending());
    }

    @Transactional
    public void addScore(long archerId, Score score) throws NoSuchElementException {
        logger.info("Adding a new score {} for archerId {}", score, archerId);
        Archer archer = archerRepository.findById(archerId).orElseThrow(() -> new NoSuchElementException("Archer not found. archerId=" + archerId));
        score.setArcherId(archer.getId());
        var ends = score.getEnds();
        score.setEnds(Collections.emptyList());
        Score persistedScore = scoreRepository.save(score);
        for (var end: ends) {
            end.setScoreId(persistedScore.getId());
            var rounds = end.getRounds();
            end.setRounds(Collections.emptyList());
            var persistedEnd = endRepository.save(end);
            for (var round: rounds) {
                round.setEndId(persistedEnd.getId());
                roundRepository.save(round);
            }
        }
    }

    public Score getScore(long id) throws NoSuchElementException {
        logger.debug("Getting score by id {}", id);
        return scoreRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Score not found. scoreId=" + id));
    }

    @Transactional
    public void deleteScore(long id) {
        logger.warn("Deleting score by id {}", id);
        scoreRepository.deleteById(id);
    }
}
