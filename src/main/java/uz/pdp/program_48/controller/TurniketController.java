package uz.pdp.program_48.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;
import uz.pdp.program_48.payload.Result;
import uz.pdp.program_48.payload.TurniketDto;
import uz.pdp.program_48.payload.WorksBetweenTime;
import uz.pdp.program_48.service.TurniketService;

import java.util.UUID;

@RestController
@RequestMapping("/api/turniket")
public class TurniketController {

    @Autowired
    TurniketService turniketService;

@PostMapping("/come")
public ResponseEntity<Result> add(@RequestBody TurniketDto turniketDto){
    Result result = turniketService.add(turniketDto);
    return ResponseEntity.status(result.isActive() ? HttpStatus.CREATED : HttpStatus.CONFLICT).body(result);
}

@GetMapping("/byUserId/{id}")
    public ResponseEntity<Result> getByUserId(@PathVariable UUID id, @RequestParam int page){
    Result result = turniketService.getByUserId(id, page);
    return ResponseEntity.status(result.isActive() ? 200 : 409).body(result);
}

@GetMapping("/byUserIdAndTimeBetween")
public ResponseEntity<Result> getByUserIdAndTimeBetween(@RequestBody WorksBetweenTime worksBetweenTime, @RequestParam int page){
    Result result = turniketService.getByUserIdAndTimeBetween(worksBetweenTime, page);
    return ResponseEntity.status(result.isActive() ? 200 : 409).body(result);
}

@PutMapping
    public ResponseEntity<Result> edit(@RequestBody TurniketDto turniketDto){
    Result result = turniketService.edit(turniketDto);
    return ResponseEntity.status(result.isActive() ? HttpStatus.ACCEPTED : HttpStatus.CONFLICT).body(result);
}


}
