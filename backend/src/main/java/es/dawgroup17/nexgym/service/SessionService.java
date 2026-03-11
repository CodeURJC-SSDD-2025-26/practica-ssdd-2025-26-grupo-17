package es.dawgroup17.nexgym.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.dawgroup17.nexgym.model.Session;
import es.dawgroup17.nexgym.repository.SessionRepository;

@Service
public class SessionService {

    @Autowired
    private SessionRepository repository;

    public Optional<Session> findById(long id) {
        return repository.findById(id);
    }

    public boolean exist(long id) {
        return repository.existsById(id);
    }

    public List<Session> findAll() {
        return repository.findAll();
    }

    public void save(Session session) {
        repository.save(session);
    }

    public void delete(long id) {
        repository.deleteById(id);
    }

}