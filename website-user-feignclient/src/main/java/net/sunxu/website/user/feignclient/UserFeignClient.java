package net.sunxu.website.user.feignclient;


import net.sunxu.website.user.dto.SocialAccountDTO;
import net.sunxu.website.user.dto.SocialType;
import net.sunxu.website.user.dto.UserCreationDTO;
import net.sunxu.website.user.dto.UserDTO;
import net.sunxu.website.user.dto.UserDetailsDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * user-service 的微服务接口
 */
@FeignClient(serviceId = "user-service")
public interface UserFeignClient {

    /**
     * 查看用户信息
     *
     * @param id 用户id
     */
    @RequestMapping("/info/user/{userId}")
    UserDTO getUserInfo(@PathVariable("userId") Long id);

    /**
     * 请求用户的详细信息.
     *
     * @param identity 用户的标识. 可以是用户名/ 邮箱.
     * @return 用户信息
     */
    @RequestMapping("/info/user/details")
    UserDetailsDTO getUserDetails(@RequestParam("identity") String identity);

    /**
     * 创建用户
     *
     * @param userCreationDTO 创建用户的信息
     */
    @RequestMapping(path = "/info/user", method = RequestMethod.POST)
    UserDetailsDTO createUser(@RequestBody UserCreationDTO userCreationDTO);

    /**
     * 通过社交账号获取用户详细信息
     */
    @RequestMapping("/info/social")
    UserDetailsDTO getUserDetailsBySocialId(@RequestParam("socialType") SocialType socialType,
            @RequestParam("socialId") String socialId);

    /**
     * 通过社交账号的信息创建用户
     *
     * @param socialAccountDTO 创建用户社交账号的信息
     */
    @RequestMapping(path = "/info/social", method = RequestMethod.POST)
    UserDetailsDTO createUserBySocialAccount(@RequestBody SocialAccountDTO socialAccountDTO);

    /**
     * 更新用户的社交账号信息
     *
     * @param userId 用户id
     * @param socialAccountDTO 用户的社交账号的信息
     */
    @RequestMapping(path = "/info/{userId}/social", method = RequestMethod.PUT)
    void updateUserSocialAccount(@PathVariable("userId") Long userId, @RequestBody SocialAccountDTO socialAccountDTO);
}
