package dk.cphbusiness.entities;

import jakarta.persistence.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "roles")
@NamedQueries(@NamedQuery(name = "Role.deleteAllRows", query = "DELETE from RoleEntity"))
public class RoleEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Basic(optional = false)
    @Column(name = "role_name", length = 20)
    private String roleName;

    @ManyToMany(mappedBy = "roleEntityList")
    private List<UserEntity> userEntityList;

    public RoleEntity() {}

    public RoleEntity(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleName() {
        return roleName;
    }

}
