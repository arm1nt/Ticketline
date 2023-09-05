package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.LocationDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.LocationMapper;
import at.ac.tuwien.sepm.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.service.LocationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.PermitAll;
import javax.validation.Valid;
import java.lang.invoke.MethodHandles;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/locations")
public class LocationEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final LocationService locationService;
    private final LocationMapper locationMapper;

    public LocationEndpoint(LocationService locationService, LocationMapper locationMapper) {
        this.locationService = locationService;
        this.locationMapper = locationMapper;
    }


    @Secured("ROLE_ADMIN")
    @GetMapping
    @Operation(summary = "Get all locations stored in the database", security = @SecurityRequirement(name = "apiKey"))
    public List<LocationDto> getAllLocations() {
        LOGGER.info("GET /api/v1/locations");

        return this.locationMapper.locationListToLocationDtoList(this.locationService.getAllLocations());
    }

    @Secured("ROLE_ADMIN")
    @GetMapping(value = "/{id}")
    @Operation(summary = "Get location specified by id", security = @SecurityRequirement(name = "apiKey"))
    public LocationDto getLocationById(@PathVariable long id) throws NotFoundException {
        LOGGER.info("GET /api/v1/locations/{}", id);

        return this.locationMapper.locationToLocationDto(this.locationService.getLocationById(id));
    }

    @Secured("ROLE_ADMIN")
    @PostMapping
    @Operation(summary = "Create new location and persist in database", security = @SecurityRequirement(name = "apiKey"))
    public LocationDto createLocation(@Valid @RequestBody LocationDto locationDto) throws ConflictException {
        LOGGER.info("POST /api/v1/locations");

        return this.locationMapper.locationToLocationDto(this.locationService.createLocation(this.locationMapper.locationDtoToLocation(locationDto)));
    }

    @PermitAll
    @GetMapping("/search")
    @Operation(summary = "Get all or search some performances")
    public List<LocationDto> searchLocations(@RequestParam(name = "name", required = false) String name,
                                             @RequestParam(name = "street", required = false) String street,
                                             @RequestParam(name = "city", required = false) String city,
                                             @RequestParam(name = "country", required = false) String country,
                                             @RequestParam(name = "zipCode", required = false) String zipCode) {
        LOGGER.info("GET /api/v1/locations/search name={}, street={}, city={}, country={}, zipCode={}", name, street, city, country, zipCode);
        return this.locationMapper.locationListToLocationDtoList(this.locationService.search(name, street, city, country, zipCode));
    }
}
