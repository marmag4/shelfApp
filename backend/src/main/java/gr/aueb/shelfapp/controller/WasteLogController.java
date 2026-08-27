package gr.aueb.shelfapp.controller;

import gr.aueb.shelfapp.dto.RecordWasteRequest;
import gr.aueb.shelfapp.dto.WasteLogDto;
import gr.aueb.shelfapp.security.CurrentUserProvider;
import gr.aueb.shelfapp.service.WasteLogService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/waste-logs")
public class WasteLogController {

    private final WasteLogService wasteLogService;
    private final CurrentUserProvider currentUserProvider;

    public WasteLogController(WasteLogService wasteLogService, CurrentUserProvider currentUserProvider) {
        this.wasteLogService = wasteLogService;
        this.currentUserProvider = currentUserProvider;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WasteLogDto recordWaste(@Valid @RequestBody RecordWasteRequest request) {
        return wasteLogService.recordWaste(request, currentUserProvider.getCurrentUserId());
    }

    /** Lists only the logged-in user's own waste logs. */
    @GetMapping
    public List<WasteLogDto> getMine() {
        return wasteLogService.findByUser(currentUserProvider.getCurrentUserId());
    }
}
