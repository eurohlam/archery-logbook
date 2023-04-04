package nz.roag.archerylogbook.rest;

import jakarta.transaction.Transactional;
import nz.roag.archerylogbook.db.ClubRepository;
import nz.roag.archerylogbook.db.model.Club;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ClubService {

    @Autowired
    private ClubRepository clubRepository;

    public List<Club> listAllClubs() {
        return clubRepository.findAll(Sort.by("name").ascending());
    }

    public Club getClub(long id) throws NoSuchElementException{
        return clubRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Club not found. clubId=" + id));
    }

    @Transactional
    public void addClub(Club club) {
        clubRepository.save(club);
    }

    @Transactional
    public void deleteClub(long id) {
        clubRepository.deleteById(id);
    }
}
