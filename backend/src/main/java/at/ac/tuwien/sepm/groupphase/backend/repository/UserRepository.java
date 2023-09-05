package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import at.ac.tuwien.sepm.groupphase.backend.entity.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<ApplicationUser, Long> {

    @Override
    Optional<ApplicationUser> findById(Long id);

    ApplicationUser findApplicationUserByUsernameIgnoreCase(String username);

    @Modifying(clearAutomatically = true)
    @Transactional
    void deleteApplicationUserByUsername(String username);

    @Override
    <S extends ApplicationUser> S save(S entity);

    @Query("UPDATE ApplicationUser u SET u.firstName = TRIM(:firstName) WHERE u.username=:username")
    @Modifying(clearAutomatically = true)
    @Transactional
    int updateFirstName(@Param("username") String username, @Param("firstName") String firstName);

    @Query("UPDATE ApplicationUser u SET u.lastName = TRIM(:lastName) WHERE u.username=:username")
    @Modifying(clearAutomatically = true)
    @Transactional
    int updateLastName(@Param("username") String username, @Param("lastName") String lastName);

    @Query("UPDATE  ApplicationUser u SET u.email = TRIM(:email) WHERE u.username=:username")
    @Modifying(clearAutomatically = true)
    @Transactional
    int updateEmail(@Param("username") String username, @Param("email") String email);

    @Query("UPDATE ApplicationUser a SET a.country = TRIM(:country), a.city = TRIM(:city)"
        + ", a.zipCode = TRIM(:zipCode), a.street = TRIM(:street) WHERE a.username = :username")
    @Modifying(clearAutomatically = true)
    @Transactional
    int updateAddress(@Param("username") String username, @Param("country") String country,
                      @Param("city") String city, @Param("zipCode") String zipCode,
                      @Param("street") String street);

    @Query("UPDATE ApplicationUser a SET a.password = (:password) WHERE a.username = :username")
    @Modifying(clearAutomatically = true)
    @Transactional
    int updatePassword(@Param("username") String username, @Param("password") String password);

    ApplicationUser findApplicationUserByEmail(String email);

    List<ApplicationUser> findApplicationUserByLockedIsTrue();

    ApplicationUser findApplicationUserByPasswordResetToken(PasswordResetToken passwordResetToken);

    @Query("SELECT u FROM ApplicationUser u WHERE u.admin = false AND u.locked = false ORDER BY u.username asc ")
    Page<ApplicationUser> findAllNonLockedCustomersOrderedByUsername(Pageable pageable);
}
