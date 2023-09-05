package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.entity.Artist;
import at.ac.tuwien.sepm.groupphase.backend.entity.Band;

import java.util.List;

public interface BandService {

    /**
     * Creates a band.
     *
     * @param band to be created
     * @return created band
     */
    Band create(Band band);

    /**
     * Get all bands.
     *
     * @return List of bands
     */
    List<Band> getAll();
}
