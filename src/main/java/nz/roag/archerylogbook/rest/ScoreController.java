package nz.roag.archerylogbook.rest;

import nz.roag.archerylogbook.db.model.Score;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/archers/{archerId}/scores")
public class ScoreController {

    private final Logger logger = LoggerFactory.getLogger(ScoreController.class);

    @Autowired
    private ScoreService scoreService;

    @GetMapping("")
    public ResponseEntity<List<Score>> listAllScores(@PathVariable long archerId) {
        try {
            List<Score> scores = scoreService.listAllScores(archerId);
            return new ResponseEntity<>(scores, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            logger.error(e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Score> getScore(@PathVariable long id) {
        try {
            Score score = scoreService.getScore(id);
            return new ResponseEntity<>(score, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            logger.error(e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/")
    public ResponseEntity<?> addScore(@PathVariable long archerId, @RequestBody Score score) {
        try {
            scoreService.addScore(archerId, score);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (NoSuchElementException e) {
            logger.error(e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public void deleteScore(@PathVariable long id) {
        scoreService.deleteScore(id);
    }
}
