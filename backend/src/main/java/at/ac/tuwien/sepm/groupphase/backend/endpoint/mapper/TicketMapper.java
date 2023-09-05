package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.TicketDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.TicketPdfDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.TicketSpotDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Ticket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.LinkedHashSet;
import java.util.Set;


@Component
public class TicketMapper {

    private final SpotTicketMapper spotTicketMapper;

    @Autowired
    public TicketMapper(SpotTicketMapper spotTicketMapper) {
        this.spotTicketMapper = spotTicketMapper;
    }

    public TicketDto ticketToTicketDto(Ticket ticket) {
        if (ticket == null) {
            return null;
        }

        TicketDto ticketDto = new TicketDto();

        ticketDto.setId(ticket.getId());
        ticketDto.setTicketId(ticket.getTicketId());
        ticketDto.setTicketStatus(ticket.getTicketStatus());
        ticketDto.setPrice(ticket.getPrice());
        ticketDto.setStandTicketDto(spotTicketMapper.standToStandTicketDto(ticket.getStand()));
        ticketDto.setSeatTicketDto(spotTicketMapper.seatToSeatTicketDto(ticket.getSeat()));

        return ticketDto;
    }

    public Set<TicketDto> ticketToTicketDto(Set<Ticket> ticket) {
        if (ticket == null) {
            return null;
        }

        Set<TicketDto> set = new LinkedHashSet<TicketDto>(Math.max((int) (ticket.size() / .75f) + 1, 16));
        for (Ticket ticket1 : ticket) {
            set.add(ticketToTicketDto(ticket1));
        }

        return set;
    }

    public Ticket ticketDtoToTicket(TicketDto ticketDto) {
        if (ticketDto == null) {
            return null;
        }

        Ticket.TicketBuilder ticket = Ticket.builder();

        ticket.id(ticketDto.getId());
        ticket.ticketId(ticketDto.getTicketId());
        ticket.ticketStatus(ticketDto.getTicketStatus());

        return ticket.build();
    }

    public Set<Ticket> ticketDtoToTicket(Set<TicketDto> ticketDto) {
        if (ticketDto == null) {
            return null;
        }

        Set<Ticket> set = new LinkedHashSet<Ticket>(Math.max((int) (ticketDto.size() / .75f) + 1, 16));
        for (TicketDto ticketDto1 : ticketDto) {
            set.add(ticketDtoToTicket(ticketDto1));
        }

        return set;
    }

    public TicketSpotDto ticketToTicketSpotDto(Ticket ticket) {
        if (ticket == null) {
            return null;
        }

        TicketSpotDto ticketSpotDto = new TicketSpotDto();

        ticketSpotDto.setId(ticket.getId());
        ticketSpotDto.setTicketId(ticket.getTicketId());
        ticketSpotDto.setTicketStatus(ticket.getTicketStatus());

        return ticketSpotDto;
    }

    public TicketPdfDto stringToTicketPdfDto(String pdf) {
        if(pdf == null) {
            return null;
        }

        return TicketPdfDto.builder()
            .pdf(pdf)
            .build();
    }
}
