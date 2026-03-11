package es.dawgroup17.nexgym.service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import es.dawgroup17.nexgym.repository.UserRepository;
import es.dawgroup17.nexgym.repository.ClassRepository;
import jakarta.annotation.PostConstruct;
import es.dawgroup17.nexgym.model.AppUser;
import es.dawgroup17.nexgym.model.Comment;
import es.dawgroup17.nexgym.model.GymClass;
import es.dawgroup17.nexgym.model.Image;
import es.dawgroup17.nexgym.model.Session;

@Component
public class DatabaseUsersLoader {

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private ClassRepository gymClassRepository;

        @Autowired
        private PasswordEncoder passwordEncoder;

        @Autowired
        private ImageService imageService;

        @PostConstruct
        private void initDatabase() throws IOException {
                // if the admin user already exists, we assume that the database has been
                // initialized and we do not need to initialize it again, so we return without
                // doing anything.
                if (userRepository.findByEmail("admin@gmail.com").isPresent()) {
                        return;
                }

                /* ================= USERS ================= */

                AppUser admin = new AppUser("admin", "admin", "admin@gmail.com",
                                passwordEncoder.encode("admin"),
                                LocalDate.of(1998, 1, 1),
                                "123456789", "USER");
                admin.getRoles().add("ADMIN");
                setProfileImage(admin, "sample_images/perfil_admin.jpg");
                userRepository.save(admin);

                AppUser user_1 = new AppUser("Maria", "Gonzalez Serrano", "maria@gmail.com",
                                passwordEncoder.encode("maria"),
                                LocalDate.of(1998, 1, 1),
                                "123456789", "USER");
                setProfileImage(user_1, "sample_images/perfil_1.jpg");
                userRepository.save(user_1);

                AppUser user_2 = new AppUser("Lucia", "Martinez Lopez", "lucia@gmail.com",
                                passwordEncoder.encode("lucia"),
                                LocalDate.of(1999, 2, 1),
                                "987654321", "USER");
                setProfileImage(user_2, "sample_images/perfil_2.jpg");
                userRepository.save(user_2);

                AppUser user_3 = new AppUser("Mario", "Gomez Lopez", "mario@gmail.com",
                                passwordEncoder.encode("mario"),
                                LocalDate.of(1970, 3, 1),
                                "123456789", "USER");
                setProfileImage(user_3, "sample_images/perfil_3.jpg");
                userRepository.save(user_3);

                AppUser user_4 = new AppUser("Jaime", "Lopez Murcia", "jaime@gmail.com",
                                passwordEncoder.encode("jaime"),
                                LocalDate.of(1990, 4, 1),
                                "987654321", "USER");
                setProfileImage(user_4, "sample_images/perfil_4.jpg");
                userRepository.save(user_4);

                AppUser user_5 = new AppUser("Raul", "Guerra Serrano", "raul@gmail.com",
                                passwordEncoder.encode("raul"),
                                LocalDate.of(2004, 5, 1),
                                "123456789", "USER");
                setProfileImage(user_5, "sample_images/perfil_5.jpg");
                userRepository.save(user_5);

                AppUser user_6 = new AppUser("Sofia", "Becerra Arce", "sofia@gmail.com",
                                passwordEncoder.encode("sofia"),
                                LocalDate.of(2005, 6, 1),
                                "987654321", "USER");
                setProfileImage(user_6, "sample_images/perfil_6.jpg");
                userRepository.save(user_6);

                AppUser user_7 = new AppUser("Vera", "Nicolas Bautista", "vera@gmail.com",
                                passwordEncoder.encode("vera"),
                                LocalDate.of(2001, 7, 1),
                                "123456789", "USER");
                setProfileImage(user_7, "sample_images/perfil_7.jpg");
                userRepository.save(user_7);

                AppUser user_8 = new AppUser("Irene", "Perez Lopez", "irene@gmail.com",
                                passwordEncoder.encode("irene"),
                                LocalDate.of(1999, 8, 8),
                                "987654321", "USER");
                setProfileImage(user_8, "sample_images/perfil_8.jpg");
                userRepository.save(user_8);

                AppUser user_9 = new AppUser("Juan", "Gutierrez Serrano", "juan@gmail.com",
                                passwordEncoder.encode("juan"),
                                LocalDate.of(1997, 9, 1),
                                "123456789", "USER");
                setProfileImage(user_9, "sample_images/perfil_9.jpg");
                userRepository.save(user_9);

                /* ================= CLASSES ================= */

                GymClass class_1 = new GymClass("YOGA", "Laura Perez", "hola",
                                new ArrayList<>(), new ArrayList<>());

                setClassImage(class_1, "sample_images/yoga_1.jpg",
                                "sample_images/yoga_2.jpg",
                                "sample_images/yoga_3.jpg",
                                "sample_images/yoga_4.jpg");

                Comment comment_1 = new Comment(user_8, 5,
                                "I loved the class! The instructor was very motivating and the exercises were great.");
                Comment comment_2 = new Comment(user_2, 4, "The instructor was amazing and the workout was intense.");
                Comment comment_3 = new Comment(user_6, 2, "I felt so bad after the class!");
                setCommentsToClass(class_1, comment_1, comment_2, comment_3);

                Session session_1 = new Session(LocalDateTime.of(2026, 3, 1, 10, 0), 20,
                                class_1);
                Session session_2 = new Session(LocalDateTime.of(2026, 3, 4, 21, 0), 20,
                                class_1);
                Session session_3 = new Session(LocalDateTime.of(2026, 3, 6, 12, 0), 20,
                                class_1);
                setClassSessions(class_1, session_1, session_2, session_3);

                gymClassRepository.save(class_1);

                GymClass class_2 = new GymClass("PILATES", "Ana Torres",
                                "Improve flexibility, strength and posture with guided pilates sessions.",
                                new ArrayList<>(), new ArrayList<>());

                setClassImage(class_2,
                                "sample_images/pilates_1.jpg",
                                "sample_images/pilates_2.jpg",
                                "sample_images/pilates_3.jpg",
                                "sample_images/pilates_4.jpg");

                setCommentsToClass(class_2,
                                new Comment(user_1, 5, "Great for core strength!"),
                                new Comment(user_3, 4, "Very complete session."),
                                new Comment(user_7, 3, "Good but intense."));

                setClassSessions(class_2,
                                new Session(LocalDateTime.of(2026, 3, 2, 18, 0), 15, class_2),
                                new Session(LocalDateTime.of(2026, 3, 5, 10, 0), 15, class_2),
                                new Session(LocalDateTime.of(2026, 3, 7, 19, 0), 15, class_2));

                gymClassRepository.save(class_2);

                GymClass class_3 = new GymClass("CROSSFIT", "Carlos Ruiz",
                                "High intensity functional training.",
                                new ArrayList<>(), new ArrayList<>());

                setClassImage(class_3,
                                "sample_images/crosstraining_1.jpg",
                                "sample_images/crosstraining_2.jpg",
                                "sample_images/crosstraining_3.jpg",
                                "sample_images/crosstraining_4.jpg");

                setCommentsToClass(class_3,
                                new Comment(user_4, 5, "Brutal workout!"),
                                new Comment(user_2, 4, "Loved the energy."),
                                new Comment(user_5, 3, "Too intense for beginners."));

                setClassSessions(class_3,
                                new Session(LocalDateTime.of(2026, 3, 1, 20, 0), 20, class_3),
                                new Session(LocalDateTime.of(2026, 3, 3, 20, 0), 20, class_3),
                                new Session(LocalDateTime.of(2026, 3, 6, 18, 0), 20, class_3));

                gymClassRepository.save(class_3);

                GymClass class_4 = new GymClass("ZUMBA", "Laura Diaz",
                                "Dance-based cardio workout.",
                                new ArrayList<>(), new ArrayList<>());

                setClassImage(class_4,
                                "sample_images/zumba_1.jpg",
                                "sample_images/zumba_2.jpg",
                                "sample_images/zumba_3.jpg",
                                "sample_images/zumba_4.jpg");

                setCommentsToClass(class_4,
                                new Comment(user_6, 5, "Super fun!"),
                                new Comment(user_8, 4, "Great music."),
                                new Comment(user_9, 5, "Best class ever."));

                setClassSessions(class_4,
                                new Session(LocalDateTime.of(2026, 3, 2, 19, 0), 25, class_4),
                                new Session(LocalDateTime.of(2026, 3, 4, 19, 0), 25, class_4),
                                new Session(LocalDateTime.of(2026, 3, 8, 11, 0), 25, class_4));

                gymClassRepository.save(class_4);

                GymClass class_5 = new GymClass("BOXING", "Miguel Santos",
                                "Boxing techniques and conditioning.",
                                new ArrayList<>(), new ArrayList<>());

                setClassImage(class_5,
                                "sample_images/boxing_1.jpg",
                                "sample_images/boxing_2.jpg",
                                "sample_images/boxing_3.jpg",
                                "sample_images/boxing_4.jpg");

                setCommentsToClass(class_5,
                                new Comment(user_3, 4, "Very demanding."),
                                new Comment(user_5, 5, "Perfect stress relief."),
                                new Comment(user_1, 4, "Great trainer."));

                setClassSessions(class_5,
                                new Session(LocalDateTime.of(2026, 3, 3, 17, 0), 18, class_5),
                                new Session(LocalDateTime.of(2026, 3, 5, 17, 0), 18, class_5),
                                new Session(LocalDateTime.of(2026, 3, 7, 17, 0), 18, class_5));

                gymClassRepository.save(class_5);

                GymClass class_6 = new GymClass("SPINNING", "Paula Moreno",
                                "Indoor cycling for endurance.",
                                new ArrayList<>(), new ArrayList<>());

                setClassImage(class_6,
                                "sample_images/spinning_1.jpg",
                                "sample_images/spinning_2.jpg",
                                "sample_images/spinning_3.jpg",
                                "sample_images/spinning_4.jpg");

                setCommentsToClass(class_6,
                                new Comment(user_7, 5, "Intense cardio!"),
                                new Comment(user_4, 4, "Loved the playlist."),
                                new Comment(user_2, 3, "Very sweaty session."));

                setClassSessions(class_6,
                                new Session(LocalDateTime.of(2026, 3, 1, 9, 0), 20, class_6),
                                new Session(LocalDateTime.of(2026, 3, 4, 9, 0), 20, class_6),
                                new Session(LocalDateTime.of(2026, 3, 6, 9, 0), 20, class_6));

                gymClassRepository.save(class_6);

                GymClass class_7 = new GymClass("HIIT", "Alberto Vega",
                                "High intensity interval training.",
                                new ArrayList<>(), new ArrayList<>());

                setClassImage(class_7,
                                "sample_images/hiit_1.jpg",
                                "sample_images/hiit_2.jpg",
                                "sample_images/hiit_3.jpg",
                                "sample_images/hiit_4.jpg");

                setCommentsToClass(class_7,
                                new Comment(user_9, 5, "Quick and effective."),
                                new Comment(user_6, 4, "Loved it."),
                                new Comment(user_8, 3, "Very hard."));

                setClassSessions(class_7,
                                new Session(LocalDateTime.of(2026, 3, 2, 8, 0), 20, class_7),
                                new Session(LocalDateTime.of(2026, 3, 5, 8, 0), 20, class_7),
                                new Session(LocalDateTime.of(2026, 3, 7, 8, 0), 20, class_7));

                gymClassRepository.save(class_7);

                GymClass class_8 = new GymClass("BODY PUMP", "Sara Jimenez",
                                "Strength training with weights.",
                                new ArrayList<>(), new ArrayList<>());

                setClassImage(class_8,
                                "sample_images/bodypump_1.jpg",
                                "sample_images/bodypump_2.jpg",
                                "sample_images/bodypump_3.jpg",
                                "sample_images/bodypump_4.jpg");

                setCommentsToClass(class_8,
                                new Comment(user_5, 5, "Muscles burning!"),
                                new Comment(user_3, 4, "Very complete."),
                                new Comment(user_1, 4, "Nice routine."));

                setClassSessions(class_8,
                                new Session(LocalDateTime.of(2026, 3, 3, 12, 0), 20, class_8),
                                new Session(LocalDateTime.of(2026, 3, 6, 12, 0), 20, class_8),
                                new Session(LocalDateTime.of(2026, 3, 8, 12, 0), 20, class_8));

                gymClassRepository.save(class_8);

                GymClass class_9 = new GymClass("CALISTHENICS", "David Ortega",
                                "Bodyweight strength training.",
                                new ArrayList<>(), new ArrayList<>());

                setClassImage(class_9,
                                "sample_images/cardioburn_1.jpg",
                                "sample_images/cardioburn_2.jpg",
                                "sample_images/cardioburn_3.jpg",
                                "sample_images/cardioburn_4.jpg");

                setCommentsToClass(class_9,
                                new Comment(user_4, 5, "Amazing progress."),
                                new Comment(user_7, 4, "Challenging."),
                                new Comment(user_6, 4, "Loved the exercises."));

                setClassSessions(class_9,
                                new Session(LocalDateTime.of(2026, 3, 1, 16, 0), 15, class_9),
                                new Session(LocalDateTime.of(2026, 3, 4, 16, 0), 15, class_9),
                                new Session(LocalDateTime.of(2026, 3, 7, 16, 0), 15, class_9));

                gymClassRepository.save(class_9);

                GymClass class_10 = new GymClass("AQUA FITNESS", "Marina Lopez",
                                "Low impact water workout.",
                                new ArrayList<>(), new ArrayList<>());

                setClassImage(class_10,
                                "sample_images/powerabs_1.jpg",
                                "sample_images/powerabs_2.jpg",
                                "sample_images/powerabs_3.jpg",
                                "sample_images/powerabs_4.jpg");

                setCommentsToClass(class_10,
                                new Comment(user_8, 5, "Very relaxing."),
                                new Comment(user_2, 4, "Good for joints."),
                                new Comment(user_9, 4, "Nice environment."));

                setClassSessions(class_10,
                                new Session(LocalDateTime.of(2026, 3, 2, 11, 0), 20, class_10),
                                new Session(LocalDateTime.of(2026, 3, 5, 11, 0), 20, class_10),
                                new Session(LocalDateTime.of(2026, 3, 8, 11, 0), 20, class_10));

                gymClassRepository.save(class_10);

                GymClass class_11 = new GymClass("STRETCHING", "Clara Ruiz",
                                "Improve flexibility and mobility.",
                                new ArrayList<>(), new ArrayList<>());

                setClassImage(class_11,
                                "sample_images/stretch_1.jpg",
                                "sample_images/stretch_2.jpg",
                                "sample_images/stretch_3.jpg",
                                "sample_images/stretch_4.jpg");

                setCommentsToClass(class_11,
                                new Comment(user_1, 4, "Very necessary."),
                                new Comment(user_5, 5, "Feel much better."),
                                new Comment(user_7, 4, "Good recovery class."));

                setClassSessions(class_11,
                                new Session(LocalDateTime.of(2026, 3, 3, 9, 0), 20, class_11),
                                new Session(LocalDateTime.of(2026, 3, 6, 9, 0), 20, class_11),
                                new Session(LocalDateTime.of(2026, 3, 8, 9, 0), 20, class_11));

                gymClassRepository.save(class_11);

                GymClass class_12 = new GymClass("TRX", "Javier Molina",
                                "Suspension strength training.",
                                new ArrayList<>(), new ArrayList<>());

                setClassImage(class_12,
                                "sample_images/functional_1.jpg",
                                "sample_images/functional_2.jpg",
                                "sample_images/functional_3.jpg",
                                "sample_images/functional_4.jpg");

                setCommentsToClass(class_12,
                                new Comment(user_6, 5, "Very complete."),
                                new Comment(user_3, 4, "Hard but good."),
                                new Comment(user_4, 3, "Need more practice."));

                setClassSessions(class_12,
                                new Session(LocalDateTime.of(2026, 3, 1, 13, 0), 18, class_12),
                                new Session(LocalDateTime.of(2026, 3, 4, 13, 0), 18, class_12),
                                new Session(LocalDateTime.of(2026, 3, 7, 13, 0), 18, class_12));

                gymClassRepository.save(class_12);

                GymClass class_13 = new GymClass("MEDITATION", "Elena Navarro",
                                "Mindfulness and breathing techniques.",
                                new ArrayList<>(), new ArrayList<>());

                setClassImage(class_13,
                                "sample_images/yoga_1.jpg",
                                "sample_images/yoga_2.jpg",
                                "sample_images/yoga_3.jpg",
                                "sample_images/yoga_4.jpg");

                setCommentsToClass(class_13,
                                new Comment(user_2, 5, "Very peaceful."),
                                new Comment(user_8, 4, "Great atmosphere."),
                                new Comment(user_1, 5, "Perfect to relax."));

                setClassSessions(class_13,
                                new Session(LocalDateTime.of(2026, 3, 2, 20, 0), 20, class_13),
                                new Session(LocalDateTime.of(2026, 3, 5, 20, 0), 20, class_13),
                                new Session(LocalDateTime.of(2026, 3, 8, 20, 0), 20, class_13));

                gymClassRepository.save(class_13);
        }

