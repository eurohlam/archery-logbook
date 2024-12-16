package nz.roag.archerylogbook.rest;

import nz.roag.archerylogbook.db.model.Archer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;

import java.util.NoSuchElementException;

import static nz.roag.archerylogbook.rest.ErrorMessage.getErrorJson;

@RestController
@RequestMapping("/archers")
public class ArcherController {

    @Autowired
    private ArcherService archerService;

    @GetMapping("")
    public ResultPage<Archer> listAllArchers(@RequestParam(required = false) String clubId,
                                       @RequestParam(name = "page", defaultValue = "0") int page,
                                       @RequestParam(name = "size", defaultValue = "20") int size) {
        ResultPage<Archer> resultPage = new ResultPage<>();
        Page<Archer> archers;
        if (clubId != null) {
             archers = archerService.listArchersByClub(Long.parseLong(clubId), page, size);
        } else {
             archers = archerService.listAllArchers(page, size);
        }
        resultPage.setItems(archers.getContent());
        resultPage.setPageNumber(page);
        resultPage.setIsFirstPage(archers.isFirst());
        resultPage.setIsLastPage(archers.isLast());
        resultPage.setTotalPages(archers.getTotalPages());
        return resultPage;
    }


    @GetMapping("/{id}")
    public ResponseEntity<?> getArcher(@PathVariable long id) {
        try {
            Archer archer = archerService.getArcher(id);
            return new ResponseEntity<>(archer, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(getErrorJson("NOT_FOUND",
                            e.getMessage(),
                            "/archers/" + id));
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
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(getErrorJson("NOT_FOUND",
                            e.getMessage(),
                            "/archers/" + id + "/"));
        }
    }
}
