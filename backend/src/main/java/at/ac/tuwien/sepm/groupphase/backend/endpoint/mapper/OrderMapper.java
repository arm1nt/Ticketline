package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.InvoicePdfDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.OrderDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.OrderOverviewDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.TicketDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.Event;
import at.ac.tuwien.sepm.groupphase.backend.entity.Order;
import at.ac.tuwien.sepm.groupphase.backend.entity.Payment;
import at.ac.tuwien.sepm.groupphase.backend.entity.Performance;
import at.ac.tuwien.sepm.groupphase.backend.entity.Reservation;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepm.groupphase.backend.type.TicketStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class OrderMapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final TicketMapper ticketMapper;

    private final UserRepository userRepository;
    private final PerformerMapper performerMapper;

    @Autowired
    public OrderMapper(TicketMapper ticketMapper, UserRepository userRepository,
                       PerformerMapper performerMapper) {
        this.ticketMapper = ticketMapper;
        this.userRepository = userRepository;
        this.performerMapper = performerMapper;
    }

    public InvoicePdfDto stringToInvoicePdfDto(String pdf) {
        LOGGER.debug("stringToInvoicePdfDto()");

        if (pdf == null) {
            return null;
        }

        InvoicePdfDto invoicePdfDto = new InvoicePdfDto();

        invoicePdfDto.setPdf(pdf);

        return invoicePdfDto;
    }

    public OrderDto orderToOrderDto(Order order) {
        LOGGER.debug("orderToOrderDto(order.id = {}, {}) ", order.getId(), order);

        if (order.getClass() == Payment.class) {
            return this.paymentToDto((Payment) order);
        }
        return this.reservationToDto((Reservation) order);
    }

    public List<OrderDto> orderToOrderDto(List<Order> orders) {
        LOGGER.debug("orderToOrderDto()");

        List<OrderDto> orderDtos = new ArrayList<>();
        for (Order o : orders) {
            orderDtos.add(orderToOrderDto(o));
        }
        return orderDtos;
    }

    public Order orderDtoToOrder(OrderDto orderDto, ApplicationUser applicationUser) {
        LOGGER.debug("orderToOrderDto()");

        //Its validated that the ticket list in the orderDto always contains at least 1 element

        if (orderDto.getReservationCode() != null && !orderDto.getReservationCode().equals("")) {
            Reservation reservation = Reservation.builder()
                .id(orderDto.getId())
                .reservationCode(orderDto.getReservationCode())
                .dueTime(orderDto.getDueTime())
                .performanceId(orderDto.getPerformanceId())
                .applicationUser(applicationUser)
                .tickets(ticketMapper.ticketDtoToTicket(orderDto.getTickets()))
                .build();
            return reservation;
        } else if (orderDto.getTotal() != null && orderDto.getTotal() != 0) {
            return Payment.builder()
                .id(orderDto.getId())
                .time(orderDto.getTime())
                .total(orderDto.getTotal())
                .performanceId(orderDto.getPerformanceId())
                .applicationUser(applicationUser)
                .tickets(ticketMapper.ticketDtoToTicket(orderDto.getTickets()))
                .build();
        } else { //NEW BOOKING
            List<TicketDto> ticketDtos;
            if (orderDto.getTickets() == null) {
                ticketDtos = new ArrayList<>();
            } else {
                ticketDtos = orderDto.getTickets().stream().toList();
            }
            if (ticketDtos.get(0).getTicketStatus() == TicketStatus.RESERVED) {
                return Reservation.builder()
                    .id(orderDto.getId())
                    .reservationCode(orderDto.getReservationCode())
                    .dueTime(orderDto.getDueTime())
                    .performanceId(orderDto.getPerformanceId())
                    .applicationUser(applicationUser)
                    .tickets(ticketMapper.ticketDtoToTicket(orderDto.getTickets()))
                    .build();
            } else {
                return Payment.builder()
                    .id(orderDto.getId())
                    .time(orderDto.getTime())
                    .performanceId(orderDto.getPerformanceId())
                    .total(orderDto.getTotal())
                    .applicationUser(applicationUser)
                    .tickets(ticketMapper.ticketDtoToTicket(orderDto.getTickets()))
                    .build();
            }
        }
    }

    public List<Order> orderDtoToOrder(List<OrderDto> orderDtos, ApplicationUser applicationUser) {
        LOGGER.debug("orderToOrderDto()");

        List<Order> orders = new ArrayList<>();
        for (OrderDto o : orderDtos
        ) {
            orders.add(orderDtoToOrder(o, applicationUser));
        }
        return orders;
    }


    private OrderDto reservationToDto(Reservation reservation) {
        LOGGER.trace("reservationToDto({})", reservation);

        return new OrderDto(
            reservation.getId(),
            ticketMapper.ticketToTicketDto(reservation.getTickets()),
            reservation.getReservationCode(),
            reservation.getDueTime(),
            null,
            null,
            reservation.getPerformanceId(),
            reservation.getCreationDateTime());
    }

    private OrderDto paymentToDto(Payment payment) {
        LOGGER.trace("paymentToDto({})", payment);

        return new OrderDto(
            payment.getId(),
            ticketMapper.ticketToTicketDto(payment.getTickets()),
            null,
            null,
            payment.getTime(),
            payment.getTotal(),
            payment.getPerformanceId(),
            payment.getCreationDateTime());
    }


    public List<OrderOverviewDto> OrdersEventsPerformancesToOrderOverviewDto(List<Order> orders, Map<Long, Performance> performances) {
        LOGGER.trace("OrdersEventsPerformancesToOrderOverviewDto({},{})", orders, performances);
        List<OrderOverviewDto> orderOverviewDtos = new ArrayList<>();

        for (Order o :
            orders) {
            OrderDto odto = orderToOrderDto(o);
            Performance p = performances.get(odto.getPerformanceId());
            Event e = p.getEvent();
            OrderOverviewDto orderOverviewDto = new OrderOverviewDto(odto.getId(),
                odto.getTickets(),
                odto.getReservationCode(),
                odto.getDueTime(),
                odto.getTime(),
                odto.getTotal(),
                p.getId(),
                p.getPerformanceName(),
                p.getStartTime(),
                e.getId(),
                e.getName(),
                performerMapper.performerListToPerformerDtoList(e.getPerformers()),
                odto.getCreationDateTime(),
                p.getLayout().getEventHall().getLocation().getName(),
                p.getLayout().getEventHall().getName());
            orderOverviewDtos.add(orderOverviewDto);
        }
        return orderOverviewDtos;
    }
}
