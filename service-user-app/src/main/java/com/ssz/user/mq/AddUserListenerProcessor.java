package com.ssz.user.mq;

import com.ssz.user.client.dto.UserDTO;
import com.ssz.user.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
@Slf4j
public class AddUserListenerProcessor {

    @Resource
    private IUserService userService;

    public void insertUser(String userName) {
        UserDTO userDTO = new UserDTO();
        userDTO.setUserName(userName);
        userDTO.setUserAge(11);
        userDTO.setUserSex(1);
        userService.insert(userDTO);
    }

}
