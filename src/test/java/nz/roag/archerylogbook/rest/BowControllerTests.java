package nz.roag.archerylogbook.rest;

import nz.roag.archerylogbook.db.ArcherRepository;
import nz.roag.archerylogbook.db.BowRepository;
import nz.roag.archerylogbook.db.model.Archer;
import nz.roag.archerylogbook.db.model.Bow;
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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class BowControllerTests extends AbstractControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ArcherRepository archerRepository;
    @MockBean
    private BowRepository bowRepository;

    private Archer archer;
    private Bow bow;
    private final String bowJson = """
                {
                    "id":0,
                    "archerId":1,
                    "name":"Test bow",
                    "type":"RECURVE",
                    "level":"INTERMEDIATE",
                    "poundage":"24-26",
                    "compoundModel":null,
                    "riserModel":"Test riser",
                    "limbsModel":null,
                    "traditionalModel":null,
                    "distanceSettingsList":[]
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

        bow =  new Bow();
        bow.setArcherId(archer.getId());
        bow.setName("Test bow");
        bow.setType(Bow.Type.RECURVE);
        bow.setLevel(Bow.Level.INTERMEDIATE);
        bow.setPoundage("24-26");
        bow.setRiserModel("Test riser");

        archer.setBowList(List.of(bow));

        //Common mocks
        given(archerRepository.findById(anyLong()))
                .willReturn(Optional.of(archer));
        given(bowRepository.findById(anyLong()))
                .willReturn(Optional.of(bow));
    }

    @Test
    void listAllBows() throws Exception{
        given(bowRepository.findByArcherIdAndArchived(anyLong(), anyBoolean(), any(Sort.class)))
                .willReturn(List.of(bow));

        mvc.perform(get("/archers/1/bows")
                        .headers(getHttpHeaders("/archers/1/bows")))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(content().json("[" + bowJson + "]"));
    }

    @Test
    void getBow() throws Exception{
        mvc.perform(get("/archers/1/bows/0")
                        .headers(getHttpHeaders("/archers/1/bows/0")))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(content().json(bowJson));
    }

    @Test
    void addBow() throws Exception{
        mvc.perform(post("/archers/1/bows/")
                        .headers(getHttpHeaders("/archers/1/bows/"))
                        .contentType("application/json")
                        .content(bowJson))
                .andExpect(status().isOk());
    }

    @Test
    void deleteBow() throws Exception{
        mvc.perform(delete("/archers/1/bows/0")
                        .headers(getHttpHeaders("/archers/1/bows/0")))
                .andExpect(status().isOk());
    }

    @Test
    void addDistanceSettings() throws Exception{
        var distanceSettingsJson = """
            {
                "distance": "50", 
                "sight": "12",
                "isTested": true
            }
            """;
        mvc.perform(put("/archers/1/bows/1/")
                        .headers(getHttpHeaders("/archers/1/bows/1/"))
                        .contentType("application/json")
                        .content(distanceSettingsJson))
                .andExpect(status().isOk());
    }

    @Test
    void updateBow() throws Exception{
        mvc.perform(post("/archers/1/bows/1/")
                        .headers(getHttpHeaders("/archers/1/bows/1/"))
                        .contentType("application/json")
                        .content(bowJson))
                .andExpect(status().isOk());
    }
}
