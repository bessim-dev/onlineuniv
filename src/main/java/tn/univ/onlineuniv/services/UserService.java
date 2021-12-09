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
import tn.univ.onlineuniv.models.Role;
import tn.univ.onlineuniv.models.User;
import tn.univ.onlineuniv.models.ConfirmationToken;
import tn.univ.onlineuniv.repositories.RoleRepository;
import tn.univ.onlineuniv.repositories.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

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
    public String saveUser(User user) {
        User userExists = userRepository.findByEmail(user.getEmail());
        if (userExists != null) {
            throw new IllegalStateException("email already taken");
        }
        String encodedPassword = bCryptPasswordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        userRepository.save(user);
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

        emailService.send(user.getEmail(),emailService.buildEmail(user.getFirstName(),link));
        return token;
    }

    public Role saveRole(Role role) {
        log.info("save a new role {} to database", role.getName());
        return roleRepository.save(role);
    }

    public void addRoleToUser(String userEmail, String roleName) {
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
        user.getRoles().forEach(role -> {
            authorities.add(new SimpleGrantedAuthority(role.getName()));
        });
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

    public int enableAppUser(String email) {
        return userRepository.enableAppUser(email);
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
