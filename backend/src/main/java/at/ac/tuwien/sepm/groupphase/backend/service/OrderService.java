package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.OrderOverviewDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.Order;
import at.ac.tuwien.sepm.groupphase.backend.entity.Ticket;
import at.ac.tuwien.sepm.groupphase.backend.exception.ForbiddenException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface OrderService {

    /**
     * Retrieve an pdf that belongs to the order specified by the id.
     *
     * @param usename Username of the person requesting the invoice.
     * @param id      id of the order to which the invoice that has to be retrieved belongs
     * @return the pdf encodes as base64 string
     */
    String getInvoice(String usename, long id);

    /**
     * Create a pdf of the invoice of an order. The pdf is then persisted in the persistent datastore.
     *
     * @param username   Username of the person performing the order
     * @param orderId    Id of the order to which the invoice belongs
     * @param time       Date of booking
     * @param ticketList List of all tickets affiliated with this order
     * @throws IOException is thrown if an error occurs while creating the pdf document.
     */
    void createInvoice(String username, long orderId, LocalDateTime time, Set<Ticket> ticketList) throws IOException;

    /**
     * Get the order with given ID.
     *
     * @param id              the ID of the order to get
     * @param applicationUser the user that wants to get the message
     * @return the order with ID {@code id}
     * @throws NotFoundException  if the order with the given ID does not exist in the persistent data store
     * @throws ForbiddenException if the user doesn't have permission to see that order
     */
    Order getById(Long id, ApplicationUser applicationUser) throws NotFoundException, ForbiddenException;

    /**
     * Save the given order and return the newly set id.
     *
     * @param order a order object to post, with no id yet
     * @return the saved order
     */
    Order save(Order order) throws NoSuchAlgorithmException, IOException;

    /**
     * Updates the order with the ID given in {@code orderDto}
     * with the data given in {@code orderDto} in the persistent data store.
     *
     * @param order                the order to update
     * @param isRunByScheduledTask indicates is update is run by scheduled task
     * @return id of the updated order
     * @throws NotFoundException                       if the order with the given ID does not exist in the persistent data store
     * @throws ForbiddenException                      if the user doesn't have permission to edit that order
     * @throws ObjectOptimisticLockingFailureException is thrown if the scheduled task and a user try to edit simultaneously
     */
    Order update(Order order, boolean isRunByScheduledTask) throws NotFoundException, ForbiddenException, IOException, ObjectOptimisticLockingFailureException;

    /**
     * Retrieve the pdf cancellation specified by the given id.
     *
     * @param username Username of the person that is requesting the cancellation.
     * @param id       id of the cancellation that should be retrieved.
     * @return the pdf encoded as base64 string
     */
    String getCancellation(String username, long id);

    /**
     * Create a pdf of the cancellation of tickets. The pdf is then persisted in the persistent datastore.
     *
     * @param username   Username of the person performing the cancellation
     * @param orderId    Id of the order to which the invoice belongs
     * @param ticketList List of all tickets of this order being cancelled.
     * @throws IOException is thrown if an error occurs while creating the pdf document.
     */
    void createCancellationInvoice(String username, LocalDateTime time, long orderId, Set<Ticket> ticketList) throws IOException;

    /**
     * Gets all orders by a user
     *
     * @param username of the user
     * @return list of orders
     */
    List<OrderOverviewDto> getAllOrdersForUser(String username);
}
