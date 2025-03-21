package it.florence.controller;

import it.florence.dto.UserRequest;
import it.florence.repository.UserRepository;
import it.florence.model.User;
import it.florence.specification.UserSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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
               .andExpect(jsonPath("$.nome").value("Mario"))
               .andExpect(jsonPath("$.cognome").value("Rossi"))
               .andExpect(jsonPath("$.email").value("mario.rossi@example.com"))
               .andExpect(jsonPath("$.indirizzo").value("Via Roma"));
    }

    @Test
    void testGetUserById() throws Exception {
        User user = new User(null, "Mario", "Rossi", "mario.rossi@example.com", "Via Roma", LocalDate.of(1990, 1, 1));
        User savedUser = userRepository.save(user);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/{id}", savedUser.getId()))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.nome").value("Mario"))
               .andExpect(jsonPath("$.cognome").value("Rossi"))
               .andExpect(jsonPath("$.email").value("mario.rossi@example.com"))
               .andExpect(jsonPath("$.indirizzo").value("Via Roma"));
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
               .andExpect(jsonPath("$.nome").value("Luigi"))
               .andExpect(jsonPath("$.cognome").value("Bianchi"))
               .andExpect(jsonPath("$.email").value("luigi.bianchi@example.com"))
               .andExpect(jsonPath("$.indirizzo").value("Via Milano"));
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
               .andExpect(jsonPath("$[0].nome").value("Mario"))
               .andExpect(jsonPath("$[1].nome").value("Luigi"));
    }

    @Test
    void testImportUsersFromCSV() throws Exception {
        String csvContent = "nome,cognome,email,indirizzo,dataNascita\n" +
                "Mario,Rossi,mario.rossi@example.com,Via Roma,1990-01-01\n" +
                "Luigi,Bianchi,luigi.bianchi@example.com,Via Milano,1992-02-02";

        InputStream inputStream = new ByteArrayInputStream(csvContent.getBytes());

        MockMultipartFile file = new MockMultipartFile("file", "users.csv", "text/csv", inputStream);

        mockMvc.perform(multipart("/api/users/import")
                       .file(file))
               .andExpect(status().isOk())
               .andExpect(content().string("File CSV importato correttamente, 2 utenti aggiunti."));


        assertEquals(2, userRepository.count());

        Specification<User> spec = UserSpecification.filterBy("Mario", "Rossi");
        User user1 = userRepository.findAll(spec).get(0);
        spec = UserSpecification.filterBy("Luigi", "Bianchi");
        User user2 = userRepository.findAll(spec).get(0);

        assertEquals("Mario", user1.getNome());
        assertEquals("Luigi", user2.getNome());
    }
}
