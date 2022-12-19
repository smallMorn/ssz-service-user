package com.ssz.service.user.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ssz.common.model.dto.UserDTO;
import com.ssz.common.model.dto.UserQueryDTO;
import com.ssz.service.user.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

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
