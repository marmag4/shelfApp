package gr.aueb.shelfapp.controller;

import gr.aueb.shelfapp.dto.NotificationDto;
import gr.aueb.shelfapp.security.CurrentUserProvider;
import gr.aueb.shelfapp.service.NotificationService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * "What's about to expire?" for the logged-in user.
 */
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final CurrentUserProvider currentUserProvider;

    public NotificationController(NotificationService notificationService, CurrentUserProvider currentUserProvider) {
        this.notificationService = notificationService;
        this.currentUserProvider = currentUserProvider;
    }

    @GetMapping
    public List<NotificationDto> getMine() {
        return notificationService.findForUser(currentUserProvider.getCurrentUserId());
    }
}
