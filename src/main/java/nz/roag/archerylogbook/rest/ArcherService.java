package nz.roag.archerylogbook.rest;

import jakarta.transaction.Transactional;
import nz.roag.archerylogbook.db.ArcherRepository;
import nz.roag.archerylogbook.db.ClubRepository;
import nz.roag.archerylogbook.db.model.Archer;
import nz.roag.archerylogbook.db.model.Club;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class ArcherService {

    private final Logger logger = LoggerFactory.getLogger(ArcherService.class);

    @Autowired
    private ArcherRepository archerRepository;

    @Autowired
    private ClubRepository clubRepository;

    public Page<Archer> listAllArchers(int page, int size) {
        logger.debug("Getting list of all archers");
        return archerRepository.findByArchived(false,
                PageRequest.of(page, size,
                        Sort.by("lastName").ascending().and(Sort.by("firstName").ascending())));
    }

    public Page<Archer> listArchersByClub(long clubId, int page, int size) {
        logger.debug("Getting list of archers for clubId {}", clubId);
        return archerRepository.findByClubIdOrderByLastNameAsc(clubId, PageRequest.of(page, size,
                Sort.by("lastName").ascending().and(Sort.by("firstName").ascending())));
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
        Optional<Archer> archerOpt = archerRepository.findById(id);
        if (archerOpt.isPresent()) {
            if (archerOpt.get().getRoundList().isEmpty() && archerOpt.get().getBowList().isEmpty()
                    && archerOpt.get().getCompetitionList().isEmpty()) {
                //no child records in DB, so we can directly delete the archer
                archerRepository.deleteById(id);
            } else {
                //archer has a history, so we archive it instead of deletion
                var archer = archerOpt.get();
                archer.setArchived(true);
                archer.getBowList().forEach(bow -> bow.setArchived(true));
                archer.getRoundList().forEach(round -> round.setArchived(true));
                archer.getCompetitionList().forEach(cmpt -> cmpt.setArchived(true));
                archerRepository.save(archer);
            }
        }
    }

    @Transactional
    public void updateArcher(long archerId, Archer archer) {
        logger.info("Updating an archer {}", archer);
        var storedArcher = archerRepository.findById(archerId).orElseThrow(() -> new NoSuchElementException("Archer not found. archerId=" + archerId));
        storedArcher.setEmail(archer.getEmail());
        storedArcher.setFirstName(archer.getFirstName());
        storedArcher.setLastName(archer.getLastName());

        if (archer.getClubName() != null) {
            Club club = clubRepository.findFirstByName(archer.getClubName());
            if (club == null) {
                Club newClub = new Club();
                newClub.setName(archer.getClubName());
                newClub.setCity(archer.getCity());
                newClub.setCountry(archer.getCountry());
                club = clubRepository.save(newClub);
            }
            storedArcher.setClubId(club.getId());
        }
        archerRepository.save(storedArcher);
    }

}
