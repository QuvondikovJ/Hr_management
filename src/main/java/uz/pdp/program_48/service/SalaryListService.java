package uz.pdp.program_48.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import uz.pdp.program_48.entity.SalaryList;
import uz.pdp.program_48.entity.User;
import uz.pdp.program_48.payload.Result;
import uz.pdp.program_48.payload.SalaryListDto;
import uz.pdp.program_48.repository.SalaryListRepository;
import uz.pdp.program_48.repository.UserRepository;

import java.util.Optional;
import java.util.UUID;

@Service
public class SalaryListService {

    @Autowired
    SalaryListRepository salaryListRepository;
    @Autowired
    UserRepository userRepository;

    public Result add(SalaryListDto salaryDto) {
        UUID id = UUID.fromString(salaryDto.getUserId());
        boolean existsByUserId = salaryListRepository.existsByUserId(id);
        if (existsByUserId) {
            return new Result("This employee already is set a salary!", false);
        }
        User userWhichEnteredSystem = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String role = userWhichEnteredSystem.getRole().getRoleName().name();
        if (role.equals("DIRECTOR") || role.equals("HR_MANAGER")) {
            Optional<User> optionalUser = userRepository.findById(id);
            User user = optionalUser.get();

            SalaryList salaryList = new SalaryList();
            salaryList.setId(UUID.randomUUID());
            salaryList.setSalary(salaryDto.getSalary());
            salaryList.setUser(user);
            salaryListRepository.save(salaryList);
            return new Result("This employee successfully is set a salary.", true);
        }
        return new Result("You do not have the right to set a salary!", false);
    }

    public Result get(int page) {
        User userWhichEnteredSystem = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String role = userWhichEnteredSystem.getRole().getRoleName().name();
        if (role.equals("DIRECTOR") || role.equals("HR_MANAGER")) {
            Pageable pageable = PageRequest.of(page, 20);
            Page<SalaryList> salaryListPage = salaryListRepository.findAll(pageable);
            return new Result(salaryListPage, true);
        }
        return new Result("You do not have the right to see a salary of employees!", false);
    }

    public Result getByUserId(UUID id) {
        User userWhichEnteredSystem = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String role = userWhichEnteredSystem.getRole().getRoleName().name();
        if (role.equals("DIRECTOR") || role.equals("HR_MANAGER") || userWhichEnteredSystem.getId() == id) {
            Optional<SalaryList> optionalSalaryList = salaryListRepository.getByUserId(id);
            return optionalSalaryList.map(salaryList -> new Result(salaryList, true)).orElseGet(() -> new Result("Such user id not exist or this employee was not set a salary yet!", false));
        }
        return new Result("You do not have the right to see a salary of other employees!", false);
    }

    public Result getById(UUID id) {
        User userWhichEnteredSystem = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String role = userWhichEnteredSystem.getRole().getRoleName().name();
        if (role.equals("DIRECTOR") || role.equals("HR_MANAGER") || userWhichEnteredSystem.getId() == id) {
            Optional<SalaryList> optionalSalaryList = salaryListRepository.findById(id);
            return optionalSalaryList.map(salaryList -> new Result(salaryList, true)).orElseGet(() -> new Result("Such salary list id not exist!", false));
        }
        return new Result("You do not have the right to see a salary of other employees!", false);

    }


    public Result edit(UUID id, SalaryListDto salaryListDto) {
        User userWhichEnteredSystem = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String role = userWhichEnteredSystem.getRole().getRoleName().name();
        if (role.equals("DIRECTOR") || role.equals("HR_MANAGER")) {
            UUID userId = UUID.fromString(salaryListDto.getUserId());

            Optional<SalaryList> optionalSalaryList = salaryListRepository.findById(id);
            if (!optionalSalaryList.isPresent()) {
                return new Result("Such salary list id not exist!", false);
            }
            SalaryList salaryList = optionalSalaryList.get();
            boolean existsByUserId = salaryListRepository.existsByUserIdAndIdNot(userId, id);
            if (existsByUserId) {
                return new Result("This employee already is set a salary!", false);
            }
            Optional<User> optionalUser = userRepository.findById(userId);
            User user = optionalUser.get();
            salaryList.setSalary(salaryListDto.getSalary());
            salaryList.setUser(user);
            salaryListRepository.save(salaryList);
            return new Result("Given salary of user successfully edited.", true);
        }
        return new Result("You do not have the right to see a salary of other employees!", false);
    }

    public Result delete(UUID id) {
        User userWhichEnteredSystem = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String role = userWhichEnteredSystem.getRole().getRoleName().name();
        if (role.equals("DIRECTOR")) {
            Optional<SalaryList> optionalSalaryList = salaryListRepository.findById(id);
            if (!optionalSalaryList.isPresent()) {
                return new Result("Such salary list id not exist!", false);
            }
            salaryListRepository.deleteById(id);
            return new Result("Given salary of user successfully deleted.", true);
        }
        return new Result("You do not have the right to delete a salary of  employees!", false);
    }

}
