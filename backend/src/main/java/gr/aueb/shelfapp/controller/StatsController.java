package gr.aueb.shelfapp.controller;

import gr.aueb.shelfapp.dto.StatsDto;
import gr.aueb.shelfapp.security.CurrentUserProvider;
import gr.aueb.shelfapp.service.StatsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Feature #5: statistics about the logged-in user's own pantry activity.
 */
@RestController
@RequestMapping("/api/stats")
public class StatsController {

    private final StatsService statsService;
    private final CurrentUserProvider currentUserProvider;

    public StatsController(StatsService statsService, CurrentUserProvider currentUserProvider) {
        this.statsService = statsService;
        this.currentUserProvider = currentUserProvider;
    }

    @GetMapping
    public StatsDto getMyStats() {
        return statsService.getStats(currentUserProvider.getCurrentUserId());
    }
}
