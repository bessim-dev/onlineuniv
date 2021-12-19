package tn.univ.onlineuniv;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import tn.univ.onlineuniv.models.ERole;
import tn.univ.onlineuniv.models.Role;
import tn.univ.onlineuniv.models.User;
import tn.univ.onlineuniv.services.UserService;

import java.util.ArrayList;

@SpringBootApplication
public class OnlineUnivApplication {


	public static void main(String[] args) {
		SpringApplication.run(OnlineUnivApplication.class, args);
	}
	@Bean
	CommandLineRunner run(UserService userService){
		return args -> {
			userService.saveRole(new Role(null,ERole.ROLE_STUDENT));
			userService.saveRole(new Role(null,ERole.ROLE_TEACHER));
			userService.saveRole(new Role(null,ERole.ROLE_ADMIN));
			userService.saveUser(new User("bessim","boujebli","bissoooobecim@gmail.com","56121337","123",new ArrayList<>(),false,true));
			userService.addRoleToUser("bissoooobecim@gmail.com",ERole.ROLE_ADMIN);
			userService.addRoleToUser("bissoooobecim@gmail.com",ERole.ROLE_STUDENT);
			userService.addRoleToUser("bissoooobecim@gmail.com",ERole.ROLE_TEACHER);
			userService.saveUser(new User("sirine","haddad","sirineHadded@gmail.com","52401136","123",new ArrayList<>(),false,true));
			userService.addRoleToUser("sirineHadded@gmail.com",ERole.ROLE_STUDENT);
			userService.saveUser(new User("dhia","haddad","dhiaHadded@gmail.com","52401136","123",new ArrayList<>(),false,true));
			userService.addRoleToUser("dhiaHadded@gmail.com",ERole.ROLE_TEACHER);
		};
	}
}
