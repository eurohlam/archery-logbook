package nz.roag.archerylogbook.rest;

import jakarta.transaction.Transactional;
import nz.roag.archerylogbook.db.ClubRepository;
import nz.roag.archerylogbook.db.model.Club;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ClubService {

    private final Logger logger = LoggerFactory.getLogger(ClubService.class);

    @Autowired
    private ClubRepository clubRepository;

    public List<Club> listAllClubs() {
        logger.debug("Getting list of all clubs");
        return clubRepository.findAll(Sort.by("name").ascending());
    }

    public Club getClub(long id) throws NoSuchElementException{
        logger.debug("Getting club by id {}", id);
        return clubRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Club not found. clubId=" + id));
    }

    @Transactional
    public void addClub(Club club) {
        logger.debug("Adding club {}", club);
        clubRepository.save(club);
    }

    @Transactional
    public void deleteClub(long id) {
        logger.debug("Deleting club with id {}", id);
        clubRepository.deleteById(id);
    }
}
