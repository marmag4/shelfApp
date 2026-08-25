package gr.aueb.shelfapp.health;

import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Just a way to prove the backend started and can talk back.
 * We'll delete this once we have real endpoints (pantry items, etc.).
 */
@RestController
public class HealthController {

    @GetMapping("/api/health")
    public Map<String, String> health() {
        return Map.of(
            "status", "UP",
            "message", "ShelfApp backend is running"
        );
    }

}
