package tn.univ.onlineuniv.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import tn.univ.onlineuniv.security.filter.CustomAuthenticationFilter;
import tn.univ.onlineuniv.security.filter.CustomAuthorisationFilter;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity

public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    private final UserDetailsService userDetailsService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
    }
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        CustomAuthenticationFilter customAuthenticationFilter = new CustomAuthenticationFilter(authenticationManagerBean());
        customAuthenticationFilter.setFilterProcessesUrl("/api/login");
        http.csrf().disable();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.authorizeRequests().antMatchers("/api/login","/refresh-token/**").permitAll();
        http.authorizeRequests().antMatchers(HttpMethod.GET,"/api/users/**").hasAnyAuthority("ROLE_ADMIN");
        http.authorizeRequests().antMatchers(HttpMethod.PUT,"/api/user/lock/**","/api/user/unlock/**").hasAnyAuthority("ROLE_ADMIN");
        http.authorizeRequests().antMatchers(HttpMethod.POST,"/api/courses/create/**").hasAnyAuthority("ROLE_TEACHER");
        http.authorizeRequests().antMatchers(HttpMethod.GET,"/api/courses/**").hasAnyAuthority("ROLE_ADMIN","ROLE_TEACHER","ROLE_STUDENT");
        http.authorizeRequests().antMatchers(HttpMethod.PUT, "/api/courses/add-comment/**").hasAnyAuthority("ROLE_ADMIN","ROLE_TEACHER","ROLE_STUDENT");
        http.authorizeRequests().antMatchers(HttpMethod.PUT, "/api/courses/rate/**").hasAnyAuthority("ROLE_ADMIN","ROLE_TEACHER","ROLE_STUDENT");
        http.authorizeRequests().antMatchers(HttpMethod.DELETE, "/api/courses/**").hasAnyAuthority("ROLE_TEACHER");
        http.authorizeRequests().anyRequest().permitAll();
        http.addFilter(customAuthenticationFilter);
        http.addFilterBefore(new CustomAuthorisationFilter(), UsernamePasswordAuthenticationFilter.class);
    }
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}