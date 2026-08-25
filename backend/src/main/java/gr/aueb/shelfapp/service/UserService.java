package gr.aueb.shelfapp.service;

import gr.aueb.shelfapp.dto.RegisterUserRequest;
import gr.aueb.shelfapp.dto.UserDto;
import gr.aueb.shelfapp.entity.User;
import gr.aueb.shelfapp.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserDto register(RegisterUserRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already registered");
        }

        String hashedPassword = passwordEncoder.encode(request.password());
        User user = new User(request.email(), hashedPassword, request.firstName(), request.lastName());
        user.setBirthDate(request.birthDate());
        user.setCity(request.city());
        user.setStreet(request.street());
        user.setStreetNumber(request.streetNumber());
        user.setPostalCode(request.postalCode());

        User saved = userRepository.save(user);
        return toDto(saved);
    }

    public UserDto findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        return toDto(user);
    }

    private UserDto toDto(User user) {
        return new UserDto(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getBirthDate(),
                user.getCity(),
                user.getStreet(),
                user.getStreetNumber(),
                user.getPostalCode()
        );
    }
}
