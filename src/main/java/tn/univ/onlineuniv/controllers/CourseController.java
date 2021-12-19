package tn.univ.onlineuniv.controllers;

import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.univ.onlineuniv.models.Comment;
import tn.univ.onlineuniv.models.Course;
import tn.univ.onlineuniv.models.Rate;
import tn.univ.onlineuniv.models.User;
import tn.univ.onlineuniv.repositories.CommentRepository;
import tn.univ.onlineuniv.repositories.CourseRepository;
import tn.univ.onlineuniv.repositories.RateRepository;
import tn.univ.onlineuniv.security.utils.JwtUtils;
import tn.univ.onlineuniv.services.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CourseController {
    private final CourseRepository courseRepository;
    private final CommentRepository commentRepository;
    private final RateRepository rateRepository;
    private final UserService userService;
    private final JwtUtils jwtUtils = new JwtUtils();
    public tn.univ.onlineuniv.models.User resolveUserFromJWT(String accessToken){
        DecodedJWT decodedJWT = jwtUtils.decodeTokens(accessToken);
        String email = decodedJWT.getSubject();
        return userService.getUser(email);
    }
    @GetMapping("/courses")
    public ResponseEntity<List<Course>> getAllCourses(@RequestParam(required = false) String title) {
        try {
            List<Course> courses = new ArrayList<>();

            if (title == null)
                courseRepository.findAll().forEach(courses::add);
            else
                courseRepository.findByTitle(title).forEach(courses::add);

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
        Optional<Course> courseData = courseRepository.findById(id);

        if (courseData.isPresent()) {
            return new ResponseEntity<>(courseData.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/courses/create")
    public ResponseEntity<Course> createCourse(@RequestBody Course course,@RequestHeader(value = "Authorization") String header) {
        String accessToken = header.substring(7);
        User user = resolveUserFromJWT(accessToken);
        try {
            Course _course = courseRepository
                    .save(new Course(course.getTitle(),course.getSubject() ,course.getDescription(), course.isPublished(),user));
            log.info("header"+header);
            return new ResponseEntity<>(_course, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/courses/{id}")
    public ResponseEntity<Course> updateCourse(@PathVariable("id") long id, @RequestBody Course course) {
        Optional<Course> courseData = courseRepository.findById(id);
        if (courseData.isPresent()) {
            Course _course = courseData.get();
            _course.setTitle(course.getTitle());
            _course.setSubject(course.getSubject());
            _course.setDescription(course.getDescription());
            _course.setPublished(course.isPublished());
            return new ResponseEntity<>(courseRepository.save(_course), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/courses/{id}")
    public ResponseEntity<HttpStatus> deleteTutorial(@PathVariable("id") long id) {
        try {
            courseRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/courses/published")
    public ResponseEntity<List<Course>> findByPublished() {
        try {
            List<Course> courses = courseRepository.findByPublished(true);

            if (courses.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(courses, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PutMapping("/courses/add-comment/{id}")
    public ResponseEntity<Course> addComment(@PathVariable("id") long id, @RequestBody Comment comment,@RequestHeader(value = "Authorization") String header) {
        Optional<Course> courseData = courseRepository.findById(id);
        if (courseData.isPresent()) {
            String accessToken = header.substring(7);
            User user = resolveUserFromJWT(accessToken);
            Comment _comment = commentRepository.save(new Comment(comment.getTitle(),comment.getText(),user));
            Course _course = courseData.get();
            List<Comment> oldComments = _course.getComments();
            oldComments.add(_comment);
            return new ResponseEntity<>(courseData.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @PutMapping("/courses/rate/{id}")
    public ResponseEntity<Course> rateCourse(@PathVariable("id") long id, @RequestBody Rate rate, @RequestHeader(value = "Authorization") String header) {
        Optional<Course> courseData = courseRepository.findById(id);
        if (courseData.isPresent()) {
            String accessToken = header.substring(7);
            User user = resolveUserFromJWT(accessToken);
            Rate _rate = rateRepository.save(new Rate(rate.getRate(),user));
            Course _course = courseData.get();
            List<Rate> oldRates = _course.getRates();
            oldRates.add(_rate);
            return new ResponseEntity<>(courseData.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
