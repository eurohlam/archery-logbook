package nz.roag.archerylogbook.rest;

import jakarta.transaction.Transactional;
import nz.roag.archerylogbook.db.ArcherRepository;
import nz.roag.archerylogbook.db.ClubRepository;
import nz.roag.archerylogbook.db.model.Archer;
import nz.roag.archerylogbook.db.model.Club;
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
        logger.debug("Getting list of all archers");
        return archerRepository.findAll(Sort.by("lastName").ascending()
                .and(Sort.by("firstName").ascending()));
    }

    public List<Archer> listArchersByClub(long clubId) {
        logger.debug("Getting list of archers for clubId {}", clubId);
        return archerRepository.findByClubIdOrderByLastNameAsc(clubId);
    }

    @Transactional
    public void addArcher(Archer archer) {
        logger.info("Adding a new archer {}", archer);
        if (archer.getClubName() != null) {
            Club club = clubRepository.findFirstByName(archer.getClubName());
            if (club == null) {
                Club newClub = new Club();
                newClub.setName(archer.getClubName());
                newClub.setCity(archer.getCity());
                newClub.setCountry(archer.getCountry());
                club = clubRepository.save(newClub);
            }
            archer.setClubId(club.getId());
        }
        archerRepository.save(archer);
    }

    public Archer getArcher(long id) throws NoSuchElementException {
        logger.debug("Getting archer by id {}", id);
        return archerRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Archer not found. archerId=" + id));
    }

    @Transactional
    public void deleteArcher(long id) {
        logger.warn("Deleting archer with id {}", id);
        archerRepository.deleteById(id);
    }

}
