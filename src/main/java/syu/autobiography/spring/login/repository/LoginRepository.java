package syu.autobiography.spring.login.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import syu.autobiography.spring.entity.Users;

import java.util.Optional;

@Repository
public interface LoginRepository extends JpaRepository<Users, Integer> {
    Optional<Users> findByUserIdAndUserPwd(String userId, String userPwd);
    boolean existsByUserId(String userId);
    boolean existsByUserPhone(@Param("userPhone") String userPhone);
    Optional<Users> findByUserNameAndUserPhone(@Param("userName") String userName, @Param("userPhone") String userPhone);
    Optional<Users> findByUserIdAndUserPhone(@Param("userId") String userId, @Param("userPhone") String userPhone);
    Optional<Users> findByUserId(@Param("userId") String userId);
}
