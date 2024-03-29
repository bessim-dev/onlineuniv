package tn.univ.onlineuniv.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import tn.univ.onlineuniv.models.User;


@Repository
@Transactional(readOnly = true)
public interface UserRepository
        extends JpaRepository<User, Long> {

    User findByEmail(String email);
    @Transactional
    @Modifying
    @Query("UPDATE User a " +
            "SET a.enabled = TRUE WHERE a.email = ?1")
    void enableAppUser(String email);
    @Transactional
    @Modifying
    @Query("UPDATE User a " +
            "SET a.locked = TRUE WHERE a.id = ?1")
    int lockUser(Long id);
    @Transactional
    @Modifying
    @Query("UPDATE User a " +
            "SET a.locked = FALSE WHERE a.id = ?1")
    int unlockUser(long id);

}
