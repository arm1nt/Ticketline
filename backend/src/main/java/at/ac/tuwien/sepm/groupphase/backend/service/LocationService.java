package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.entity.Location;
import at.ac.tuwien.sepm.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;

import java.util.List;

public interface LocationService {

    /**
     * Retrieve a list of all locations in the persistent data store
     *
     * @return list of all locations
     */
    List<Location> getAllLocations();

    /**
     * Get location specified by id.
     *
     * @param id id of the location to be retrieved
     * @return location that has the given id
     * @throws NotFoundException s thrown if no location with given id exists in the database
     */
    Location getLocationById(long id) throws NotFoundException;

    /**
     * Store new location in persistent data store
     *
     * @param location new location that should be persistet
     * @return returns entity that has been stored in the database
     * @throws ConflictException if this Location already exists in the database
     */
    Location createLocation(Location location) throws ConflictException;

    /**
     * Gets all locations which fullfill all the search parameters.
     *
     * @param name    of the location
     * @param street  of the location
     * @param city    of the location
     * @param country of the location
     * @param zipCode of the location
     * @return list of locations that fit the ssearch terms
     */
    List<Location> search(String name, String street, String city, String country, String zipCode);
}
