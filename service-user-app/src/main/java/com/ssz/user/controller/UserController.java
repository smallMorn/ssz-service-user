package com.ssz.user.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ssz.user.client.api.UserClient;
import com.ssz.user.client.dto.UserDTO;
import com.ssz.user.client.dto.UserQueryDTO;
import com.ssz.common.web.result.ResultInfo;
import com.ssz.user.cache.UserCache;
import com.ssz.user.entity.User;
import com.ssz.user.service.IUserService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@AllArgsConstructor
@RestController
@RequestMapping(value = "/user")
public class UserController implements UserClient {

    private final IUserService userService;

    private final UserCache userCache;

    @PostMapping("/insert")
    public ResultInfo insert(@RequestBody UserDTO dto) {
        userService.insert(dto);
        return ResultInfo.success();
    }

    @PostMapping("/query/list")
    public ResultInfo<Page<UserDTO>> list(@RequestBody UserQueryDTO queryDTO) {
        return ResultInfo.success(userService.list(queryDTO));
    }

    @GetMapping("/cache/list")
    public ResultInfo cacheList() {
        List<User> userList = new ArrayList<>();
        List<Long> list = userCache.userList();
        for (Long id : list) {
            User user = userCache.selectById(id);
            if (Objects.nonNull(user)) {
                userList.add(user);
            }
        }
        return ResultInfo.success(userList);
    }

    @GetMapping("/selectById/{id}")
    public ResultInfo selectById(@PathVariable Long id) {
        return ResultInfo.success(userCache.selectById(id));
    }
}
