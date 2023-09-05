package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.LayoutOverviewDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.LayoutMapper;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.service.LayoutService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.lang.invoke.MethodHandles;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/layouts")
public class LayoutEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final LayoutService layoutService;
    private final LayoutMapper layoutMapper;

    public LayoutEndpoint(LayoutService layoutService, LayoutMapper layoutMapper) {
        this.layoutService = layoutService;
        this.layoutMapper = layoutMapper;
    }

    @Secured("ROLE_ADMIN")
    @GetMapping
    @Operation(summary = "Get all layouts or specified layout by name", security = @SecurityRequirement(name = "apiKey"))
    public List<LayoutOverviewDto> getAll(@RequestParam(name = "name", required = false) String layoutname, @RequestParam(name = "evenHallId", required = false) Long evenHallId) {
        LOGGER.info("GET /api/v1/layouts");
        if (evenHallId != null) {
            return this.layoutMapper.layoutListToLayoutOverviewDtoList(this.layoutService.getByEventHallId(evenHallId));
        }

        return this.layoutMapper.layoutListToLayoutOverviewDtoList(this.layoutService.getAll(layoutname));
    }

    @Secured("ROLE_ADMIN")
    @GetMapping(value = "/{id}")
    @Operation(summary = "Get specified layout by id", security = @SecurityRequirement(name = "apiKey"))
    public LayoutOverviewDto getLayoutById(@PathVariable long id) throws NotFoundException {
        LOGGER.info("GET /api/v1/layouts/{}", id);
        return this.layoutMapper.layoutToLayoutOverviewDto(this.layoutService.getById(id));
    }
}
