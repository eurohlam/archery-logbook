package nz.roag.archerylogbook.rest;

import nz.roag.archerylogbook.db.model.Score;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/archers/{archerId}/scores")
public class ScoreController {

    @Autowired
    private ScoreService scoreService;

    @GetMapping("")
    public List<Score> listAllScores(@PathVariable long archerId) {
        return scoreService.listAllScores(archerId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Score> getScore(@PathVariable long id) {
        try {
            Score score = scoreService.getScore(id);
            return new ResponseEntity<>(score, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/")
    public ResponseEntity<?> addScore(@PathVariable long archerId, @RequestBody Score score) {
        try {
            scoreService.addScore(archerId, score);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public void deleteScore(@PathVariable long id) {
        scoreService.deleteScore(id);
    }
}
