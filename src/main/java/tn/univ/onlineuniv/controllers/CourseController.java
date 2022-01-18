package tn.univ.onlineuniv.controllers;

import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tn.univ.onlineuniv.models.Course;
import tn.univ.onlineuniv.models.PublicCourse;
import tn.univ.onlineuniv.models.User;
import tn.univ.onlineuniv.security.utils.JwtUtils;
import tn.univ.onlineuniv.services.CourseService;
import tn.univ.onlineuniv.services.UserService;


import java.util.Collection;
import java.util.List;


@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;
    private final UserService userService;
    private final JwtUtils jwtUtils = new JwtUtils();
    public tn.univ.onlineuniv.models.User resolveUserFromJWT(String accessToken){
        DecodedJWT decodedJWT = jwtUtils.decodeTokens(accessToken);
        String email = decodedJWT.getSubject();
        return userService.getUser(email);
    }
    @GetMapping("/courses/public")
    public ResponseEntity<List<PublicCourse>> getPublicCourses(@RequestParam("limit") int limit) {
        try {
            List<PublicCourse> courses = courseService.listPublicCourses(limit);

            if (courses.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(courses, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    @GetMapping("/courses/private")
    public ResponseEntity<List<Course>> getPrivateCourses(@RequestParam("limit") int limit) {
        try {
            List<Course> courses = courseService.listPrivateCourses(limit);
            if (courses.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(courses, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/courses/{id}")
    public ResponseEntity<Course> getCourseById(@PathVariable("id") long id) {
        try {
            Course course = courseService.getCourse(id);
            return new ResponseEntity<>(course,HttpStatus.OK);
        }catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/courses/create")
    public ResponseEntity<Course> createCourse(@RequestBody Course course,@RequestHeader(value = "Authorization") String header) {
        String accessToken = header.substring(7);
        User user = resolveUserFromJWT(accessToken);
        try {
            Course _course = new Course(course.getTitle(),course.getSubject() ,course.getDescription(), course.isPublished(),course.getThumbnailUrl(),course.getVideoUrl(),user);
            Course __course = courseService.create(_course);
            log.info("header"+header);
            return new ResponseEntity<>(__course, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/courses/{id}")
    public ResponseEntity<Course> updateCourse(@PathVariable("id") long id, @RequestBody Course course) {
        try {
            Course updatedCourse = courseService.update(id,course);
            return new ResponseEntity<>(updatedCourse, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }
    @DeleteMapping("/courses/{id}")
    public ResponseEntity<HttpStatus> deleteCourse(@PathVariable("id") long id) {
        try {
            courseService.delete(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/courses/published")
    public ResponseEntity<Collection<Course>> findByPublished(@RequestParam("limit") int limit) {
        try {
            Collection<Course> courses = courseService.publishedList(limit) ;

            if (courses.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(courses, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/courses/author")
    public ResponseEntity<Collection<Course>> findByAuthor(@RequestHeader(value = "Authorization") String header){
        String accessToken = header.substring(7);
        User user = resolveUserFromJWT(accessToken);
        try {
            Collection<Course> courses = courseService.listByAuthor(user) ;
            if (courses.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(courses, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
