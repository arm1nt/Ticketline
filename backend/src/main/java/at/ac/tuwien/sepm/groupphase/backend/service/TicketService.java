package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.Order;
import at.ac.tuwien.sepm.groupphase.backend.entity.Ticket;
import at.ac.tuwien.sepm.groupphase.backend.exception.ForbiddenException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;

import java.io.IOException;
import java.util.Set;

public interface TicketService {

    /**
     * Get the order with given ID.
     *
     * @param id the ID of the performance to get tickets for
     * @return the order with ID {@code id}
     * @throws NotFoundException if the order with the given ID does not exist in the persistent data store
     */
    Set<Ticket> getAllByPerformanceId(Long id) throws NotFoundException;

    /**
     * Returns a pdf version to print out of the specified ticket.
     *
     * @param id id of the requested ticket
     * @param username username of the person requesting the ticket
     * @return the pdf of the ticket as base64 encoded string.
     * @throws IOException is thrown if there is an error creating the pdf
     * @throws ForbiddenException is thrown if the user who requests the ticket does not own it
     * @throws NotFoundException is thrown if the requested ticket does not exist
     */
    String getTicketPdf(long id, String username) throws IOException, ForbiddenException, NotFoundException;


    /**
     * Create a pdf for the ticket with the given id.
     *
     * @param id id of the ticket for which we want to create a ticket
     * @return the pdf encoded as base64 string
     * @throws IOException is thrown if there is an error creating the pdf.
     */
    String createPdfOfTicket(long id) throws IOException;

}
