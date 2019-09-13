package net.sunxu.website.user.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import net.sunxu.website.config.feignclient.exception.InvalidException;
import net.sunxu.website.test.dbunit.annotation.DbUnitClearTable;
import net.sunxu.website.test.helputil.assertion.AssertHelpUtils;
import net.sunxu.website.user.dto.SocialAccountDTO;
import net.sunxu.website.user.dto.SocialType;
import net.sunxu.website.user.dto.UserCreationDTO;
import net.sunxu.website.user.dto.UserDTO;
import net.sunxu.website.user.dto.UserDetailsDTO;
import net.sunxu.website.user.dto.UserState;
import net.sunxu.website.user.service.config.UserManageProperties;
import net.sunxu.website.user.service.repo.UserInfoRepo;
import net.sunxu.website.user.service.repo.UserSocialInfoRepo;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

public class InfoControllerTest extends AbstractTest {

    @Autowired
    private UserManageProperties properties;

    @Autowired
    private UserInfoRepo userInfoRepo;

    @Autowired
    private UserSocialInfoRepo userSocialInfoRepo;

    @DbUnitClearTable({"user_info", "user_info_role_names"})
    @Test
    public void testCreateUser() throws Exception {
        var res = createUser();

        assertNotNull(res.getId());
        assertEquals("test-name", res.getName());
        assertEquals(properties.getDefaultAvatarUrl(), res.getAvatarUrl());
        assertEquals(UserState.NORMAL, res.getUserState());
        assertNotNull(res.getPassword());
        assertEquals("someone@somewhere.com", res.getMailAddress());
        AssertHelpUtils.assertCollectionEquals(properties.getDefaultRoles(), res.getRoles());

        var dbData = userInfoRepo.findById(res.getId()).orElse(null);
        assertNotNull(dbData);
        assertEquals(res.getId(), dbData.getId());
        assertEquals("test-name", dbData.getName());
        assertEquals(properties.getDefaultAvatarUrl(), dbData.getAvatarUrl());
        assertEquals(UserState.NORMAL, dbData.getUserState());
        assertEquals(res.getPassword(), dbData.getPassword());
        assertEquals("someone@somewhere.com", dbData.getMailAddress());
        AssertHelpUtils.assertCollectionEquals(properties.getDefaultRoles(), dbData.getRoleNames());
    }

    private UserDetailsDTO createUser() throws Exception {
        UserCreationDTO dto = new UserCreationDTO();
        dto.setUserName("test-name");
        dto.setPassword("password");
        dto.setMailAddress("someone@somewhere.com");
        return restful(post("/info/user")
                        .content(objectMapper.writeValueAsBytes(dto)),
                UserDetailsDTO.class);
    }

    @DbUnitClearTable({"user_info", "user_info_role_names"})
    @Test
    public void testGetuserInfo() throws Exception {
        var user = createUser();
        UserDTO res = restful(get("/info/user/" + user.getId()), UserDTO.class);

        assertEquals(user.getId(), res.getId());
        assertEquals(user.getName(), res.getName());
        assertEquals(user.getAvatarUrl(), res.getAvatarUrl());
        assertEquals(user.getMailAddress(), res.getMailAddress());
        AssertHelpUtils.assertCollectionEquals(user.getRoles(), res.getRoles());
    }

    @DbUnitClearTable({"user_info", "user_info_role_names"})
    @Test
    public void testGetUserDetails() throws Exception {
        var user = createUser();
        UserDetailsDTO res = restful(get("/info/user/details")
                .param("identity", user.getName()), UserDetailsDTO.class);
        assertUserDetailsDTOEqual(user, res);

        res = restful(get("/info/user/details")
                .param("identity", user.getMailAddress()), UserDetailsDTO.class);
        assertUserDetailsDTOEqual(user, res);
    }

