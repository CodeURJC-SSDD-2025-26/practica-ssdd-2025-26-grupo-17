package es.dawgroup17.nexgym.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.dawgroup17.nexgym.model.Session;

import java.time.LocalDateTime;
import java.util.List;

public interface SessionRepository extends JpaRepository<Session, Long> {

    List<Session> findByGymClassId(long gymClassId);

    @Query("SELECT s FROM Session s WHERE s.gymClass.id = :classId AND s.date >= :from AND s.date < :to ORDER BY s.date ASC")
    List<Session> findByGymClassIdAndDateBetween(
            @Param("classId") Long classId,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to);

}