package gr.aueb.shelfapp.controller;

import gr.aueb.shelfapp.dto.CreateDonationRequest;
import gr.aueb.shelfapp.dto.DonationDto;
import gr.aueb.shelfapp.service.DonationService;
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
@RequestMapping("/api/donations")
public class DonationController {

    private final DonationService donationService;

    public DonationController(DonationService donationService) {
        this.donationService = donationService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DonationDto create(@Valid @RequestBody CreateDonationRequest request) {
        return donationService.create(request);
    }

    @GetMapping
    public List<DonationDto> getByUser(@RequestParam Long userId) {
        return donationService.findByUser(userId);
    }
}
