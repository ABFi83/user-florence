package it.florence.controller;


import it.florence.dto.UserRequest;
import it.florence.dto.UserResponse;
import it.florence.model.User;
import it.florence.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;



@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public List<UserResponse> getAllUsers(
            @RequestParam(name = "nome", required = false) String nome,
            @RequestParam(name = "cognome", required = false) String cognome) {
        return userService.getAllUsers(nome, cognome);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable("id") Long id) {
        Optional<UserResponse> user = userService.getUserById(id);
        return user.map(ResponseEntity::ok)
                   .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserRequest userRequest) {
        UserResponse createdUser = userService.createUser(userRequest);
        return ResponseEntity.ok(createdUser);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable("id") Long id,
            @Valid @RequestBody UserRequest userRequest) {
        return userService.updateUser(id, userRequest)
                          .map(ResponseEntity::ok)
                          .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }


    @PostMapping("/import")
    public ResponseEntity<String> importUsersFromCSV(@RequestParam("file") MultipartFile file) {
        try {
            List<User> users = userService.importUsersFromCSV(file);
            return ResponseEntity.ok("File CSV importato correttamente, " + users.size() + " utenti aggiunti.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Errore nell'importazione del file CSV: " + e.getMessage());
        }
    }
}
