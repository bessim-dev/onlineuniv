package tn.univ.onlineuniv.models;

import lombok.Data;

import java.util.Set;
@Data
public class UpdateUserForm {


        private String firstName;
        private String lastName;
        private Set<Role> roles;
        private Boolean locked;
        private Boolean enabled;

}
