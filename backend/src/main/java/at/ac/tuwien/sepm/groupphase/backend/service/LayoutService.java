package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CreateLayoutDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.LayoutDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.EventHall;
import at.ac.tuwien.sepm.groupphase.backend.entity.Layout;
import at.ac.tuwien.sepm.groupphase.backend.exception.ForbiddenException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;

import java.util.List;

public interface LayoutService {

    /**
     * Add a layout to an already existing eventhall
     *
     * @param eventHall already existing eventhall
     * @param layoutDto layout to be added
     * @return returns the newly created layout
     */
    Layout addLayoutToEventHall(EventHall eventHall, CreateLayoutDto layoutDto);

    /**
     * Get the layout with given ID.
     *
     * @param id the ID of the layout to get
     * @return the layout with ID {@code id}
     * @throws NotFoundException  if the order with the given ID does not exist in the persistent data store
     * @throws ForbiddenException if the user doesn't have permission to see that order
     */
    Layout getById(Long id) throws NotFoundException, ForbiddenException;

    /**
     * Gets all layouts.
     *
     * @param layoutName null if all layouts should be retrieved, otherwise the name of a specified layout
     * @return List of all layouts
     */
    List<Layout> getAll(String layoutName);

    LayoutDto getLayoutDtoByPerformanceIdWithTicketSpotDtos(Long performanceId) throws NotFoundException;

    /**
     * Gets all layouts for a specified eventhall.
     *
     * @param evenHallId of the eventhall
     * @return list of layouts of the eventhall
     */
    List<Layout> getByEventHallId(Long evenHallId);
}
