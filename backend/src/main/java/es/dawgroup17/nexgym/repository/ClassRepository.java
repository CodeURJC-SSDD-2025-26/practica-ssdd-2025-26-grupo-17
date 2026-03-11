package es.dawgroup17.nexgym.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import es.dawgroup17.nexgym.model.GymClass;

public interface ClassRepository extends JpaRepository<GymClass, Long> {
    Optional<GymClass> findGymClassById(Long id);
    Optional<GymClass> findGymClassByName(String name);



}