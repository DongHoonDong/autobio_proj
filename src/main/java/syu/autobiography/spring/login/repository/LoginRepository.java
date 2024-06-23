package syu.autobiography.spring.login.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import syu.autobiography.spring.entity.Users;

import java.util.Optional;

public interface LoginRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByUserIdAndUserPwd(String userId, String userPwd);
    boolean existsByUserId(String userId);

}