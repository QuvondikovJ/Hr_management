package uz.pdp.program_48.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uz.pdp.program_48.entity.Task;
import uz.pdp.program_48.entity.Turniket;

import java.sql.Timestamp;
import java.util.Optional;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, UUID> {
Optional<Task> getByTaskName(String taskName);

@Query(value = "select * from task join users on task.user_id=users.id where task.email_code=:emailCode and users.email=:email", nativeQuery = true)
    Optional<Task> getByUsersEmailAndEmailCode(String emailCode, String email);

boolean existsByUserId(UUID user_id);
Page<Task> getByUserId(UUID user_id, Pageable pageable);

Optional<Task> getByTaskNameAndIdNot(String taskName, UUID id);

@Query(value = "select * from task where task.deadline <:timeStamp and task.task_status=:taskStatus", nativeQuery = true)
    Page<Task> getByDeadlineAndNotAccepted(Timestamp timeStamp,String taskStatus,  Pageable pageable);

@Query(value = "select * from task where task.deadline <:timeStamp and task.task_status=:taskStatus", nativeQuery = true)
    Page<Task> getByDeadlineAndAccepted(Timestamp timeStamp, String taskStatus,  Pageable pageable);


    @Query(value = "select * from task where task.user_id=:userId " +
            " and task.created_at >:timeStamp1 and task.created_at <:timeStamp2 and task.task_status=:taskStatus", nativeQuery = true)
    Page<Task> getByUserIdAndComeToOfficeBetween(UUID userId, Timestamp timeStamp1, Timestamp timeStamp2, String taskStatus, Pageable pageable);


}
