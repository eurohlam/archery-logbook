package nz.roag.archerylogbook.rest;

import jakarta.transaction.Transactional;
import nz.roag.archerylogbook.db.*;
import nz.roag.archerylogbook.db.model.Archer;
import nz.roag.archerylogbook.db.model.Competition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class CompetitionService {

    private final Logger logger = LoggerFactory.getLogger(CompetitionService.class);

    @Autowired
    private CompetitionRepository competitionRepository;

    @Autowired
    private ArcherRepository archerRepository;

    public Page<Competition> listAllCompetitions(long archerId, int page, int size) throws NoSuchElementException {
        logger.debug("Getting competitions for archerId {}; page {} size {}", archerId, page, size);
        var archer = archerRepository.findById(archerId).orElseThrow(() -> new NoSuchElementException("Archer not found. archerId=" + archerId));
        return competitionRepository.findByArcherIdAndArchived(archer.getId(), false, PageRequest.of(page, size, Sort.by("competitionDate").descending()));
    }

    public Competition getCompetition(long id) throws NoSuchElementException {
        return competitionRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Competition not found. competitionId=" + id));
    }

    @Transactional
    public void addCompetition(long archerId, Competition competition) throws NoSuchElementException, IllegalArgumentException {
        logger.info("Adding a new competition {} for archerId {}", competition, archerId);
        Archer archer = archerRepository.findById(archerId).orElseThrow(() -> new NoSuchElementException("Archer not found. archerId=" + archerId));
        if (competition.getRoundsCount() == 0) {
            throw new IllegalArgumentException("Competition has to have at least one round");
        }
        if (competition.getRounds().stream().anyMatch(round -> round.getShotsCount()==0)) {
            throw new IllegalArgumentException("Round has to have at least one shot");
        }
        competition.setArcherId(archer.getId());
        for (var round : competition.getRounds()) {
            round.setArcherId(archer.getId());
        }
        competitionRepository.save(competition);
    }

    @Transactional
    public void deleteCompetition(long id) {
        competitionRepository.deleteById(id);
    }
}
