package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.PerformerSearchDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.PerformerSearchResultDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Performer;

import java.util.List;

public interface PerformerService {

    /**
     * Get all artists.
     *
     * @return List of performers
     */
    List<Performer> getAll();


    /**
     * Gets all artists that fit the search criteria and their events/performances.
     *
     * @param performerSearchDto search criteria
     * @return list of artist an their events
     */
    List<PerformerSearchResultDto> search(PerformerSearchDto performerSearchDto);
}
