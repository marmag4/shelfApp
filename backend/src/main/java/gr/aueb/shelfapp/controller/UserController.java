package gr.aueb.shelfapp.controller;

import gr.aueb.shelfapp.dto.RegisterUserRequest;
import gr.aueb.shelfapp.dto.UpdateUserRequest;
import gr.aueb.shelfapp.dto.UserDto;
import gr.aueb.shelfapp.security.CurrentUserProvider;
import gr.aueb.shelfapp.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final CurrentUserProvider currentUserProvider;

    public UserController(UserService userService, CurrentUserProvider currentUserProvider) {
        this.userService = userService;
        this.currentUserProvider = currentUserProvider;
    }

    /** Public - no token yet.
     *  We will get one - via /api/auth/login afterwards.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto register(@Valid @RequestBody RegisterUserRequest request) {
        return userService.register(request);
    }

    /**
     * Returns MY OWN profile, so there's no id to swap out and peek at someone else's data with.
     */
    @GetMapping("/me")
    public UserDto getCurrentUser() {
        return userService.findById(currentUserProvider.getCurrentUserId());
    }

    /** Edits my own profile details (not email/password). */
    @PutMapping("/me")
    public UserDto updateCurrentUser(@Valid @RequestBody UpdateUserRequest request) {
        return userService.update(currentUserProvider.getCurrentUserId(), request);
    }

    /** Permanently deletes my own account and everything that belongs to it. */
    @DeleteMapping("/me")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCurrentUser() {
        userService.delete(currentUserProvider.getCurrentUserId());
    }
}
