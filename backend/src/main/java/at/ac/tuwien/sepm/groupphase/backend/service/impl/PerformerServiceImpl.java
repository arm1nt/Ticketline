package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.PerformerSearchDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.PerformerSearchResultDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.SearchResultMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.Event;
import at.ac.tuwien.sepm.groupphase.backend.entity.Performer;
import at.ac.tuwien.sepm.groupphase.backend.repository.ArtistRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.EventRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.PerformerRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.PerformerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.invoke.MethodHandles;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

@Transactional(readOnly = true)
@Service
public class PerformerServiceImpl implements PerformerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final PerformerRepository performerRepository;
    private final ArtistRepository artistRepository;
    private final EventRepository eventRepository;

    private final SearchResultMapper searchResultMapper;


    public PerformerServiceImpl(PerformerRepository performerRepository, ArtistRepository artistRepository, EventRepository eventRepository, SearchResultMapper searchResultMapper) {
        this.performerRepository = performerRepository;
        this.artistRepository = artistRepository;
        this.eventRepository = eventRepository;
        this.searchResultMapper = searchResultMapper;
    }


    @Override
    public List<Performer> getAll() {
        LOGGER.trace("getAll()");

        return performerRepository.findAll();
    }


    @Override
    public List<PerformerSearchResultDto> search(PerformerSearchDto performerSearchDto) {
        LOGGER.trace("search({})", performerSearchDto);

        Collection<Performer> allPerformersThatFit = new HashSet<>();

        if (performerSearchDto.getFirstname() != null || performerSearchDto.getLastname() != null) {
            allPerformersThatFit.addAll(
                artistRepository.findByPerformerNameContainsIgnoreCaseAndFirstNameContainsIgnoreCaseAndLastNameContainsIgnoreCaseOrderByPerformerNameAsc(performerSearchDto.getArtistname(), performerSearchDto.getFirstname(),
                    performerSearchDto.getLastname()));
        } else {
            allPerformersThatFit.addAll(performerRepository.findDistinctByPerformerNameContainsIgnoreCaseOrderByPerformerNameAsc(performerSearchDto.getArtistname()));
        }

        Map<Long, List<Event>> eventsMap = new HashMap<>();
        for (Performer p : allPerformersThatFit) {
            var events = eventRepository.findDistinctByPerformers_Id(p.getId());
            eventsMap.put(p.getId(), events);
        }
        return searchResultMapper.performerListAndEventMapToPerformerSearchResultList(allPerformersThatFit, eventsMap);
    }
}
