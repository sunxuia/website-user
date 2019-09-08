package net.sunxu.website.user.service.repo;

import java.util.List;
import net.sunxu.website.user.service.entity.UserInfo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserInfoRepo extends CrudRepository<UserInfo, Long> {

    UserInfo findByMailAddress(String mailAddress);

    boolean existsByMailAddress(String mailAddress);

    UserInfo findByName(String name);

    boolean existsByNameIgnoreCase(String name);

    @Query("select name from UserInfo where upper(name) like upper(concat(?1, '!__%')) escape '!'")
    List<String> findNamesOfPrefix(String name);

}
