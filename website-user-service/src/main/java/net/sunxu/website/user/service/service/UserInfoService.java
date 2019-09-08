package net.sunxu.website.user.service.service;


import net.sunxu.website.user.dto.SocialAccountDTO;
import net.sunxu.website.user.dto.SocialType;
import net.sunxu.website.user.dto.UserCreationDTO;
import net.sunxu.website.user.dto.UserDetailsDTO;

public interface UserInfoService {

    UserDetailsDTO getUser(String identity);

    UserDetailsDTO createUser(UserCreationDTO dto);

    UserDetailsDTO getUserBySocialId(SocialType socialType, String socialId);

    UserDetailsDTO createUserBySocialAccount(SocialAccountDTO dto);

    void updateUserSocialAccount(Long userId, SocialAccountDTO dto);

}
