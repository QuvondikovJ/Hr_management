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
import java.sql.Timestamp;
import java.time.Month;
import java.util.Set;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
public class GiveSalary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToMany
    private Set<SalaryList> salaryList;

    @Enumerated(EnumType.STRING)
    private Month forWhichMonth;


    private Integer forWhichYear;

@CreationTimestamp
    private Timestamp createdAt;

@CreatedBy
    private UUID createdBy;

@UpdateTimestamp
    private Timestamp updatedAt;

@LastModifiedBy
    private UUID updatedBy;



}
