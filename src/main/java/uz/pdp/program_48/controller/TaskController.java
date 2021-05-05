package uz.pdp.program_48.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;
import uz.pdp.program_48.payload.Result;
import uz.pdp.program_48.payload.TaskDto;
import uz.pdp.program_48.service.TaskService;

import java.util.UUID;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    TaskService taskService;

@PostMapping
    public ResponseEntity<Result> add(@RequestBody TaskDto taskDto){
    Result result = taskService.add(taskDto);
    return ResponseEntity.status(result.isActive() ? HttpStatus.CREATED : HttpStatus.CONFLICT).body(result);
}

@GetMapping("/verifyEmailForAcceptTask")
    public ResponseEntity<Result> verifyEmailForAcceptTask(@RequestParam String email,@RequestParam String emailCode){
    Result result = taskService.verifyEmailForAcceptTask(email, emailCode);
    return ResponseEntity.status(result.isActive() ? 200 : 409).body(result);
}

@GetMapping("/allTasks")
    public ResponseEntity<Result> get(@RequestParam int page){
    Result result = taskService.getTasks(page);
    return ResponseEntity.status(result.isActive() ? 200 : 409).body(result);
}

@GetMapping("/tasksByEmployeeId/{id}")
    public ResponseEntity<Result> getTasksByEmployeeId(@PathVariable UUID id, @RequestParam int page){
    Result result = taskService.getTasksByEmployeeId(id, page);
    return ResponseEntity.status(result.isActive() ? 200 : 409).body(result);
}

@GetMapping("/byId/{id}")
    public ResponseEntity<Result> getTasksById(@PathVariable UUID id){
    Result result = taskService.getById(id);
    return ResponseEntity.status(result.isActive() ? 200 : 409).body(result);
}

@PutMapping("/{id}")
    public ResponseEntity<Result> edit(@PathVariable UUID id, @RequestBody TaskDto taskDto){
    Result result = taskService.edit(id, taskDto);
    return ResponseEntity.status(result.isActive() ? HttpStatus.ACCEPTED : HttpStatus.CONFLICT).body(result);
}

@DeleteMapping("/{id}")
public ResponseEntity<Result> delete(@PathVariable UUID id){
    Result result = taskService.delete(id);
    return ResponseEntity.status(result.isActive() ? 200 : 409).body(result);
}

@GetMapping("/completedTask/{taskId}")
    public ResponseEntity<Result> completedTask(@PathVariable UUID taskId){
    Result result =  taskService.completedTask(taskId);
return ResponseEntity.ok(result);

}

@GetMapping("/verifyEmailByEmployer")
    public ResponseEntity<Result> verifyEmailByEmployer(@RequestParam String email, @RequestParam String emailCode){
    Result result = taskService.verifyEmailByEmployer(email, emailCode);
    return ResponseEntity.status(result.isActive() ? 200 : 409).body(result);
}

@GetMapping("/worksNotCompletedOnTimeAndNotAccepted")
    public ResponseEntity<Result> getWorksNotCompletedOnTimeAndNotAccepted(@RequestParam int page){
    Result result = taskService.worksNotCompletedOnTimeAndNotAccepted(page);
    return ResponseEntity.status(result.isActive() ? 200 : 409).body(result);
}

    @GetMapping("/worksNotCompletedOnTimeButAccepted")
    public ResponseEntity<Result> getWorksNotCompletedOnTimeButAccepted(@RequestParam int page){
        Result result = taskService.worksNotCompletedOnTimeButAccepted(page);
        return ResponseEntity.status(result.isActive() ? 200 : 409).body(result);
    }
}
