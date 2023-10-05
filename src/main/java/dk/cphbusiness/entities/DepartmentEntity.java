package dk.cphbusiness.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "department")
@NamedQueries({@NamedQuery(name="DepartmentEntity.deleteAllRows", query = "DELETE from DepartmentEntity")})
public class DepartmentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;
    private String departmentName;
    private String departmentCode;
    private String departmentDescription;

    @OneToMany(mappedBy = "departmentEntity")
    private java.util.Set<EmployeeEntity> employeeEntities;

    public DepartmentEntity(String departmentName, String departmentCode, String departmentDescription) {
        this.departmentName = departmentName;
        this.departmentCode = departmentCode;
        this.departmentDescription = departmentDescription;
    }

//    public DepartmentEntity(DepartmentDTO departmentDTO) {
//        this.id = departmentDTO.getId();
//        this.departmentName = departmentDTO.getDepartmentName();
//        this.departmentCode = departmentDTO.getDepartmentCode();
//        this.departmentDescription = departmentDTO.getDepartmentDescription();
//        if(departmentDTO.getEmployeeDTOs() != null) {
//            this.employeeEntities = departmentDTO.getEmployeeDTOs().stream().map(EmployeeEntity::new).collect(java.util.stream.Collectors.toSet());
//        }
//    }

    public void addEmployee(EmployeeEntity employeeEntity) {
        this.employeeEntities.add(employeeEntity);
    }
    public void removeEmployee(EmployeeEntity employeeEntity) {
        this.employeeEntities.remove(employeeEntity);
    }
}