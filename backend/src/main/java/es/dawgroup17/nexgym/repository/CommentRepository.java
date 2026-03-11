package es.dawgroup17.nexgym.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import es.dawgroup17.nexgym.model.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {

}
