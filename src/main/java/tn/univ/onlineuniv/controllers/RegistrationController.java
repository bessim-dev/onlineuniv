package tn.univ.onlineuniv.controllers;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tn.univ.onlineuniv.models.RegistrationRequest;
import tn.univ.onlineuniv.services.Registration;
@RestController
@RequestMapping(path = "api/v1/registration")
@AllArgsConstructor
public class RegistrationController {
    private final Registration registrationService;
    @PostMapping
    public String register(@RequestBody RegistrationRequest request){
        return  registrationService.register(request);
    }
    @GetMapping(path = "confirm")
    public String confirm(@RequestParam("token") String token){
        return registrationService.confirmToken(token);
    }
}
