package com.ssz.user.binlog.listener;

import com.alibaba.fastjson.JSON;
import com.github.shyiko.mysql.binlog.BinaryLogClient;
import com.github.shyiko.mysql.binlog.event.*;
import com.ssz.user.binlog.handler.HandlerFactory;
import com.ssz.user.binlog.module.BinLogItem;
import com.ssz.user.binlog.module.ColumnInfo;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class CommonEventListener implements BinaryLogClient.EventListener {

    /**
     * 这个监听的表结构
     */
    private final Map<String, Map<String, ColumnInfo>> schemaTable;

    /**
     * 这个监听这个库tableId和table关系
     */
    private final Map<Long, String> tableIdMapping = new HashMap<>();

    public CommonEventListener(Map<String, Map<String, ColumnInfo>> schemaTable) {
        this.schemaTable = schemaTable;
    }

    @Override
    public void onEvent(Event event) {
        EventType eventType = event.getHeader().getEventType();

        if (eventType == EventType.TABLE_MAP) {
            TableMapEventData tableData = event.getData();
            String table = tableData.getTable();
            long tableId = tableData.getTableId();
            tableIdMapping.put(tableId, table);
            log.info("监听到binlog table信息：{}, tableIdMapping：{}", JSON.toJSONString(event), JSON.toJSONString(tableIdMapping));
        }

        // 只处理添加删除更新三种操作
        if (EventType.isWrite(eventType) || EventType.isUpdate(eventType) || EventType.isDelete(eventType)) {
            handle(event, eventType);
        }

    }

    /**
     * 业务处理模板方法
     *
     * @param event     事件
     * @param eventType 事件类型
     */
    private void handle(Event event, EventType eventType) {
        if (EventType.isWrite(eventType)) {

            EventHeaderV4 header = event.getHeader();
            long nextPosition = header.getNextPosition();

            WriteRowsEventData data = event.getData();
            long tableId = data.getTableId();
            if (!tableIdMapping.containsKey(tableId)) {
                log.warn("tableId:{}没有对应的table,tableIdMapping:{}", tableId, JSON.toJSONString(tableIdMapping));
                return;
            }
            String table = tableIdMapping.get(tableId);
            log.info("监听到binlog table:{} add信息：{}", table, JSON.toJSONString(event));
            for (Serializable[] row : data.getRows()) {
                BinLogItem item = BinLogItem.itemFromInsertOrDeleted(row, schemaTable.get(table), eventType, nextPosition);

                try {
                    log.info("处理binlog table:{} add信息：{}", table, JSON.toJSONString(item));
                    HandlerFactory.getInstance(table).insertHandle(item);
                } catch (Exception e) {
                    log.error("处理binlog table:{} add信息失败：{}", table, JSON.toJSONString(item), e);
                }

            }
        }
        if (EventType.isUpdate(eventType)) {
            UpdateRowsEventData data = event.getData();
            long tableId = data.getTableId();

            EventHeaderV4 header = event.getHeader();
            long nextPosition = header.getNextPosition();
            if (!tableIdMapping.containsKey(tableId)) {
                log.warn("tableId:{}没有对应的table,tableIdMapping:{}", tableId, JSON.toJSONString(tableIdMapping));
                return;
            }
            String table = tableIdMapping.get(tableId);
            log.info("监听到binlog table:{}, update信息：{}", table, JSON.toJSONString(event));

            for (Map.Entry<Serializable[], Serializable[]> row : data.getRows()) {
                BinLogItem item = BinLogItem.itemFromUpdate(row, schemaTable.get(table), eventType, nextPosition);

                try {
                    log.info("处理binlog table:{},update信息：{}", table, JSON.toJSONString(item));
                    HandlerFactory.getInstance(table).updateHandle(item);
                } catch (Exception e) {
                    log.error("处理binlog table:{},update信息失败：{}", table, JSON.toJSONString(item), e);
                }

            }

        }
        if (EventType.isDelete(eventType)) {
            DeleteRowsEventData data = event.getData();
            long tableId = data.getTableId();

            EventHeaderV4 header = event.getHeader();
            long nextPosition = header.getNextPosition();

            if (!tableIdMapping.containsKey(tableId)) {
                log.warn("tableId:{}没有对应的table,tableIdMapping:{}", tableId, JSON.toJSONString(tableIdMapping));
                return;
            }
            String table = tableIdMapping.get(tableId);
            log.info("监听到binlog table:{} delete信息：{}", table, JSON.toJSONString(event));

            for (Serializable[] row : data.getRows()) {
                BinLogItem item = BinLogItem.itemFromInsertOrDeleted(row, schemaTable.get(table), eventType, nextPosition);
                try {
                    log.info("处理binlog table:{} delete信息：{}", table, JSON.toJSONString(item));
                    HandlerFactory.getInstance(table).deleteHandle(item);
                } catch (Exception e) {
                    log.error("处理binlog table:{} delete信息失败：{}", table, JSON.toJSONString(item), e);
                }

            }
        }
    }

}
