package tn.univ.onlineuniv.repositories;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import tn.univ.onlineuniv.models.Comment;
import tn.univ.onlineuniv.models.Course;

import java.util.List;


public interface CommentRepository extends JpaRepository<Comment,Long> {
    List<Comment> findByCourse(Course course, Pageable pageable);
}
