package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.EventDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Event;
import at.ac.tuwien.sepm.groupphase.backend.type.EventType;
import org.springframework.data.util.Pair;

import java.util.List;

public interface EventService {

    /**
     * Creates an event.
     *
     * @param event to be created
     * @return created event
     */
    Event create(Event event);

    /**
     * Get event with corresponding id.
     *
     * @param id of the event
     * @return event with corresponding id
     */
    Event getById(Long id);

    /**
     * Get all events.
     *
     * @return list of all events
     */
    List<Event> getAll();

    /**
     * Get all events paged
     *
     * @param page current page
     * @param size size of page
     * @return List of events in this page
     */
    List<EventDto> getAllEventsPaged(int page, int size);

    /**
     * Gets all events that fit the search criteria.
     *
     * @param name      of the event
     * @param eventType of the event
     * @param duration  of the event
     * @param tolerance of the duration in search
     * @return list of events that fit
     */
    List<Event> search(String name, EventType eventType, Integer duration, Integer tolerance);

    List<EventDto> getTop10Events(String eventType);
}
