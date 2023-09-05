package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.News;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface NewsRepository extends JpaRepository<News, Long> {
    /**
     * Find all news entries ordered by published at date (descending).
     *
     * @return ordered list of all news entries
     */
    List<News> findAllByOrderByPublishedAtDesc();

    /**
     * Find all news entries with other ids than the given ordered by published at date (descending).
     *
     * @param ids list of news ids to exclude
     * @return ordered list of all news entries
     */
    @Query("select n from News n where n.id not in :ids order by n.publishedAt DESC")
    List<News> findByIdNotInOrderByPublishedAtDesc(@Param("ids") Collection<Long> ids);


    /**
     * Find all news entries with ids given ordered by published at date (descending).
     *
     * @param ids list of news ids to include
     * @return ordered list of all news entries
     */
    List<News> findByIdInOrderByPublishedAtDesc(Collection<Long> ids);
}
