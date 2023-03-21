package com.ssz.user.client.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ssz.user.client.dto.UserDTO;
import com.ssz.user.client.dto.UserQueryDTO;
import com.ssz.user.client.fallback.UserClientFallback;
import com.ssz.common.web.result.ResultInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "ssz-service-user", path = "/user", contextId = "UserClient", fallbackFactory = UserClientFallback.class)
public interface UserClient {

    @PostMapping(value = "/query/list")
    ResultInfo<Page<UserDTO>> list(@RequestBody UserQueryDTO queryDTO);

    @GetMapping(value = "/selectById/{userId}")
    ResultInfo<UserDTO> selectById(@PathVariable(value = "userId") Long userId);
}
