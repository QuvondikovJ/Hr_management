package uz.pdp.program_48.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import uz.pdp.program_48.entity.Task;
import uz.pdp.program_48.entity.Turniket;
import uz.pdp.program_48.entity.User;
import uz.pdp.program_48.payload.GeneralTurniketAndTask;
import uz.pdp.program_48.payload.Result;
import uz.pdp.program_48.payload.TurniketDto;
import uz.pdp.program_48.payload.WorksBetweenTime;
import uz.pdp.program_48.repository.TaskRepository;
import uz.pdp.program_48.repository.TurniketRepository;
import uz.pdp.program_48.repository.UserRepository;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class TurniketService {

    @Autowired
    TurniketRepository turniketRepository;
    @Autowired
    UserRepository userRepository;
@Autowired
    TaskRepository taskRepository;

    public Result add(TurniketDto turniketDto) {
        UUID id = UUID.fromString(turniketDto.getUserId());
        Optional<User> optionalUser = userRepository.findById(id);
        User user = optionalUser.get();
        Turniket turniket = new Turniket();
        LocalDateTime localDateTime = LocalDateTime.now();
        Timestamp timestamp = Timestamp.valueOf(localDateTime);

        LocalDate localDate = LocalDate.now();
        LocalTime localTime = LocalTime.of(0, 0);
        LocalDateTime localDateTime1 = LocalDateTime.of(localDate, localTime);
        Timestamp timestamp1 = Timestamp.valueOf(localDateTime1);

        Optional<Turniket> optionalTurniket = turniketRepository.getByUserIdAndComeToOffice(user.getId(), timestamp1);
        if (optionalTurniket.isPresent()) {
            return new Result("Today you already came to office!", false);
        }
        turniket.setId(UUID.randomUUID());
        turniket.setComeToOffice(timestamp);
        turniket.setUser(user);
        turniketRepository.save(turniket);
        return new Result("Welcome to Office.", true);
    }


    public Result getByUserId(UUID id, int page) {
        boolean existsByUserId = turniketRepository.existsByUserId(id);
        if (!existsByUserId) {
            return new Result("This employee has not yet passed the turniket!", false);
        }
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String role = user.getRole().getRoleName().name();
        if (role.equals("DIRECTOR") || role.equals("HR_MANAGER") || user.getId() == id) {
            Pageable pageable = PageRequest.of(page, 20);
            Page<Turniket> page1 = turniketRepository.getByUserId(id, pageable);
            return new Result(page1, true);
        }
        return new Result("You can not see turniket information of this employee!", false);
    }

    public Result getByUserIdAndTimeBetween(WorksBetweenTime worksBetweenTime, int page) {
        UUID id = UUID.fromString(worksBetweenTime.getId());
        boolean existsByUserId = turniketRepository.existsByUserId(id);
        if (!existsByUserId) {
            return new Result("This employee has not yet passed the turniket!", false);
        }
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String role = user.getRole().getRoleName().name();
        if (role.equals("DIRECTOR") || role.equals("HR_MANAGER")) {

            LocalDate localDate1 = LocalDate.parse(worksBetweenTime.getDate1());
            LocalDate localDate2 = LocalDate.parse(worksBetweenTime.getDate2());
            LocalTime localTime = LocalTime.of(0, 0);
            LocalDateTime localDateTime1 = LocalDateTime.of(localDate1, localTime);
            LocalDateTime localDateTime2 = LocalDateTime.of(localDate2, localTime);
            Timestamp timestamp1 = Timestamp.valueOf(localDateTime1);
            Timestamp timestamp2 = Timestamp.valueOf(localDateTime2);

            Pageable pageable = PageRequest.of(page, 20);
            Page<Turniket> page1 = turniketRepository.getByUserIdAndComeToOfficeBetween(id, timestamp1, timestamp2, pageable);
            Page<Task> page2= taskRepository.getByUserIdAndComeToOfficeBetween(id, timestamp1, timestamp2, "Completed", pageable);
GeneralTurniketAndTask generalTurniketAndTask = new GeneralTurniketAndTask(page1, page2);
            return new Result(generalTurniketAndTask, true);
        }
        return new Result("You can not see other employees' works!", false);
    }


    public Result edit(TurniketDto turniketDto) {
        LocalDate localDate = LocalDate.now();
        LocalTime localTime = LocalTime.of(0, 0);
        LocalDateTime localDateTime = LocalDateTime.of(localDate, localTime);
        Timestamp timestamp = Timestamp.valueOf(localDateTime);

        UUID id = UUID.fromString(turniketDto.getUserId());
        Optional<Turniket> optionalTurniket = turniketRepository.getByUserIdAndComeToOffice(id, timestamp);
        if (!optionalTurniket.isPresent()) {
            return new Result("Today you did not come to office!", false);
        }
        LocalDateTime localDateTime1 = LocalDateTime.now();
        Timestamp timestamp1 = Timestamp.valueOf(localDateTime1);
        Turniket turniket = optionalTurniket.get();
        turniket.setLeaveFromOffice(timestamp1);
        turniketRepository.save(turniket);
        return new Result("See you, bye.", true);
    }


}
