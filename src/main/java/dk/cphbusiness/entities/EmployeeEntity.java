package dk.cphbusiness.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "employee")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class EmployeeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    @ManyToOne
    @JoinColumn(name = "department_id", referencedColumnName = "id")
    private DepartmentEntity departmentEntity;

//    public EmployeeEntity(EmployeeDTO employeeDTO) {
//        this.id = employeeDTO.getId();
//        this.firstName = employeeDTO.getFirstName();
//        this.lastName = employeeDTO.getLastName();
//        this.email = employeeDTO.getEmail();
////        if(employeeDTO.getDepartmentName() != null) {
////            this.departmentEntity = new DepartmentEntity(employeeDTO.getDepartmentName());
////        }
//    }

    public EmployeeEntity(String firstName, String lastName, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }
}