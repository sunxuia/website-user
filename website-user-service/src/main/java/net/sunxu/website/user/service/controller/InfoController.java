package net.sunxu.website.user.service.controller;

import java.util.ArrayList;
import java.util.List;
import net.sunxu.website.config.security.rbac.annotation.AccessResource;
import net.sunxu.website.user.dto.SocialAccountDTO;
import net.sunxu.website.user.dto.SocialType;
import net.sunxu.website.user.dto.UserCreationDTO;
import net.sunxu.website.user.dto.UserDTO;
import net.sunxu.website.user.dto.UserDetailsDTO;
import net.sunxu.website.user.service.service.UserInfoService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/info")
@AccessResource("user.info")
@RestController
public class InfoController {

    @Autowired
    private UserInfoService userInfoService;

    @AccessResource("view")
    @GetMapping("/user/{userId:\\d+}")
    public UserDTO getUserInfo(@PathVariable("userId") Long id) {
        var user = userInfoService.getUser(id.toString());
        var res = new UserDTO();
        BeanUtils.copyProperties(user, res);
        return res;
    }

    @AccessResource("view")
    @GetMapping("/users")
    public List<UserDTO> getBatchUserInfo(@RequestParam("ids") List<Long> ids) {
        List<UserDTO> res = new ArrayList<>(ids.size());
        for (Long id : ids) {
            var user = userInfoService.getUser(id.toString());
            var dto = new UserDTO();
            BeanUtils.copyProperties(user, dto);
            res.add(dto);
        }
        return res;
    }

    @AccessResource("details")
    @GetMapping("/user/details")
    public UserDetailsDTO getUserDetails(@RequestParam("identity") String identity) {
        return userInfoService.getUser(identity);
    }

    @AccessResource("create")
    @PostMapping("/user")
    public UserDetailsDTO createUser(@RequestBody UserCreationDTO userCreationDTO) {
        return userInfoService.createUser(userCreationDTO);
    }

    @AccessResource("details")
    @GetMapping("/social")
    public UserDetailsDTO getUserDetailsBySocialId(@RequestParam("socialType") SocialType socialType,
            @RequestParam("socialId") String socialId) {
        return userInfoService.getUserBySocialId(socialType, socialId);
    }

    @AccessResource("create")
    @PostMapping("/social")
    public UserDetailsDTO createUserBySocialAccount(@RequestBody SocialAccountDTO socialAccountDTO) {
        return userInfoService.createUserBySocialAccount(socialAccountDTO);
    }

    @AccessResource("view")
    @PutMapping("/{userId:\\d+}/social")
    public void updateUserSocialAccount(@PathVariable("userId") Long userId,
            @RequestBody SocialAccountDTO socialAccountDTO) {
        userInfoService.updateUserSocialAccount(userId, socialAccountDTO);
    }
}
