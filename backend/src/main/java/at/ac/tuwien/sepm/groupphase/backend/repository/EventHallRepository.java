package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.EventHall;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventHallRepository extends JpaRepository<EventHall, Long> {

    @Query("select e from EventHall e where e.location.id = ?1")
    List<EventHall> findByLocation_Id(long id);

    @Override
    Page<EventHall> findAll(Pageable pageable);
}
