package tn.univ.onlineuniv.controllers;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tn.univ.onlineuniv.models.*;
import tn.univ.onlineuniv.repositories.UserRepository;
import tn.univ.onlineuniv.security.utils.FilterException;
import tn.univ.onlineuniv.security.utils.JwtUtils;
import tn.univ.onlineuniv.services.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserController {
    private final UserService userService;
    private final UserRepository userRepository;
    private final FilterException filterException = new FilterException();
    private final JwtUtils jwtUtils = new JwtUtils();
    @PostMapping("/sign-up")
    public ResponseEntity<?> SignUp(@RequestBody SignUpRequest signUpRequest) {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/sign-up").toUriString());
        return ResponseEntity.created(uri).body(userService.SignUpUser(signUpRequest));
    }
    @GetMapping("/users")
    public ResponseEntity<List<User>>getUsers(){
        return ResponseEntity.ok().body(userService.getUsers());
    }
    @PostMapping("/role/save")
    public ResponseEntity<Role>saveUser(@RequestBody Role role){
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/role/save").toUriString());
        return ResponseEntity.created(uri).body(userService.saveRole(role));
    }
    @PostMapping("/role/add-to-user")
    public ResponseEntity<?>addRoleToUser(@RequestBody RoleToUserForm form){
        userService.addRoleToUser(form.getUsername(),form.getRole());
        return ResponseEntity.ok().build();
    }
    @GetMapping(path = "/confirm-user")
    public String confirm(@RequestParam("token") String token) {
        return userService.confirmToken(token);
    }
    @PutMapping("/user/{id}")
    public ResponseEntity<User> updateUser(@PathVariable("id") long id,@RequestBody UpdateUserForm user) {
        try {
                User _user = userService.updateUser(id,user);
                return new ResponseEntity<>(_user, HttpStatus.OK);

        } catch (Exception e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @PutMapping("/user/lock/{id}")
    public ResponseEntity<?> lockUserAccount(@PathVariable("id") long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            if(!user.get().isAccountNonLocked()){
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }else{
                return new ResponseEntity<>(userService.lockUser(id), HttpStatus.OK);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @PutMapping("/user/unlock/{id}")
    public ResponseEntity<?> unlockUserAccount(@PathVariable("id") long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            if(!user.get().isAccountNonLocked()){
                return new ResponseEntity<>(userService.unlockUser(id), HttpStatus.OK);

            }else{
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @GetMapping("/refresh-token")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ")){
            try {
                String refreshToken = authorizationHeader.substring(7);
                DecodedJWT decodedJWT = jwtUtils.decodeTokens(refreshToken);
                String email = decodedJWT.getSubject();
                User user = userService.getUser(email);
                List<String> authorities = user.getRoles().stream().map(Role -> Role.getName().name()).collect(Collectors.toList());
                String accessToken = jwtUtils.createAccessToken(user,authorities,request);
                Map<String,String> tokens = new HashMap<>();
                tokens.put("accessToken",accessToken);
                tokens.put("refreshToken",refreshToken);
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(),tokens);
            } catch (Exception e){
                filterException.failedFilter(e,response);
            }
        }else{
            throw new RuntimeException("refresh token missing");
        }
    }
}
@Data
class RoleToUserForm{
    private String username;
    private ERole role;
}
