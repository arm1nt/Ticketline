package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ArtistDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.BandDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.ArtistMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.BandMapper;
import at.ac.tuwien.sepm.groupphase.backend.service.ArtistService;
import at.ac.tuwien.sepm.groupphase.backend.service.BandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.lang.invoke.MethodHandles;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/band")
public class BandEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final BandService bandService;
    private final BandMapper bandMapper;

    @Autowired
    public BandEndpoint(BandService bandService, BandMapper bandMapper) {
        this.bandService = bandService;
        this.bandMapper = bandMapper;
    }


    @Secured("ROLE_ADMIN")
    @GetMapping
    @Operation(summary = "Get list of bands", security = @SecurityRequirement(name = "apiKey"))
    public List<BandDto> getAll() {
        LOGGER.info("GET /api/v1/band");

        return bandMapper.bandListToBandDtoList(bandService.getAll());
    }

    @Secured("ROLE_ADMIN")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    @Operation(summary = "Add a new band", security = @SecurityRequirement(name = "apiKey"))
    public BandDto create(@Valid @RequestBody BandDto bandDto) {
        LOGGER.info("Posting{}", bandDto);

        return this.bandMapper.bandToBandDto(this.bandService.create(this.bandMapper.bandDtoToBand(bandDto)));
    }

}
