package syu.autobiography.spring.login.service;

import syu.autobiography.spring.dto.UserDTO;
import syu.autobiography.spring.entity.Users;

public interface UserService {
    Users login(UserDTO userDTO);
    void register(UserDTO userDTO) throws Exception;
    void updateUser(Users user);
    void deleteUser(int userNo);

}
