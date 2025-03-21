package nz.roag.archerylogbook.rest;

import nz.roag.archerylogbook.db.model.Competition;
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
@RequestMapping("/archers/{archerId}/competitions")
public class CompetitionController {

    private final Logger logger = LoggerFactory.getLogger(CompetitionController.class);

    @Autowired
    private CompetitionService competitionService;

    @GetMapping("")
    public ResponseEntity<?> listAllCompetitions(@PathVariable long archerId,
                                           @RequestParam(name = "page", defaultValue = "0") int page,
                                           @RequestParam(name = "size", defaultValue = "20") int size) {
        try {
            if (page < 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(getErrorJson("BAD_REQUEST",
                                "The value of page parameter can not be less then 0",
                                "/archers/" + archerId + "/competitions"));
            }
            if (size < 1 || size > 100) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(getErrorJson("BAD_REQUEST",
                                "The value of size parameter should be between 1 and 100",
                                "/archers/" + archerId + "/competitions"));
            }
            Page<Competition> competitions = competitionService.listAllCompetitions(archerId, page, size);

            ResultPage<Competition> competitionResultPage = new ResultPage<>();
            competitionResultPage.setItems(competitions.getContent());
            competitionResultPage.setTotalPages(competitions.getTotalPages());
            competitionResultPage.setIsFirstPage(competitions.isFirst());
            competitionResultPage.setIsLastPage(competitions.isLast());
            competitionResultPage.setPageNumber(page);

            return ResponseEntity.status(HttpStatus.OK)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(competitionResultPage);
        } catch (NoSuchElementException e) {
            logger.error(e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(getErrorJson("NOT_FOUND",
                            e.getMessage(),
                            "/archers/" + archerId + "/competitions"));
        }
    }

    @GetMapping("/{competitionId}")
    public ResponseEntity<?> getCompetition(@PathVariable long archerId, @PathVariable long competitionId) {
        try {
            Competition competition = competitionService.getCompetition(competitionId);
            return new ResponseEntity<>(competition, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            logger.error(e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(getErrorJson("NOT_FOUND",
                            e.getMessage(),
                            "/archers/" + archerId + "/competitions/" + competitionId));
        }
    }


    @PostMapping("")
    public ResponseEntity<?> addCompetition(@PathVariable long archerId, @RequestBody Competition competition) {
        try {
            competitionService.addCompetition(archerId, competition);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (NoSuchElementException e) {
            logger.error(e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(getErrorJson("NOT_FOUND",
                            e.getMessage(),
                            "/archers/" + archerId + "/competitions"));
        } catch (IllegalArgumentException e) {
            logger.error(e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(getErrorJson("BAD_REQUEST",
                            e.getMessage(),
                            "/archers/" + archerId + "/competitions"));
        }
    }

    @DeleteMapping("/{competitionId}")
    public void deleteCompetition(@PathVariable long competitionId) {
        competitionService.deleteCompetition(competitionId);
    }
}
