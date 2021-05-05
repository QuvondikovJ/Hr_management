package uz.pdp.program_48.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import uz.pdp.program_48.entity.GiveSalary;
import uz.pdp.program_48.entity.SalaryList;
import uz.pdp.program_48.entity.User;
import uz.pdp.program_48.payload.GiveSalaryDto;
import uz.pdp.program_48.payload.Result;
import uz.pdp.program_48.repository.GiveSalaryRepository;
import uz.pdp.program_48.repository.SalaryListRepository;
import uz.pdp.program_48.repository.UserRepository;

import java.time.Month;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
public class GiveSalaryService {

    @Autowired
    GiveSalaryRepository giveSalaryRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    SalaryListRepository salaryListRepository;

    public Result add(GiveSalaryDto giveSalaryDto) {
        User userWhichEnteredSystem = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String role = userWhichEnteredSystem.getRole().getRoleName().name();
        if (role.equals("DIRECTOR") || role.equals("HR_MANAGER")) {
            UUID id = UUID.fromString(giveSalaryDto.getSalaryListId());
            Optional<SalaryList> optionalSalaryList = salaryListRepository.findById(id);
            SalaryList salaryList = optionalSalaryList.get();
            Month month = Month.valueOf(giveSalaryDto.getForWhichMonth());

            Optional<GiveSalary> optionalGiveSalary = giveSalaryRepository.getByForWhichMonthAndForWhichYear(month, giveSalaryDto.getForWhichYear());
            if (optionalGiveSalary.isPresent()) {
                GiveSalary giveSalary = optionalGiveSalary.get();
                Set<SalaryList> setGiveSalary = giveSalary.getSalaryList();
                setGiveSalary.add(salaryList);
                giveSalary.setSalaryList(setGiveSalary);
                giveSalaryRepository.save(giveSalary);
            } else {
                GiveSalary giveSalary = new GiveSalary();
                giveSalary.setSalaryList(Collections.singleton(salaryList));
                giveSalary.setForWhichMonth(month);
                giveSalary.setForWhichYear(giveSalaryDto.getForWhichYear());
                giveSalaryRepository.save(giveSalary);
            }
            return new Result("You successfully gave salary to this employee.", true);
        }
        return new Result("You can not give salary to employees!", false);
    }

    public Result getByMonth(String byMonth, int page) {
        User userWhichEnteredSystem = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String role = userWhichEnteredSystem.getRole().getRoleName().name();
        if (role.equals("DIRECTOR") || role.equals("HR_MANAGER")) {
            Month month = Month.valueOf(byMonth);
            Pageable pageable = PageRequest.of(page, 20);
            Page<GiveSalary> page1 = giveSalaryRepository.getByForWhichMonth(month, pageable);
            return new Result(page1, true);
        }
        return new Result("You do not have the right to see information about salary!", false);
    }

    public Result getCountGivenSalaryAndCountTokenEmployeeByMonth(String byMonth, Integer year) {
        User userWhichEnteredSystem = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String role = userWhichEnteredSystem.getRole().getRoleName().name();
        if (role.equals("DIRECTOR") || role.equals("HR_MANAGER")) {
            Double countGivenSalary = giveSalaryRepository.getCountGivenSalaryByMonth(byMonth, year);
            Integer countTokenEmployee = giveSalaryRepository.getCountTokenEmployeeByMonth(byMonth, year);
            return new Result("In this month " + countGivenSalary + " sum given to " + countTokenEmployee + " employees.", true);
        }
        return new Result("You do not have the right to see information about salary!", false);
    }


    public Result getByYear(Integer byYear, int page) {
        User userWhichEnteredSystem = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String role = userWhichEnteredSystem.getRole().getRoleName().name();
        if (role.equals("DIRECTOR") || role.equals("HR_MANAGER")) {
            Pageable pageable = PageRequest.of(page, 20);
            Page<GiveSalary> page1 = giveSalaryRepository.getByForWhichYear(byYear, pageable);
            return new Result(page1, true);
        }
        return new Result("You do not have the right to see information about salary!", false);
    }



public Result edit(Integer id, UUID salaryListId, GiveSalaryDto giveSalaryDto){
    /** Bu metodga qaysi give salary id da userlarga maosh berilgani va qaysi user1 (user1=salaryListId) id ga maosh berilgani
     * va bu user1 ga maosh berilmasligi kerak bo'lgan va boshqa user2 (user2=givesalaryDto.getSalaryListId() )
     * ga maosh berilishi kerak bo'lgan shu sababli user1 dan berilgan maoshni user2 ga berilgan qilib o'zgartiramiz
     */

    User userWhichEnteredSystem = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String role = userWhichEnteredSystem.getRole().getRoleName().name();
    if (role.equals("DIRECTOR") || role.equals("HR_MANAGER")){
        Optional<GiveSalary> optionalGiveSalary = giveSalaryRepository.findById(id);
    if (!optionalGiveSalary.isPresent()) {
        return new Result("Such giveSalary id not exist!", false);
    }
    GiveSalary giveSalary = optionalGiveSalary.get();

    // bu yerda String toifada kelgan month ni Month toifaga o'girib oldik
    Month month = Month.valueOf(giveSalaryDto.getForWhichMonth());
UUID salaryListId2 =  UUID.fromString(giveSalaryDto.getSalaryListId());
    // bu yerda o'sha berilgan maoshni o'sha give salary id da (ya'ni o'sha maosh berilgan yil va oyda)
        // o'zgartirmoqchi bo'lgan user2 haqiqatdan ham maosh olmaganini tekshiryapmiz
    Optional<GiveSalary> optionalGiveSalary1 = giveSalaryRepository.
            getByForWhichYearAndForWhichMonthAndSalaryListId(giveSalaryDto.getForWhichMonth(), giveSalaryDto.getForWhichYear(),salaryListId2);
    if (optionalGiveSalary1.isPresent()){
        return new Result("This employee already token his salary!", false);
    }
    //bu yerda user2 maosh olmagan user1 maosh olgan, maosh olganlar ro'yxatidan user1 ni
        // o'chirib user2 ni maosh olganlar ro'yxatiga qo'shib qo'yyapmiz
    Optional<SalaryList> optionalSalaryList = salaryListRepository.findById(salaryListId);
    SalaryList user = optionalSalaryList.get();
      Optional<SalaryList> optionalSalaryList2 = salaryListRepository.findById(salaryListId2);
    SalaryList user2 = optionalSalaryList2.get();

    Set<SalaryList> set = giveSalary.getSalaryList();
    set.remove(user);
    set.add(user2);

    giveSalary.setSalaryList(set);
    giveSalary.setForWhichMonth(month);
    giveSalary.setForWhichYear(giveSalaryDto.getForWhichYear());
    giveSalaryRepository.save(giveSalary);
return new Result("Given give salary successfully edited.", true);
    }
    return new Result("You do not have the right to see information about salary!", false);
}
}