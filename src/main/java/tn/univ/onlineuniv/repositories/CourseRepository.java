package tn.univ.onlineuniv.repositories;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import tn.univ.onlineuniv.models.Course;
import tn.univ.onlineuniv.models.User;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course,Long> {
    List<Course> findByPublished(boolean published, Pageable pageable);
    List<Course> findByUser(User user);
}
