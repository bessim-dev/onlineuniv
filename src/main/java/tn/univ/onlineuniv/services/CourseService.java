package tn.univ.onlineuniv.services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.univ.onlineuniv.models.Course;
import tn.univ.onlineuniv.repositories.CourseRepository;

import java.util.Collection;

@Service
@Transactional
@RequiredArgsConstructor
public class CourseService {
    private final CourseRepository  courseRepository;
    public Course create(Course course){
        return courseRepository.save(course);
    }
    public Course update(Long id,Course course){
        Course oldCourse = courseRepository.getById(id);
        oldCourse.setTitle(course.getTitle());
        oldCourse.setSubject(course.getSubject());
        oldCourse.setDescription(course.getDescription());
        oldCourse.setPublished(course.isPublished());
        oldCourse.setThumbnailUrl(course.getThumbnailUrl());
        oldCourse.setVideoUrl(course.getVideoUrl());
        return oldCourse ;

    }
    public void delete(Long id){
        courseRepository.deleteById(id);
    }
    public Course getCourse(Long id){
        return courseRepository.getById(id);
    }
    public Collection<Course> list(int limit){
        return courseRepository.findAll(PageRequest.of(0,limit)).toList();
    }
    public Collection<Course> publishedList(int limit){
        return courseRepository.findByPublished(true,PageRequest.of(0,limit));
    }
}
