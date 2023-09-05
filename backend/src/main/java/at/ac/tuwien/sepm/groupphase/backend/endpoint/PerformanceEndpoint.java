package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.LayoutDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.PerformanceDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.PerformanceSearchDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.PerformanceSearchResultDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.PerformanceMapper;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.service.LayoutService;
import at.ac.tuwien.sepm.groupphase.backend.service.PerformanceService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.lang.invoke.MethodHandles;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/performance")
public class PerformanceEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final PerformanceService performanceService;
    private final PerformanceMapper performanceMapper;

    private final LayoutService layoutService;


    @Autowired
    public PerformanceEndpoint(PerformanceService performanceService, PerformanceMapper performanceMapper,
                               LayoutService layoutService) {
        this.performanceService = performanceService;
        this.performanceMapper = performanceMapper;
        this.layoutService = layoutService;
    }

    @Secured("ROLE_USER")
    @GetMapping
    @Operation(summary = "Get performances which fit search parameters", security = @SecurityRequirement(name = "apiKey"))
    public List<PerformanceDto> searchPerformances(@RequestParam(name = "locationId", required = true) long locationId) {
        LOGGER.info("GET /api/v1/performance locationId={}", locationId);

        return performanceMapper.performanceListToPerformanceDtoList(performanceService.search(locationId));
    }

    @Secured("ROLE_USER")
    @GetMapping("/{id}")
    @Operation(summary = "Get the performance referenced by the id", security = @SecurityRequirement(name = "apiKey"))
    public PerformanceDto getPerformance(@RequestHeader(name = "Authorization") String token, @PathVariable long id)
        throws NotFoundException {
        LOGGER.info("GET /api/v1/performance/{}", id);

        return performanceMapper.performanceToPerformanceDto(this.performanceService.getById(id));

    }

    @Secured("ROLE_ADMIN")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    @Operation(summary = "Add a new performance", security = @SecurityRequirement(name = "apiKey"))
    public PerformanceDto create(@Valid @RequestBody PerformanceDto performanceDto) {
        LOGGER.info("Posting {}", performanceDto);

        return this.performanceMapper.performanceToPerformanceDto(this.performanceService.create(performanceDto));

    }

    @Secured("ROLE_USER")
    @GetMapping("/{id}/layout")
    @Operation(summary = "Get the layout of this performance with its event hall")
    public LayoutDto getLayoutWithTicketsForPerformance(@PathVariable long id) {
        LOGGER.info("GET api/v1/performance/{}/layout", id);

        return layoutService.getLayoutDtoByPerformanceIdWithTicketSpotDtos(id);
    }

    @Secured("ROLE_USER")
    @GetMapping("/maxPrice")
    @Operation(summary = "Get maximum price of any sector in any performance")
    public Double getMaxPrice() {
        LOGGER.info("GET api/v1/performance/maxPrice");
        return performanceService.findMaxPrice();
    }

    @Secured("ROLE_USER")
    @GetMapping("/search")
    @Operation(summary = "Search for performances")
    public List<PerformanceSearchResultDto> searchForPerformances(PerformanceSearchDto searchDto) {
        LOGGER.info("GET api/v1/performance/search");
        LOGGER.debug("searchForPerformances with {}", searchDto);
        return performanceService.searchForPerformances(searchDto);
    }
}
