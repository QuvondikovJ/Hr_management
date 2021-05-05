package uz.pdp.program_48.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uz.pdp.program_48.entity.Role;
import uz.pdp.program_48.entity.enums.RoleName;

public interface RoleRepository extends JpaRepository<Role, Integer> {

@Query(value = "select * from role where role.role_name=:roleName", nativeQuery = true)
    Role getByRoleName(String roleName);


}
