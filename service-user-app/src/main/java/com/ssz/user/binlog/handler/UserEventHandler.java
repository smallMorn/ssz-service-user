package com.ssz.user.binlog.handler;

import com.alibaba.fastjson.JSON;
import com.ssz.user.binlog.module.BinLogItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserEventHandler implements CommonEventHandler {

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
    }
}
