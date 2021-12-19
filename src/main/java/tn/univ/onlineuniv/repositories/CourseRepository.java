package tn.univ.onlineuniv.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.univ.onlineuniv.models.Course;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course,Long> {
    List<Course> findByPublished(boolean published);

    List<Course> findByTitle(String title);
}