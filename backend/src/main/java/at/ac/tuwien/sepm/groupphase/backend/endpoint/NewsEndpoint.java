package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.NewsDetailDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.NewsShortDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.NewsMapper;
import at.ac.tuwien.sepm.groupphase.backend.service.NewsService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.PermitAll;
import javax.validation.Valid;
import java.lang.invoke.MethodHandles;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/news")
public class NewsEndpoint {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final NewsService newsService;

    private final NewsMapper newsMapper;

    private final SecurityProperties securityProperties;

    @Autowired
    public NewsEndpoint(NewsService newsService, NewsMapper newsMapper, SecurityProperties securityProperties) {
        this.newsService = newsService;
        this.newsMapper = newsMapper;
        this.securityProperties = securityProperties;
    }

    @Secured("ROLE_ADMIN")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    @Operation(summary = "Publish a new news entry", security = @SecurityRequirement(name = "apiKey"))
    public NewsDetailDto create(@Valid @RequestBody NewsDetailDto newsDto) {
        LOGGER.info("POST /api/v1/news body: {}", newsDto);
        return newsMapper.newsToNewsDetailDto(newsService.publishNews(newsMapper.newsDetailDtoToNews(newsDto)));
    }


    @PermitAll
    @GetMapping
    @Operation(summary = "Get list of news without details", security = @SecurityRequirement(name = "apiKey"))
    public List<NewsShortDto> findAll() {
        LOGGER.info("GET /api/v1/news");
        return newsMapper.newsToNewsShortDto(newsService.findAll());
    }

    @Secured("ROLE_USER")
    @GetMapping(value = "/{id}")
    @Operation(summary = "Get detailed information about a specific news", security = @SecurityRequirement(name = "apiKey"))
    public NewsDetailDto find(@RequestHeader(name = "Authorization") String token, @PathVariable Long id) {
        LOGGER.info("GET /api/v1/news/{}", id);
        return newsMapper.newsToNewsDetailDto(newsService.findById(retrieveUsername(token), id));
    }


    @Secured("ROLE_USER")
    @GetMapping("/new")
    @Operation(summary = "Get list of unread before news without details", security = @SecurityRequirement(name = "apiKey"))
    public List<NewsShortDto> findAllNew(@RequestHeader(name = "Authorization") String token) {
        LOGGER.info("GET /api/v1/news/new");
        return newsMapper.newsToNewsShortDto(newsService.findAllNew(retrieveUsername(token)));
    }

    @Secured("ROLE_USER")
    @GetMapping("/old")
    @Operation(summary = "Get list of read before news without details", security = @SecurityRequirement(name = "apiKey"))
    public List<NewsShortDto> findAllOld(@RequestHeader(name = "Authorization") String token) {
        LOGGER.info("GET /api/v1/news/old");
        return newsMapper.newsToNewsShortDto(newsService.findAllOld(retrieveUsername(token)));
    }

    private String retrieveUsername(String jwtToken) {
        byte[] signingKey = securityProperties.getJwtSecret().getBytes();
        Claims claims = Jwts.parserBuilder().setSigningKey(signingKey).build()
            .parseClaimsJws(jwtToken.replace(securityProperties.getAuthTokenPrefix(), ""))
            .getBody();
        String user = claims.getSubject();
        return user;
    }
}
