package it.florence.controller;

import it.florence.dto.UserRequest;
import it.florence.repository.UserRepository;
import it.florence.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void testCreateUser() throws Exception {
        UserRequest userRequest = new UserRequest();
        userRequest.setNome("Mario");
        userRequest.setCognome("Rossi");
        userRequest.setEmail("mario.rossi@example.com");
        userRequest.setIndirizzo("Via Roma");
        userRequest.setDataNascita(LocalDate.of(1990, 1, 1));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/users")
                                              .contentType(MediaType.APPLICATION_JSON)
                                              .content("{\n" +
                                                      "\"nome\": \"Mario\",\n" +
                                                      "\"cognome\": \"Rossi\",\n" +
                                                      "\"email\": \"mario.rossi@example.com\",\n" +
                                                      "\"indirizzo\": \"Via Roma\",\n" +
                                                      "\"dataNascita\": \"1990-01-01\"\n" +
                                                      "}"))
               .andExpect(status().isOk())
               .andExpect(MockMvcResultMatchers.jsonPath("$.nome").value("Mario"))
               .andExpect(MockMvcResultMatchers.jsonPath("$.cognome").value("Rossi"))
               .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("mario.rossi@example.com"))
               .andExpect(MockMvcResultMatchers.jsonPath("$.indirizzo").value("Via Roma"));
    }

    @Test
    void testGetUserById() throws Exception {
        User user = new User(null, "Mario", "Rossi", "mario.rossi@example.com", "Via Roma", LocalDate.of(1990, 1, 1));
        User savedUser = userRepository.save(user);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/{id}", savedUser.getId()))
               .andExpect(status().isOk())
               .andExpect(MockMvcResultMatchers.jsonPath("$.nome").value("Mario"))
               .andExpect(MockMvcResultMatchers.jsonPath("$.cognome").value("Rossi"))
               .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("mario.rossi@example.com"))
               .andExpect(MockMvcResultMatchers.jsonPath("$.indirizzo").value("Via Roma"));
    }

    @Test
    void testUpdateUser() throws Exception {

        User user = new User(null, "Mario", "Rossi", "mario.rossi@example.com", "Via Roma", LocalDate.of(1990, 1, 1));
        User savedUser = userRepository.save(user);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/users/{id}", savedUser.getId())
                                              .contentType(MediaType.APPLICATION_JSON)
                                              .content("{\n" +
                                                      "\"nome\": \"Luigi\",\n" +
                                                      "\"cognome\": \"Bianchi\",\n" +
                                                      "\"email\": \"luigi.bianchi@example.com\",\n" +
                                                      "\"indirizzo\": \"Via Milano\",\n" +
                                                      "\"dataNascita\": \"1992-02-02\"\n" +
                                                      "}"))
               .andExpect(status().isOk())
               .andExpect(MockMvcResultMatchers.jsonPath("$.nome").value("Luigi"))
               .andExpect(MockMvcResultMatchers.jsonPath("$.cognome").value("Bianchi"))
               .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("luigi.bianchi@example.com"))
               .andExpect(MockMvcResultMatchers.jsonPath("$.indirizzo").value("Via Milano"));
    }

    @Test
    void testDeleteUser() throws Exception {
        User user = new User(null, "Mario", "Rossi", "mario.rossi@example.com", "Via Roma", LocalDate.of(1990, 1, 1));
        User savedUser = userRepository.save(user);
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/users/{id}", savedUser.getId()))
               .andExpect(status().isNoContent());
        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/{id}", savedUser.getId()))
               .andExpect(status().isNotFound());
    }

    @Test
    void testGetAllUsers() throws Exception {
        User user1 = new User(null, "Mario", "Rossi", "mario.rossi@example.com", "Via Roma", LocalDate.of(1990, 1, 1));
        User user2 = new User(null, "Luigi", "Bianchi", "luigi.bianchi@example.com", "Via Milano", LocalDate.of(1992, 2, 2));
        userRepository.save(user1);
        userRepository.save(user2);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/users"))
               .andExpect(status().isOk())
               .andExpect(MockMvcResultMatchers.jsonPath("$[0].nome").value("Mario"))
               .andExpect(MockMvcResultMatchers.jsonPath("$[1].nome").value("Luigi"));
    }
}
