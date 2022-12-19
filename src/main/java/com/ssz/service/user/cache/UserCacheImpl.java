package com.ssz.service.user.cache;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ssz.common.web.cache.BaseCache;
import com.ssz.common.web.cache.CacheKey;
import com.ssz.service.user.entity.User;
import com.ssz.service.user.mapper.UserMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Date 2021/5/17 14:18
 * Author ssz
 */
@Service
@AllArgsConstructor
public class UserCacheImpl extends BaseCache implements UserCache {

    private final UserMapper userMapper;

    @Override
    public List<Long> userList() {
        String key = CacheKey.LIST_USER.key();

        JedisCallback<List<Long>> dbToRedis = () -> {
            List<Long> list = new ArrayList<>();
            for (int i = 1; ; i++) {
                Page<User> page = new Page<>(i, 1000);
                List<Long> idList = userMapper.selectUserIdList(page);
                if (!CollectionUtils.isEmpty(idList)){
                    for (Long id : idList) {
                        jedis.rpush(key, id.toString());
                    }
                }
                list.addAll(idList);
                if (idList.size() < 1000) {
                    break;
                }
            }
            return list;
        };

        JedisCallback<List<Long>> fromRedis = () -> {
            List<String> list = jedis.lrange(key, 0, -1);
            return list.stream().map(p -> Long.valueOf(p)).collect(Collectors.toList());
        };

        Optional<List<Long>> execute = execute(CacheKey.LIST_USER, dbToRedis, fromRedis);
        return execute.orElse(new ArrayList<>());
    }

    @Override
    public User selectById(Long id) {
        String key = CacheKey.KV_USER_DETAIL.key(id);

        JedisCallback<User> dbToRedis = () -> {
            User user = userMapper.selectById(id);
            if (Objects.nonNull(user)) {
                jedis.set(key, JSON.toJSONString(user));
            }
            return user;
        };

        JedisCallback<User> fromRedis = () -> JSON.parseObject(jedis.get(key), User.class);

        Optional<User> execute = execute(CacheKey.KV_USER_DETAIL, id, dbToRedis, fromRedis);
        return execute.orElse(null);
    }

}
