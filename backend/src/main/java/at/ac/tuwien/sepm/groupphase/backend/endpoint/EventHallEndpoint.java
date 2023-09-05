package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CreateEventHallDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.EventHallDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.EventHallOverviewDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.LayoutOverviewDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.EventHallMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.LayoutMapper;
import at.ac.tuwien.sepm.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.service.EventHallService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.lang.invoke.MethodHandles;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/eventhalls")
public class EventHallEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final EventHallMapper eventHallMapper;
    private final LayoutMapper layoutMapper;
    private final EventHallService eventHallService;

    public EventHallEndpoint(EventHallMapper eventHallMapper, EventHallService eventHallService,
                             LayoutMapper layoutMapper) {
        this.eventHallMapper = eventHallMapper;
        this.eventHallService = eventHallService;
        this.layoutMapper = layoutMapper;
    }

    @Secured("ROLE_ADMIN")
    @GetMapping
    @Operation(summary = "Get eventhalls", security = @SecurityRequirement(name = "apiKey"))
    public List<EventHallOverviewDto> getAll(
        @RequestParam(name = "hallname", required = false) String hallname,
        @RequestParam(name = "locationId", required = false) Long locationId,
        @RequestParam(name = "page", required = false) Integer page,
        @RequestParam(name = "size", required = false) Integer size) {
        LOGGER.info("GET /api/v1/eventhalls");

        if (locationId != null) {
            return this.eventHallMapper.eventHallListToEventHallOverviewDtoList(this.eventHallService.getByLocationId(locationId));
        }

        if (hallname != null) {
            return this.eventHallMapper.eventHallListToEventHallOverviewDtoList(this.eventHallService.getAll(hallname));
        }

       //paginated results
        if (page != null && size != null) {
            return this.eventHallService.getAllPaged(page, size);
        }

        return this.eventHallMapper.eventHallListToEventHallOverviewDtoList(this.eventHallService.getAll(hallname));

    }

    @Secured("ROLE_USER")
    @GetMapping("/{id}")
    @Operation(summary = "Get eventhall by id", security = @SecurityRequirement(name = "apiKey"))
    public EventHallDto getById(@PathVariable long id) throws NotFoundException {
        LOGGER.info("GET /api/v1/eventhalls/{}", id);

        return this.eventHallMapper.eventHallToEventHallDto(this.eventHallService.findById(id));
    }

    @Secured("ROLE_ADMIN")
    @PostMapping
    @Operation(summary = "Add new eventhall", security = @SecurityRequirement(name = "apiKey"))
    public EventHallOverviewDto createEventHall(@Valid @RequestBody CreateEventHallDto eventHall)
        throws NotFoundException, ConflictException {
        LOGGER.info("POST /api/v1/eventhalls");
        LOGGER.debug("body: {}", eventHall);

        return this.eventHallMapper.eventHallToEventHallOverviewDto(this.eventHallService.createEventHall(eventHall));
    }

    @Secured("ROLE_ADMIN")
    @PatchMapping(value = "/{id}")
    @Operation(summary = "Add layout to existing eventhall", security = @SecurityRequirement(name = "apiKey"))
    public EventHallOverviewDto addLayoutToEventHall(@PathVariable long id, @RequestBody CreateEventHallDto layout)
        throws NotFoundException, ConflictException {
        LOGGER.info("PATCH /api/v1/eventhalls/{}", id);
        LOGGER.debug("body: {}", layout);

        return this.eventHallMapper.eventHallToEventHallOverviewDto(
            this.eventHallService.addLayoutToEventHall(id, layout));
    }

    @Secured("ROLE_ADMIN")
    @GetMapping(value = "/{id}/layouts")
    @Operation(summary = "Get all layouts from an eventhall", security = @SecurityRequirement(name = "apiKey"))
    public List<LayoutOverviewDto> getAllLayoutsFromEventHall(@PathVariable long id) throws NotFoundException {
        LOGGER.info("GET /api/v1/eventhalls/{}/layouts", id);

        return this.layoutMapper.layoutListToLayoutOverviewDtoList(this.eventHallService.getAllLayoutsOfEventHall(id));
    }

}
