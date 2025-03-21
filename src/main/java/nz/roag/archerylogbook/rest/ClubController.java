package nz.roag.archerylogbook.rest;

import nz.roag.archerylogbook.db.model.Club;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

import static nz.roag.archerylogbook.rest.ErrorMessage.getErrorJson;

@RestController
@RequestMapping("/clubs")
public class ClubController {


    @Autowired
    private ClubService clubService;

    @GetMapping("")
    public List<Club> listAllClubs() {
        return clubService.listAllClubs();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getClub(@PathVariable long id) {
        try {
            Club club = clubService.getClub(id);
            return new ResponseEntity<>(club, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(getErrorJson("NOT_FOUND",
                            e.getMessage(),
                            "/clubs/" + id));
        }
    }

    @PostMapping("")
    public void addClub(@RequestBody Club club) {
        clubService.addClub(club);
    }

    @DeleteMapping("/{id}")
    public void deleteClub(@PathVariable long id) {
        clubService.deleteClub(id);
    }
}
