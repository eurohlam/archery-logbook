package nz.roag.archerylogbook.rest;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
public class ScorePage<Score> {

    @Setter
    private int pageNumber = 0;

    @Setter
    private int totalPages = 1;

    @Setter
    private Boolean isLastPage = true;

    @Setter
    private Boolean isFirstPage = true;

    @Setter
    private List<Score> scores;
}
