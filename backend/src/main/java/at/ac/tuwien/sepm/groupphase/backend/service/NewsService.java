package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.entity.News;

import java.util.List;

public interface NewsService {

    /**
     * Publish a single news entry.
     *
     * @param news to publish
     * @return published news entry
     */
    News publishNews(News news);

    /**
     * Find all news entries ordered by published at date (descending).
     *
     * @return ordered list of all news entries
     */
    List<News> findAll();

    /**
     * Finds a single news entry.
     *
     * @param id       of the to find
     * @param username of the user who reads the news
     * @return found news entry
     */
    News findById(String username, long id);

    /**
     * Find all news that the user has not yet seen. Entries ordered by published at date (descending).
     *
     * @param userName username which identifies the user
     * @return ordered list of all news entries the user has not yet seen
     */
    List<News> findAllNew(String userName);

    /**
     * Find all news that the user has seen. Entries ordered by published at date (descending).
     *
     * @param userName username which identifies the user
     * @return ordered list of all news entries the user has seen
     */
    List<News> findAllOld(String userName);
}
