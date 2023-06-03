package nz.roag.archerylogbook.rest;

import nz.roag.archerylogbook.db.model.Bow;
import nz.roag.archerylogbook.db.model.DistanceSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/archers/{archerId}/bows")
public class BowController {
    private final Logger logger = LoggerFactory.getLogger(BowController.class);

    @Autowired
    private BowService bowService;

    @GetMapping("")
    public ResponseEntity<List<Bow>> listAllBows(@PathVariable long archerId) {
        try {
            List<Bow> bows = bowService.listAllBows(archerId);
            return new ResponseEntity<>(bows, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            logger.error(e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

    }

    @GetMapping("/{bowId}")
    public ResponseEntity<Bow> getBow(@PathVariable long bowId) {
        try {
            Bow bow = bowService.getBow(bowId);
            return new ResponseEntity<>(bow, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            logger.error(e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/")
    public ResponseEntity<?> addBow(@PathVariable long archerId, @RequestBody Bow bow) {
        try {
            bowService.addBow(archerId, bow);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (NoSuchElementException e) {
            logger.error(e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/{bowId}/")
    public ResponseEntity<?> updateBow(@RequestBody Bow bow) {
        try {
            bowService.updateBow(bow);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (NoSuchElementException e) {
            logger.error(e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{bowId}/")
    public ResponseEntity<?> addDistanceSettings(@RequestBody DistanceSettings distanceSettings, @PathVariable long bowId) {
        try {
            bowService.addDistanceSettings(bowId, distanceSettings);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (NoSuchElementException e) {
            logger.error(e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public void deleteBow(@PathVariable long archerId, @PathVariable long id) {
        bowService.deleteBow(id);
    }
}
