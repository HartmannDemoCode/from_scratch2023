package dk.cphbusiness.daos;
import dk.cphbusiness.entities.User;

import java.util.Set;

public interface ISecurityDAO<U,R> {
U getVerifiedUser(String username, String password);
R createRole(String role);
boolean hasRole(String role, User user);
String createToken(String username, Set<String> roles);
U verifyToken(String token);

}
