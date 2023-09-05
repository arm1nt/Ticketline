package at.ac.tuwien.sepm.groupphase.backend.endpoint;


import at.ac.tuwien.sepm.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.OrderDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.TicketDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.TicketPdfDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.OrderMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.TicketMapper;
import at.ac.tuwien.sepm.groupphase.backend.exception.ForbiddenException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.service.OrderService;
import at.ac.tuwien.sepm.groupphase.backend.service.TicketService;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.Set;

@RestController
@RequestMapping(value = "/api/v1/tickets")
public class TicketEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final SecurityProperties securityProperties;
    private final TicketMapper ticketMapper;
    private final TicketService ticketService;

    public TicketEndpoint(TicketService ticketService, TicketMapper ticketMapper,
                          SecurityProperties securityProperties) {
        this.securityProperties = securityProperties;
        this.ticketMapper = ticketMapper;
        this.ticketService = ticketService;
    }

    @Secured("ROLE_USER")
    @GetMapping("/{id}")
    @Operation(summary = "Get the tickets with given performence id", security = @SecurityRequirement(name = "apiKey"))
    public Set<TicketDto> getTicketsByPerformanceId(@RequestHeader(name = "Authorization") String token, @PathVariable long id)
        throws NotFoundException {
        LOGGER.info("GET /api/v1/tickets/{}", id);
        return this.ticketMapper.ticketToTicketDto(this.ticketService.getAllByPerformanceId(id));
    }

    @Secured("ROLE_USER")
    @GetMapping("/pdf/{id}")
    @Operation(summary = "Get the pdf print of an ticket given by the id", security = @SecurityRequirement(name = "apiKey"))
    public TicketPdfDto getPdfTicket(@RequestHeader(name = "Authorization") String token, @PathVariable long id)
        throws IOException, ForbiddenException, NotFoundException {
        LOGGER.info("GET /api/v1/tickets/pdf/{}", id);

        String username = this.retrieveUsername(token);
        return this.ticketMapper.stringToTicketPdfDto(this.ticketService.getTicketPdf(id, username));
    }

    private String retrieveUsername(String jwtToken) {
        byte[] signingKey = securityProperties.getJwtSecret().getBytes();
        Claims claims = Jwts.parserBuilder().setSigningKey(signingKey).build()
            .parseClaimsJws(jwtToken.replace(securityProperties.getAuthTokenPrefix(), ""))
            .getBody();
        String user = claims.getSubject();
        return user;
    }


}
