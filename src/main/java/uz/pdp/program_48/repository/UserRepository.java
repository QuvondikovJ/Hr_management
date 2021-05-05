package uz.pdp.program_48.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uz.pdp.program_48.entity.Role;
import uz.pdp.program_48.entity.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
Optional<User> getByEmail(String email);
Optional<User> getByEmailAndEmailCode(String email, String emailCode);
boolean existsByRole(Role role);
Page<User> getByRole(Role role, Pageable pageable);
}
