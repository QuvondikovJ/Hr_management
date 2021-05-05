package uz.pdp.program_48.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import uz.pdp.program_48.payload.GiveSalaryDto;
import uz.pdp.program_48.payload.Result;
import uz.pdp.program_48.service.GiveSalaryService;

import java.util.UUID;

@RestController
@RequestMapping("/api/giveSalary")
public class GiveSalaryController {

    @Autowired
    GiveSalaryService giveSalaryService;

    @PostMapping
    public ResponseEntity<Result> add(@RequestBody GiveSalaryDto giveSalaryDto){
        Result result = giveSalaryService.add(giveSalaryDto);
        return ResponseEntity.status(result.isActive() ? 201 : 409).body(result);
    }

    @GetMapping("/byMonth")
    public ResponseEntity<Result> getByMonth(@RequestParam String byMonth, @RequestParam int page){
        Result result = giveSalaryService.getByMonth(byMonth, page);
        return ResponseEntity.status(result.isActive() ? 200 : 409).body(result);
    }

    @GetMapping("/countSalaryAndCountEmployeeByMonth")
public ResponseEntity<Result> getCountGivenSalaryAndCountTokenEmployeeByMonth(@RequestParam String month, @RequestParam Integer year){
        Result result = giveSalaryService.getCountGivenSalaryAndCountTokenEmployeeByMonth(month, year);
        return ResponseEntity.status(result.isActive() ? 200 : 409).body(result);
    }

    @GetMapping("/byYear")
    public ResponseEntity<Result> getByYear(@RequestParam Integer year, @RequestParam int page){
        Result result = giveSalaryService.getByYear(year, page);
        return ResponseEntity.status(result.isActive() ? 200 : 409).body(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Result> edit(@PathVariable Integer id, @RequestParam UUID salaryListId, @RequestBody GiveSalaryDto giveSalaryDto){
        Result result = giveSalaryService.edit(id, salaryListId, giveSalaryDto);
        return ResponseEntity.status(result.isActive() ? HttpStatus.ACCEPTED : HttpStatus.CONFLICT).body(result);
    }


}
