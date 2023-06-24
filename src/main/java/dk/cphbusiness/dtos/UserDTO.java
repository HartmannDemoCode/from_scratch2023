package dk.cphbusiness.dtos;


import dk.cphbusiness.entities.User;
import java.security.Principal;
import java.util.Set;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class UserDTO implements IDTO<User> {

    private String username;

    private String password;

    private Set<String> roles;

    public UserDTO(User user) {
        this.username = user.getUserName();
        this.roles = user.getRolesAsStrings();
    }

//    public UserDTO(String username, String[] roles) {
//        this.username = username;
//        this.roles = Set.of(roles);
//    }

    public UserDTO(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public boolean isUserInRole(String role) {
        return this.roles.contains(role);
    }

    @Override
    public User asEntity() {
        return new User(username, password);
    }
}
