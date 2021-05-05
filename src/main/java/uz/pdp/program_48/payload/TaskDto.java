package uz.pdp.program_48.payload;

import lombok.Data;

import java.util.UUID;

@Data
public class TaskDto {
private String taskName;
private String description;
private String deadline;
private String   userId;

}
