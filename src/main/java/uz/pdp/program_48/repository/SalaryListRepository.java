package uz.pdp.program_48.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.pdp.program_48.entity.SalaryList;

import java.util.Optional;
import java.util.UUID;

public interface SalaryListRepository extends JpaRepository<SalaryList, UUID> {
boolean existsByUserId(UUID user_id);
Optional<SalaryList> getByUserId(UUID user_id);
boolean existsByUserIdAndIdNot(UUID user_id, UUID id);

}
