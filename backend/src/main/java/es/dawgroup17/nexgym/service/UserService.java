package es.dawgroup17.nexgym.service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.dawgroup17.nexgym.model.AppUser;
import es.dawgroup17.nexgym.model.Comment;
import es.dawgroup17.nexgym.model.GymClass;
import es.dawgroup17.nexgym.model.Image;
import es.dawgroup17.nexgym.model.Session;
import es.dawgroup17.nexgym.repository.ClassRepository;
import es.dawgroup17.nexgym.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ImageService imageService;

    public Optional<AppUser> findById(long id) {
        return userRepository.findById(id);
    }

    public Optional<AppUser> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public List<AppUser> findAll() {
        return userRepository.findAll();
    }

    public void save(AppUser user) {
        userRepository.save(user);
    }

    // Register of a new user encoding its password
    public void registerUser(AppUser user, String rawPassword) throws IOException {
        user.setEncodedPassword(passwordEncoder.encode(rawPassword));
        Resource image = new ClassPathResource("sample_images/perfil_admin.jpg");
        Image createdImage = imageService.createImage(image.getInputStream());
        user.setImage(createdImage);
        userRepository.save(user);
    }

    // Update of the password of an existing user
    public void updatePassword(AppUser user, String rawPassword) {
        user.setEncodedPassword(passwordEncoder.encode(rawPassword));
        userRepository.save(user);
    }

    public void deleteByEmail(String email) {
        userRepository.findByEmail(email).ifPresent(user -> {
            userRepository.delete(user);
        });
    }

    public void delete(long id) {
        Optional<AppUser> userOpt = userRepository.findById(id);
        if (userOpt.isPresent()) {
            AppUser user = userOpt.get();

            // Remove this user's comments from their associated classes
            for (Comment comment : user.getComments()) {
                GymClass gymClass = comment.getGymClass();
                if (gymClass != null) {
                    gymClass.getComments().remove(comment);
                }
            }

            // Clear ManyToMany join table entries
            user.getUserSessions().clear();

            // Clear profile image reference
            user.setImage(null);

            userRepository.save(user);
            userRepository.deleteById(id);
        }
    }

    // Logic for getting the class distribution during the last 30 days. Used for
    // the circular graph of user
    @Transactional
    public Map<String, Integer> getClassDistributionLast30Days(AppUser user) {
        LocalDate cutoff = LocalDate.now().minusDays(30);
        Map<String, Integer> result = new LinkedHashMap<>();

        for (Session session : user.getUserSessions()) {
            if (session.getDate().toLocalDate().isAfter(cutoff)) {
                String className = session.getGymClass().getName();
                result.put(className, result.getOrDefault(className, 0) + 1);
            }
        }

        return result;
    }
}
