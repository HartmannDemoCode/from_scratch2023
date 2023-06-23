package dk.cphbusiness.entities;

import dk.cphbusiness.entities.Employee;
import dk.cphbusiness.dtos.DepartmentDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "department")
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;
    private String departmentName;
    private String departmentCode;
    private String departmentDescription;

    @OneToMany(mappedBy = "department")
    private java.util.Set<Employee> employees;

    public Department(DepartmentDTO departmentDTO) {
        this.id = departmentDTO.getId();
        this.departmentName = departmentDTO.getDepartmentName();
        this.departmentCode = departmentDTO.getDepartmentCode();
        this.departmentDescription = departmentDTO.getDepartmentDescription();
        if(departmentDTO.getEmployeeDTOs() != null) {
            this.employees = departmentDTO.getEmployeeDTOs().stream().map(Employee::new).collect(java.util.stream.Collectors.toSet());
        }
    }

    public void addEmployee(Employee employee) {
        this.employees.add(employee);
    }
    public void removeEmployee(Employee employee) {
        this.employees.remove(employee);
    }
}