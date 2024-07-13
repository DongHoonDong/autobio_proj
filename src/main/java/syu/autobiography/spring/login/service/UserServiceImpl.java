package syu.autobiography.spring.login.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import syu.autobiography.spring.dto.UserDTO;
import syu.autobiography.spring.entity.Users;
import syu.autobiography.spring.login.repository.LoginRepository;

import java.util.Optional;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final LoginRepository userRepository;


    @Autowired
    public UserServiceImpl(LoginRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Users login(UserDTO userDTO) {
        Optional<Users> userOptional = userRepository.findByUserIdAndUserPwd(userDTO.getUserId(), userDTO.getUserPwd());
        return userOptional.orElse(null);
    }

    @Override
    public void register(UserDTO userDTO) throws Exception {
        if (userRepository.existsByUserId(userDTO.getUserId())) {
            throw new Exception("이미 존재하는 아이디입니다.");
        }
        if (userRepository.existsByUserPhone(userDTO.getUserPhone())) {
            throw new Exception("이미 존재하는 전화번호입니다.");
        }
        Users user = new Users();
        user.setUserId(userDTO.getUserId());
        user.setUserName(userDTO.getUserName());
        user.setUserPwd(userDTO.getUserPwd());
        user.setUserPhone(userDTO.getUserPhone());
        user.setUserBirth(userDTO.getUserBirth());
        user.setUserGender(userDTO.getUserGender());
        user.setUserRole("N");

        userRepository.save(user);
    }

    @Override
    public void updateUser(Users user) {
        userRepository.save(user);
    }

    @Override
    public void deleteUser(int userNo) {
        userRepository.deleteById(userNo);
    }

    @Override
    public String findUserId(String userName, String userPhone) {
        Optional<Users> userOptional = userRepository.findByUserNameAndUserPhone(userName, userPhone);
        return userOptional.map(Users::getUserId).orElse(null);
    }

    @Override
    public Users findUserByIdAndPhone(String userId, String userPhone) {
        return userRepository.findByUserIdAndUserPhone(userId, userPhone).orElse(null);
    }

    @Override
    public void updatePassword(String userId, String newPassword) {
        Users user = userRepository.findByUserId(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setUserPwd(newPassword);
        userRepository.save(user);
    }

    @Override
    public boolean existsByUserId(String userId) {
        return userRepository.existsByUserId(userId);
    }

    @Override
    public boolean existsByUserPhone(String userPhone) {
        return userRepository.existsByUserPhone(userPhone);
    }


    @Override
    public List<Users> getAllUsers() {
        return userRepository.findAll();
    }
}