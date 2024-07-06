package syu.autobiography.spring.login.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import syu.autobiography.spring.entity.Users;

import java.util.Optional;

@Repository
public interface LoginRepository extends JpaRepository<Users, Integer> {
    Optional<Users> findByUserIdAndUserPwd(String userId, String userPwd);
    boolean existsByUserId(String userId);
}
