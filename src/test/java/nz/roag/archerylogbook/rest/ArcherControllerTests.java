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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ArcherControllerTests {

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
                    "clubName":null,
                    "country":null,
                    "city":null
                }
                """;

    @BeforeEach
    void beforeEach() {
        archer = new Archer();
        archer.setId(1L);
        archer.setFirstName("Robin");
        archer.setLastName("Hood");
        archer.setEmail("robin@hood.arch");
        archer.setClubId(11L);
    }

    @Test
    void listAllArchersTest() throws Exception {
        given(archerRepository.findAll(any(Sort.class)))
                .willReturn(List.of(archer));

        mvc.perform(get("/archers"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(content().json("[" + json + "]"));
    }

    @Test
    void listAllArchersByClubTest() throws Exception {
        given(archerRepository.findByClubIdOrderByLastNameAsc(anyLong()))
                .willReturn(List.of(archer));

        mvc.perform(get("/archers?clubId=222"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(content().json("[" + json + "]"));
    }

    @Test
    void getArcherTest() throws Exception {
        given(archerRepository.findById(anyLong()))
                .willReturn(Optional.of(archer));

        mvc.perform(get("/archers/111"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(content().json(json));
    }

    @Test
    void addArcher() throws Exception {
        mvc.perform(post("/archers/")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isOk());
    }

    @Test
    void deleteArcher() throws Exception {
        mvc.perform(delete("/archers/111"))
                .andExpect(status().isOk());
    }

}
