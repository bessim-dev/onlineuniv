package tn.univ.onlineuniv.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import tn.univ.onlineuniv.security.utils.JwtUtils;
import tn.univ.onlineuniv.services.UserService;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils = new JwtUtils() ;
    public CustomAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email,password);
        return authenticationManager.authenticate(authenticationToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException {
        User user = (User) authentication.getPrincipal();
        List<String> authorities = user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
        String accessToken = jwtUtils.createAccessToken(user,authorities,request);
        String refreshToken = jwtUtils.createRefreshToken(user, request);
        Map<String,String> tokens = new HashMap<>();
        tokens.put("accessToken",accessToken);
        tokens.put("refreshToken",refreshToken);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getOutputStream(),tokens);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException {
        Map<String,String> error = new HashMap<>();
        if (e instanceof LockedException) {
            log.error("Error logging in: {}",e.getMessage());
            error.put("error","Account locked, please contact administrator!");
        } else if (e instanceof CredentialsExpiredException) {
            log.error("Error logging in: {}",e.getMessage());
            error.put("error","Password expired, please contact the administrator!");
        } else if (e instanceof AccountExpiredException) {
            log.error("Error logging in: {}",e.getMessage());
            error.put("error","Account expired, please contact administrator!");
        } else if (e instanceof DisabledException) {
            log.error("Error logging in: {}",e.getMessage());
            error.put("error","Account is disabled, please check your email!");
        } else if (e instanceof BadCredentialsException) {
            log.error("Error logging in: {}",e.getMessage());
            error.put("error","User name or password input error, please re-enter!");
        }
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getOutputStream(),error);
    }
}
