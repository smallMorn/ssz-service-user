package com.ssz.service.user.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ssz.common.model.dto.UserDTO;
import com.ssz.common.model.dto.UserQueryDTO;
import com.ssz.common.model.result.ResultInfo;
import com.ssz.service.user.cache.UserCache;
import com.ssz.service.user.entity.User;
import com.ssz.service.user.service.IUserService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@AllArgsConstructor
@RestController
@RequestMapping(value = "/user")
public class UserController {

    private final IUserService userService;

    private final UserCache userCache;

    @PostMapping("/insert")
    public ResultInfo insert(@RequestBody UserDTO dto) {
        userService.insert(dto);
        return ResultInfo.success();
    }

    @PostMapping("/list")
    public ResultInfo list(@RequestBody UserQueryDTO queryDTO) {
        Page page = userService.list(queryDTO);
        return ResultInfo.success(page);
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
        User user = userCache.selectById(id);
        return ResultInfo.success(user);
    }
}
