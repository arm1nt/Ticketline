package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
    @Query("""
        select l from Location l
        where (?1 is null or upper(l.name) like upper(concat('%', ?1, '%')))
         and (?2 is null or upper(l.street) like upper(concat('%', ?2, '%')))
          and (?3 is null or upper(l.city) like upper(concat('%', ?3, '%')))
           and (?4 is null or upper(l.country) like upper(concat('%', ?4, '%')))
            and (?5 is null or upper(l.zipCode) like upper(concat('%', ?5, '%')))
        order by l.name""")
    List<Location> findByNameContainsIgnoreCaseAndStreetContainsIgnoreCaseAndCityContainsIgnoreCaseAndCountryContainsIgnoreCaseAndZipCodeContainsIgnoreCaseOrderByNameAsc(String name, String street, String city, String country,
                                                                                                                                                                          String zipCode);

    List<Location> findByName(String name);

    Location findById(long id);

}
