package nz.roag.archerylogbook.rest;

import nz.roag.archerylogbook.db.model.Round;
import nz.roag.archerylogbook.db.model.TargetFace;
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
@RequestMapping("/archers/{archerId}/rounds")
public class RoundController {

    private final Logger logger = LoggerFactory.getLogger(RoundController.class);

    @Autowired
    private RoundService roundService;

    @GetMapping("")
    public ResponseEntity<?> listAllRounds(@PathVariable long archerId,
                                           @RequestParam(name = "page", defaultValue = "0") int page,
                                           @RequestParam(name = "size", defaultValue = "20") int size,
                                           @RequestParam(name = "bowId", required = false) Long bowId,
                                           @RequestParam(name = "distance", required = false) String distance,
                                           @RequestParam(name = "targetFace", required = false) String targetFace,
                                           @RequestParam(name = "archived", required = false) Boolean archived) {
        try {
            if (page < 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(getErrorJson("BAD_REQUEST",
                                "The value of page parameter can not be less then 0",
                                "/archers/" + archerId + "/rounds"));
            }
            if (size < 1 || size > 100) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(getErrorJson("BAD_REQUEST",
                                "The value of size parameter should be between 1 and 100",
                                "/archers/" + archerId + "/rounds"));
            }

            // Parse targetFace if provided
            TargetFace targetFaceEnum = null;
            if (targetFace != null && !targetFace.isEmpty()) {
                try {
                    targetFaceEnum = TargetFace.fromValue(targetFace);
                } catch (IllegalArgumentException e) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(getErrorJson("BAD_REQUEST",
                                    "Invalid targetFace value: " + targetFace,
                                    "/archers/" + archerId + "/rounds"));
                }
            }

            // Use filtering method if any filter is provided, otherwise use the simple method
            org.springframework.data.domain.Page<Round> rounds;
            if (bowId != null || distance != null || targetFaceEnum != null || archived != null) {
                rounds = roundService.listRoundsWithFilters(archerId, page, size, bowId, distance, targetFaceEnum, archived);
            } else {
                rounds = roundService.listAllRounds(archerId, page, size);
            }

            ResultPage<Round> roundResultPage = new ResultPage<>();
            roundResultPage.setItems(rounds.getContent());
            roundResultPage.setTotalPages(rounds.getTotalPages());
            roundResultPage.setIsFirstPage(rounds.isFirst());
            roundResultPage.setIsLastPage(rounds.isLast());
            roundResultPage.setPageNumber(page);

            return ResponseEntity.status(HttpStatus.OK)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(roundResultPage);
        } catch (NoSuchElementException e) {
            logger.error(e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(getErrorJson("NOT_FOUND",
                            e.getMessage(),
                            "/archers/" + archerId + "/rounds"));
        }
    }

    @GetMapping("/{roundId}")
    public ResponseEntity<?> getRound(@PathVariable long archerId, @PathVariable long roundId) {
        try {
            Round round = roundService.getRound(roundId);
            return new ResponseEntity<>(round, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            logger.error(e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(getErrorJson("NOT_FOUND",
                            e.getMessage(),
                            "/archers/" + archerId + "/rounds/" + roundId));
        }
    }

    @PostMapping("")
    public ResponseEntity<?> addRound(@PathVariable long archerId, @RequestBody Round round) {
        try {
            roundService.addRound(archerId, round);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (NoSuchElementException e) {
            logger.error(e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(getErrorJson("NOT_FOUND",
                            e.getMessage(),
                            "/archers/" + archerId + "/rounds"));
        } catch (IllegalArgumentException e) {
            logger.error(e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(getErrorJson("BAD_REQUEST",
                            e.getMessage(),
                            "/archers/" + archerId + "/rounds"));
        }
    }

    @DeleteMapping("/{roundId}")
    public void deleteRound(@PathVariable long roundId) {
        roundService.deleteRound(roundId);
    }


    /*** Dashboard statistics ****/


    @GetMapping("/statistics/best")
    public ResponseEntity<?> listBestRounds(@PathVariable long archerId) {
        try {
            List<Round> rounds = roundService.listBestRounds(archerId);
            return new ResponseEntity<>(rounds, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            logger.error(e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(getErrorJson("NOT_FOUND",
                            e.getMessage(),
                            "/archers/" + archerId + "/rounds/best"));
        }
    }

    @GetMapping("/statistics/total")
    public ResponseEntity<Integer> getTotalRounds(@PathVariable long archerId) {
        try {
            var total = roundService.getTotalRounds(archerId);
            return new ResponseEntity<>(total, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            logger.error(e.getMessage(), e);
            return new ResponseEntity<>(0, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/statistics/total/lastMonth")
    public ResponseEntity<Integer> getTotalLastMonthRounds(@PathVariable long archerId) {
        try {
            var total = roundService.getTotalLastMonthRounds(archerId);
            return new ResponseEntity<>(total, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            logger.error(e.getMessage(), e);
            return new ResponseEntity<>(0, HttpStatus.NOT_FOUND);
        }
    }

}