    private void assertUserDetailsDTOEqual(UserDetailsDTO expected, UserDetailsDTO actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getAvatarUrl(), actual.getAvatarUrl());
        assertEquals(expected.getMailAddress(), actual.getMailAddress());
        assertEquals(expected.getPassword(), actual.getPassword());
        AssertHelpUtils.assertCollectionEquals(expected.getRoles(), actual.getRoles());
    }

    @DbUnitClearTable({"user_info", "user_info_role_names", "user_social_info"})
    @Test
    public void testCreateUserBySocialAccount() throws Exception {
        var res = createUserBySocialAccount("100", "test-name");

        assertNotNull(res.getId());
        assertEquals("test-name", res.getName());
        assertEquals("http://test-avatar", res.getAvatarUrl());
        assertEquals(UserState.NORMAL, res.getUserState());
        assertNull(res.getPassword());
        assertNull(res.getMailAddress());
        AssertHelpUtils.assertCollectionEquals(properties.getDefaultRoles(), res.getRoles());

        var dbData = userInfoRepo.findById(res.getId()).orElse(null);
        assertNotNull(dbData);
        assertEquals(res.getId(), dbData.getId());
        assertEquals("test-name", dbData.getName());
        assertEquals("http://test-avatar", dbData.getAvatarUrl());
        assertEquals(UserState.NORMAL, dbData.getUserState());
        assertNull(dbData.getPassword());
        assertNull(dbData.getMailAddress());
        AssertHelpUtils.assertCollectionEquals(properties.getDefaultRoles(), dbData.getRoleNames());

        var socialDbData = userSocialInfoRepo.findBySocialTypeAndSocialId(SocialType.GITHUB, "100");
        assertNotNull(dbData);
        assertEquals(res.getId(), socialDbData.getUserId());
        assertEquals(SocialType.GITHUB, socialDbData.getSocialType());
        assertEquals("100", socialDbData.getSocialId());
        assertEquals("test-name", socialDbData.getSocialName());
        assertEquals("http://test-avatar", socialDbData.getAvatarUrl());
        assertEquals("http://test-profile", socialDbData.getProfileUrl());

        res = createUserBySocialAccount("101", "test-name");
        Assert.assertEquals("test-name_2", res.getName());

        res = createUserBySocialAccount("102", "test-name");
        Assert.assertEquals("test-name_3", res.getName());
    }

    private UserDetailsDTO createUserBySocialAccount(String socialId, String socialName) throws Exception {
        SocialAccountDTO dto = new SocialAccountDTO();
        dto.setAvatarUrl("http://test-avatar");
        dto.setProfileUrl("http://test-profile");
        dto.setSocialId(socialId);
        dto.setSocialType(SocialType.GITHUB);
        dto.setSocialName(socialName);

        return restful(post("/info/social")
                        .content(objectMapper.writeValueAsBytes(dto)),
                UserDetailsDTO.class);
    }

    @DbUnitClearTable({"user_info", "user_info_role_names", "user_social_info"})
    @Test
    public void testGetUserDetailsBySocialId() throws Exception {
        final String testSocialId = "1000";
        final String testName = "testGetUserDetailsBySocialId";
        var user = createUserBySocialAccount(testSocialId, testName);

        var res = restful((get("/info/social").param("socialType", "GITHUB")
                .param("socialId", testSocialId)), UserDetailsDTO.class);
        assertUserDetailsDTOEqual(user, res);
    }

    @DbUnitClearTable({"user_info", "user_info_role_names", "user_social_info"})
    @Test
    public void testUpdateUserSocialAccount() throws Exception {
        var user = createUser();
        SocialAccountDTO dto = new SocialAccountDTO();
        dto.setAvatarUrl("http://test-avatar");
        dto.setProfileUrl("http://test-profile");
        dto.setSocialId("1000");
        dto.setSocialType(SocialType.GITHUB);
        dto.setSocialName("test-social-name");

        performUpdateUserSocialAccount(dto, user.getId());

        var dbData = userSocialInfoRepo.findBySocialTypeAndSocialId(SocialType.GITHUB, "1000");
        assertNotNull(dbData);
        assertEquals(user.getId(), dbData.getUserId());
        assertEquals(SocialType.GITHUB, dbData.getSocialType());
        assertEquals("1000", dbData.getSocialId());
        assertEquals("test-social-name", dbData.getSocialName());
        assertEquals("http://test-avatar", dbData.getAvatarUrl());
        assertEquals("http://test-profile", dbData.getProfileUrl());

        dto.setAvatarUrl("http://test-avatar-2");
        dto.setSocialName("test-social-name-2");

        performUpdateUserSocialAccount(dto, user.getId());

        dbData = userSocialInfoRepo.findBySocialTypeAndSocialId(SocialType.GITHUB, "1000");
        assertEquals("http://test-avatar-2", dbData.getAvatarUrl());
        assertEquals("test-social-name-2", dbData.getSocialName());
    }

    private void performUpdateUserSocialAccount(SocialAccountDTO dto, Long userId) throws Exception {
        mockMvc.perform(put("/info/" + userId + "/social")
                .header("Authorization", getToken("service", "auth"))
                .header("Content-Type", "application/json")
                .content(objectMapper.writeValueAsBytes(dto))
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());
    }

    @DbUnitClearTable({"user_info", "user_info_role_names", "user_social_info"})
    @Test
    public void testUpdateUserSocialAccountAlreadyBinded() throws Exception {
        createUserBySocialAccount("1000", "test-social-name");
        var user = createUser();
        SocialAccountDTO dto = new SocialAccountDTO();
        dto.setSocialId("1000");
        dto.setSocialType(SocialType.GITHUB);
        dto.setSocialName("test-social-name");

        boolean exceptionThrown = false;
        boolean throwBizValidationException = true;
        try {
            performUpdateUserSocialAccount(dto, user.getId());
        } catch (Exception err) {
            exceptionThrown = true;
            Throwable e = err;
            while (e != null) {
                if (err instanceof InvalidException) {
                    throwBizValidationException = true;
                    break;
                }
                e = e.getCause();
            }
        }
        Assert.assertTrue(exceptionThrown);
        Assert.assertTrue(throwBizValidationException);
    }
}
