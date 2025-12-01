package nz.roag.archerylogbook.rest;

import nz.roag.archerylogbook.db.*;
import nz.roag.archerylogbook.db.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class CompetitionControllerTest extends AbstractControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private ArcherRepository archerRepository;
    @MockitoBean
    private CompetitionRepository competitionRepository;
    @MockitoBean
    private RoundRepository roundRepository;
    @MockitoBean
    private EndRepository endRepository;
    @MockitoBean
    private ShotRepository shotRepository;

    private Archer archer;
    private Competition competition;
    private Round round;
    private End end;
    private final String json = """
            {
             "id": 0,
             "archerId": 1,
             "competitionDate": "2024-12-14",
             "competitionType": "Short Canadian 1200",
             "rounds": [
               {
                "id":0,
                "archerId":1,
                "bowId": 0,
                "roundDate":"2023-01-15T11:15:00.000+00:00",
                "distance":"30",
                "targetFace":"122cm",
                "comment":null,
                "country":"England",
                "city":"Nottingham",
                "ends":[
                    {
                        "id":0,
                        "endNumber":1,
                        "shots":[
                            {
                                "id":0,
                                "shotNumber":1,
                                "shotScore":10
                            }],
                        "sum":10,
                        "avg":"10.00",
                        "shotsCount":1
                    }],
                    "sum":10,
                    "avg":"10.00",
                    "endsCount":1
                }]
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

        round = new Round();
        round.setArcherId(archer.getId());
        round.setDistance("30");
        round.setTargetFace(TargetFace.TF_122cm);
        round.setCity("Nottingham");
        round.setRoundDate(Date.from(
                LocalDateTime.of(2023, 1, 15, 11, 15)
                        .toInstant(ZoneOffset.UTC)));
        round.setCountry("England");

        end = new End();
        end.setEndNumber((short) 1);

        var shot = new Shot();
        shot.setShotNumber((short) 1);
        shot.setShotScore((short) 10);
        end.setShots(List.of(shot));
        round.setEnds(List.of(end));

        competition = new Competition();
        competition.setCompetitionType(Competition.CompetitionType.SHORT_CANADIAN_1200);
        competition.setCompetitionDate(LocalDate.of(2024,12,14));
        competition.setArcherId(archer.getId());
        competition.setRounds(List.of(round));

        //Common mocks
        given(archerRepository.findById(anyLong()))
                .willReturn(Optional.of(archer));
    }

    @Test
    void listAllCompetitions() throws Exception {
        given(competitionRepository.findByArcherIdAndArchived(anyLong(), anyBoolean(), any(Pageable.class)))
                .willReturn(new PageImpl(List.of(competition)));

        var pageJson = """
                {
                "pageNumber":0,
                "totalPages":1,
                "isLastPage":true,
                "isFirstPage":true,
                "items":[
                """ + json + "]}";

        mvc.perform(get("/archers/1/competitions")
                        .headers(getHttpHeaders("/archers/1/competitions")))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(content().json(pageJson));

        mvc.perform(get("/archers/1/competitions?page=0&size=5")
                        .headers(getHttpHeaders("/archers/1/competitions?page=0&size=5")))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(content().json(pageJson));

        mvc.perform(get("/archers/1/competitions?page=-1&size=20")
                        .headers(getHttpHeaders("/archers/1/competitions?page=-1&size=20")))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json"))
                .andExpect(content().json("""
                        {
                            "status": "BAD_REQUEST",
                            "errorMessage": "The value of page parameter can not be less then 0",
                            "path": "/archers/1/competitions"
                        }
                        """));
    }

    @Test
    void getCompetition() throws Exception {
        given(competitionRepository.findById(anyLong()))
                .willReturn(Optional.of(competition));

        mvc.perform(get("/archers/1/competitions/1")
                        .headers(getHttpHeaders("/archers/1/competitions/1")))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(content().json(json));
    }

    @Test
    void addCompetition() throws Exception {
        given(competitionRepository.save(any()))
                .willReturn(competition);
        given(roundRepository.save(any()))
                .willReturn(round);
        given(endRepository.save(any()))
                .willReturn(end);

        String competitionJson = """
            {
            "competitionType": "WA1440",
            "rounds": [{
                "distance":"30",
                "targetFace":"122cm",
                "ends":[
                    {
                        "endNumber":1,
                        "shots":[
                            {
                                "shotNumber":1,
                                "shotScore":10
                            },
                            {
                                "shotNumber":2,
                                "shotScore":10
                            },
                            {
                                "shotNumber":3,
                                "shotScore":10
                            }
                        ]
                    },
                    {
                        "endNumber": 2,
                        "shots":[
                            {
                                "shotNumber":1,
                                "shotScore":10
                            },
                            {
                                "shotNumber":2,
                                "shotScore":10
                            },
                            {
                                "shotNumber":3,
                                "shotScore":10
                            }
                        ]
                    }
                ]
              }]
            }
            """;

        mvc.perform(post("/archers/1/competitions")
                        .headers(getHttpHeaders("/archers/1/competitions"))
                        .contentType("application/json")
                        .content(competitionJson))
                .andExpect(status().isOk());
    }


    @Test
    void deleteCompetition() throws Exception {
        mvc.perform(delete("/archers/1/competitions/1")
                        .headers(getHttpHeaders("/archers/1/competitions/1")))
                .andExpect(status().isOk());
    }
}
