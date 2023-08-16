package com.ssz.user.binlog.handler;

import com.ssz.user.binlog.module.BinLogItem;

public interface CommonEventHandlerListener {

    String getTableName();

    /**
     * Insert handle
     */
    void insertHandle(BinLogItem binLogItem);

    /**
     * Update handle
     */
    void updateHandle(BinLogItem binLogItem);

    /**
     * Delete handle
     */
    void deleteHandle(BinLogItem binLogItem);
}
