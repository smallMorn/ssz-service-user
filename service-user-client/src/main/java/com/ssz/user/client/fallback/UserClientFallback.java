package com.ssz.user.client.fallback;

import com.ssz.user.client.api.UserClient;
import com.ssz.user.client.dto.UserQueryDTO;
import com.ssz.common.web.result.ResultInfo;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
public class UserClientFallback implements FallbackFactory<UserClient> {

    @Override
    public UserClient create(Throwable cause) {
        return new UserClient() {
            @Override
            public ResultInfo list(UserQueryDTO queryDTO) {
                return ResultInfo.fail("调用user服务的列表查询失败！");
            }
        };
    }
}
