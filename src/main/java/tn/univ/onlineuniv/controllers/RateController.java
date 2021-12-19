package tn.univ.onlineuniv.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tn.univ.onlineuniv.repositories.RateRepository;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class RateController {
    private final RateRepository rateRepository;
}
