package tn.univ.onlineuniv.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.univ.onlineuniv.models.Comment;
import tn.univ.onlineuniv.models.Course;
import tn.univ.onlineuniv.repositories.CommentRepository;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    public Comment create(Comment comment){
        return commentRepository.save(comment);
    }
    public Comment update(Comment comment){
        return commentRepository.save(comment);
    }
    public Boolean delete(Long id){
        commentRepository.deleteById(id);
        return Boolean.TRUE;
    }
    public List<Comment> listByCourse(Course course, int limit){
        return commentRepository.findByCourse(course,PageRequest.of(0,limit));
    }
}

