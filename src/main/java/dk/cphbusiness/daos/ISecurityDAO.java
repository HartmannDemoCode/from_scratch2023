package dk.cphbusiness.daos;
import dk.cphbusiness.entities.User;

import java.util.Set;

public interface ISecurityDAO<U,R> {
U getVerifiedUser(String username, String password);
R createRole(String role);
U addUserRole(String username, String role);
U removeUserRole(String username, String role);
boolean hasRole(String role, User user);
String createToken(U user) throws Exception;
U verifyToken(String token) throws Exception;

}
