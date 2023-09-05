package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CreateLayoutDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.LayoutDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.RowDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SeatDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SeatingDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SectorDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.StandDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.StandingDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.LayoutMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.TicketMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.EventHall;
import at.ac.tuwien.sepm.groupphase.backend.entity.Geometry;
import at.ac.tuwien.sepm.groupphase.backend.entity.Layout;
import at.ac.tuwien.sepm.groupphase.backend.entity.Performance;
import at.ac.tuwien.sepm.groupphase.backend.entity.RectangleGeometry;
import at.ac.tuwien.sepm.groupphase.backend.entity.Row;
import at.ac.tuwien.sepm.groupphase.backend.entity.Seat;
import at.ac.tuwien.sepm.groupphase.backend.entity.SeatGeometry;
import at.ac.tuwien.sepm.groupphase.backend.entity.Seating;
import at.ac.tuwien.sepm.groupphase.backend.entity.Stand;
import at.ac.tuwien.sepm.groupphase.backend.entity.Standing;
import at.ac.tuwien.sepm.groupphase.backend.entity.Ticket;
import at.ac.tuwien.sepm.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.repository.EventHallRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.GeometryRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.LayoutRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.LocationRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.RowRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.SeatRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.SectorRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.StandRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.LayoutService;
import at.ac.tuwien.sepm.groupphase.backend.service.PerformanceService;
import at.ac.tuwien.sepm.groupphase.backend.service.TicketService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Transactional(readOnly = true)
@Service
public class LayoutServiceImpl implements LayoutService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final LayoutRepository layoutRepository;
    private final GeometryRepository geometryRepository;
    private final EventHallRepository eventHallRepository;
    private final SectorRepository sectorRepository;
    private final StandRepository standRepository;
    private final RowRepository rowRepository;
    private final SeatRepository seatRepository;
    private final LocationRepository locationRepository;
    private final PerformanceService performanceService;
    private final TicketMapper ticketMapper;
    private final LayoutMapper layoutMapper;
    private final TicketService ticketService;

    public LayoutServiceImpl(LayoutRepository layoutRepository, GeometryRepository geometryRepository,
                             EventHallRepository eventHallRepository, SectorRepository sectorRepository,
                             StandRepository standRepository, RowRepository rowRepository,
                             SeatRepository seatRepository, LocationRepository locationRepository,
                             PerformanceService performanceService, TicketMapper ticketMapper,
                             LayoutMapper layoutMapper, TicketService ticketService) {
        this.layoutRepository = layoutRepository;
        this.geometryRepository = geometryRepository;
        this.eventHallRepository = eventHallRepository;
        this.sectorRepository = sectorRepository;
        this.standRepository = standRepository;
        this.rowRepository = rowRepository;
        this.seatRepository = seatRepository;
        this.locationRepository = locationRepository;
        this.performanceService = performanceService;
        this.ticketMapper = ticketMapper;
        this.layoutMapper = layoutMapper;
        this.ticketService = ticketService;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Layout addLayoutToEventHall(EventHall eventHall, CreateLayoutDto layoutDto)
        throws NotFoundException, ConflictException {
        LOGGER.trace("createLayoutForEventHall({}, {})", eventHall, layoutDto);

        Layout checkForAlreadyExistingName = this.layoutRepository.findByName(layoutDto.getName());
        if (checkForAlreadyExistingName != null) {
            throw new ConflictException(String.format("Eventhall with the name %s already exists", layoutDto.getName()));
        }


        Layout layout = Layout.builder()
            .name(layoutDto.getName())
            .eventHall(eventHall)
            .build();

        layout = this.layoutRepository.save(layout);

        for (int i = 0; i < layoutDto.getSectors().length; i++) {
            if (layoutDto.getSectors()[i].getType().equals("Standing")) {
                RectangleGeometry standingGeometry = RectangleGeometry.rectangleBuilder()
                    .x(layoutDto.getSectors()[i].getX())
                    .y(layoutDto.getSectors()[i].getY())
                    .width(layoutDto.getSectors()[i].getWidth())
                    .height(layoutDto.getSectors()[i].getHeight())
                    .build();

                this.geometryRepository.save(standingGeometry);

                Standing standing = Standing.builder()
                    .sectorId(String.valueOf(layoutDto.getSectors()[i].getColorSector()))
                    .price(layoutDto.getSectors()[i].getPrice())
                    .capacity((int) layoutDto.getSectors()[i].getCapacity())
                    .color(layoutDto.getSectors()[i].getColor())
                    .geometry(this.generateRectangleGeometry(
                        layoutDto.getSectors()[i].getX(),
                        layoutDto.getSectors()[i].getY(),
                        layoutDto.getSectors()[i].getWidth(),
                        layoutDto.getSectors()[i].getHeight()
                    ))
                    .layout(layout)
                    .build();

                this.sectorRepository.save(standing);
                this.generateStands(standing);
                this.sectorRepository.save(standing);
            } else if (layoutDto.getSectors()[i].getType().equals("Seating")) {
                Seating seating = Seating.builder()
                    .sectorId(String.valueOf(layoutDto.getSectors()[i].getColorSector()))
                    .price(layoutDto.getSectors()[i].getPrice())
                    .geometry(this.generateRectangleGeometry(
                        layoutDto.getSectors()[i].getX(),
                        layoutDto.getSectors()[i].getY(),
                        layoutDto.getSectors()[i].getWidth(),
                        layoutDto.getSectors()[i].getHeight()
                    ))
                    .color(layoutDto.getSectors()[i].getColor())
                    .layout(layout)
                    .build();
                this.sectorRepository.save(seating);

                for (int j = 0; j < layoutDto.getSectors()[i].getRows().length; j++) {
                    Row row = null;
                    if (layoutDto.getSectors()[i].getRows()[j].getNumber() >= 10) {
                        row = Row.builder()
                            .rowNumber(layoutDto.getSectors()[i].getRows()[j].getNumber())
                            .seating(seating)
                            .geometry(this.generateGeometry(
                                layoutDto.getSectors()[i].getRows()[j].getX() + 12,
                                layoutDto.getSectors()[i].getRows()[j].getY() + 6
                            ))
                            .build();
                    } else {
                        row = Row.builder()
                            .rowNumber(layoutDto.getSectors()[i].getRows()[j].getNumber())
                            .seating(seating)
                            .geometry(this.generateGeometry(
                                layoutDto.getSectors()[i].getRows()[j].getX() + 15,
                                layoutDto.getSectors()[i].getRows()[j].getY() + 6
                            ))
                            .build();
                    }
                    this.rowRepository.save(row);

                    for (int k = 0; k < layoutDto.getSectors()[i].getRows()[j].getSeats().length; k++) {
                        SeatGeometry seatGeometry = this.generateStandardSeatAtPosition(
                            layoutDto.getSectors()[i].getRows()[j].getSeats()[k].getX() - 17,
                            layoutDto.getSectors()[i].getRows()[j].getSeats()[k].getY() - 17
                        );

                        int addOneToRowNumber = Integer.parseInt(layoutDto.getSectors()[i].getRows()[j].getSeats()[k].getSeatId().split(",")[1]);
                        addOneToRowNumber++;

                        Seat seat = Seat.builder()
                            .seatId(String.valueOf(addOneToRowNumber))
                            .geometry(seatGeometry)
                            .row(row)
                            .build();
                        this.seatRepository.save(seat);
                    }
                }
            }
        }

        return layout;
    }

    @Override
    public LayoutDto getLayoutDtoByPerformanceIdWithTicketSpotDtos(Long performanceId) throws NotFoundException {
        LOGGER.trace("getLayoutDtoByPerformanceIdWithTicketSpotDtos({})", performanceId);

        Performance performance = this.performanceService.getById(performanceId);
        if (performance == null) {
            throw new NotFoundException("Performance for which the layout has been requested does not exist");
        }

        Set<Ticket> tickets = this.ticketService.getAllByPerformanceId(performanceId);
        long layoutId = performance.getLayout().getId();
        Layout layout = this.layoutRepository.getLayoutById(layoutId);


        Map<Long, Ticket> seatIdTicketMap = new HashMap<>();
        Map<Long, Ticket> standIdTicketMap = new HashMap<>();

        for (Ticket t : tickets) {
            if (t.getSeat() != null) {
                seatIdTicketMap.put(t.getSeat().getId(), t);
            } else {
                standIdTicketMap.put(t.getStand().getId(), t);
            }
        }

        if (layout == null) {
            return null;
        }

        LayoutDto layoutDto = new LayoutDto();

        //got sectors down to  seats/stands
        layoutDto.setName(layout.getName());
        layoutDto.setEventHall(layoutMapper.eventHallToEventHallDto(layout.getEventHall()));
        layoutDto.setId(layout.getId());
        layoutDto.setSectors(layoutMapper.mapSectors(layout.getSectors()));
        //add tickets to seats of the layoutDto
        for (SectorDto s :
            layoutDto.getSectors()) {
            if (s.getClass() == SeatingDto.class) {
                for (RowDto r : ((SeatingDto) s).getRows()) {
                    for (SeatDto seat :
                        r.getSeats()) {
                        seat.setTicket(this.ticketMapper.ticketToTicketSpotDto(seatIdTicketMap.get(seat.getId())));
                    }
                }
            } else if (s.getClass() == StandingDto.class) {
                for (StandDto stand :
                    ((StandingDto) s).getStands()) {
                    stand.setTicket(this.ticketMapper.ticketToTicketSpotDto(standIdTicketMap.get(stand.getId())));
                }
            }
        }

        return layoutDto;
    }

    @Override
    public List<Layout> getByEventHallId(Long evenHallId) {
        LOGGER.trace("getByEventHallId({})", evenHallId);

        return layoutRepository.findByEventHall_Id(evenHallId);
    }


    @Override
    public Layout getById(Long id) throws NotFoundException {
        LOGGER.trace("getById({})", id);

        Layout layout = layoutRepository.getLayoutById(id);

        if (layout == null) {
            throw new NotFoundException("This layout does not exist");
        }

        return layout;
    }

    @Override
    public List<Layout> getAll(String layoutName) {
        LOGGER.trace("getAll({})", layoutName);

        List<Layout> layouts = this.layoutRepository.findAll();
        List<Layout> layoutByName = new ArrayList<>();

        if (layoutName == null) {
            return layouts;
        }

        for (Layout l : layouts) {
            if (l.getName().equals(layoutName)) {
                layoutByName.add(l);
                break;
            }
        }

        return layoutByName;
    }

    private void generateStands(Standing standing) {
        LOGGER.trace("generateStands({})", standing);

        Ticket ticket;
        Stand stand;
        LOGGER.debug("generating {} stand entries", standing.getCapacity());
        for (int k = 0; k < standing.getCapacity(); k++) {
            stand = Stand.builder()
                .standing(standing)
                .build();
            standing.getStands().add(stand);
            LOGGER.debug("saving stand {}", stand);
            standRepository.save(stand);
        }
    }

    private SeatGeometry generateStandardSeatAtPosition(double x, double y) {
        LOGGER.trace("generateStandardSeatAtPosition({}, {})", x, y);

        SeatGeometry seatGeometry = SeatGeometry.seatBuilder()
            .x(x)
            .y(y)
            .width(30)
            .height(30)
            .legSpaceDepth(12)
            .build();
        LOGGER.debug("saving standing geometry {}", seatGeometry);
        geometryRepository.save(seatGeometry);
        return seatGeometry;
    }

    private Geometry generateGeometry(double x, double y) {
        LOGGER.trace("generateGeometry({}, {})", x, y);

        Geometry geometry = Geometry.builder()
            .x(x)
            .y(y)
            .build();
        LOGGER.debug("saving geometry {}", geometry);
        geometryRepository.save(geometry);
        return geometry;
    }

    private RectangleGeometry generateRectangleGeometry(double x, double y, double width, double height) {
        LOGGER.trace("generateRectangleGeometry({}, {}, {}, {})", x, y, width, height);

        RectangleGeometry geometry = RectangleGeometry.rectangleBuilder()
            .x(x)
            .y(y)
            .width(width)
            .height(height)
            .build();
        LOGGER.debug("saving rectangle geometry {}", geometry);
        geometryRepository.save(geometry);
        return geometry;
    }
}
