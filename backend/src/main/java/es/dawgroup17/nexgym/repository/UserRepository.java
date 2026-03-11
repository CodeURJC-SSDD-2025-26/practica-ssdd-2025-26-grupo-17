package es.dawgroup17.nexgym.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

import es.dawgroup17.nexgym.model.AppUser;


public interface UserRepository extends JpaRepository<AppUser, Long> {

	//The other classes can only access a user by its email
	boolean existsByEmail(String email);

	Optional<AppUser> findByEmail(String email);



}
