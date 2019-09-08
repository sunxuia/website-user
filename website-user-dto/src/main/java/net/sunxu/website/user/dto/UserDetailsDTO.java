package net.sunxu.website.user.dto;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString
@EqualsAndHashCode(callSuper = true)
public class UserDetailsDTO extends UserDTO {

    private static final long serialVersionUID = -1L;

    private String password;

}
