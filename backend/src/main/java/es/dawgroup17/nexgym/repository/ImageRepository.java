package es.dawgroup17.nexgym.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import es.dawgroup17.nexgym.model.Image;

public interface ImageRepository extends JpaRepository<Image, Long> {

}
