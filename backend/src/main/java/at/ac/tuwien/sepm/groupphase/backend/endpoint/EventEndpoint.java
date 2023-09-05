package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.EventDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.LayoutDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.EventMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.LayoutMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.OrderMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.Event;
import at.ac.tuwien.sepm.groupphase.backend.service.EventService;
import at.ac.tuwien.sepm.groupphase.backend.service.LayoutService;
import at.ac.tuwien.sepm.groupphase.backend.service.OrderService;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import at.ac.tuwien.sepm.groupphase.backend.type.EventType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.PermitAll;
import javax.validation.Valid;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "api/v1/events")
public class EventEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final EventMapper eventMapper;
    private final EventService eventService;
    private final OrderService orderService;
    private final OrderMapper orderMapper;
    private final LayoutMapper layoutMapper;
    private final SecurityProperties securityProperties;
    private final UserService userService;
    private final LayoutService layoutService;

    @Autowired
    public EventEndpoint(EventService eventService, EventMapper eventMapper, OrderService orderService, OrderMapper orderMapper, LayoutMapper layoutMapper, UserService userService, SecurityProperties securityProperties,
                         LayoutService layoutService) {
        this.eventService = eventService;
        this.eventMapper = eventMapper;
        this.orderService = orderService;
        this.orderMapper = orderMapper;
        this.layoutMapper = layoutMapper;
        this.userService = userService;
        this.securityProperties = securityProperties;
        this.layoutService = layoutService;
    }

    @PermitAll
    @GetMapping(value = "/{id}")
    @Operation(summary = "Get event by id")
    public EventDto getById(@PathVariable Long id) {
        LOGGER.info("GET /ap1/v1/events/{}", id);

        return this.eventMapper.eventToEventDto(this.eventService.getById(id));
    }


    @Secured("ROLE_ADMIN")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    @Operation(summary = "Create a new event", security = @SecurityRequirement(name = "apiKey"))
    public EventDto create(@Valid @RequestBody EventDto eventDto) {
        LOGGER.info("POST /api/v1/events body: {}", eventDto);

        return this.eventMapper.eventToEventDto(this.eventService.create(this.eventMapper.eventDtoToEvent(eventDto)));

    }

    @PermitAll
    @GetMapping(path="/top10")
    public List<EventDto> getTop10Events(@Parameter(required = true, description = "The event type of the top 10 events") @RequestParam("eventType") Optional<EventType> eventType) {
        LOGGER.info("GET /api/v1/events/top10?eventType="+eventType);
        List<EventDto> list;
        if (eventType.isPresent()) {
            EventType eventType1 = eventType.get();
            list = eventService.getTop10Events(eventType1.toString());
        } else {
            list = eventService.getTop10Events("");
        }
        return list;
    }

    @Secured("ROLE_USER")
    @GetMapping("/layout/{layoutId}")
    @Operation(summary = "Get the layout with its event hall")
    public LayoutDto getLayoutEvent(@PathVariable long layoutId) {
        LOGGER.info("GET /api/v1/events/layout/{}", layoutId);

        return layoutMapper.layoutToLayoutDto(layoutService.getById(layoutId));
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/layout")
    @Operation(summary = "Get all layout with its event hall")
    public List<LayoutDto> getAllLayouts() {
        LOGGER.info("GET /api/v1/events/layout/");

        return layoutMapper.layoutListToLayoutDtoList(layoutService.getAll(null));
    }

    @PermitAll
    @GetMapping
    @Operation(summary = "Get events which fit search parameters", security = @SecurityRequirement(name = "apiKey"))
    public List<EventDto> searchEvents(@RequestParam(name = "name", required = false) String name,
                                       @RequestParam(name = "eventType", required = false) EventType eventType,
                                       @RequestParam(name = "duration", required = false) Integer duration,
                                       @RequestParam(name = "tolerance", required = false) Integer tolerance,
                                       @RequestParam(name = "page", required = false) Integer page,
                                       @RequestParam(name = "size", required = false) Integer size) {
        LOGGER.info("GET /api/v1/events name={}, eventType={}, duration={}, tolerance={}", name, eventType, duration, tolerance);
        if (name == null && eventType == null && duration == null && tolerance == null) {

            if (page == null && size == null) {
                return this.eventMapper.eventListToEventDtoList(this.eventService.getAll());
            }

            Integer currPage = (page != null) ? page : 0;
            Integer currSize = (size != null) ? size : 12;

            return this.eventService.getAllEventsPaged(currPage, currSize);


        }
        return this.eventMapper.eventListToEventDtoList(this.eventService.search(name, eventType, duration, tolerance));
    }
}
