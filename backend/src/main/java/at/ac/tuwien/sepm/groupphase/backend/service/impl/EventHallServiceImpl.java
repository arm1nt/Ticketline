package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CreateEventHallDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.EventHallOverviewDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.EventHallMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.EventHall;
import at.ac.tuwien.sepm.groupphase.backend.entity.Layout;
import at.ac.tuwien.sepm.groupphase.backend.entity.Location;
import at.ac.tuwien.sepm.groupphase.backend.entity.RectangleGeometry;
import at.ac.tuwien.sepm.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.repository.EventHallRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.GeometryRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.LocationRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.EventHallService;
import at.ac.tuwien.sepm.groupphase.backend.service.LayoutService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional(readOnly = true)
public class EventHallServiceImpl implements EventHallService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final EventHallRepository eventHallRepository;
    private final LocationRepository locationRepository;
    private final LayoutService layoutService;
    private final GeometryRepository geometryRepository;
    private final EventHallMapper eventHallMapper;

    private final ObjectMapper objectMapper;

    public EventHallServiceImpl(EventHallRepository eventHallRepository, LocationRepository locationRepository,
                                LayoutService layoutService, GeometryRepository geometryRepository,
                                EventHallMapper eventHallMapper, ObjectMapper objectMapper) {
        this.eventHallRepository = eventHallRepository;
        this.locationRepository = locationRepository;
        this.layoutService = layoutService;
        this.geometryRepository = geometryRepository;
        this.eventHallMapper = eventHallMapper;
        this.objectMapper = objectMapper;
    }


    @Override
    public EventHall findById(long id) throws NotFoundException {
        LOGGER.trace("findById({})", id);

        Optional<EventHall> eventHall = this.eventHallRepository.findById(id);

        if (eventHall.isEmpty()) {
            throw new NotFoundException(String.format("There exists no eventhall with given id %s", id));
        }

        return eventHall.get();
    }


    @Override
    public List<EventHall> getAll(String hallname) {
        LOGGER.trace("getAll()");

        List<EventHall> halls = this.eventHallRepository.findAll();
        List<EventHall> eventHallWithSpecifiedName = new ArrayList<>();

        if (hallname == null) {
            return halls;
        }

        for (EventHall eventHall : halls) {
            if (eventHall.getName().equals(hallname)) {
                eventHallWithSpecifiedName.add(eventHall);
                break;
            }
        }

        return eventHallWithSpecifiedName;

    }

    @Transactional
    @Override
    public List<EventHallOverviewDto> getAllPaged(int page, int size) {
        LOGGER.trace("getAllPaged({}, {})", page, size);

        Page<EventHallOverviewDto> returnedPage = this.eventHallRepository.findAll(PageRequest.of(page, size))
            .map(eventHallMapper::eventHallToEventHallOverviewDto);

        return returnedPage.getContent();
    }

    @Override
    public EventHall createEventHall(CreateEventHallDto eventHallDto) throws NotFoundException, ConflictException {
        LOGGER.trace("createEventHall({})", eventHallDto);

        List<EventHall> checkForName = this.getAll(eventHallDto.getName());

        if (!checkForName.isEmpty()) {
            throw new ConflictException(String.format("An eventhall with the name %s already exists.", eventHallDto.getName()));
        }

        Location location = this.locationRepository.findById(eventHallDto.getLocation().getId());

        if (location == null) {
            throw new NotFoundException("Given location does not exist");
        }

        RectangleGeometry eventHallGeometry = RectangleGeometry.rectangleBuilder()
            .width(eventHallDto.getWidth())
            .height(eventHallDto.getHeight())
            .build();

        this.geometryRepository.save(eventHallGeometry);

        EventHall eventHall = EventHall.builder()
            .name(eventHallDto.getName())
            .geometry(eventHallGeometry)
            .location(location)
            .build();

        this.eventHallRepository.save(eventHall);

        Layout layout = this.layoutService.addLayoutToEventHall(eventHall, eventHallDto.getLayout());
        Set<Layout> layouts = eventHall.getLayouts();
        if (layouts == null) {
            layouts = new HashSet<>();
        }
        layouts.add(layout);
        eventHall.setLayouts(layouts);
        return this.eventHallRepository.save(eventHall);
    }

    @Transactional
    @Override
    public EventHall addLayoutToEventHall(long id, CreateEventHallDto eventHallDto)
        throws NotFoundException, ConflictException {
        LOGGER.trace("addLayoutToEventHall({}, {})", id, eventHallDto);

        Optional<EventHall> checkEventHall = this.eventHallRepository.findById(id);

        if (checkEventHall.isEmpty()) {
            throw new NotFoundException(String.format("There exists no eventhall with given id %s", id));
        }

        EventHall eventHall = checkEventHall.get();

        if (eventHall.getLocation() == null) {
            throw new ConflictException("Eventhall has no location");
        }

        Location checkLocation = this.locationRepository.findById(eventHall.getLocation().getId());

        if (checkLocation == null) {
            throw new NotFoundException(String.format("There exists no location with given id %s", id));
        }

        Layout layout = this.layoutService.addLayoutToEventHall(eventHall, eventHallDto.getLayout());

        Set<Layout> layouts = eventHall.getLayouts();
        if (layouts == null) {
            layouts = new HashSet<>();
        }
        layouts.add(layout);
        eventHall.setLayouts(layouts);
        return this.eventHallRepository.save(eventHall);
    }


    @Override
    public List<Layout> getAllLayoutsOfEventHall(long id) throws NotFoundException {
        LOGGER.trace("getAllLayoutsOfEventHall({})", id);

        Optional<EventHall> eventHallOptional = this.eventHallRepository.findById(id);

        if (eventHallOptional.isEmpty()) {
            throw new NotFoundException(String.format("No eventhall with the given id %s exists", id));
        }

        return eventHallOptional.get().getLayouts().stream().toList();
    }


    @Override
    public List<EventHall> getByLocationId(Long locationId) {
        LOGGER.trace("getByLocationId({})", locationId);

        return eventHallRepository.findByLocation_Id(locationId);
    }
}
