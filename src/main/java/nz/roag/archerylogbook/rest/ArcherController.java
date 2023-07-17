package nz.roag.archerylogbook.rest;

import nz.roag.archerylogbook.db.model.Archer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.util.MultiValueMapAdapter;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/archers")
public class ArcherController {

    @Autowired
    private ArcherService archerService;

    @GetMapping("")
    public List<Archer> listAllArchers(@RequestParam(required = false) String clubId) {
        if (clubId != null) {
            return archerService.listArchersByClub(Long.parseLong(clubId));
        } else {
            return archerService.listAllArchers();
        }
    }


    @GetMapping("/{id}")
    public ResponseEntity<Archer> getArcher(@PathVariable long id) {
        try {
            Archer archer = archerService.getArcher(id);
            return new ResponseEntity<>(archer, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/")
    public void addArcher(@RequestBody Archer archer) {
        archerService.addArcher(archer);
    }

    @DeleteMapping("/{id}")
    public void deleteArcher(@PathVariable long id) {
        archerService.deleteArcher(id);
    }

    @PostMapping("/{id}/")
    public ResponseEntity<?> updateArcher(@PathVariable long id, @RequestBody Archer archer) {
        try {
            archerService.updateArcher(id, archer);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
