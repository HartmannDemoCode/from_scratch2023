package dk.cphbusiness.dtos;

import lombok.*;

@AllArgsConstructor
@ToString
@Getter
@Setter
@NoArgsConstructor
public class TokenDTO {
    private String token;
    private String username;
}
