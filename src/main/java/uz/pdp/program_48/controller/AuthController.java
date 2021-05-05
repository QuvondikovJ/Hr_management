package uz.pdp.program_48.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.pdp.program_48.entity.User;
import uz.pdp.program_48.payload.LoginDto;
import uz.pdp.program_48.payload.Result;
import uz.pdp.program_48.payload.UserDto;
import uz.pdp.program_48.service.AuthService;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    AuthService authService;


    @PostMapping("/register")
    public ResponseEntity<Result> register(@RequestBody UserDto userDto) {
        Result result = authService.register(userDto);
       return ResponseEntity.status(result.isActive() ? 200 : 409).body(result);
    }

    @GetMapping("/verifyEmail")
    public ResponseEntity<Result> verifyEmail(@RequestParam String email, @RequestParam String emailCode){
        Result result = authService.verifyEmail(email, emailCode);
        return ResponseEntity.status(result.isActive() ? 200 : 409).body(result);
    }

    @GetMapping("/login")
    public ResponseEntity<Result> logIn(@RequestBody LoginDto loginDto){
        Result result = authService.logIn(loginDto);
        return ResponseEntity.status(result.isActive() ? 200 : 409).body(result);
    }

    @GetMapping("/employees")
    public ResponseEntity<Result> getEmployeesByRole(@RequestParam Integer role, @RequestParam int page){
        Result result = authService.getEmployeesByRole(role, page);
        return ResponseEntity.status(result.isActive() ? 200 : 204).body(result);
    }

    @GetMapping("/employee/{id}")
    public ResponseEntity<Result> getEmployeeById(@PathVariable UUID id){
        Result result = authService.getEmployeeById(id);
        return ResponseEntity.status(result.isActive() ? 200 : 409).body(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Result> edit(@PathVariable UUID id, @RequestBody UserDto userDto){
        Result result = authService.edit(id, userDto);
        return ResponseEntity.status(result.isActive() ? HttpStatus.ACCEPTED : HttpStatus.CONFLICT).body(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Result> delete(@PathVariable UUID id){
        Result result = authService.delete(id);
        return ResponseEntity.status(result.isActive() ? 200 : 409).body(result);
    }

}
