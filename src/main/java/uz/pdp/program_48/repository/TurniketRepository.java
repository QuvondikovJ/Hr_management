package uz.pdp.program_48.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uz.pdp.program_48.entity.Turniket;

import java.sql.Timestamp;
import java.util.Optional;
import java.util.UUID;

public interface TurniketRepository extends JpaRepository<Turniket, UUID> {

 @Query(value = "select * from turniket where turniket.user_id=:userId and turniket.come_to_office > :timestamp", nativeQuery = true)
 Optional<Turniket> getByUserIdAndComeToOffice(UUID userId, Timestamp timestamp);

@Query(value = "select * from turniket where turniket.user_id=:userId and turniket.come_to_office >:timeStamp1 " +
        " and turniket.leave_from_office <:timeStamp2 ", nativeQuery = true)
Page<Turniket> getByUserIdAndComeToOfficeBetween(UUID userId, Timestamp timeStamp1, Timestamp timeStamp2, Pageable pageable);

     boolean existsByUserId(UUID user_id);
    Page<Turniket> getByUserId(UUID user_id, Pageable pageable);

}
