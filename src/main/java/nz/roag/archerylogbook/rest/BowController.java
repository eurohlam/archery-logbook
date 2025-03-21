package nz.roag.archerylogbook.rest;

import nz.roag.archerylogbook.db.model.Bow;
import nz.roag.archerylogbook.db.model.DistanceSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

import static nz.roag.archerylogbook.rest.ErrorMessage.getErrorJson;

@RestController
@RequestMapping("/archers/{archerId}/bows")
public class BowController {
    private final Logger logger = LoggerFactory.getLogger(BowController.class);

    @Autowired
    private BowService bowService;

    @GetMapping("")
    public ResponseEntity<?> listAllBows(@PathVariable long archerId) {
        try {
            List<Bow> bows = bowService.listAllBows(archerId);
            return new ResponseEntity<>(bows, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            logger.error(e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(getErrorJson("NOT_FOUND",
                            e.getMessage(),
                            "/archers/" + archerId + "/bows"));
        }

    }

    @GetMapping("/{bowId}")
    public ResponseEntity<?> getBow(@PathVariable long archerId, @PathVariable long bowId) {
        try {
            Bow bow = bowService.getBow(bowId);
            return new ResponseEntity<>(bow, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            logger.error(e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(getErrorJson("NOT_FOUND",
                            e.getMessage(),
                            "/archers/" + archerId + "/bows/" + bowId));
        }
    }

    @PostMapping("")
    public ResponseEntity<?> addBow(@PathVariable long archerId, @RequestBody Bow bow) {
        try {
            bowService.addBow(archerId, bow);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (NoSuchElementException e) {
            logger.error(e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(getErrorJson("NOT_FOUND",
                            e.getMessage(),
                            "/archers/" + archerId + "/bows"));
        }
    }

    @PutMapping("/{bowId}")
    public ResponseEntity<?> updateBow(@PathVariable long archerId, @PathVariable long bowId, @RequestBody Bow bow) {
        try {
            bowService.updateBow(bowId, bow);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (NoSuchElementException e) {
            logger.error(e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(getErrorJson("NOT_FOUND",
                            e.getMessage(),
                            "/archers/" + archerId + "/bows/" + bowId));
        }
    }

    @PatchMapping("/{bowId}")
    public ResponseEntity<?> addDistanceSettings(@PathVariable long archerId,
                                                 @RequestBody DistanceSettings distanceSettings,
                                                 @PathVariable long bowId) {
        try {
            bowService.addDistanceSettings(bowId, distanceSettings);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (NoSuchElementException e) {
            logger.error(e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(getErrorJson("NOT_FOUND",
                            e.getMessage(),
                            "/archers/" + archerId + "/bows/" + bowId));
        }
    }

    @DeleteMapping("/{bowId}")
    public void deleteBow(@PathVariable long archerId, @PathVariable long bowId) {
        bowService.deleteBow(bowId);
    }
}
