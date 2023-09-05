package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CreateEventHallDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.EventHallOverviewDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.EventHall;
import at.ac.tuwien.sepm.groupphase.backend.entity.Layout;
import at.ac.tuwien.sepm.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;

import java.util.List;

public interface EventHallService {

    /**
     * Get all eventhalls stored in the persistent data store.
     *
     * @param hallname name of the hall to be retrieved, if null then all halls are returned
     * @return List of all persisted eventhalls.
     */
    List<EventHall> getAll(String hallname);

    /**
     * Get all Eventhalls paginated.
     *
     * @param page the number of the page
     * @param size the size of the page
     * @return Page containing the requested eventhalls.
     */
    List<EventHallOverviewDto> getAllPaged(int page, int size);

    /**
     * Find eventhall specified by the given id.
     *
     * @param id id of the eventhall to be retrieved.
     * @return Retrieved Eventhall
     * @throws NotFoundException is thrown if there exists no eventhall with the given id in the database.
     */
    EventHall findById(long id) throws NotFoundException;

    /**
     * Adds the given layout to the set of all layouts assoicated with the specified eventhall
     *
     * @param id        id of the eventhall to which a layout should be added
     * @param eventHall layout that should be added
     * @return eventhall to which the layout has been added
     * @throws NotFoundException is thrown if no eventhall with the given id exists in the database.
     * @throws ConflictException is thrown if an eventhall or layout with the same name already exists
     */
    EventHall addLayoutToEventHall(long id, CreateEventHallDto eventHall) throws NotFoundException, ConflictException;

    /**
     * Persist newly created layout and event hall in backend
     *
     * @param eventHall eventhall with location and layout
     * @return newly created eventhall
     * @throws NotFoundException is thrown if the location of the eventhall does not exist.
     * @throws ConflictException is thrown if an eventhall or layout with the same name already exists
     */
    EventHall createEventHall(CreateEventHallDto eventHall) throws NotFoundException, ConflictException;

    /**
     * Returns a list of all layouts associated with the eventhall specified by the given id
     *
     * @param id id of the eventhall whose layouts should be retrieved
     * @return List of all layouts assoicated with the eventhall
     * @throws NotFoundException is thrown if no eventhall with the given id exists
     */
    List<Layout> getAllLayoutsOfEventHall(long id) throws NotFoundException;

    /**
     * Gets all eventhalls for a specific location.
     *
     * @param locationId of the location
     * @return list of event hall for the location
     */
    List<EventHall> getByLocationId(Long locationId);
}
