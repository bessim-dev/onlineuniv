package tn.univ.onlineuniv.controllers;


import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.univ.onlineuniv.models.Comment;
import tn.univ.onlineuniv.models.Course;
import tn.univ.onlineuniv.models.User;
import tn.univ.onlineuniv.repositories.CourseRepository;
import tn.univ.onlineuniv.security.utils.JwtUtils;
import tn.univ.onlineuniv.services.CommentService;
import tn.univ.onlineuniv.services.UserService;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;
    private final UserService userService;
    private final CourseRepository courseRepository;
    private final JwtUtils jwtUtils = new JwtUtils();

    public tn.univ.onlineuniv.models.User resolveUserFromJWT(String accessToken){
        DecodedJWT decodedJWT = jwtUtils.decodeTokens(accessToken);
        String email = decodedJWT.getSubject();
        return userService.getUser(email);
    }

    @PutMapping("/add-comment/{id}")
    public ResponseEntity<Comment> addComment(@PathVariable("id") long id, @RequestBody Comment comment, @RequestHeader(value = "Authorization") String header) {
        Optional<Course> course = courseRepository.findById(id);
        if (course.isPresent()) {
            String accessToken = header.substring(7);
            User user = resolveUserFromJWT(accessToken);
            Comment _comment = commentService.create(new Comment(comment.getText(),comment.getRate(),user,course.get()));
            return new ResponseEntity<>(_comment, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @PutMapping("/delete-comment/{id}")
    public ResponseEntity<?> deleteComment(@PathVariable("id") long id) {
        Optional<Course> course = courseRepository.findById(id);
        if (course.isPresent()) {
            commentService.delete(id);
            return new ResponseEntity<>("deleted successfully",HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @PutMapping("/update-comment/{id}")
    public ResponseEntity<Comment> updateComment(@PathVariable("id") long id, @RequestBody Comment comment, @RequestHeader(value = "Authorization") String header) {
        Optional<Course> course = courseRepository.findById(id);
        if (course.isPresent()) {
            String accessToken = header.substring(7);
            User user = resolveUserFromJWT(accessToken);
            Comment _comment = commentService.update(new Comment(comment.getText(),comment.getRate(),user,course.get()));
            return new ResponseEntity<>(_comment,HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/comments/{id}")
    public ResponseEntity<List<Comment>> listComments(@PathVariable("id") long id,@RequestParam("limit") int limit) {
        Optional<Course> course = courseRepository.findById(id);
        if (course.isPresent()) {
            List<Comment> _comments = commentService.listByCourse(course.get(),limit);
            return new ResponseEntity<>(_comments, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}
