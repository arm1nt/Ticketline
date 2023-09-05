package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.PerformerDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.PerformerSearchDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.PerformerSearchResultDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.PerformerMapper;
import at.ac.tuwien.sepm.groupphase.backend.service.PerformerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.PermitAll;
import java.lang.invoke.MethodHandles;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/performer")
public class PerformerEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final PerformerService performerService;
    private final PerformerMapper performerMapper;

    @Autowired
    public PerformerEndpoint(PerformerService performerService, PerformerMapper performerMapper) {
        this.performerService = performerService;
        this.performerMapper = performerMapper;
    }

    @Secured("ROLE_ADMIN")
    @GetMapping
    @Operation(summary = "Get list of performers", security = @SecurityRequirement(name = "apiKey"))
    public List<PerformerDto> getAll() {
        LOGGER.info("GET /api/v1/performer");
        return performerMapper.performerListToPerformerDtoList(performerService.getAll());
    }

    @PermitAll
    @GetMapping("/search")
    @Operation(summary = "Get all or search some performances")
    public List<PerformerSearchResultDto> getPerformances(@RequestParam(name = "firstname", required = false) String firstname,
                                                          @RequestParam(name = "lastname", required = false) String lastname,
                                                          @RequestParam(name = "artistname", required = false) String artistname) {
        LOGGER.info("GET /api/v1/performer/search firstname={}, lastname={}, artistname={}", firstname, lastname, artistname);
        PerformerSearchDto performerSearchDto = new PerformerSearchDto(firstname, lastname, artistname);
        return this.performerService.search(performerSearchDto);
    }

}
