package tn.univ.onlineuniv;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import tn.univ.onlineuniv.models.ERole;
import tn.univ.onlineuniv.models.Role;
import tn.univ.onlineuniv.models.SignUpRequest;
import tn.univ.onlineuniv.services.UserService;

@SpringBootApplication
public class OnlineUnivApplication {

	public static void main(String[] args) {
		SpringApplication.run(OnlineUnivApplication.class, args);
	}
	@Bean
	public WebMvcConfigurer configurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**").allowedOrigins("*").allowedMethods("GET", "POST","PUT", "DELETE");
			}
		};
	}

	@Bean
	CommandLineRunner run(UserService userService){
		return args -> {
			userService.saveRole(new Role(null,ERole.ROLE_STUDENT));
			userService.saveRole(new Role(null,ERole.ROLE_TEACHER));
			userService.saveRole(new Role(null,ERole.ROLE_ADMIN));
			userService.SignUpUser(new SignUpRequest("bessim","boujebli","bissoooobecim@gmail.com","56121337","123",ERole.ROLE_ADMIN));
			userService.SignUpUser(new SignUpRequest("sirine","haddad","sirineHadded@gmail.com","52401136","123",ERole.ROLE_TEACHER));
			userService.SignUpUser(new SignUpRequest("dhia","haddad","dhiaHadded@gmail.com","52401136","123",ERole.ROLE_STUDENT));
		};
	}
}
