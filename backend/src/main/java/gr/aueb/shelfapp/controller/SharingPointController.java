package gr.aueb.shelfapp.controller;

import gr.aueb.shelfapp.dto.CreateSharingPointRequest;
import gr.aueb.shelfapp.dto.SharingPointDto;
import gr.aueb.shelfapp.service.SharingPointService;
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
@RequestMapping("/api/sharing-points")
public class SharingPointController {

    private final SharingPointService sharingPointService;

    public SharingPointController(SharingPointService sharingPointService) {
        this.sharingPointService = sharingPointService;
    }

    @GetMapping
    public List<SharingPointDto> getAll() {
        return sharingPointService.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SharingPointDto create(@Valid @RequestBody CreateSharingPointRequest request) {
        return sharingPointService.create(request);
    }
}
