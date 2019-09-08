package net.sunxu.website.user.service.entity;

import java.time.LocalDateTime;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.Data;
import lombok.ToString;
import net.sunxu.website.user.dto.UserState;

@Data
@ToString
@Entity
public class UserInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @Column
    private String password;

    @Column(unique = true)
    private String mailAddress;

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> roleNames;

    @Column(nullable = false)
    private UserState userState;

    @Column
    private String avatarUrl;

    @Column
    private LocalDateTime createTime;

}
