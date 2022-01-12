package tn.univ.onlineuniv.security.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor
@Slf4j
public class FilterException {
    public void failedFilter(Exception e, HttpServletResponse response) throws IOException {
        log.error("Error logging in: {}",e.getMessage());
        response.setStatus(403);
        Map<String,String> error = new HashMap<>();
        error.put("error",e.getMessage());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getOutputStream(),error);
    }
}
