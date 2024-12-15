package nz.roag.archerylogbook.rest;

import nz.roag.archerylogbook.db.ArcherRepository;
import nz.roag.archerylogbook.db.EndRepository;
import nz.roag.archerylogbook.db.ShotRepository;
import nz.roag.archerylogbook.db.RoundRepository;
import nz.roag.archerylogbook.db.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class RoundControllerTests extends AbstractControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ArcherRepository archerRepository;
    @MockBean
    private RoundRepository roundRepository;
    @MockBean
    private EndRepository endRepository;
    @MockBean
    private ShotRepository shotRepository;

    private Archer archer;
    private Round round;
    private End end;
    private final String json = """
            {
                "id":0,
                "archerId":1,
                "bowId": null,
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

        //Common mocks
        given(archerRepository.findById(anyLong()))
                .willReturn(Optional.of(archer));
    }

    @Test
    void listAllRounds() throws Exception {
        given(roundRepository.findByArcherIdAndArchived(anyLong(), anyBoolean(), any(Pageable.class)))
                .willReturn(new PageImpl(List.of(round)));

        var pageJson = """
                {
                "pageNumber":0,
                "totalPages":1,
                "isLastPage":true,
                "isFirstPage":true,
                "items":[
                """ + json + "]}";

        mvc.perform(get("/archers/1/rounds")
                        .headers(getHttpHeaders("/archers/1/rounds")))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(content().json(pageJson));

        mvc.perform(get("/archers/1/rounds?page=0&size=5")
                        .headers(getHttpHeaders("/archers/1/rounds?page=0&size=5")))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(content().json(pageJson));

        mvc.perform(get("/archers/1/rounds?page=-1&size=20")
                        .headers(getHttpHeaders("/archers/1/rounds?page=-1&size=20")))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json"))
                .andExpect(content().json("""
                        {
                            "status": "BAD_REQUEST",
                            "errorMessage": "The value of page parameter can not be less then 0",
                            "path": "/archers/1/rounds"
                        }
                        """));
    }

    @Test
    void getRound() throws Exception {
        given(roundRepository.findById(anyLong()))
                .willReturn(Optional.of(round));

        mvc.perform(get("/archers/1/rounds/1")
                        .headers(getHttpHeaders("/archers/1/rounds/1")))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(content().json(json));
    }

    @Test
    void addRound() throws Exception {
        given(roundRepository.save(any()))
                .willReturn(round);
        given(endRepository.save(any()))
                .willReturn(end);

        String roundJson = """
            {
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
            }
            """;

        mvc.perform(post("/archers/1/rounds/")
                        .headers(getHttpHeaders("/archers/1/rounds/"))
                        .contentType("application/json")
                        .content(roundJson))
                .andExpect(status().isOk());
    }

    @Test
    void addRoundFailure() throws Exception {
        String noEndsRound = """
            {
                "distance":"30",
                "targetFace":"122cm"
            }
            """;
        mvc.perform(post("/archers/1/rounds/")
                        .headers(getHttpHeaders("/archers/1/rounds/"))
                        .contentType("application/json")
                        .content(noEndsRound))
                .andExpect(status().isBadRequest());

        String emptyEndRound = """
            {
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
                        "shots":[]
                    }
                ]
            }
            """;
        mvc.perform(post("/archers/1/rounds/")
                        .headers(getHttpHeaders("/archers/1/rounds/"))
                        .contentType("application/json")
                        .content(emptyEndRound))
                .andExpect(status().isBadRequest());

    }

    @Test
    void deleteRound() throws Exception {
        mvc.perform(delete("/archers/1/rounds/1")
                        .headers(getHttpHeaders("/archers/1/rounds/1")))
                .andExpect(status().isOk());
    }
}
