package es.dawgroup17.nexgym.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import es.dawgroup17.nexgym.model.Comment;
import es.dawgroup17.nexgym.repository.CommentRepository;


@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    public Optional<Comment> findById(long id) {
        return commentRepository.findById(id);
    }

    public boolean exist(long id) {
        return commentRepository.existsById(id);
    }

    public List<Comment> findAll() {
        return commentRepository.findAll();
    }

    public void save(Comment comment) {
        commentRepository.save(comment);
    }

    public void delete(long id) {
        commentRepository.deleteById(id);
    }
}