package com.ssz.user.binlog.handler;

import com.alibaba.fastjson.JSON;
import com.ssz.user.binlog.module.BinLogItem;
import com.ssz.user.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class UserEventHandler implements CommonEventHandler {

    private static Map<String, String> mapping = new HashMap<>();

    static {
        mapping.put("id", "id");
        mapping.put("userName", "user_name");
        mapping.put("userSex", "user_sex");
        mapping.put("userAge", "user_age");
        mapping.put("gmtCreated", "gmt_created");
        mapping.put("gmtModified", "gmt_modified");
        mapping.put("deleted", "deleted");
    }

    @Override
    public String getTableName() {
        return "user";
    }

    @Override
    public void insertHandle(BinLogItem binLogItem) {
        log.info("user表 添加数据:{}", JSON.toJSONString(binLogItem));
    }

    @Override
    public void updateHandle(BinLogItem binLogItem) {
        log.info("user表 编辑数据:{}", JSON.toJSONString(binLogItem));
    }

    @Override
    public void deleteHandle(BinLogItem binLogItem) {
        log.info("user表 删除数据:{}", JSON.toJSONString(binLogItem));
        Map<String, Serializable> itemBefore = binLogItem.getBefore();
        User user = new User();
        user.setId((Long) itemBefore.get(mapping.get("id")));
    }
}
