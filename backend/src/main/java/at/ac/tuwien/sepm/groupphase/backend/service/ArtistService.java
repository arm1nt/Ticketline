package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ArtistDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Artist;

import java.util.List;

public interface ArtistService {

    /**
     * Creates an artist.
     *
     * @param artist to be created
     * @return created artist
     */
    Artist create(Artist artist);

    /**
     * Get all artists.
     *
     * @return List of artists
     */
    List<Artist> getAll();
}
