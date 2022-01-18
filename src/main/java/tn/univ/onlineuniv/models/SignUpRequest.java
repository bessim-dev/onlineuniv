package tn.univ.onlineuniv.models;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class SignUpRequest {
    private final String firstName;
    private final String lastName;
    private final String email;
    private final String password;
    private final ERole role;
}
