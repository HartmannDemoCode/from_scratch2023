package dk.cphbusiness.dtos;

import dk.cphbusiness.entities.EmployeeEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDTO implements IDTO<EmployeeEntity>{
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String departmentName;
    public EmployeeDTO(EmployeeEntity e){
        this.id = e.getId();
        this.firstName = e.getFirstName();
        this.lastName = e.getLastName();
        this.email = e.getEmail();
        if(e.getDepartmentEntity() != null) {
            this.departmentName = e.getDepartmentEntity().getDepartmentName();
        }
    }
    public EmployeeDTO(String firstName, String lastName, String email, String departmentName) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.departmentName = departmentName;
    }
    public Set<EmployeeDTO> getEmployeeDTOs(Set<EmployeeEntity> employeeEntities) {
        Set<EmployeeDTO> employeeDTOS = employeeEntities.stream().map(EmployeeDTO::new).collect(java.util.stream.Collectors.toSet());
        return employeeDTOS;
    }

    @Override
    public String toString() {
        return "EmployeeDTO{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", departmentName='" + departmentName + '\'' +
                '}';
    }
    @Override
    public EmployeeEntity asEntity() {
        return new EmployeeEntity(firstName, lastName, email);
    }
}
