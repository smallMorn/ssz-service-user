package com.ssz.user.binlog.handler;

import com.alibaba.fastjson.JSON;
import com.ssz.common.web.util.DateUtil;
import com.ssz.user.binlog.module.BinLogItem;
import com.ssz.user.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class UserEventHandler implements CommonEventHandler {

    private final static Map<String, String> mapping = new HashMap<>();

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
        Map<String, Serializable> itemAfter = binLogItem.getAfter();
        User user = this.assembleUser(itemAfter);
        log.info("组装后的user对象:{}", JSON.toJSONString(user));
        //业务逻辑
    }

    @Override
    public void updateHandle(BinLogItem binLogItem) {
        Map<String, Serializable> itemBefore = binLogItem.getBefore();
        User beforeUser = this.assembleUser(itemBefore);
        log.info("组装后的beforeUser对象:{}", JSON.toJSONString(beforeUser));
        Map<String, Serializable> itemAfter = binLogItem.getAfter();
        User afterUser = this.assembleUser(itemAfter);
        log.info("组装后的afterUser对象:{}", JSON.toJSONString(afterUser));
        //业务逻辑
    }

    @Override
    public void deleteHandle(BinLogItem binLogItem) {
        Map<String, Serializable> itemBefore = binLogItem.getBefore();
        User user = this.assembleUser(itemBefore);
        log.info("组装后的user对象:{}", JSON.toJSONString(user));
        //业务逻辑

    }

    private User assembleUser(Map<String, Serializable> item) {
        User user = new User();
        user.setId((Long) item.get("id"));
        user.setUserName((String) item.get("user_name"));
        user.setUserSex((Integer) item.get("user_sex"));
        user.setUserAge((Integer) item.get("user_age"));
        Date createdDate = (Date) item.get("gmt_created");
        user.setGmtCreated((DateUtil.dateToLocalDateTime(createdDate)));
        Date modifiedDate = (Date) item.get("gmt_modified");
        user.setGmtModified(DateUtil.dateToLocalDateTime(modifiedDate));
        user.setDeleted((Integer) item.get("deleted") == 1);
        return user;
    }
}
