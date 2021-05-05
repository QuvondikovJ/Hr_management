package uz.pdp.program_48.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.pdp.program_48.payload.Result;
import uz.pdp.program_48.payload.SalaryListDto;
import uz.pdp.program_48.service.SalaryListService;

import java.util.UUID;

@RestController
@RequestMapping("/api/salaryList")
public class SalaryListController {

    @Autowired
    SalaryListService salaryListService;

@PostMapping
    public ResponseEntity<Result> add(@RequestBody SalaryListDto salaryListDto){
    Result result = salaryListService.add(salaryListDto);
    return ResponseEntity.status(result.isActive() ? HttpStatus.CREATED : HttpStatus.CONFLICT).body(result);
}

@GetMapping
    public ResponseEntity<Result> get(@RequestParam int page) {
    Result result = salaryListService.get(page);
    return ResponseEntity.status(result.isActive() ? 200 : 403).body(result);
}

@GetMapping("/byUserId/{id}")
public ResponseEntity<Result> getByUserId(@PathVariable UUID id){
    Result result = salaryListService.getByUserId(id);
    return ResponseEntity.status(result.isActive() ? 200 : 409).body(result);
}

@GetMapping("/{id}")
    public ResponseEntity<Result> getById(@PathVariable UUID id){
    Result result = salaryListService.getById(id);
    return ResponseEntity.status(result.isActive() ? 200 : 409).body(result);
}

@PutMapping("/{id}")
    public ResponseEntity<Result> edit(@PathVariable UUID id, @RequestBody SalaryListDto salaryListDto){
    Result result= salaryListService.edit(id, salaryListDto);
    return ResponseEntity.status(result.isActive() ? HttpStatus.ACCEPTED : HttpStatus.CONFLICT).body(result);
}

@DeleteMapping("/{id}")
    public ResponseEntity<Result> delete(@PathVariable UUID id){
    Result result = salaryListService.delete(id);
    return ResponseEntity.status(result.isActive() ? 200 : 409).body(result);
}

}
