package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.Ticket;
import at.ac.tuwien.sepm.groupphase.backend.type.TicketStatus;
import org.hibernate.annotations.OptimisticLock;
import org.hibernate.annotations.OptimisticLocking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.LockModeType;
import java.util.List;
import java.util.Set;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    @Override
    <S extends Ticket> S save(S entity);

    Ticket getTicketById(Long id);

    Set<Ticket> getTicketsByPerformanceId(@Param("id") Long id);

    @Query("select t from Ticket t where t.id in (:ids)")
    Set<Ticket> getTicketsByIds(@Param("ids") Set<Long> ids);

    @Query("UPDATE Ticket t SET t.ticketStatus = :ticketStatus WHERE t.id=:id")
    @Modifying(clearAutomatically = true)
    @Transactional
    int updateTicket(@Param("id") Long id, @Param("ticketStatus") TicketStatus ticketStatus);


    @Lock(LockModeType.OPTIMISTIC_FORCE_INCREMENT)
    @Override
    <S extends Ticket> S saveAndFlush(S entity);

    @Query("UPDATE Ticket t SET t.ticketStatus = :ticketStatus, t.applicationUser = :applicationUser," +
        "t.ticketPdf = :pdf where t.id = :id")
    @Modifying(clearAutomatically = true)
    @Transactional
    int updateTicketWhenBuying(@Param("id") Long id, @Param("ticketStatus") TicketStatus ticketStatus,
                               @Param("applicationUser")ApplicationUser applicationUser,
                               @Param("pdf") String pdf);
}
