package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.UserHasSeenNews;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserHasSeenNewsRepository extends JpaRepository<UserHasSeenNews, Long> {

    /**
     * Finds all UserHasSeenNews entries for specific user with given id.
     *
     * @param id of the user
     * @return list of entries of userHasSeenNews of the user
     */
    List<UserHasSeenNews> findByUser_Id(long id);

}
