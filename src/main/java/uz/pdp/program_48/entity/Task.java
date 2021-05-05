package uz.pdp.program_48.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Task {
@Id
    @GeneratedValue
    private UUID id;

@Column(nullable = false, unique = true)
private String taskName;

private String taskStatus = "New" ;

private String description;

private Timestamp deadline;

@ManyToOne
private User user;

private String emailCode;

@CreatedBy
private UUID createdBy;

@CreationTimestamp
private Timestamp createdAt;

@LastModifiedBy
private UUID updatedBy;

@UpdateTimestamp
private Timestamp updatedAt;


}
