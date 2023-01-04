package com.ssz.user.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ssz.user.client.dto.UserDTO;
import com.ssz.user.client.dto.UserQueryDTO;
import com.ssz.user.entity.User;
import com.ssz.user.mapper.UserMapper;
import com.ssz.user.service.IUserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;


/**
 *  服务实现类
 *
 * @author ssz
 * @since 2021-01-11
 */
@Service
@AllArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    private final UserMapper userMapper;

    @Override
    public Boolean insert(UserDTO dto) {
        User user = new User();
        user.setUserName(dto.getUserName());
        user.setUserSex(dto.getUserSex());
        user.setUserAge(dto.getUserAge());
        return 1 == userMapper.insert(user);
    }

    @Override
    public Page list(UserQueryDTO queryDTO) {
        Page<User> page = new Page<>(queryDTO.getPageCurrent(), queryDTO.getPageSize());
        Page iPage = userMapper.selectList(page, queryDTO);
        return iPage;
    }
}
