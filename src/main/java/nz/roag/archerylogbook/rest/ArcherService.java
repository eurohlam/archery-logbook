package nz.roag.archerylogbook.rest;

import jakarta.transaction.Transactional;
import nz.roag.archerylogbook.db.ArcherRepository;
import nz.roag.archerylogbook.db.ClubRepository;
import nz.roag.archerylogbook.db.model.Archer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ArcherService {

    private final Logger logger = LoggerFactory.getLogger(ArcherService.class);

    @Autowired
    private ArcherRepository archerRepository;

    @Autowired
    private ClubRepository clubRepository;

    public List<Archer> listAllArchers() {
        return archerRepository.findAll(Sort.by("lastName").ascending()
                .and(Sort.by("firstName").ascending()));
    }

    public List<Archer> listArchersByClub(long clubId) {
        return archerRepository.findByClubIdOrderByLastNameAsc(clubId);
    }

    @Transactional
    public void addArcher(Archer archer) {
        logger.debug("Adding a new archer {}", archer);
        archerRepository.save(archer);
    }

    public Archer getArcher(long id) throws NoSuchElementException {
        return archerRepository.findById(id).orElseThrow(NoSuchElementException::new);
    }

    @Transactional
    public void deleteArcher(long id) {
        archerRepository.deleteById(id);
    }

}
