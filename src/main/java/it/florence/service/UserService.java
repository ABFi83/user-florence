package it.florence.service;

import it.florence.dto.UserRequest;
import it.florence.dto.UserResponse;
import it.florence.model.User;
import it.florence.repository.UserRepository;
import it.florence.specification.UserSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private  UserRepository userRepository;



    //TODO CREARE UN MAPPER
    private UserResponse convertToResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getNome(),
                user.getCognome(),
                user.getEmail(),
                user.getIndirizzo(),
                user.getDataNascita()
        );
    }

    public List<UserResponse> getAllUsers(String nome, String cognome) {


        Specification<User> spec = UserSpecification.filterBy(nome, cognome);
        List<User> users = userRepository.findAll(spec);

        return users.stream()
                    .map(this::convertToResponse)
                    .toList();
    }

    public Optional<UserResponse> getUserById(Long id) {
        return userRepository.findById(id)
                             .map(this::convertToResponse);
    }

    public UserResponse createUser(UserRequest userRequest) {
        User user = new User();
        user.setNome(userRequest.getNome());
        user.setCognome(userRequest.getCognome());
        user.setEmail(userRequest.getEmail());
        user.setIndirizzo(userRequest.getIndirizzo());
        user.setDataNascita(userRequest.getDataNascita());

        User savedUser = userRepository.save(user);
        return convertToResponse(savedUser);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public Optional<UserResponse> updateUser(Long id, UserRequest userRequest) {
        return userRepository.findById(id).map(existingUser -> {
            existingUser.setNome(userRequest.getNome());
            existingUser.setCognome(userRequest.getCognome());
            existingUser.setEmail(userRequest.getEmail());
            existingUser.setIndirizzo(userRequest.getIndirizzo());
            existingUser.setDataNascita(userRequest.getDataNascita());

            User updatedUser = userRepository.save(existingUser);
            return convertToResponse(updatedUser);
        });
    }
}
