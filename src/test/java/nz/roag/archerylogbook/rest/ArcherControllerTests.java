package nz.roag.archerylogbook.rest;

import nz.roag.archerylogbook.db.ArcherRepository;
import nz.roag.archerylogbook.db.model.Archer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Sort;
import org.springframework.test.web.servlet.MockMvc;

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
class ArcherControllerTests extends AbstractControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ArcherRepository archerRepository;

    private Archer archer;
    private final String json = """
                {
                    "id":1,"firstName":"Robin",
                    "lastName":"Hood",
                    "email":"robin@hood.arch",
                    "clubId":11,
                    "bowList":[],
                    "scoreList":[],
                    "clubName":"Thieves",
                    "country":"England",
                    "city":"Nottingham"
                }
                """;

    @BeforeEach
    void beforeEach() {
        init();

        archer = new Archer();
        archer.setId(1L);
        archer.setFirstName("Robin");
        archer.setLastName("Hood");
        archer.setEmail("robin@hood.arch");
        archer.setClubId(11L);
        archer.setClubName("Thieves");
        archer.setCountry("England");
        archer.setCity("Nottingham");


    }



    @Test
    void listAllArchersTest() throws Exception {
        given(archerRepository.findAll(any(Sort.class)))
                .willReturn(List.of(archer));

        mvc.perform(get("/archers")
                        .headers(getHttpHeaders("/archers")))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(content().json("[" + json + "]"));
    }

    @Test
    void listAllArchersByClubTest() throws Exception {
        given(archerRepository.findByClubIdOrderByLastNameAsc(anyLong()))
                .willReturn(List.of(archer));

        mvc.perform(get("/archers?clubId=222")
                        .headers(getHttpHeaders("/archers?clubId=222")))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(content().json("[" + json + "]"));
    }

    @Test
    void getArcherTest() throws Exception {
        given(archerRepository.findById(anyLong()))
                .willReturn(Optional.of(archer));

        mvc.perform(get("/archers/111")
                        .headers(getHttpHeaders("/archers/111")))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(content().json(json));
    }

    @Test
    void addArcher() throws Exception {
        mvc.perform(post("/archers/")
                        .headers(getHttpHeaders("/archers/"))
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isOk());
    }

    @Test
    void deleteArcher() throws Exception {
        mvc.perform(delete("/archers/111")
                        .headers(getHttpHeaders("/archers/111")))
                .andExpect(status().isOk());
    }

    @Test
    void updateArcher() throws Exception {
        given(archerRepository.findById(anyLong()))
                .willReturn(Optional.of(archer));

        mvc.perform(post("/archers/1/")
                        .headers(getHttpHeaders("/archers/1/"))
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isOk());
    }
}
