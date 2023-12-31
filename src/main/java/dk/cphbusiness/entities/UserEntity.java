package dk.cphbusiness.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.mindrot.jbcrypt.BCrypt;

import java.io.Serial;
import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@NamedQueries(@NamedQuery(name = "User.deleteAllRows", query = "DELETE from UserEntity"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Basic(optional = false)
    @Column(name = "user_name", length = 25)
    private String userName;
    @Basic(optional = false)
    @Column(name = "user_pass")
    private String userPass;
    @JoinTable(name = "user_roles", joinColumns = {
            @JoinColumn(name = "user_name", referencedColumnName = "user_name")}, inverseJoinColumns = {
            @JoinColumn(name = "role_name", referencedColumnName = "role_name")})
    @ManyToMany(fetch = FetchType.EAGER)
    private Set<RoleEntity> roleEntityList = new LinkedHashSet<>();

    public Set<String> getRolesAsStrings() {
        if (roleEntityList.isEmpty()) {
            return null;
        }
        Set<String> rolesAsStrings = new LinkedHashSet<>();
        roleEntityList.forEach((roleEntity) -> {
            rolesAsStrings.add(roleEntity.getRoleName());
        });
        return rolesAsStrings;
    }

    public boolean verifyPassword(String pw) {
        return BCrypt.checkpw(pw, userPass);
    }

    public UserEntity(String userName, String userPass) {
        this.userName = userName;
        this.userPass = BCrypt.hashpw(userPass, BCrypt.gensalt());
    }
    public UserEntity(String userName, Set<RoleEntity> roleEntityList) {
        this.userName = userName;
        this.roleEntityList = roleEntityList;
    }

    public void addRole(String userRole) {
        roleEntityList.add(new RoleEntity(userRole));
    }

}
