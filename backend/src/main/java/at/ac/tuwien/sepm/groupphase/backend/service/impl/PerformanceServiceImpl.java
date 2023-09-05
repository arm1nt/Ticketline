package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.PerformanceDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.PerformanceSearchDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.PerformanceSearchResultDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Event;
import at.ac.tuwien.sepm.groupphase.backend.entity.EventHall;
import at.ac.tuwien.sepm.groupphase.backend.entity.Layout;
import at.ac.tuwien.sepm.groupphase.backend.entity.Location;
import at.ac.tuwien.sepm.groupphase.backend.entity.Performance;
import at.ac.tuwien.sepm.groupphase.backend.entity.Row;
import at.ac.tuwien.sepm.groupphase.backend.entity.Seat;
import at.ac.tuwien.sepm.groupphase.backend.entity.Seating;
import at.ac.tuwien.sepm.groupphase.backend.entity.Sector;
import at.ac.tuwien.sepm.groupphase.backend.entity.Stand;
import at.ac.tuwien.sepm.groupphase.backend.entity.Standing;
import at.ac.tuwien.sepm.groupphase.backend.entity.Ticket;
import at.ac.tuwien.sepm.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.repository.EventHallRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.EventRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.LayoutRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.LocationRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.PerformanceRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.SectorRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.TicketRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.PerformanceService;
import at.ac.tuwien.sepm.groupphase.backend.type.TicketStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ValidationException;
import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
@Service
public class PerformanceServiceImpl implements PerformanceService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final PerformanceRepository performanceRepository;
    private final EventRepository eventRepository;
    private final LayoutRepository layoutRepository;
    private final LocationRepository locationRepository;
    private final EventHallRepository eventHallRepository;
    private final TicketRepository ticketRepository;
    private final SectorRepository sectorRepository;

    public PerformanceServiceImpl(PerformanceRepository performanceRepository,
                                  EventRepository eventRepository,
                                  LayoutRepository layoutRepository,
                                  LocationRepository locationRepository,
                                  EventHallRepository eventHallRepository,
                                  TicketRepository ticketRepository,
                                  SectorRepository sectorRepository) {
        this.performanceRepository = performanceRepository;
        this.eventRepository = eventRepository;
        this.layoutRepository = layoutRepository;
        this.locationRepository = locationRepository;
        this.eventHallRepository = eventHallRepository;
        this.ticketRepository = ticketRepository;
        this.sectorRepository = sectorRepository;
    }

    @Transactional(readOnly = false)
    @Override
    public Performance create(PerformanceDto performanceDto) {
        LOGGER.trace("create({})", performanceDto);

        Optional<Event> event = eventRepository.findById(performanceDto.getEventId());
        Optional<Layout> layout = layoutRepository.findById(performanceDto.getLayoutId());
        Optional<Location> location = locationRepository.findById(performanceDto.getLocationId());
        Optional<EventHall> eventHall = eventHallRepository.findById(performanceDto.getEventhallId());

        if (layout.isEmpty()) {
            throw new NotFoundException("Layout with id " + performanceDto.getLayoutId() + " in given Performance with id " + performanceDto.getPerformanceName() + " not found");
        }

        if (event.isEmpty()) {
            throw new NotFoundException("Event with id " + performanceDto.getEventId() + " in given Performance with id " + performanceDto.getPerformanceName() + " not found");
        }

        if (location.isEmpty()) {
            throw new NotFoundException("Location with id " + performanceDto.getLayoutId() + " in given Performance with id " + performanceDto.getPerformanceName() + " not found");
        }

        if (eventHall.isEmpty()) {
            throw new NotFoundException("EventHall with id " + performanceDto.getEventId() + " in given Performance with id " + performanceDto.getPerformanceName() + " not found");
        }

        List<Performance> performances = event.get().getPerformances();
        for (Performance performanceTemp : performances) {
            if (performanceDto.getStartTime().isBefore(performanceTemp.getEndTime()) && performanceDto.getStartTime().isAfter(performanceTemp.getStartTime())) {
                throw new ValidationException("Overlapping Performances");
            }
        }

        if (eventHall.get().getLocation().getId() != location.get().getId()) {
            throw new ConflictException("Eventhall \"" + eventHall.get().getName() + "\" is not part of the location \"" + location.get().getName() + "\"");
        }


        if (!eventHall.get().getLayouts().stream().map(Layout::getId).toList().contains(layout.get().getId())) {
            throw new ConflictException("Layout \"" + layout.get().getName() + "\" doesn't belong to eventhall " + eventHall.get().getName() + "\"");
        }

        Performance performanceTemp = new Performance();
        performanceTemp.setPerformanceName(performanceDto.getPerformanceName());
        performanceTemp.setStartTime(performanceDto.getStartTime());
        performanceTemp.setEndTime(performanceDto.getStartTime().plusMinutes(event.get().getDuration()));
        performanceTemp.setEvent(event.get());
        performanceTemp.setLayout(layout.get());
        Performance createdPerformance = this.performanceRepository.save(performanceTemp);

        generateTicketsForPerformanceInLayout(createdPerformance, layout.get());

        return createdPerformance;
    }

    @Override
    public Performance getById(long id) {
        LOGGER.trace("getById({})", id);

        return this.performanceRepository.getPerformanceById(id);
    }

    @Override
    public List<Performance> search(long locationId) {
        LOGGER.trace("search({})", locationId);

        Optional<Location> location = Optional.ofNullable(locationRepository.findById(locationId));
        if (location.isEmpty()) {
            return new ArrayList<>();
        }
        return this.performanceRepository.findByLayout_EventHall_LocationOrderByStartTimeAsc(location.get());
    }

    @Override
    public Double findMaxPrice() {
        LOGGER.trace("findMaxPrice()");
        Double maxPrice = sectorRepository.findMaxPrice();
        return maxPrice == null ? 0 : maxPrice;
    }

    @Override
    public List<PerformanceSearchResultDto> searchForPerformances(PerformanceSearchDto searchDto) {
        LOGGER.trace("SearchForPerformances()");
        PerformanceSearchDto emptySearchDto = new PerformanceSearchDto();
        if (searchDto.getMaxPrice() != null && searchDto.getMaxPrice().equals(0.0)) {
            searchDto.setMaxPrice(null);
        }
        if (searchDto.equals(emptySearchDto)) {
            return new ArrayList<>();
        }
        LocalDateTime minTime = searchDto.getTime() != null ? searchDto.getTime().plusMinutes(30) : null;
        LocalDateTime maxTime = searchDto.getTime() != null ? searchDto.getTime().minusMinutes(30) : null;
        return performanceRepository.searchForPerformances(
            minTime,
            maxTime,
            searchDto.getMinPrice(),
            searchDto.getMaxPrice(),
            searchDto.getEventName(),
            searchDto.getHallName(),
            searchDto.getEventType(),
            searchDto.getPerformanceName()
        );
    }


    private void generateTicketsForPerformanceInLayout(Performance performance, Layout layout) {
        LOGGER.trace("generateTicketsForPerformanceInLayout()");

        Ticket ticket;
        for (Sector s :
            layout.getSectors()) {
            if (s.getClass() == Seating.class) {
                for (Row r : ((Seating) s).getRows()) {
                    for (Seat seat :
                        r.getSeats()) {
                        ticket = generateTicket(("Seat " + seat.getSeatId() + " - Row " + r.getRowNumber()), TicketStatus.FREE, seat, performance, s.getPrice());
                        ticketRepository.save(ticket);
                    }
                }
            } else if (s.getClass() == Standing.class) {
                int k = 0;
                for (Stand stand :
                    ((Standing) s).getStands()) {
                    ticket = generateTicket("Stand " + ++k + " - Sector " + s.getSectorId(), TicketStatus.FREE, stand, performance, s.getPrice());
                    ticketRepository.save(ticket);
                }
            }
        }
    }


    private Ticket generateTicket(String ticketId, TicketStatus status, Seat seat, Performance performance, double price) {
        LOGGER.trace("generateTicket({}, {}, {}, {}, {})", ticketId, status, seat, performance, price);

        return Ticket.builder()
            .ticketId(ticketId)
            .ticketStatus(status)
            .seat(seat)
            .performance(performance)
            .price(price)
            .build();
    }

    private Ticket generateTicket(String ticketId, TicketStatus status, Stand stand, Performance performance, double price) {
        LOGGER.trace("generateTicket({}, {}, {}, {}, {})", ticketId, status, stand, performance, price);

        return Ticket.builder()
            .ticketId(ticketId)
            .ticketStatus(status)
            .stand(stand)
            .performance(performance)
            .price(price)
            .build();
    }

}
