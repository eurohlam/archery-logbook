package nz.roag.archerylogbook.rest;

import nz.roag.archerylogbook.db.model.Score;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

import static nz.roag.archerylogbook.rest.ErrorMessage.getErrorJson;

@RestController
@RequestMapping("/archers/{archerId}/scores")
public class ScoreController {

    private final Logger logger = LoggerFactory.getLogger(ScoreController.class);

    @Autowired
    private ScoreService scoreService;

    @GetMapping("")
    public ResponseEntity<?> listAllScores(@PathVariable long archerId,
                                           @RequestParam(name = "page", defaultValue = "0") int page,
                                           @RequestParam(name = "size", defaultValue = "20") int size) {
        try {
            if (page < 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(getErrorJson("BAD_REQUEST",
                                "The value of page parameter can not be less then 0",
                                "/archers/" + archerId + "/scores"));
            }
            if (size < 1 || size > 100) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(getErrorJson("BAD_REQUEST",
                                "The value of size parameter should be between 1 and 100",
                                "/archers/" + archerId + "/scores"));
            }
            Page<Score> scores = scoreService.listAllScores(archerId, page, size);

            return ResponseEntity.status(HttpStatus.OK)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("totalPages", String.valueOf(scores.getTotalPages()))
                    .header("isLastPage", String.valueOf(scores.isLast()))
                    .header("isFirstPage", String.valueOf(scores.isFirst()))
                    .body(scores.getContent());
        } catch (NoSuchElementException e) {
            logger.error(e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(getErrorJson("NOT_FOUND",
                            e.getMessage(),
                            "/archers/" + archerId + "/scores"));
        }
    }

    @GetMapping("/{scoreId}")
    public ResponseEntity<?> getScore(@PathVariable long archerId, @PathVariable long scoreId) {
        try {
            Score score = scoreService.getScore(scoreId);
            return new ResponseEntity<>(score, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            logger.error(e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(getErrorJson("NOT_FOUND",
                            e.getMessage(),
                            "/archers/" + archerId + "/scores/" + scoreId));
        }
    }

    @PostMapping("/")
    public ResponseEntity<?> addScore(@PathVariable long archerId, @RequestBody Score score) {
        try {
            scoreService.addScore(archerId, score);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (NoSuchElementException e) {
            logger.error(e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(getErrorJson("NOT_FOUND",
                            e.getMessage(),
                            "/archers/" + archerId + "/scores/"));
        }
    }

    @DeleteMapping("/{scoreId}")
    public void deleteScore(@PathVariable long scoreId) {
        scoreService.deleteScore(scoreId);
    }
}
