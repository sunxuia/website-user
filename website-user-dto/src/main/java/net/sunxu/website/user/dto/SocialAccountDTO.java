package net.sunxu.website.user.dto;

import java.io.Serializable;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class SocialAccountDTO implements Serializable {

    private static final long serialVersionUID = -1L;

    @NotNull
    private SocialType socialType;

    @NotNull
    private String socialId;

    @NotEmpty
    private String socialName;

    private String avatarUrl;

    private String profileUrl;
}
