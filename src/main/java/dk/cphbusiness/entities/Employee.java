package dk.cphbusiness.entities;

import dk.cphbusiness.entities.Department;
import dk.cphbusiness.dtos.EmployeeDTO;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "employee")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    @ManyToOne
    @JoinColumn(name = "department_id", referencedColumnName = "id")
    private Department department;

    public Employee(EmployeeDTO employeeDTO) {
        this.id = employeeDTO.getId();
        this.firstName = employeeDTO.getFirstName();
        this.lastName = employeeDTO.getLastName();
        this.email = employeeDTO.getEmail();
//        if(employeeDTO.getDepartmentName() != null) {
//            this.department = new Department(employeeDTO.getDepartmentName());
//        }
    }

    public Employee(String firstName, String lastName, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }
}