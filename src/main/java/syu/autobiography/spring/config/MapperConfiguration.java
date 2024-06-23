package syu.autobiography.spring.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import syu.autobiography.spring.dto.UserDTO;
import syu.autobiography.spring.entity.Users;

@Configuration
public class MapperConfiguration {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        //매핑 설정
        modelMapper.typeMap(Users.class, UserDTO.class).addMappings(mapper -> {
            mapper.map(Users::getUserNo, UserDTO::setUserNo);
        });

        return modelMapper;
    }
}
