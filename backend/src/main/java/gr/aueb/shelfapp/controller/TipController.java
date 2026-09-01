package gr.aueb.shelfapp.controller;

import gr.aueb.shelfapp.dto.TipDto;
import gr.aueb.shelfapp.service.TipService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Short motivational tip, optionally tailored to a category
 * Here, later we can add more tips, even manualy.
 */
@RestController
@RequestMapping("/api/tips")
public class TipController {

    private final TipService tipService;

    public TipController(TipService tipService) {
        this.tipService = tipService;
    }

    @GetMapping("/random")
    public TipDto getRandomTip(@RequestParam(required = false) Long categoryId) {
        return tipService.randomTip(categoryId);
    }
}
