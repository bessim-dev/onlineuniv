package tn.univ.onlineuniv;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
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
			userService.saveRole(new Role(null,"ROLE_USER"));
			userService.saveRole(new Role(null,"ROLE_MANAGER"));
			userService.saveRole(new Role(null,"ROLE_ADMIN"));
			userService.saveUser(new User("bessim","boujebli","bissoooobecim@gmail.com","56121337","123",new ArrayList<>()));
			userService.addRoleToUser("bissoooobecim@gmail.com","ROLE_USER");
			userService.addRoleToUser("bissoooobecim@gmail.com","ROLE_ADMIN");
		};
	}
}
