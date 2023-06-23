package dk.cphbusiness.dtos;

import dk.cphbusiness.entities.Department;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DepartmentDTO {
    private Long id;
    private String departmentName;
    private String departmentCode;
    private String departmentDescription;
    private Set<EmployeeDTO> employeeDTOs;

    public DepartmentDTO(Department d) {
        this.id = d.getId();
        this.departmentName = d.getDepartmentName();
        this.departmentCode = d.getDepartmentCode();
        this.departmentDescription = d.getDepartmentDescription();
        if(d.getEmployees() != null) {
            this.employeeDTOs = d.getEmployees().stream().map(EmployeeDTO::new).collect(java.util.stream.Collectors.toSet());
        }
    }
    public DepartmentDTO(String departmentName, String departmentCode, String departmentDescription) {
        this.departmentName = departmentName;
        this.departmentCode = departmentCode;
        this.departmentDescription = departmentDescription;
    }
    public Set<DepartmentDTO> getDepartmentDTOs(Set<Department> departments) {
        return departments.stream().map(DepartmentDTO::new).collect(java.util.stream.Collectors.toSet());
    }
}
