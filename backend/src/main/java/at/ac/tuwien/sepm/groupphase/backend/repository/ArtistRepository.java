package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.Artist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArtistRepository extends JpaRepository<Artist, Long> {
    @Query("select a from Artist a " +
        "where (?1 is null or upper(a.performerName) like upper(concat('%', ?1, '%'))) " +
        "and (?2 is null or upper(a.firstName) like upper(concat('%', ?2, '%'))) " +
        "and (?3 is null or upper(a.lastName) like upper(concat('%', ?3, '%'))) order by a.performerName")
    List<Artist> findByPerformerNameContainsIgnoreCaseAndFirstNameContainsIgnoreCaseAndLastNameContainsIgnoreCaseOrderByPerformerNameAsc(String performerName, String firstName, String lastName);

}
