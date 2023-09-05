package at.ac.tuwien.sepm.groupphase.backend.repository;


import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.PerformanceSearchResultDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Location;
import at.ac.tuwien.sepm.groupphase.backend.entity.Performance;
import at.ac.tuwien.sepm.groupphase.backend.type.EventType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Repository
public interface PerformanceRepository extends JpaRepository<Performance, Long> {
    @Query("select p from Performance p where p.layout.eventHall.location = ?1 order by p.startTime")
    List<Performance> findByLayout_EventHall_LocationOrderByStartTimeAsc(Location location);

    List<Performance> findByIdIn(Collection<Long> ids);

    Performance getPerformanceById(long id);

    @Query(
        "SELECT DISTINCT p.id AS id, p.event.name AS eventName, p.performanceName AS performanceName,"
        + " p.startTime AS startTime, p.endTime AS endTime, p.event.eventType AS eventType FROM Performance p "
        + "INNER JOIN Layout l ON p.layout = l"
        + " JOIN Sector s ON s.layout = l AND ( ( s.price >= ?3 OR ?3 IS NULL) AND ( s.price <= ?4 OR ?4 IS NULL ) )"
        + "WHERE ((p.startTime <= ?1 OR ?1 IS NULL) AND (p.endTime >= ?2 OR ?2 IS NULL)) "
        + "AND (UPPER(p.event.name) LIKE UPPER(CONCAT('%', ?5, '%')) OR ?5 IS NULL)"
        + "AND (UPPER(l.eventHall.name) LIKE UPPER(CONCAT('%', ?6, '%')) OR ?6 IS NULL)"
        + "AND (p.event.eventType = ?7 OR ?7 IS NULL)"
        + "AND (UPPER(p.performanceName) LIKE UPPER(CONCAT('%', ?8, '%')) OR ?8 IS NULL)"
        )
    List<PerformanceSearchResultDto> searchForPerformances(LocalDateTime minTime,
                                                           LocalDateTime maxTime,
                                                           Double minPrice,
                                                           Double maxPrice,
                                                           String eventName,
                                                           String hallName,
                                                           EventType eventType,
                                                           String performanceName);
}
