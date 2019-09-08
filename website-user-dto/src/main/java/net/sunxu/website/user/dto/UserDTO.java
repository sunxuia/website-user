package net.sunxu.website.user.dto;

import java.io.Serializable;
import java.util.Collection;
import lombok.Data;

@Data
public class UserDTO implements Serializable {

    private static final long serialVersionUID = -1;

    /**
     * 用户id
     */
    private Long id;

    /**
     * 用户名
     */
    private String name;

    /**
     * 头像
     */
    private String avatarUrl;

    /**
     * 用户状态
     */
    private UserState userState;

    /**
     * 用户邮箱
     */
    private String mailAddress;

    /**
     * 角色
     */
    private Collection<String> roles;

}
