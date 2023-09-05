package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.Invoice;
import at.ac.tuwien.sepm.groupphase.backend.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Order getOrderById(long id);

    Optional<Order> findById(Long id);

    void deleteById(Long id);

    @Query("UPDATE reservation r SET r.reservationCode = TRIM(:reservationCode), r.dueTime = :dueTime WHERE r.id=:id")
    @Modifying(clearAutomatically = true)
    @Transactional
    int updateReservation(@Param("id") Long id, @Param("reservationCode") String reservationCode, @Param("dueTime") LocalDateTime dueTime);

    @Query("UPDATE payment p SET p.total = :total, p.time = :time WHERE p.id=:id")
    @Modifying(clearAutomatically = true)
    @Transactional
    int updatePayment(@Param("id") Long id, @Param("total") Double total, @Param("time") LocalDateTime time);

    @Query("update payment p set p.invoice = :invoice where p.id = :id")
    @Modifying(clearAutomatically = true)
    @Transactional
    int updateInvoice(@Param("id") long id, @Param("invoice") Invoice invoice);


    List<Order> findByApplicationUser_UsernameOrderByCreationDateTimeDesc(String username);
}
