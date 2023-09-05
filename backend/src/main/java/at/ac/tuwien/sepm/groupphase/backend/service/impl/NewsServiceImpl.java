package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.News;
import at.ac.tuwien.sepm.groupphase.backend.entity.UserHasSeenNews;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.repository.NewsRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserHasSeenNewsRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.NewsService;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
@Service
public class NewsServiceImpl implements NewsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final NewsRepository newsRepository;
    private final UserHasSeenNewsRepository userHasSeenNewsRepository;
    private final UserService userService;

    public NewsServiceImpl(NewsRepository newsRepository, UserService userService, UserHasSeenNewsRepository userHasSeenNewsRepository) {
        this.newsRepository = newsRepository;
        this.userService = userService;
        this.userHasSeenNewsRepository = userHasSeenNewsRepository;
    }

    @Transactional
    @Override
    public News publishNews(News news) {
        LOGGER.trace("publishNews({})", news);

        news.setPublishedAt(LocalDateTime.now());
        return newsRepository.save(news);
    }


    @Override
    public List<News> findAll() {
        LOGGER.trace("findAll()");

        return newsRepository.findAllByOrderByPublishedAtDesc();
    }

    @Transactional
    @Override
    public News findById(String username, long id) {
        LOGGER.trace("findId({},{})", username, id);

        ApplicationUser user = userService.findApplicationUserByUsername(username);
        Optional<News> news = newsRepository.findById(id);
        if (news.isPresent()) {
            UserHasSeenNews userHasSeenNews = new UserHasSeenNews();
            userHasSeenNews.setNews(news.get());
            userHasSeenNews.setUser(user);
            userHasSeenNewsRepository.save(userHasSeenNews);
            return news.get();
        } else {
            throw new NotFoundException(String.format("Could not find news with id %s", id));
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<News> findAllNew(String username) {
        LOGGER.trace("findAllNew({})", username);

        ApplicationUser user = userService.findApplicationUserByUsername(username);
        var seenNewsIds = userHasSeenNewsRepository.findByUser_Id(user.getId()).stream().map(UserHasSeenNews::getNews).map(News::getId).toList();
        return newsRepository.findByIdNotInOrderByPublishedAtDesc(seenNewsIds);
    }

    @Transactional(readOnly = true)
    @Override
    public List<News> findAllOld(String username) {
        LOGGER.trace("findAllOld({})", username);

        ApplicationUser user = userService.findApplicationUserByUsername(username);
        var seenNewsIds = userHasSeenNewsRepository.findByUser_Id(user.getId()).stream().map(UserHasSeenNews::getNews).map(News::getId).toList();
        return newsRepository.findByIdInOrderByPublishedAtDesc(seenNewsIds);
    }
}
