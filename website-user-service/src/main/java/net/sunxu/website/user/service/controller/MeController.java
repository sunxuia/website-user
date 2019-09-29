package net.sunxu.website.user.service.controller;

import net.sunxu.website.config.feignclient.exception.ServiceException;
import net.sunxu.website.config.security.authentication.SecurityHelpUtils;
import net.sunxu.website.user.dto.UserDTO;
import net.sunxu.website.user.service.service.UserInfoService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/me")
public class MeController {

    @Autowired
    private UserInfoService userInfoService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/info")
    public UserDTO getUserDTO(Authentication authentication) {
        Long id = SecurityHelpUtils.getCurrentUserId();
        if (id == null) {
            throw ServiceException.newException("无法识别的认证");
        }
        var user = userInfoService.getUser(id.toString());
        var res = new UserDTO();
        BeanUtils.copyProperties(user, res);
        return res;
    }
}
