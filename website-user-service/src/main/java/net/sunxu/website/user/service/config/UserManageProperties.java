package net.sunxu.website.user.service.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

@Data
@ToString
@Component
@ConfigurationProperties("website.user.management")
@RefreshScope
@Valid
public class UserManageProperties {

    @NotEmpty
    private Set<String> defaultRoles;

    public void setDefaultRoles(Set<String> roles) {
        defaultRoles = roles.stream().map(r -> {
            r = r.trim().toUpperCase();
            if (!r.startsWith("ROLE_")) {
                r = "ROLE_" + r;
            }
            return r;
        }).collect(Collectors.toSet());
    }

    @NotEmpty
    private String defaultAvatarUrl;

    private List<String> forbiddenUserNamePrefix = new ArrayList<>();

    public void setForbiddenUserNamePrefix(Set<String> names) {
        forbiddenUserNamePrefix = names.stream()
                .map(n -> n.trim().toUpperCase())
                .collect(Collectors.toList());
    }

    private String adminUserName = "admin";

    private String adminUserRole = "ROLE_ADMIN";

}
