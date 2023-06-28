package dk.cphbusiness.dtos;


import dk.cphbusiness.entities.UserEntity;

import java.util.Set;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class UserDTO implements IDTO<UserEntity> {

    private String username;
    private String password;
    private Set<String> roles;

    public UserDTO(UserEntity userEntity) {
        if(userEntity == null) {
            return;
        }
        this.username = userEntity.getUserName();
        this.roles = userEntity.getRolesAsStrings();
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
    public UserEntity asEntity() {
        return new UserEntity(username, password);
    }
}
