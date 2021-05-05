package uz.pdp.program_48.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uz.pdp.program_48.entity.GiveSalary;

import java.time.Month;
import java.util.Optional;
import java.util.UUID;

public interface GiveSalaryRepository extends JpaRepository<GiveSalary, Integer> {

Optional<GiveSalary> getByForWhichMonthAndForWhichYear(Month forWhichMonth, Integer forWhichYear);
Page<GiveSalary> getByForWhichMonth(Month forWhichMonth, Pageable pageable);
Page<GiveSalary> getByForWhichYear(Integer forWhichYear, Pageable pageable);

@Query(value = "select count(*) from give_salary_salary_list  as a join give_salary as b on a.give_salary_id=b.id " +
        " where b.for_which_month=:month and b.for_which_year=:year", nativeQuery = true)
Integer getCountTokenEmployeeByMonth(String month, Integer year);

    @Query(value = "select sum(salary) from give_salary_salary_list  as a join give_salary as b on a.give_salary_id=b.id " +
            " join salary_list as c on a.salary_list_id = c.id  where b.for_which_month=:month and b.for_which_year=:year", nativeQuery = true)
    Double getCountGivenSalaryByMonth(String month, Integer year);

    @Query(value = "select * from give_salary as a join give_salary_salary_list as b on a.id=b.give_salary_id " +
            " where a.for_which_month=:month and a.for_which_year=:year and b.salary_list_id=:id", nativeQuery = true)
    Optional<GiveSalary> getByForWhichYearAndForWhichMonthAndSalaryListId(String month, Integer year, UUID id);

}
