package uz.pdp.program_48.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import uz.pdp.program_48.entity.Role;
import uz.pdp.program_48.entity.User;
import uz.pdp.program_48.entity.enums.RoleName;
import uz.pdp.program_48.repository.RoleRepository;
import uz.pdp.program_48.repository.UserRepository;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.UUID;

@Component
public class DataLoader implements CommandLineRunner {
@Autowired
    RoleRepository roleRepository;
@Autowired
    UserRepository userRepository;
@Autowired
    PasswordEncoder passwordEncoder;

@Value(value = "${spring.datasource.initialization-mode}")
    private String initialMode;

UUID id = UUID.fromString("83778dbb-0c0f-46c4-8fed-b55819201f8a");
Timestamp timestamp = Timestamp.valueOf("2021-05-04 11:54:36.100");


    @Override
    public void run(String... args) throws Exception {
        if (initialMode.equals("always")){
            Role role1 = new Role(1, RoleName.DIRECTOR);
            Role role2 = new Role(2, RoleName.HR_MANAGER);
            Role role3 = new Role(3, RoleName.WORKER);


            User user = new User(id, "director",
                    "directorov", "d@gmail.com", passwordEncoder.encode("1111"),
                    timestamp,  null, timestamp,  null,
                    null, role1,
                    true,
                    true, true, true);

            roleRepository.saveAll(Arrays.asList(role1,role2,role3));
            userRepository.save(user);
        }
    }
}
