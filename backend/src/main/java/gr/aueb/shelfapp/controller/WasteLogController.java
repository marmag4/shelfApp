package gr.aueb.shelfapp.controller;

import gr.aueb.shelfapp.dto.RecordWasteRequest;
import gr.aueb.shelfapp.dto.WasteLogDto;
import gr.aueb.shelfapp.service.WasteLogService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/waste-logs")
public class WasteLogController {

    private final WasteLogService wasteLogService;

    public WasteLogController(WasteLogService wasteLogService) {
        this.wasteLogService = wasteLogService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WasteLogDto recordWaste(@Valid @RequestBody RecordWasteRequest request) {
        return wasteLogService.recordWaste(request);
    }

    @GetMapping
    public List<WasteLogDto> getByUser(@RequestParam Long userId) {
        return wasteLogService.findByUser(userId);
    }
}
