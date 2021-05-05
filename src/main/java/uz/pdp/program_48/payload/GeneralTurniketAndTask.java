package uz.pdp.program_48.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import uz.pdp.program_48.entity.Task;
import uz.pdp.program_48.entity.Turniket;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GeneralTurniketAndTask {
private Page<Turniket> turnikets;
private Page<Task> tasks;
}
