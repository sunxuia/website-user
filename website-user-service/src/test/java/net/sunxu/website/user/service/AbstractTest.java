package net.sunxu.website.user.service;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.SQLException;
import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import net.sunxu.website.test.dbunit.DbUnitRule;
import net.sunxu.website.test.dbunit.DbUnitRuleFactory;
import net.sunxu.website.test.helputil.authtoken.AuthTokenBuilder;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public abstract class AbstractTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected DbUnitRuleFactory factory;

    @Autowired
    protected DataSource dataSource;

    @Rule
    public DbUnitRule dbUnitRule;

    @PostConstruct
    public void initial() throws SQLException {
        dbUnitRule = factory.builder().connection(dataSource.getConnection()).build();
    }

    protected String getToken(String... roles) {
        var builder = new AuthTokenBuilder();
        builder.id(100L)
                .name("auth-service")
                .exipreSeconds(1000L)
                .issuer("test-issuer");
        for (String role : roles) {
            if ("service".equalsIgnoreCase(role)) {
                builder.service(true);
            }
            builder.addRole(role);
        }
        return builder.build();
    }

    protected <T> T restful(MockHttpServletRequestBuilder builder, Class<T> expectedType)
            throws Exception {
        String json = mockMvc.perform(builder
                .header("Authorization", getToken("service", "auth"))
                .header("Content-Type", "application/json")
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_UTF8))
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readValue(json, expectedType);
    }
}
