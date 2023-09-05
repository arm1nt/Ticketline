package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.entity.Band;
import at.ac.tuwien.sepm.groupphase.backend.repository.BandRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.PerformerRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.BandService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ValidationException;
import java.lang.invoke.MethodHandles;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class BandServiceImpl implements BandService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final BandRepository bandRepository;
    private final PerformerRepository performerRepository;

    public BandServiceImpl(BandRepository bandRepository, PerformerRepository performerRepository) {
        this.bandRepository = bandRepository;
        this.performerRepository = performerRepository;
    }


    @Transactional
    @Override
    public Band create(Band band) {
        LOGGER.trace("create({})", band);

        if(performerRepository.getByPerformerName(band.getPerformerName()).size()>0){
            throw new ValidationException("Performer with name " + band.getPerformerName() + " already exists");
        } else {
            return bandRepository.save(band);
        }
    }


    @Override
    public List<Band> getAll() {
        LOGGER.trace("getAll()");

        return bandRepository.findAll();
    }
}
