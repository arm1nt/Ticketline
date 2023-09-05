package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.Layout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LayoutRepository extends JpaRepository<Layout, Long> {
    @Query("select l from Layout l where l.eventHall.id = ?1")
    List<Layout> findByEventHall_Id(long id);

    Layout getLayoutById(long id);

    Layout findByName(String name);

}
