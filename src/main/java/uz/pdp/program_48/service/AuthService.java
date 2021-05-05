package uz.pdp.program_48.service;

import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import uz.pdp.program_48.entity.Role;
import uz.pdp.program_48.entity.User;
import uz.pdp.program_48.payload.LoginDto;
import uz.pdp.program_48.payload.Result;
import uz.pdp.program_48.payload.UserDto;
import uz.pdp.program_48.repository.RoleRepository;
import uz.pdp.program_48.repository.UserRepository;
import uz.pdp.program_48.security.JwtProvider;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
public class AuthService implements UserDetailsService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    JavaMailSender javaMailSender;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    JwtProvider jwtProvider;
@Autowired
    PasswordEncoder passwordEncoder;
@Autowired
    RoleRepository roleRepository;

    public Result register(UserDto userDto) {
        Optional<User> optionalUser = userRepository.getByEmail(userDto.getEmail());
        if (optionalUser.isPresent()) {
            return new Result("Such email of user already exist!", false);
        }

        User userWhichEnteredSystem = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
          Role roleWhichAddingUser = userWhichEnteredSystem.getRole();
          Role role = roleRepository.getByRoleName(userDto.getRole());

            if (roleWhichAddingUser.getRoleName().name().equals("DIRECTOR") ||
                    (roleWhichAddingUser.getRoleName().name().equals("HR_MANAGER") && role.getRoleName().name().equals("WORKER"))) {

                User user = new User();
                user.setId(UUID.randomUUID());
                user.setFirstName(userDto.getFirstName());
                user.setLastName(userDto.getLastName());
                user.setEmail(userDto.getEmail());
                user.setPassword(passwordEncoder.encode(userDto.getPassword()));
                user.setRole(role);
                user.setEmailCode(UUID.randomUUID().toString());

                userRepository.save(user);
                sendEmail(user.getEmail(), user.getEmailCode());

                if (user.getRole().getRoleName().name().equals("HR_MANAGER")) {
                    return new Result("HR_MANAGER successfully added to system." +
                            " Accountni faollashtirish uchun emailingizga yuborilgan xabarni tasdiqlashingiz lozim.", true);
                }
                return new Result("WORKER successfully added to system."
                + " Accountni faollashtirish uchun emailingizga yuborilgan xabarni tasdiqlashingiz lozim.", true);

        }
            return new Result("You can not add this user!", false);
    }


    public void sendEmail(String email, String emailCode) {
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom("noreply@gmail.com");
            mailMessage.setTo(email);
            mailMessage.setSubject("Accountni tasdiqlash...");
            String query = "<a href = 'http://localhost:8080/api/auth/verifyEmail?email="
                    + email + "&emailCode=" + emailCode + "'> Accountni Tasdiqlash. </a>";
            mailMessage.setText(query);
            javaMailSender.send(mailMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Result verifyEmail(String email, String emailCode) {
        Optional<User> optionalUser = userRepository.getByEmailAndEmailCode(email, emailCode);
        if (!optionalUser.isPresent()) {
            return new Result("You already activated your account!", false);
        }
        User user = optionalUser.get();
        user.setEnabled(true);
        user.setEmailCode(null);
        userRepository.save(user);
        return new Result("Account activated", true);
    }

    public Result logIn(LoginDto loginDto) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginDto.getUsername(),
                            loginDto.getPassword()
                    ));
            String token = jwtProvider.generateToken(loginDto.getUsername());
            return new Result(token, true);
        } catch (BadCredentialsException e) {
            return new Result("Login or password is wrong!", false);
        }
    }

    public Result getEmployeesByRole(Integer role, int page) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String role1 = user.getRole().getRoleName().name();
        if (role1.equals("DIRECTOR") || role1.equals("HR_MANAGER")) {
        Optional<Role> optionalRole = roleRepository.findById(role);
        boolean existsByRole = userRepository.existsByRole(optionalRole.get());
        if (!existsByRole) {
            return new Result("This role does not any employees!", false);
        }
            Pageable pageable = PageRequest.of(page, 20);
            Page<User> page1 = userRepository.getByRole(optionalRole.get(), pageable);
            return new Result(page1, true);
        }
        return new Result("You can not see list of employees!", false);
    }

    public Result getEmployeeById(UUID id){
        Optional<User> optionalUser = userRepository.findById(id);
        if (!optionalUser.isPresent()){
            return new Result("Such employee id not exist!", false);
        }
        User user = optionalUser.get();
        User user1 = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String role = user1.getRole().getRoleName().name();
        if (role.equals("DIRECTOR") || role.equals("HR_MANAGER") || user1.getId()==id){
            return new Result(user, true);
        }
        return new Result("You can not see information of this employee!", false);
    }



    public Result edit(UUID id, UserDto userDto){
        Optional<User> optionalUser = userRepository.findById(id);
        if (!optionalUser.isPresent()){
            return new Result("Such employee id not exist!", false);
        }
        User user1 = optionalUser.get();

        Optional<User> optionalUser1 = userRepository.getByEmail(userDto.getEmail());
        if (optionalUser1.isPresent()){
            return new Result("Such email of user already exist!", false);
        }
        Role role = roleRepository.getByRoleName(userDto.getRole());

        User userWhichEnteredSystem =(User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
       String roleWhichEnteredSystem = userWhichEnteredSystem.getRole().getRoleName().name();
        if (roleWhichEnteredSystem.equals("DIRECTOR") ||
                (userWhichEnteredSystem.getId()==id && userWhichEnteredSystem.getRole().equals(role))
        || (roleWhichEnteredSystem.equals("HR_MANAGER") && !userDto.getRole().equals("DIRECTOR"))){

         /** Yuqoridagi shartda: DIRECTOR istalgan xodim malumotlarini o'zgartirishi mumkin, yoki
          istalgan xodim o'zini lavozimidan tashqari malumotlarini o'zgartirishi mumkin yoki
          HR_MANAGER DIRECTOR dan boshqa istalgan xodimni malumotlarini o'zgartirishi mumkin
          **/

            user1.setFirstName(userDto.getFirstName());
            user1.setLastName(userDto.getLastName());
            user1.setEmail(userDto.getEmail());
            user1.setPassword(passwordEncoder.encode(userDto.getPassword()));
            userRepository.save(user1);
            return new Result("Given user  successfully edited!", true);
        }
        return new Result("You can not edit information of this employee!", false);
    }


public Result delete(UUID id){
        Optional<User> optionalUser = userRepository.findById(id);
    if (!optionalUser.isPresent()) {
        return new Result("Such employee id not exist!", false);
    }
    User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String role = user.getRole().getRoleName().name();
    if (role.equals("DIRECTOR")){
        User user1 = optionalUser.get();
        user1.setEnabled(false);
        userRepository.save(user1);
        return new Result("Given employee successfully deleted.", true);
    }
    return new Result("You can not delete this employee!", false);
}



    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Optional<User> optionalUser = userRepository.getByEmail(username);
       if (optionalUser.isPresent()){
            User user = optionalUser.get();
            return user;
        }
        throw new UsernameNotFoundException(username+" topilmadi!");

    }
    }
