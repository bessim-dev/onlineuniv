package tn.univ.onlineuniv.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.univ.onlineuniv.models.*;
import tn.univ.onlineuniv.repositories.RoleRepository;
import tn.univ.onlineuniv.repositories.UserRepository;

import java.time.LocalDateTime;
import java.util.*;

@Service
@AllArgsConstructor
@Transactional
@Slf4j
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ConfirmationTokenService confirmationTokenService;
    private final EmailService emailService;
    public String SignUpUser(SignUpRequest signUpRequest) {
        User userExists = userRepository.findByEmail(signUpRequest.getEmail());
        if (userExists != null) {
            throw new IllegalStateException("email already taken");
        }
        String encodedPassword = bCryptPasswordEncoder.encode(signUpRequest.getPassword());

        Set<Role> roles = new HashSet<>();
        Role role = roleRepository.findByName(signUpRequest.getRole());
        if (role == null) {
            throw new IllegalStateException("role name doesn't exist");
        }
        roles.add(role);
        User user = userRepository.save(new User(signUpRequest.getFirstName(),
                signUpRequest.getLastName(),
                signUpRequest.getEmail(),
                encodedPassword,
                roles
                ));
        log.info("save a {} to database", user.getFirstName());

        String token = UUID.randomUUID().toString();
        ConfirmationToken confirmationToken = new ConfirmationToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15),
                user
        );
        confirmationTokenService.saveConfirmationToken(confirmationToken);
        log.info("save a new user token to database");
        String link = "http://localhost:8080/api/confirm-user?token=" + token;
        if(!user.getEnabled()){
            emailService.send(user.getEmail(),emailService.buildEmail(user.getFirstName(),link));
        }
        return "Check your inbox to confirm your email";
    }

    public Role saveRole(Role role) {
        log.info("save a new role {} to database", role.getName().name());
        return roleRepository.save(role);
    }

    public void addRoleToUser(String userEmail, ERole roleName) {
        User user = userRepository.findByEmail(userEmail);
        Role role = roleRepository.findByName(roleName);
        log.info("add {} to {} successfully", role.getName(), user.getFirstName());
        user.getRoles().add(role);
    }

    public User getUser(String email) {
        log.info("fetching user");
        return userRepository.findByEmail(email);
    }
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            log.info("User with email {} found in the database", email);
            throw new UsernameNotFoundException("user not found in DB");
        } else {
            log.info("User {} found in the database with roles {}", user.getUsername(), user.getRoles());
        }

        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        user.getRoles().forEach(role -> authorities.add(new SimpleGrantedAuthority(role.getName().name())));
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.getEnabled(),
                user.isAccountNonExpired(),
                user.isCredentialsNonExpired(),
                user.isAccountNonLocked(),
                authorities
        );
    }

    public void enableAppUser(String email) {
        userRepository.enableAppUser(email);
    }
    public int lockUser(long id){
                return userRepository.lockUser(id);
    }
    public int unlockUser(Long id) {
        return userRepository.unlockUser(id);
    }
    public User updateUser(long id,UpdateUserForm user){
        Optional<User> user_db = userRepository.findById(id);
        if (user_db.isEmpty()) {
            log.info("User with id {} found in the database", id);
            throw new UsernameNotFoundException("user not found in DB");
        }
        user_db.get().setEnabled(user.getEnabled());
        user_db.get().setLocked(user.getLocked());
        user_db.get().setRoles(user.getRoles());
        user_db.get().setFirstName(user.getFirstName());
        user_db.get().setLastName(user.getLastName());
        userRepository.save(user_db.get());
        return user_db.get();
    }
    @Transactional
    public String confirmToken(String token) {
        ConfirmationToken confirmationToken = confirmationTokenService.getToken(token)
                .orElseThrow(() ->
                        new IllegalStateException("token not found"));

        if (confirmationToken.getConfirmedAt() != null) {
            throw new IllegalStateException("email already confirmed");
        }
        LocalDateTime expiredAt = confirmationToken.getExpiresAt();

        if (expiredAt.isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("token expired");
        }
        confirmationTokenService.setConfirmedAt(token);
        enableAppUser(
                confirmationToken.getUser().getEmail()
        );
        return "confirmed";
    }
}
