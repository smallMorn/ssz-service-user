package com.ssz.user.binlog.handler;

import com.ssz.user.binlog.module.BinLogItem;
import org.springframework.stereotype.Service;

@Service
public class UserEventHandler implements CommonEventHandler {

    @Override
    public String getTableName() {
        return "user";
    }

    @Override
    public void insertHandle(BinLogItem binLogItem) {

    }

    @Override
    public void updateHandle(BinLogItem binLogItem) {

    }

    @Override
    public void deleteHandle(BinLogItem binLogItem) {

    }
}