        public void setProfileImage(AppUser user, String path) throws IOException {
                Resource image = new ClassPathResource(path);
                Image createdImage = imageService.createImage(image.getInputStream());
                user.setImage(createdImage);
        }

        public void setClassImage(GymClass gymClass,
                        String path1, String path2,
                        String path3, String path4) throws IOException {

                Resource image1 = new ClassPathResource(path1);
                Resource image2 = new ClassPathResource(path2);
                Resource image3 = new ClassPathResource(path3);
                Resource image4 = new ClassPathResource(path4);

                ArrayList<Image> images = new ArrayList<>();
                images.add(imageService.buildImage(image1.getInputStream()));
                images.add(imageService.buildImage(image2.getInputStream()));
                images.add(imageService.buildImage(image3.getInputStream()));
                images.add(imageService.buildImage(image4.getInputStream()));

                gymClass.setImages(images);
        }

        public void setCommentsToClass(GymClass gymClass,
                        Comment comment1,
                        Comment comment2,
                        Comment comment3) {

                comment1.setGymClass(gymClass);
                comment2.setGymClass(gymClass);
                comment3.setGymClass(gymClass);
                gymClass.getComments().add(comment1);
                gymClass.getComments().add(comment2);
                gymClass.getComments().add(comment3);
        }

        public void setClassSessions(GymClass gymClass,
                        Session session1,
                        Session session2,
                        Session session3) {

                session1.setGymClass(gymClass);
                session2.setGymClass(gymClass);
                session3.setGymClass(gymClass);
                gymClass.getSessions().add(session1);
                gymClass.getSessions().add(session2);
                gymClass.getSessions().add(session3);
        }
}
