package dk.cphbusiness.dtos;

import dk.cphbusiness.entities.DepartmentEntity;
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

    public DepartmentDTO(DepartmentEntity d) {
        this.id = d.getId();
        this.departmentName = d.getDepartmentName();
        this.departmentCode = d.getDepartmentCode();
        this.departmentDescription = d.getDepartmentDescription();
        if(d.getEmployeeEntities() != null) {
            this.employeeDTOs = d.getEmployeeEntities().stream().map(EmployeeDTO::new).collect(java.util.stream.Collectors.toSet());
        }
    }
    public DepartmentDTO(String departmentName, String departmentCode, String departmentDescription) {
        this.departmentName = departmentName;
        this.departmentCode = departmentCode;
        this.departmentDescription = departmentDescription;
    }
    public Set<DepartmentDTO> getDepartmentDTOs(Set<DepartmentEntity> departmentEntities) {
        return departmentEntities.stream().map(DepartmentDTO::new).collect(java.util.stream.Collectors.toSet());
    }
}
