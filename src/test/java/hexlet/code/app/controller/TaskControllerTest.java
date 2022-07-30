package hexlet.code.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.junit5.api.DBRider;
import hexlet.code.app.TestUtils;
import hexlet.code.app.config.TestConfig;
import hexlet.code.app.dto.TaskDTO;
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
import java.util.HashSet;
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

    @Autowired
    MockMvc mockMvc;

    static ObjectMapper mapper = new ObjectMapper();

    @Autowired
    TaskRepository taskRepository;

    @Autowired
    LabelRepository labelRepository;

    @Autowired
    TestUtils testUtils;

    private static TaskDTO testTaskDTO;
    private static Task expectedTask;
    private static final String WORK_DIR = Paths.get(".").toAbsolutePath().normalize().toString();

    @BeforeAll
    static void init() throws Exception {
        testTaskDTO = mapper.readValue(
                new File(WORK_DIR + "/src/test/resources/datasets/testTaskDTO"),
                TaskDTO.class);
        expectedTask = new Task(testTaskDTO);
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
                .content(mapper.writeValueAsString(testTaskDTO))
                .contentType(MediaType.APPLICATION_JSON);

        testUtils.authorizedRequest(createRequest, "ivanov@mail.ru")
                .andDo(print())
                .andExpectAll(status().isCreated(),
                        content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        assertEquals(3, taskRepository.count());
        Task actualTask = taskRepository.findByName(testTaskDTO.getName()).get();
        assertEquals(expectedTask.getDescription(), actualTask.getDescription());
    }

    @Test
    void testUpdateTask() throws Exception {
        MockHttpServletRequestBuilder updateRequest = put("/api/tasks/2")
                .content(mapper.writeValueAsString(testTaskDTO))
                .contentType(MediaType.APPLICATION_JSON);

        testUtils.authorizedRequest(updateRequest, "petrov@mail.ru")
                .andDo(print())
                .andExpectAll(status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON));

        assertEquals(2, taskRepository.count());
        Task actualTask = taskRepository.findById(2L).get();
        assertEquals(expectedTask.getDescription(), actualTask.getDescription());
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
        Task testTask = taskRepository.findById(1L).get();
        taskRepository.findById(1L).get()
                .addLabel(labelRepository.findById(1L).get());
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
