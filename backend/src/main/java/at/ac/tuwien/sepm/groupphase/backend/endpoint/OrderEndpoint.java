package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.InvoicePdfDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.OrderDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.OrderOverviewDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.OrderMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.exception.IllegalOperationException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.service.OrderService;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.security.NoSuchAlgorithmException;
import java.util.List;


@RestController
@RequestMapping(value = "/api/v1/orders")
public class OrderEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final OrderService orderService;

    private final UserService userService;

    private final OrderMapper orderMapper;

    private final SecurityProperties securityProperties;


    @Autowired
    public OrderEndpoint(OrderService orderService, OrderMapper orderMapper,
                         UserService userService, SecurityProperties securityProperties) {
        this.orderService = orderService;
        this.userService = userService;
        this.orderMapper = orderMapper;
        this.securityProperties = securityProperties;
    }

    @Secured("ROLE_USER")
    @GetMapping(value = "/invoice/{id}")
    @Operation(summary = "Get the pdf invoice of the order specified by the id", security = @SecurityRequirement(name = "apiKey"))
    public InvoicePdfDto getInvoice(@PathVariable long id, @RequestHeader(name = "Authorization") String token) {
        LOGGER.info("GET /api/v1/orders/invoice/{}", id);

        String username = this.retrieveUsername(token);

        return this.orderMapper.stringToInvoicePdfDto(this.orderService.getInvoice(username, id));
    }

    @Secured("ROLE_USER")
    @GetMapping("/{id}")
    @Operation(summary = "Get the order referenced by the id", security = @SecurityRequirement(name = "apiKey"))
    public OrderDto getOrder(@RequestHeader(name = "Authorization") String token, @PathVariable long id)
        throws NotFoundException {
        LOGGER.info("GET /api/v1/orders/{}", id);

        String username = this.retrieveUsername(token);
        ApplicationUser applicationUser = this.userService.findApplicationUserByUsername(username);

        return this.orderMapper.orderToOrderDto(this.orderService.getById(id, applicationUser));
    }

    @Secured("ROLE_USER")
    @GetMapping
    @Operation(summary = "Get all orders belonging to the user requesting it", security = @SecurityRequirement(name = "apiKey"))
    public List<OrderOverviewDto> getOrders(@RequestHeader(name = "Authorization") String token) {
        LOGGER.info("GET /api/v1/orders");

        String username = this.retrieveUsername(token);
        return this.orderService.getAllOrdersForUser(username);

    }

    @Secured("ROLE_USER")
    @PostMapping
    @Operation(summary = "Create an payment or a reservation for an performance", security = @SecurityRequirement(name = "apiKey"))
    public OrderDto createBooking(@RequestHeader(name = "Authorization") String token, @Valid @RequestBody OrderDto orderDto)
        throws NoSuchAlgorithmException, IOException, IllegalOperationException {
        LOGGER.info("POST /api/v1/orders");

        String username = this.retrieveUsername(token);
        ApplicationUser applicationUser = this.userService.findApplicationUserByUsername(username);

        return this.orderMapper.orderToOrderDto(this.orderService.save(this.orderMapper.orderDtoToOrder(orderDto, applicationUser)));
    }

    @Secured("ROLE_USER")
    @PutMapping("/{id}")
    @Operation(summary = "Update an order by either cancelling it or upgrading reservation to payment", security = @SecurityRequirement(name = "apiKey"))
    public OrderDto updateOrder(@RequestHeader(name = "Authorization") String token,
                                @PathVariable long id,
                                @Valid @RequestBody OrderDto orderDto)
        throws IOException, IllegalOperationException, ObjectOptimisticLockingFailureException {
        LOGGER.info("PUT /api/v1/orders/{}", id);
        LOGGER.debug("Body: {}", orderDto);

        String username = this.retrieveUsername(token);
        ApplicationUser applicationUser = this.userService.findApplicationUserByUsername(username);

        return this.orderMapper.orderToOrderDto(this.orderService.update(this.orderMapper.orderDtoToOrder(orderDto, applicationUser), false));
    }

    @Secured("ROLE_USER")
    @GetMapping(value = "/cancellation/{id}")
    @Operation(summary = "Get the pdf cancellation of the order specified by the id", security = @SecurityRequirement(name = "apiKey"))
    public InvoicePdfDto getCancellation(@PathVariable long id, @RequestHeader(name = "Authorization") String token) {
        LOGGER.info("GET /api/v1/orders/cancellation/{}", id);

        String username = this.retrieveUsername(token);
        return this.orderMapper.stringToInvoicePdfDto(this.orderService.getCancellation(username, id));
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
