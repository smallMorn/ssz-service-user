package com.ssz.user.mq;

import com.ssz.user.client.dto.UserDTO;
import com.ssz.user.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import redis.clients.jedis.commands.JedisCommands;
import redis.clients.jedis.params.SetParams;

import javax.annotation.Resource;

@Service
@Slf4j
public class AddUserListenerProcessor {

    @Resource
    private IUserService userService;

    @Resource
    protected JedisCommands jedis;

    public void insertUser(String userName) {
        SetParams setParams = new SetParams();
        setParams.nx();
        String result = this.jedis.set(userName, "lockKey", setParams);
        if ("OK".equals(result)) {
            UserDTO userDTO = new UserDTO();
            userDTO.setUserName(userName);
            userDTO.setUserAge(11);
            userDTO.setUserSex(1);
            userService.insert(userDTO);
            jedis.del(userName);
        }
    }

}
