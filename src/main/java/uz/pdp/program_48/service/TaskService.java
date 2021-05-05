package uz.pdp.program_48.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.MailMessage;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import uz.pdp.program_48.config.SecurityConfig;
import uz.pdp.program_48.entity.Task;
import uz.pdp.program_48.entity.User;
import uz.pdp.program_48.payload.Result;
import uz.pdp.program_48.payload.TaskDto;
import uz.pdp.program_48.repository.TaskRepository;
import uz.pdp.program_48.repository.UserRepository;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class TaskService {

    @Autowired
    TaskRepository taskRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
  JavaMailSender javaMailSender;

public Result add(TaskDto taskDto) {
    Optional<Task> optionalTask = taskRepository.getByTaskName(taskDto.getTaskName());
    if (optionalTask.isPresent()) {
        return new Result("This task is already attached to another employee!", false);
    }
    UUID id = UUID.fromString(taskDto.getUserId());
    Optional<User> optionalUser = userRepository.findById(id);
    User user = optionalUser.get();
    User userWhichEnteredSystem = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String role = userWhichEnteredSystem.getRole().getRoleName().name();
    if (role.equals("DIRECTOR") ||
            (role.equals("HR_MANAGER") && user.getRole().getRoleName().name().equals("WORKER"))) {

        LocalDate localDate = LocalDate.parse(taskDto.getDeadline());
        LocalTime localTime = LocalTime.of(0,0);
        LocalDateTime localDateTime = LocalDateTime.of(localDate, localTime);
        Timestamp timestamp = Timestamp.valueOf(localDateTime);

        Task task = new Task();
        task.setId(UUID.randomUUID());
        task.setTaskName(taskDto.getTaskName());
        task.setDescription(taskDto.getDescription());
        task.setDeadline(timestamp);
        task.setUser(user);
        task.setEmailCode(UUID.randomUUID().toString());
        taskRepository.save(task);
        sendEmail(user.getEmail(), task.getEmailCode());

        return new Result("This task successfully attached to this employee.", true);
    }
    return new Result("You can not attach task to anyone!", false);
}

public void sendEmail(String email, String emailCode) {
    try {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom("quvondikovj6@gmail.com");
        mailMessage.setTo(email);
        mailMessage.setSubject("Approval of a new task.");
        String query = "<a href='http://localhost:8080/api/task/verifyEmailForAcceptTask?email="
                + email + "&emailCode=" + emailCode + "'>Accept a new task</a>";
        mailMessage.setText(query);
        javaMailSender.send(mailMessage);
    } catch (Exception e) {
        e.printStackTrace();
    }
}

public Result verifyEmailForAcceptTask(String email, String emailCode){
  Optional<Task> optionalTask = taskRepository.getByUsersEmailAndEmailCode(emailCode, email);
  if (!optionalTask.isPresent()){
      return new Result("You already accepted this task!", false);
  }
  Task task = optionalTask.get();
  task.setTaskStatus("In progress");
  task.setEmailCode(null);
  taskRepository.save(task);
  return new Result("You successfully accepted this task.", true);
}

public Result getTasks(int page){
    User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String role = user.getRole().getRoleName().name();
    if (role.equals("DIRECTOR") || role.equals("HR_MANAGER")){
        Pageable pageable = PageRequest.of(page, 20);
        Page<Task> page1 = taskRepository.findAll(pageable);
        return new Result(page1, true);
    }
    return new Result("You can not see all tasks!", false);
}

public Result getTasksByEmployeeId(UUID id, int page) {
    User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String role = user.getRole().getRoleName().name();
    if (role.equals("DIRECTOR") || role.equals("HR_MANAGER") || user.getId()==id) {
        boolean existsByUserId = taskRepository.existsByUserId(id);
        if (!existsByUserId) {
            return new Result("This employee does not any tasks!", false);
        }
        Pageable pageable = PageRequest.of(page, 20);
        Page<Task> page1 = taskRepository.getByUserId(id, pageable);
        return new Result(page1, true);
    }
    return new Result("You can not see tasks of this employee!", false);
}


public Result getById(UUID id){
    Optional<Task> optionalTask = taskRepository.findById(id);
    if (!optionalTask.isPresent()){
        return new Result("Such task id not exist!", false);
    }
    Task task = optionalTask.get();
    User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String role = user.getRole().getRoleName().name();
    if (role.equals("DIRECTOR") || role.equals("HR_MANAGER") || user==task.getUser()){
        return new Result(task, true);
    }
    return new Result("You can not see this task!", false);
}

public Result edit(UUID id, TaskDto taskDto){
    Optional<Task> optionalTask = taskRepository.findById(id);
    if (!optionalTask.isPresent()){
        return new Result("Such task id not exist!", false);
    }
    Optional<Task> optionalTask1 = taskRepository.getByTaskNameAndIdNot(taskDto.getTaskName(), id);
    if (optionalTask1.isPresent()){
        return new Result("This task name belongs to other task!", false);
    }
    User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String role = user.getRole().getRoleName().name();
    if (role.equals("DIRECTOR") || role.equals("HR_MANAGER")){


        LocalDate localDate = LocalDate.parse(taskDto.getDeadline());
        LocalTime localTime = LocalTime.of(0,0);
        LocalDateTime localDateTime = LocalDateTime.of(localDate, localTime);
        Timestamp timestamp = Timestamp.valueOf(localDateTime);

UUID id1 = UUID.fromString(taskDto.getUserId());
        Optional<User> optionalUser = userRepository.findById(id1);
        User user1 = optionalUser.get();
        Task task = optionalTask.get();
        task.setTaskName(taskDto.getTaskName());
        task.setDescription(taskDto.getDescription());
        task.setDeadline(timestamp);
        task.setUser(user1);
        taskRepository.save(task);
        return new Result("Given task successfully edited.", true);
    }
    return new Result("You can not edit tasks!", false);
}

public Result delete(UUID id){
    Optional<Task> optionalTask = taskRepository.findById(id);
    if (!optionalTask.isPresent()) {
        return new Result("Such task id not exist!", false);
    }
    User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String role = user.getRole().getRoleName().name();
    if (role.equals("DIRECTOR")){
        taskRepository.deleteById(id);
        return new Result("Given task successfully deleted!", true);
    }
    return new Result("You can not delete any tasks!", false);
}


public Result completedTask(UUID taskId){
Optional<Task> optionalTask = taskRepository.findById(taskId);
Task task = optionalTask.get();
task.setEmailCode(UUID.randomUUID().toString());
taskRepository.save(task);
    Optional<User> optionalUser = userRepository.findById(task.getCreatedBy());
User user = optionalUser.get();
    sendEmailToEmployer(user.getEmail(), task.getEmailCode());
    return new Result("Your request sent. Shortly you will accept response!", true);
}

public void sendEmailToEmployer(String email, String emailCode){
    try{
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom("quvondikovj6@gmail.com");
        mailMessage.setTo(email);
        mailMessage.setSubject("Confirmation task.");
        String query = "<a href = 'http://localhost:8080/api/task/verifyEmailByEmployer?email="
                +email+"&emailCode="+emailCode+"'>Confirm the task.</a>";
        mailMessage.setText(query);
        javaMailSender.send(mailMessage);
    }catch (Exception e){
   e.printStackTrace();
    }
}

public Result verifyEmailByEmployer(String email, String emailCode){
    Optional<Task> optionalTask = taskRepository.getByUsersEmailAndEmailCode(emailCode, email);
    if (!optionalTask.isPresent()){
        return new Result("This task already confirmed by you!", false);
    }
    Task task = optionalTask.get();
    task.setTaskStatus("Completed");
    taskRepository.save(task);
    return new Result("This task successfully confirmed by you.", true);
}

public Result worksNotCompletedOnTimeAndNotAccepted(int page){
    LocalDateTime localDateTime = LocalDateTime.now();
    Timestamp timestamp = Timestamp.valueOf(localDateTime);

    User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String role = user.getRole().getRoleName().name();
    if (role.equals("DIRECTOR") || role.equals("HR_MANAGER")){
        Pageable pageable = PageRequest.of(page, 20);
        Page<Task> page1 = taskRepository.getByDeadlineAndNotAccepted(timestamp,"New", pageable);
        return new Result(page1, true);
    }
    return new Result("You can not see works not completed on time!", false);
}

public Result worksNotCompletedOnTimeButAccepted(int page){
    LocalDateTime localDateTime = LocalDateTime.now();
    Timestamp timestamp = Timestamp.valueOf(localDateTime);

    User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String role = user.getRole().getRoleName().name();
    if (role.equals("DIRECTOR") || role.equals("HR_MANAGER")) {
Pageable pageable =PageRequest.of(page, 20);
Page<Task> page1 = taskRepository.getByDeadlineAndAccepted(timestamp, "In progress", pageable);
return new Result(page1, true);
    }
    return new Result("You can not see works not completed on time!", false);
}
}
