package hexlet.code.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.junit5.api.DBRider;
import hexlet.code.app.TestUtils;
import hexlet.code.app.config.TestConfig;
import hexlet.code.app.dto.TaskDto;
import hexlet.code.app.model.Label;
import hexlet.code.app.model.Task;
import hexlet.code.app.repository.LabelRepository;
import hexlet.code.app.repository.TaskRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.nio.file.Paths;
import java.util.Set;

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
public class TaskControllerTest {

    private static final String WORK_DIR = Paths.get(".").toAbsolutePath().normalize().toString();
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static TaskDto testTaskDto;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private TestUtils testUtils;

    @BeforeAll
    static void init() throws Exception {
        testTaskDto = MAPPER.readValue(
                new File(WORK_DIR + "/src/test/resources/datasets/testTaskDTO"),
                TaskDto.class);
    }

    @Test
    void testGetTasks() throws Exception {
        MockHttpServletResponse response = mockMvc
                .perform(get("/api/tasks"))
                .andDo(print())
                .andExpectAll(status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();
        assertThat(response.getContentAsString()).contains("testTask1", "testTask2");
    }

    @Test
    void testGetTaskById() throws Exception {
        MockHttpServletResponse response =
                testUtils.authorizedRequest(get("/api/tasks/2"), "petrov@mail.ru")
                        .andDo(print())
                        .andExpectAll(status().isOk(),
                                content().contentType(MediaType.APPLICATION_JSON))
                        .andReturn()
                        .getResponse();
        assertThat(response.getContentAsString()).contains("testTask2");
        assertThat(response.getContentAsString()).doesNotContain("testTask1");
    }

    @Test
    void testCreateTask() throws Exception {
        MockHttpServletRequestBuilder createRequest = post("/api/tasks")
                .content(MAPPER.writeValueAsString(testTaskDto))
                .contentType(MediaType.APPLICATION_JSON);

        testUtils.authorizedRequest(createRequest, "ivanov@mail.ru")
                .andDo(print())
                .andExpectAll(status().isCreated(),
                        content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        assertEquals(3, taskRepository.count());
        Task actualTask = taskRepository.findByName(testTaskDto.getName()).get();
        assertEquals(testTaskDto.getDescription(), actualTask.getDescription());
    }

    @Test
    void testUpdateTask() throws Exception {
        MockHttpServletRequestBuilder updateRequest = put("/api/tasks/2")
                .content(MAPPER.writeValueAsString(testTaskDto))
                .contentType(MediaType.APPLICATION_JSON);

        testUtils.authorizedRequest(updateRequest, "petrov@mail.ru")
                .andDo(print())
                .andExpectAll(status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON));

        assertEquals(2, taskRepository.count());
        Task actualTask = taskRepository.findById(2L).get();
        assertEquals(testTaskDto.getDescription(), actualTask.getDescription());
    }

    @Test
    void testDeleteTask() throws Exception {
        assertEquals(2, taskRepository.count());

        testUtils.authorizedRequest(delete("/api/tasks/1"), "ivanov@mail.ru")
                .andDo(print())
                .andExpect(status().isOk());

        assertEquals(1, taskRepository.count());
    }

    @Test
    void testDeleteTaskFail() throws Exception {
        assertEquals(2, taskRepository.count());

        testUtils.authorizedRequest(delete("/api/tasks/1"), "petrov@mail.ru")
                .andDo(print())
                .andExpect(status().isForbidden());

        assertEquals(2, taskRepository.count());
    }

    //@Disabled
    @Test
    void testTaskFiltration() throws Exception {
        final Task testTask = taskRepository.findById(1L).get();
        Set<Label> labels = testTask.getLabels();
        labels.add(labelRepository.findById(1L).get());
        testTask.setLabels(labels);
        taskRepository.save(testTask);

        MockHttpServletResponse response = mockMvc
                .perform(get("/api/tasks?taskStatus=1&executorId=1&labels=1"))
                .andDo(print())
                .andExpectAll(status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();
        assertThat(response.getContentAsString()).contains("testTask1");
        assertThat(response.getContentAsString()).doesNotContain("testTask2");

        MockHttpServletResponse response2 = mockMvc
                .perform(get("/api/tasks?taskStatus=2"))
                .andDo(print())
                .andExpectAll(status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();
        assertThat(response2.getContentAsString())
                .doesNotContain("testTask1")
                .doesNotContain("testTask2");
    }

}
