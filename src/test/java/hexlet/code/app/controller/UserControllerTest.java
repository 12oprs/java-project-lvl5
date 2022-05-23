package hexlet.code.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.junit5.api.DBRider;
import hexlet.code.app.dto.UserCreationDTO;
import hexlet.code.app.model.User;
import hexlet.code.app.repository.UserRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@Transactional
@DBRider
@DataSet("users.yml")
public final class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    static ObjectMapper mapper = new ObjectMapper();

    @Autowired
    UserRepository repository;

    private static UserCreationDTO testUserDTO;
    private static User expectedUser;
    private static String workDir = Paths.get(".").toAbsolutePath().normalize().toString();

    @BeforeAll
    static void init() throws IOException {
        testUserDTO = mapper.readValue(
                new File(workDir + "/src/test/resources/datasets/testUserDTO"),
                UserCreationDTO.class);
        expectedUser = new User(testUserDTO);
    }

    @Test
    void testGetUsers() throws Exception {
        MockHttpServletResponse response = mockMvc
                .perform(get("/api/users"))
                .andDo(print())
                .andExpectAll(status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();
        assertThat(response.getContentAsString()).contains("Ivanov", "Petrov");
    }

    @Test
    void testGetUserById() throws Exception {
        MockHttpServletResponse response = mockMvc
                .perform(get("/api/users/2"))
                .andDo(print())
                .andExpectAll(status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();
        assertThat(response.getContentAsString()).contains("Petrov");
        assertThat(response.getContentAsString()).doesNotContain("Ivanov");
    }

    @Test
    void testCreateUser() throws Exception {
        MockHttpServletResponse response = mockMvc
                .perform(post("/api/users/")
                        .content(mapper.writeValueAsString(testUserDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpectAll(status().isCreated(),
                        content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        assertEquals(3, repository.count());
        User actualUser = repository.findByEmail(expectedUser.getEmail()).get();
        assertEquals(expectedUser.getFirstName(), actualUser.getFirstName());
        assertEquals(expectedUser.getLastName(), actualUser.getLastName());
    }

    @Test
    void testUpdateUser() throws Exception {
        MockHttpServletResponse response = mockMvc
                .perform(patch("/api/users/2")
                        .content(mapper.writeValueAsString(testUserDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpectAll(status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        assertEquals(2, repository.count());
        User actualUser = repository.findByEmail(expectedUser.getEmail()).get();
        assertEquals(expectedUser.getFirstName(), actualUser.getFirstName());
        assertEquals(expectedUser.getLastName(), actualUser.getLastName());
    }

    @Test
    void testDeleteUser() throws Exception {
        assertEquals(2, repository.count());

        mockMvc.perform(delete("/api/users/2"))
                .andDo(print())
                .andExpect(status().isOk());

        assertEquals(1, repository.count());
    }

    @Test
    void testGetUserByIdFail() throws Exception {
        mockMvc
                .perform(get("/api/users/3"))
                .andDo(print())
                .andExpect(result -> assertNotNull(result.getResolvedException()))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(result -> assertThat(result.getResolvedException().getMessage())
                        .contains("User not found"));
    }

    @Test
    void testUpdateUserFail() throws Exception {
        mockMvc
                .perform(patch("/api/users/3")
                        .content(mapper.writeValueAsString(testUserDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(result -> assertNotNull(result.getResolvedException()))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(result -> assertThat(result.getResolvedException().getMessage())
                        .contains("Can't update. User not found"));
    }

    @Test
    void testDeleteUserFail() throws Exception {
        mockMvc.perform(delete("/api/users/3"))
                .andDo(print())
                .andExpect(status().isForbidden());

        assertEquals(2, repository.count());
    }

}
