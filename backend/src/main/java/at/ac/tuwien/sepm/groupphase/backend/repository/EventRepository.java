package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.Event;
import at.ac.tuwien.sepm.groupphase.backend.type.EventType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    Page<Event> findAll(Pageable pageable);

    @Query("select e from Event e where (?1 is null or upper(e.name) like upper(concat('%', ?1, '%'))) and (e.eventType = ?2 OR ?2 IS NULL) order by e.name")
    List<Event> searchForEvents(String name, EventType eventType);

    @Query("select e from Event e where (?1 is null or upper(e.name) like upper(concat('%', ?1, '%'))) and (e.eventType = ?2 OR ?2 IS NULL) and e.duration between ?3 and ?4 order by e.name")
    List<Event> searchForEventsWithDuration(String name, EventType eventType, int durationStart, int durationEnd);

    @Query("select distinct e from Event e inner join e.performers performers where performers.id = ?1")
    List<Event> findDistinctByPerformers_Id(Long id);

    Event getEventById(Long id);

    @Query(value = "SELECT TOP 10 e.id, SUM(p.sold_tickets) "
        + "FROM PERFORMANCE p, EVENT e "
        + "WHERE p.event_id = e.id AND p.start_time >= :date AND LOWER(e.event_type) LIKE LOWER(CONCAT('%',:eventType,'%')) "
        + "GROUP BY e.id "
        + "ORDER BY SUM(p.sold_tickets) DESC", nativeQuery = true)
    List<Object[]> getTop10Events(@Param("date") LocalDateTime date, @Param("eventType")String eventType);

}
