package uz.pdp.program_48.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import java.sql.Timestamp;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Turniket {
@Id
    @GeneratedValue
    private UUID id;


private Timestamp comeToOffice;


private Timestamp leaveFromOffice;

@OneToOne
    private User user;

}
