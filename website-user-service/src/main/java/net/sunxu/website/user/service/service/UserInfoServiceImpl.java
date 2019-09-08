package net.sunxu.website.user.service.service;


import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;
import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import net.sunxu.website.help.exception.BizValidationException;
import net.sunxu.website.user.dto.SocialAccountDTO;
import net.sunxu.website.user.dto.SocialType;
import net.sunxu.website.user.dto.UserCreationDTO;
import net.sunxu.website.user.dto.UserDetailsDTO;
import net.sunxu.website.user.dto.UserState;
import net.sunxu.website.user.service.config.UserManageProperties;
import net.sunxu.website.user.service.entity.UserInfo;
import net.sunxu.website.user.service.entity.UserSocialInfo;
import net.sunxu.website.user.service.repo.UserInfoRepo;
import net.sunxu.website.user.service.repo.UserSocialInfoRepo;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserInfoServiceImpl implements UserInfoService {

    private static final Logger logger = LoggerFactory.getLogger(UserInfoServiceImpl.class);

    private static final Pattern NUMBER_PATTERN = Pattern.compile("\\d+");

    private static final Pattern MAIL_ADDRESS_PATTERN = Pattern
            .compile("^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$");

    private PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    @Autowired
    private UserInfoRepo userInfoRepo;

    @Autowired
    private UserSocialInfoRepo userSocialInfoRepo;

    @Autowired
    private UserManageProperties properties;

    @PostConstruct
    public void initialNewAdmin() {
        if (userInfoRepo.count() == 0) {
            UserCreationDTO newUser = new UserCreationDTO();
            newUser.setUserName(properties.getAdminUserName());
            newUser.setPassword(UUID.randomUUID().toString());
            newUser.setMailAddress("");

            createUser(newUser);

            logger.info("create admin user {}, password is {}", newUser.getUserName(), newUser.getMailAddress());
        }
    }

    @Override
    public UserDetailsDTO getUser(String identity) {
        UserInfo user;
        if (NUMBER_PATTERN.matcher(identity).matches()) {
            user = userInfoRepo.findById(Long.parseLong(identity)).get();
        } else if (MAIL_ADDRESS_PATTERN.matcher(identity).matches()) {
            user = userInfoRepo.findByMailAddress(identity);
        } else {
            user = userInfoRepo.findByName(identity);
        }
        return user == null ? null : convertToUserDetailsDTO(user);
    }

    private UserDetailsDTO convertToUserDetailsDTO(UserInfo user) {
        UserDetailsDTO res = new UserDetailsDTO();
        BeanUtils.copyProperties(user, res);
        res.setRoles(user.getRoleNames());
        return res;
    }

    @Transactional
    @Override
    public UserDetailsDTO createUser(UserCreationDTO dto) {
        BizValidationException.assertFalse(userInfoRepo.existsByNameIgnoreCase(dto.getUserName()), "用户名已存在");
        BizValidationException.assertTrue(isNameAvailable(dto.getUserName()), "用户名不允许使用");
        BizValidationException.assertFalse(userInfoRepo.existsByMailAddress(dto.getMailAddress()), "邮箱已存在");

        UserInfo user = new UserInfo();
        user.setName(dto.getUserName());
        String password = passwordEncoder.encode(dto.getPassword());
        user.setPassword(password);
        user.setMailAddress(dto.getMailAddress());
        user.setCreateTime(LocalDateTime.now());
        user.setRoleNames(properties.getDefaultRoles());
        user.setUserState(UserState.NORMAL);
        user.setAvatarUrl(properties.getDefaultAvatarUrl());
        userInfoRepo.save(user);

        return convertToUserDetailsDTO(user);
    }

    private boolean isNameAvailable(String userName) {
        userName = userName.toUpperCase();
        for (String forbiddenUserNamePrefix : properties.getForbiddenUserNamePrefix()) {
            if (userName.startsWith(forbiddenUserNamePrefix)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public UserDetailsDTO getUserBySocialId(SocialType socialType, String socialId) {
        var socialInfo = userSocialInfoRepo.findBySocialTypeAndSocialId(socialType, socialId);
        return socialInfo == null ? null : getUser(socialInfo.getUserId().toString());
    }

    @Transactional(rollbackOn = Exception.class)
    @Override
    public UserDetailsDTO createUserBySocialAccount(SocialAccountDTO dto) {
        String userName = dto.getSocialName();
        if (!isNameAvailable(userName)) {
            userName = dto.getSocialName() + "_" + userName;
        }
        if (userInfoRepo.existsByNameIgnoreCase(userName)) {
            int index = userName.length() + 1;
            List<String> existNames = userInfoRepo.findNamesOfPrefix(userName);
            long maxNo = 1L;
            for (String existName : existNames) {
                String affix = existName.substring(index);
                if (NUMBER_PATTERN.matcher(affix).matches()) {
                    maxNo = Math.max(Long.parseUnsignedLong(affix), maxNo);
                }
            }
            userName = userName + "_" + ++maxNo;
        }

        UserInfo user = new UserInfo();
        user.setName(userName);
        user.setCreateTime(LocalDateTime.now());
        user.setRoleNames(properties.getDefaultRoles());
        user.setUserState(UserState.NORMAL);
        user.setAvatarUrl(StringUtils.isEmpty(dto.getAvatarUrl()) ?
                properties.getDefaultAvatarUrl() : dto.getAvatarUrl());
        userInfoRepo.save(user);

        var socialInfo = createNewUserSocialInfo(user.getId(), dto);
        updateSocialStatus(socialInfo, dto);
        userSocialInfoRepo.save(socialInfo);

        return convertToUserDetailsDTO(user);
    }

    private UserSocialInfo createNewUserSocialInfo(Long userId, SocialAccountDTO dto) {
        var socialInfo = new UserSocialInfo();
        socialInfo.setCreateTime(LocalDateTime.now());
        socialInfo.setUserId(userId);
        socialInfo.setSocialType(dto.getSocialType());
        socialInfo.setSocialId(dto.getSocialId());
        socialInfo.setNew(true);
        return socialInfo;
    }

    private void updateSocialStatus(UserSocialInfo socialInfo, SocialAccountDTO dto) {
        socialInfo.setSocialName(dto.getSocialName());
        socialInfo.setAvatarUrl(dto.getAvatarUrl());
        socialInfo.setProfileUrl(dto.getProfileUrl());
        socialInfo.setLastUsedTime(LocalDateTime.now());
    }

    @Transactional(rollbackOn = Exception.class)
    @Override
    public void updateUserSocialAccount(Long userId, SocialAccountDTO dto) {
        var socialInfo = userSocialInfoRepo.findBySocialTypeAndSocialId(dto.getSocialType(), dto.getSocialId());
        if (socialInfo == null) {
            var user = userInfoRepo.findById(userId).get();
            socialInfo = createNewUserSocialInfo(user.getId(), dto);
        } else if (!userId.equals(socialInfo.getUserId())) {
            throw BizValidationException.newException("%s 已经绑定到其它账户了", dto.getSocialName());
        }
        updateSocialStatus(socialInfo, dto);
        userSocialInfoRepo.save(socialInfo);
    }
}
