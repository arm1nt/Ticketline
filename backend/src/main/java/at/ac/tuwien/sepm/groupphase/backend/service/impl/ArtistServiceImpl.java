package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.entity.Artist;
import at.ac.tuwien.sepm.groupphase.backend.repository.ArtistRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.PerformerRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.ArtistService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ValidationException;
import java.lang.invoke.MethodHandles;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class ArtistServiceImpl implements ArtistService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final ArtistRepository artistRepository;

    private final PerformerRepository performerRepository;

    public ArtistServiceImpl(ArtistRepository artistRepository, PerformerRepository performerRepository) {
        this.artistRepository = artistRepository;
        this.performerRepository = performerRepository;
    }

    @Transactional
    public Artist create(Artist artist) {
        LOGGER.trace("create({})", artist);

        if(performerRepository.getByPerformerName(artist.getPerformerName()).size()>0){
            throw new ValidationException("Performer with name " + artist.getPerformerName() + " already exists");
        } else {
            return artistRepository.save(artist);
        }
    }


    @Override
    public List<Artist> getAll() {
        LOGGER.trace("getAll()");

        return artistRepository.findAll();
    }
}
