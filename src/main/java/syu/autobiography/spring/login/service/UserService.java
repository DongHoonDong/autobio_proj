package syu.autobiography.spring.login.service;

import syu.autobiography.spring.dto.UserDTO;
import syu.autobiography.spring.entity.Users;

public interface UserService {
    Users login(UserDTO userDTO);
    void register(UserDTO userDTO) throws Exception;
    void updateUser(Users user);
    void deleteUser(int userNo);
    String findUserId(String userName, String userPhone);
    Users findUserByIdAndPhone(String userId, String userPhone);
    void updatePassword(String userId, String newPassword);
    boolean existsByUserId(String userId);
    boolean existsByUserPhone(String userPhone);
}
