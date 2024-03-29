package hexlet.code.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.junit5.api.DBRider;
import hexlet.code.app.TestUtils;
import hexlet.code.app.config.TestConfig;
import hexlet.code.app.model.Label;
import hexlet.code.app.repository.LabelRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Paths;

import static hexlet.code.app.config.TestConfig.TEST_PROFILE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ActiveProfiles(TEST_PROFILE)
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = TestConfig.class)
@Transactional
@DBRider
@DataSet("dataset.yml")
public class LabelControllerTest {

    private static final String WORK_DIR = Paths.get(".").toAbsolutePath().normalize().toString();
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static Label testLabel;
    private static Label expectedLabel;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LabelRepository repository;

    @Autowired
    private TestUtils testUtils;

    @BeforeAll
    static void init() throws IOException {
        testLabel = Label.builder().name("testLabel").build();
        expectedLabel = Label.builder().name("testLabel").build();
    }

    @Test
    void testGetLabels() throws Exception {
        MockHttpServletResponse response = mockMvc
                .perform(get("/api/labels"))
                .andDo(print())
                .andExpectAll(status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();
        assertThat(response.getContentAsString()).contains("bug", "feature");
    }

    @Test
    void testGetLabelById() throws Exception {
        MockHttpServletResponse response = mockMvc
                .perform(get("/api/labels/2"))
                .andDo(print())
                .andExpectAll(status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();
        assertThat(response.getContentAsString()).contains("feature");
        assertThat(response.getContentAsString()).doesNotContain("bug");
    }

    @Test
    void testCreateLabel() throws Exception {


        MockHttpServletRequestBuilder createRequest = post("/api/labels")
                .content(MAPPER.writeValueAsString(testLabel))
                .contentType(MediaType.APPLICATION_JSON);

        testUtils.authorizedRequest(createRequest, "petrov@mail.ru")
                .andDo(print())
                .andExpectAll(status().isCreated(),
                        content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        assertEquals(3, repository.count());
        Label actualLabel = repository.findByName(testLabel.getName()).get();
        assertEquals(expectedLabel.getName(), actualLabel.getName());
    }

    @Test
    void testUpdateLabel() throws Exception {
        MockHttpServletRequestBuilder updateRequest = put("/api/labels/2")
                .content(MAPPER.writeValueAsString(testLabel))
                .contentType(MediaType.APPLICATION_JSON);

        testUtils.authorizedRequest(updateRequest, "petrov@mail.ru")
                .andDo(print())
                .andExpectAll(status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON));

        assertEquals(2, repository.count());
        Label actualLabel = repository.findByName(testLabel.getName()).get();
        assertEquals(expectedLabel.getName(), actualLabel.getName());
    }

    @Test
    void testDeleteLabel() throws Exception {
        assertEquals(2, repository.count());

        testUtils.authorizedRequest(delete("/api/labels/2"), "petrov@mail.ru")
                .andDo(print())
                .andExpect(status().isOk());

        assertEquals(1, repository.count());
    }

    @Test
    void testUnauthorizedUser() throws Exception {
        MockHttpServletResponse createResponse = mockMvc
                .perform(post("/api/labels")
                        .content(MAPPER.writeValueAsString(testLabel))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andReturn()
                .getResponse();

        MockHttpServletResponse updateResponse = mockMvc
                .perform(put("/api/labels/2")
                        .content(MAPPER.writeValueAsString(testLabel))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andReturn()
                .getResponse();

        MockHttpServletResponse deleteResponse = mockMvc
                .perform(delete("/api/labels/2"))
                .andDo(print())
                .andReturn()
                .getResponse();

        assertThat(createResponse.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
        assertThat(updateResponse.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
        assertThat(deleteResponse.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }
}
