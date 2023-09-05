package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.entity.Location;
import at.ac.tuwien.sepm.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.repository.LocationRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.LocationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.invoke.MethodHandles;
import java.util.List;

@Transactional(readOnly = true)
@Service
public class LocationServiceImpl implements LocationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final LocationRepository locationRepository;

    public LocationServiceImpl(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    @Transactional
    @Override
    public Location createLocation(Location location) throws ConflictException {
        LOGGER.trace("createLocation({})", location);

        List<Location> locationWithTheSameName = this.locationRepository.findByName(location.getName());

        for (Location potentialDuplicate : locationWithTheSameName) {
            if (potentialDuplicate.equals(location)) {
                throw new ConflictException("This location already exists");
            }
        }

        return this.locationRepository.save(location);
    }


    @Override
    public List<Location> search(String name, String street, String city, String country, String zipCode) {
        LOGGER.trace("search({},{},{},{},{})", name, street, city, country, zipCode);

        return this.locationRepository.findByNameContainsIgnoreCaseAndStreetContainsIgnoreCaseAndCityContainsIgnoreCaseAndCountryContainsIgnoreCaseAndZipCodeContainsIgnoreCaseOrderByNameAsc(name, street, city, country, zipCode);
    }


    @Override
    public List<Location> getAllLocations() {
        LOGGER.trace("getAllLocations()");

        return this.locationRepository.findAll();
    }


    @Override
    public Location getLocationById(long id) throws NotFoundException {
        LOGGER.trace("getLocationById({})", id);

        Location retrievedLocation = this.locationRepository.findById(id);

        if (retrievedLocation == null) {
            throw new NotFoundException(String.format("No Location with given id %s exists", id));
        }

        return retrievedLocation;
    }
}
