package net.sunxu.website.user.service.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import net.sunxu.website.user.dto.SocialType;
import net.sunxu.website.user.service.entity.UserSocialInfo.UserSocialInfoKey;
import org.springframework.data.domain.Persistable;

@Data
@ToString
@Entity
@IdClass(UserSocialInfoKey.class)
@Table(indexes = @Index(columnList = "socialId"))
public class UserSocialInfo implements Persistable<UserSocialInfoKey> {

    @Id
    private Long userId;

    @Id
    private SocialType socialType;

    @Column(nullable = false)
    private String socialId;

    @Column
    private String socialName;

    @Column
    private String avatarUrl;

    @Column
    private String profileUrl;

    @Column
    private LocalDateTime createTime;

    @Column
    private LocalDateTime lastUsedTime;

    @Transient
    private UserSocialInfoKey id;

    @Transient
    private boolean isNew;

    @Override
    public UserSocialInfoKey getId() {
        if (id == null) {
            id = new UserSocialInfoKey(userId, socialType);
        }
        return id;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserSocialInfoKey implements Serializable {

        @Id
        private Long userId;

        @Id
        private SocialType socialType;
    }

}
