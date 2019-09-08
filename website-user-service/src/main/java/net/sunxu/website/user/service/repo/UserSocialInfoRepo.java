package net.sunxu.website.user.service.repo;

import net.sunxu.website.user.dto.SocialType;
import net.sunxu.website.user.service.entity.UserSocialInfo;
import net.sunxu.website.user.service.entity.UserSocialInfo.UserSocialInfoKey;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserSocialInfoRepo extends CrudRepository<UserSocialInfo, UserSocialInfoKey> {

    UserSocialInfo findBySocialTypeAndSocialId(SocialType socialType, String socialId);

}
