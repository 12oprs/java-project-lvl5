package hexlet.code.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.junit5.api.DBRider;
import hexlet.code.app.TestUtils;
import hexlet.code.app.config.TestConfig;
import hexlet.code.app.dto.TaskDTO;
import hexlet.code.app.model.Task;
import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.model.User;
import hexlet.code.app.repository.TaskRepository;
import hexlet.code.app.repository.TaskStatusRepository;
import hexlet.code.app.repository.UserRepository;
import hexlet.code.app.service.TaskStatusService;
import hexlet.code.app.service.UserService;
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
import java.io.IOException;
import java.nio.file.Paths;

import static hexlet.code.app.config.TestConfig.TEST_PROFILE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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

}
