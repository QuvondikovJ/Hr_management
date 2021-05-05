package uz.pdp.program_48.payload;

import lombok.Data;

import java.util.UUID;

@Data
public class GiveSalaryDto {
private String salaryListId;
private String forWhichMonth;
private Integer forWhichYear;

}
