package com.ssz.user.cache;


import com.ssz.user.entity.User;

import java.util.List;

/**
 * Date 2021/5/17 14:18
 * Author ssz
 */
public interface UserCache {

    List<Long> userList();

    User selectById(Long id);
}
