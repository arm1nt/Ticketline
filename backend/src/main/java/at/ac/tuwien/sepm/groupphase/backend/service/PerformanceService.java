package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.PerformanceDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.PerformanceSearchDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.PerformanceSearchResultDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Performance;

import java.util.List;

public interface PerformanceService {

    /**
     * Creates a performance.
     *
     * @param performanceDto to be created
     * @return created performance
     */
    Performance create(PerformanceDto performanceDto);

    Performance getById(long ig);

    /**
     * Gets performances that fit the given parameters.
     *
     * @param locationId of a location
     * @return list of performances that fit the parameters
     */
    List<Performance> search(long locationId);

    /**
     * Returns the maximum price of any sector for any performance.
     *
     * @return The maximum price.
     */
    Double findMaxPrice();

    /**
     *
     * Search for performances by time, price, event and hall names.
     * For the specified time, performances which take places at that time +/- 30 minutes are returned.
     * For the specified price, all performances which have a sector with a price within 10% of the given price
     * are returned.
     *
     * @param searchDto Dto containing the search parameters.
     * @return Results of the search.
     */
    List<PerformanceSearchResultDto> searchForPerformances(PerformanceSearchDto searchDto);
}
