package nz.roag.archerylogbook.rest;

import nz.roag.archerylogbook.db.ArcherRepository;
import nz.roag.archerylogbook.db.EndRepository;
import nz.roag.archerylogbook.db.RoundRepository;
import nz.roag.archerylogbook.db.ScoreRepository;
import nz.roag.archerylogbook.db.model.Archer;
import nz.roag.archerylogbook.db.model.End;
import nz.roag.archerylogbook.db.model.Round;
import nz.roag.archerylogbook.db.model.Score;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ScoreControllerTests extends AbstractControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ArcherRepository archerRepository;
    @MockBean
    private ScoreRepository scoreRepository;
    @MockBean
    private EndRepository endRepository;
    @MockBean
    private RoundRepository roundRepository;

    private Archer archer;
    private Score score;
    private End end;
    private final String json = """
            {
                "id":0,
                "archerId":1,
                "bowId": null,
                "scoreDate":"2023-01-15T11:15:00.000+00:00",
                "match":"30",
                "comment":null,
                "country":"England",
                "city":"Nottingham",
                "ends":[
                    {
                        "id":0,
                        "scoreId":0,
                        "endNumber":1,
                        "rounds":[
                            {
                                "id":0,
                                "endId":0,
                                "roundNumber":1,
                                "roundScore":10
                            }],
                        "sum":10,
                        "avg":"10.00",
                        "roundsCount":1
                    }],
                    "sum":10,
                    "avg":"10.00",
                    "endsCount":1
                }]
            }
            """;

    @BeforeEach
    void beforeEach() throws Exception {
        init();

        archer = new Archer();
        archer.setId(1L);
        archer.setFirstName("Robin");
        archer.setLastName("Hood");
        archer.setEmail("robin@hood.arch");
        archer.setClubId(11L);

        score = new Score();
        score.setArcherId(archer.getId());
        score.setMatch("30");
        score.setCity("Nottingham");
        score.setScoreDate(Date.from(
                LocalDateTime.of(2023, 1, 15, 11, 15)
                        .toInstant(ZoneOffset.UTC)));
        score.setCountry("England");

        end = new End();
        end.setEndNumber((short) 1);

        var round = new Round();
        round.setRoundNumber((short) 1);
        round.setRoundScore((short) 10);
        end.setRounds(List.of(round));
        score.setEnds(List.of(end));

        //Common mocks
        given(archerRepository.findById(anyLong()))
                .willReturn(Optional.of(archer));
    }

    @Test
    void listAllScores() throws Exception {
        given(scoreRepository.findByArcherId(anyLong(), any(Pageable.class)))
                .willReturn(List.of(score));

        mvc.perform(get("/archers/1/scores")
                        .headers(getHttpHeaders("/archers/1/scores")))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(content().json("[" + json + "]"));

        mvc.perform(get("/archers/1/scores?page=0&size=20")
                        .headers(getHttpHeaders("/archers/1/scores?page=0&size=20")))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(content().json("[" + json + "]"));

        mvc.perform(get("/archers/1/scores?page=-1&size=20")
                        .headers(getHttpHeaders("/archers/1/scores?page=-1&size=20")))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json"))
                .andExpect(content().json("""
                        {
                            "status": "BAD_REQUEST",
                            "errorMessage": "The value of page parameter can not be less then 0",
                            "path": "/archers//archers/1/scores/scores"
                        }
                        """));
    }

    @Test
    void getScore() throws Exception {
        given(scoreRepository.findById(anyLong()))
                .willReturn(Optional.of(score));

        mvc.perform(get("/archers/1/scores/1")
                        .headers(getHttpHeaders("/archers/1/scores/1")))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(content().json(json));
    }

    @Test
    void addScore() throws Exception {
        given(scoreRepository.save(any()))
                .willReturn(score);
        given(endRepository.save(any()))
                .willReturn(end);

        mvc.perform(post("/archers/1/scores/")
                        .headers(getHttpHeaders("/archers/1/scores/"))
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isOk());
    }

    @Test
    void deleteScore() throws Exception {
        mvc.perform(delete("/archers/1/scores/1")
                        .headers(getHttpHeaders("/archers/1/scores/1")))
                .andExpect(status().isOk());
    }
}
