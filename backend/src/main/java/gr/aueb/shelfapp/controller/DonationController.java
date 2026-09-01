package gr.aueb.shelfapp.controller;

import gr.aueb.shelfapp.dto.CreateDonationRequest;
import gr.aueb.shelfapp.dto.DonationDto;
import gr.aueb.shelfapp.security.CurrentUserProvider;
import gr.aueb.shelfapp.service.DonationService;
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
@RequestMapping("/api/donations")
public class DonationController {

    private final DonationService donationService;
    private final CurrentUserProvider currentUserProvider;

    public DonationController(DonationService donationService, CurrentUserProvider currentUserProvider) {
        this.donationService = donationService;
        this.currentUserProvider = currentUserProvider;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DonationDto create(@Valid @RequestBody CreateDonationRequest request) {
        return donationService.create(request, currentUserProvider.getCurrentUserId());
    }

    /** Lists only the logged-in user's own donations.
     */
    @GetMapping
    public List<DonationDto> getMine() {
        return donationService.findByUser(currentUserProvider.getCurrentUserId());
    }
}
