package nz.roag.archerylogbook.rest;

import nz.roag.archerylogbook.db.model.Archer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/archers")
public class ArcherController {

    @Autowired
    private ArcherService archerService;

    @GetMapping("")
    public List<Archer> listAllArchers() {
        return archerService.listAllArchers();
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
}
