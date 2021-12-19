package tn.univ.onlineuniv.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.univ.onlineuniv.models.Comment;


public interface CommentRepository extends JpaRepository<Comment,Long> {
}
