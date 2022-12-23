package com.ssz.user.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ssz.client.dto.UserDTO;
import com.ssz.client.dto.UserQueryDTO;
import com.ssz.user.entity.User;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author ssz
 * @since 2021-01-11
 */
public interface IUserService extends IService<User> {

    Boolean insert(UserDTO dto);

    Page list(UserQueryDTO queryDTO);
}
