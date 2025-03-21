package it.florence.service;

import it.florence.dto.UserRequest;
import it.florence.dto.UserResponse;
import it.florence.model.User;
import it.florence.repository.UserRepository;
import it.florence.specification.UserSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@SpringBootTest
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserSpecification userSpecification;

    @InjectMocks
    private UserService userService;

    private UserRequest userRequest;

    @BeforeEach
    void setUp() {
        userRequest = new UserRequest();
        userRequest.setNome("Mario");
        userRequest.setCognome("Rossi");
        userRequest.setEmail("mario.rossi@example.com");
        userRequest.setIndirizzo("Via Roma");
        userRequest.setDataNascita(LocalDate.of(1990, 1, 1));
    }

    @Test
    void testGetAllUsers() {
        User user = new User(1L, "Mario", "Rossi", "mario.rossi@example.com", "Via Roma", LocalDate.of(1990, 1, 1));
        when(userRepository.findAll(Mockito.any(Specification.class))).thenReturn(List.of(user));

        List<UserResponse> userResponses = userService.getAllUsers("Mario", "Rossi");

        assertEquals(1, userResponses.size());
        assertEquals("Mario", userResponses.get(0).getNome());
        assertEquals("Rossi", userResponses.get(0).getCognome());
        assertEquals(LocalDate.of(1990, 1, 1), userResponses.get(0).getDataNascita());

        verify(userRepository, times(1)).findAll(Mockito.any(Specification.class));
    }

    @Test
    void testGetUserById() {
        User user = new User(1L, "Mario", "Rossi", "mario.rossi@example.com", "Via Roma", LocalDate.of(1990, 1, 1));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Optional<UserResponse> userResponse = userService.getUserById(1L);

        assertTrue(userResponse.isPresent());
        assertEquals("Mario", userResponse.get().getNome());
        assertEquals("Rossi", userResponse.get().getCognome());
        assertEquals(LocalDate.of(1990, 1, 1), userResponse.get().getDataNascita());

        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void testCreateUser() {
        User user = new User(1L, "Mario", "Rossi", "mario.rossi@example.com", "Via Roma", LocalDate.of(1990, 1, 1));
        when(userRepository.save(Mockito.any(User.class))).thenReturn(user);

        UserResponse userResponse = userService.createUser(userRequest);

        assertEquals("Mario", userResponse.getNome());
        assertEquals("Rossi", userResponse.getCognome());
        assertEquals(LocalDate.of(1990, 1, 1), userResponse.getDataNascita());

        verify(userRepository, times(1)).save(Mockito.any(User.class));
    }

    @Test
    void testDeleteUser() {
        doNothing().when(userRepository).deleteById(1L);

        userService.deleteUser(1L);

        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void testUpdateUser() {
        User existingUser = new User(1L, "Mario", "Rossi", "mario.rossi@example.com", "Via Roma", LocalDate.of(1990, 1, 1));
        UserRequest updateRequest = new UserRequest();
        updateRequest.setNome("Luigi");
        updateRequest.setCognome("Bianchi");
        updateRequest.setEmail("luigi.bianchi@example.com");
        updateRequest.setIndirizzo("Via Milano");
        updateRequest.setDataNascita(LocalDate.of(1992, 2, 2));

        User updatedUser = new User(1L, "Luigi", "Bianchi", "luigi.bianchi@example.com", "Via Milano", LocalDate.of(1992, 2, 2));
        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(Mockito.any(User.class))).thenReturn(updatedUser);

        Optional<UserResponse> userResponse = userService.updateUser(1L, updateRequest);

        assertTrue(userResponse.isPresent());
        assertEquals("Luigi", userResponse.get().getNome());
        assertEquals("Bianchi", userResponse.get().getCognome());
        assertEquals(LocalDate.of(1992, 2, 2), userResponse.get().getDataNascita());

        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(Mockito.any(User.class));
    }

    @Test
    void testImportUsersFromCSV() throws Exception {
        String csvContent = "nome,cognome,email,indirizzo,dataNascita\n" +
                "Mario,Rossi,mario.rossi@example.com,Via Roma,1990-01-01\n" +
                "Luigi,Bianchi,luigi.bianchi@example.com,Via Milano,1992-02-02";

        InputStream inputStream = new ByteArrayInputStream(csvContent.getBytes());
        MultipartFile file = new MockMultipartFile("file", "users.csv", "text/csv", inputStream);

        User user1 = new User(null, "Mario", "Rossi", "mario.rossi@example.com", "Via Roma", LocalDate.of(1990, 1, 1));
        User user2 = new User(null, "Luigi", "Bianchi", "luigi.bianchi@example.com", "Via Milano", LocalDate.of(1992, 2, 2));
        when(userRepository.saveAll(Mockito.anyList())).thenReturn(List.of(user1, user2));

         List<User> importedUsers = userService.importUsersFromCSV(file);

        assertEquals(2, importedUsers.size());
        assertEquals("Mario", importedUsers.get(0).getNome());
        assertEquals("Luigi", importedUsers.get(1).getNome());
        assertEquals(LocalDate.of(1990, 1, 1), importedUsers.get(0).getDataNascita());
        assertEquals(LocalDate.of(1992, 2, 2), importedUsers.get(1).getDataNascita());

        verify(userRepository, times(1)).saveAll(Mockito.anyList());
    }
}
