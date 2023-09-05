package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.Performer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PerformerRepository extends JpaRepository<Performer, Long> {
    @Query("select distinct p from Performer p where upper(p.performerName) like upper(concat('%', ?1, '%')) order by p.performerName")
    List<Performer> findDistinctByPerformerNameContainsIgnoreCaseOrderByPerformerNameAsc(@Nullable String performerName);


    @Query("select p from Performer p where p.performerName = :performerName")
    List<Performer> getByPerformerName(@Param("performerName") String performerName);

}
