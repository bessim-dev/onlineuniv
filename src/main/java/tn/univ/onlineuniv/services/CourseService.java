package tn.univ.onlineuniv.services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.univ.onlineuniv.models.Course;
import tn.univ.onlineuniv.models.PublicCourse;
import tn.univ.onlineuniv.models.User;
import tn.univ.onlineuniv.repositories.CourseRepository;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

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
        courseRepository.save(oldCourse);
        return oldCourse ;
    }
    public void delete(Long id){
        courseRepository.deleteById(id);
    }
    public Course getCourse(Long id){
        return courseRepository.getById(id);
    }

    public List<PublicCourse> list(int limit){
        Collection<Course> _courses = courseRepository.findAll(PageRequest.of(0,limit)).toList();
        return _courses.stream().map(course -> {
            PublicCourse result = new PublicCourse();
            result.setTitle(course.getTitle());
            result.setSubject(course.getSubject());
            result.setDescription(course.getDescription());
            result.setAuthor(course.getUser().getEmail());
            result.setThumbnailUrl(course.getThumbnailUrl());
            result.setId(course.getId());
            result.setCreatedAt(course.getCreatedAt());
            return result;
        }).collect(Collectors.toList());

    }
    public Collection<Course> publishedList(int limit){
        return courseRepository.findByPublished(true,PageRequest.of(0,limit));
    }
    public Collection<Course> listByAuthor(User user){
        return courseRepository.findByUser(user);
    }
}
