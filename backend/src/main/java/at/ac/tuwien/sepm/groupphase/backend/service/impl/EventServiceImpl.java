package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.EventDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.EventMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.Artist;
import at.ac.tuwien.sepm.groupphase.backend.entity.Event;
import at.ac.tuwien.sepm.groupphase.backend.entity.Performance;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.repository.EventRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.EventService;
import org.hibernate.Hibernate;
import at.ac.tuwien.sepm.groupphase.backend.type.EventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.invoke.MethodHandles;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
@Service
public class EventServiceImpl implements EventService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;

    public EventServiceImpl(EventRepository eventRepository, EventMapper eventMapper) {
        this.eventRepository = eventRepository;
        this.eventMapper = eventMapper;
    }

    @Transactional
    @Override
    public Event create(Event event) {
        LOGGER.trace("create({})", event);

        return eventRepository.save(event);
    }

    @Override
    public Event getById(Long id) {
        LOGGER.trace("getById({})", id);

        Optional<Event> event = eventRepository.findById(id);
        LOGGER.info(String.valueOf(event));
        if (event.isPresent()) {
            return event.get();
        } else {
            throw new NotFoundException("Event with id " + id + " not found");
        }
    }

    @Override
    public List<Event> getAll() {
        LOGGER.trace("getAll()");

        return eventRepository.findAll();
    }

    @Override
    public List<EventDto> getAllEventsPaged(int page, int size) {
        LOGGER.trace("getAllEventsPaged({}, {})", page, size);

        Page<EventDto> returnedPage = this.eventRepository.findAll(PageRequest.of(page, size))
            .map(eventMapper::eventToEventDto);

        return returnedPage.getContent();
    }

    @Override
    public List<EventDto> getTop10Events(String eventType) {
        LOGGER.info("Get top 10 events of current month");
        LocalDateTime month = LocalDateTime.of(LocalDateTime.now().getYear(), LocalDateTime.now().getMonth(), 1, 0, 0);
        List<Object[]> top10Events = eventRepository.getTop10Events(month, eventType);
        List<Pair<Integer, Event>> result = new ArrayList<>();

        for (Object[] e : top10Events) {
            Long id = ((BigInteger) e[0]).longValue();
            int sumTickets = ((BigInteger) e[1]).intValue();

            Optional<Event> event = eventRepository.findById(id);
            if(event.isPresent()) {
                Event e1 = event.get();
                result.add(Pair.of(sumTickets, e1));
            }
        }

        List<EventDto> resultEvents = new ArrayList<>();
        for (Pair<Integer, Event> pair : result) {
            EventDto eventDto = eventMapper.eventToEventDto(pair.getSecond());
            eventDto.setSoldTickets(pair.getFirst());
            resultEvents.add(eventDto);
        }
        return resultEvents;
    }

    @Override
    public List<Event> search(String name, EventType eventType, Integer duration, Integer tolerance) {
        if (tolerance == null && duration != null) {
            return eventRepository.searchForEventsWithDuration(name, eventType, Math.max((duration - 30), 0), duration + 30);
        }
        if (tolerance != null && duration != null) {
            return eventRepository.searchForEventsWithDuration(name, eventType, Math.max((duration - tolerance), 0), duration + tolerance);
        }
        return eventRepository.searchForEvents(name, eventType);
    }
}
