package dk.cphbusiness.entities;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "employee")
@NamedQueries({
        @NamedQuery(name="EmployeeEntity.deleteAllRows", query = "DELETE from EmployeeEntity"),
        @NamedQuery(name="EmployeeEntity.getAll", query = "SELECT e from EmployeeEntity e"),
        @NamedQuery(name="EmployeeEntity.getByName", query = "SELECT e from EmployeeEntity e WHERE e.firstName = :firstName"),
        @NamedQuery(name="EmployeeEntity.getByEmail", query = "SELECT e from EmployeeEntity e WHERE e.email = :email"),
        @NamedQuery(name="EmployeeEntity.getByDepartment", query = "SELECT e from EmployeeEntity e WHERE e.departmentEntity.departmentName = :departmentName"),
        @NamedQuery(name="EmployeeEntity.getByDepartmentId", query = "SELECT e from EmployeeEntity e WHERE e.departmentEntity.id = :departmentId"),
        @NamedQuery(name="EmployeeEntity.getById", query = "SELECT e from EmployeeEntity e WHERE e.id = :id")
})
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

    public EmployeeEntity(String firstName, String lastName, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }
}