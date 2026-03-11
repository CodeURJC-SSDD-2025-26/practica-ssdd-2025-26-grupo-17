package es.dawgroup17.nexgym.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.dawgroup17.nexgym.model.AppUser;
import es.dawgroup17.nexgym.model.GymClass;
import es.dawgroup17.nexgym.model.Session;
import es.dawgroup17.nexgym.repository.ClassRepository;
import es.dawgroup17.nexgym.repository.SessionRepository;
import es.dawgroup17.nexgym.repository.UserRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
public class ClassService {

    @Autowired
    private ClassRepository repository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SessionRepository sessionRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public Optional<GymClass> findById(long id) {
        return repository.findById(id);
    }

    public Optional<GymClass> findByName(String name) {
        return repository.findGymClassByName(name);
    }

    public boolean exist(long id) {
        return repository.existsById(id);
    }

    public Page<GymClass> findAllPage(Pageable page) {
        return repository.findAll(page);
    }

    public List<GymClass> findAll() {
        return repository.findAll();
    }

    public void save(GymClass gymClass) {
        repository.save(gymClass);
    }

    /**
     * Removes all sessions from a GymClass correctly:
     * 1. For each session, removes it from the owner side (AppUser.userSessions)
     * so the ManyToMany join table gets cleaned up properly.
     * 2. Flushes the join-table deletes to DB.
     * 3. Clears the sessions list on the class and flushes the session row deletes.
     */
    @Transactional
    public void clearSessions(GymClass gymClass) {
        List<Session> sessions = new ArrayList<>(gymClass.getSessions());

        for (Session session : sessions) {
            // session.getSignedUsers() is the INVERSE side — read it just to get the users
            List<AppUser> users = new ArrayList<>(session.getSignedUsers());
            for (AppUser user : users) {
                // Remove from the OWNER side so JPA deletes the join table row
                user.getUserSessions().remove(session);
                userRepository.save(user);
            }
        }

        // Flush owner-side deletions to DB before we delete the sessions themselves
        entityManager.flush();

        // Now it is safe to remove the sessions
        gymClass.getSessions().clear();
        repository.save(gymClass);
        entityManager.flush();
    }

    @Transactional
    public void delete(long id) {
        Optional<GymClass> gymClass = repository.findById(id);
        if (gymClass.isPresent()) {
            GymClass gc = gymClass.get();

            // Clear images join table
            gc.getImages().clear();
            repository.save(gc);
            entityManager.flush();

            // Properly remove sessions (clears ManyToMany join table from owner side)
            clearSessions(gc);

            repository.deleteById(id);
        }
    }

    // Logic for getting the last week attendance for a class
    public Map<String, List<Integer>> getLastWeekEnrollmentsAllClasses() {
        LocalDate today = LocalDate.now();
        LocalDateTime from = today.minusDays(6).atStartOfDay();
        LocalDateTime to = today.plusDays(1).atStartOfDay();

        List<GymClass> classes = repository.findAll(); // or inject ClassService
        Map<String, List<Integer>> result = new LinkedHashMap<>();

        for (GymClass gymClass : classes) {
            List<Session> sessions = sessionRepository.findByGymClassIdAndDateBetween(
                    gymClass.getId(), from, to);

            List<Integer> dailyCounts = new ArrayList<>();
            for (int i = 6; i >= 0; i--) {
                LocalDate day = today.minusDays(i);
                int total = 0;
                for (Session s : sessions) {
                    if (s.getDate().toLocalDate().equals(day)) {
                        total += s.getSignedUsers() != null ? s.getSignedUsers().size() : 0;
                    }
                }
                dailyCounts.add(total);
            }
            result.put(String.valueOf(gymClass.getId()), dailyCounts);
        }

        return result;
    }

}