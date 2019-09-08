package net.sunxu.website.user.dto;

import java.io.Serializable;
import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class UserCreationDTO implements Serializable {

    private static final long serialVersionUID = -1L;

    @Size(min = 6, max = 20, message = "用户长度在6-20个字")
    @Pattern(regexp = "^[- \\u4e00-\\u9fa5_a-zA-Z0-9]+$")
    private String userName;

    public void setUserName(String userName) {
        this.userName = userName == null ? null : userName.trim();
    }

    @Email
    private String mailAddress;

    public void setMailAddress(String mailAddress) {
        this.mailAddress = mailAddress == null ? null : mailAddress.toLowerCase();
    }

    @Size(min = 6, message = "密码必须大于6位")
    private String password;

}
