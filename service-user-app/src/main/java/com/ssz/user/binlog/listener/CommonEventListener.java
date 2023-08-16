package com.ssz.user.binlog.listener;

import com.alibaba.fastjson.JSON;
import com.github.shyiko.mysql.binlog.BinaryLogClient;
import com.github.shyiko.mysql.binlog.event.*;
import com.ssz.user.binlog.config.BinLogDbProperty;
import com.ssz.user.binlog.handler.HandlerFactory;
import com.ssz.user.binlog.module.BinLogItem;
import com.ssz.user.binlog.module.ColumnInfo;
import com.ssz.user.binlog.util.BinLogUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class CommonEventListener implements BinaryLogClient.EventListener {

    /**
     * 这个监听的表结构
     */
    private Map<String, Map<String, ColumnInfo>> schemaTable;

    /**
     * 这个监听的数据库信息
     */
    private BinLogDbProperty binLogDbProperty;

    /**
     * 这个监听监听的表
     */
    private List<String> tables;

    protected String lock = "binlog:idempotent:%s";


    /**
     * 这个监听这个库tableId和table关系
     */
    private Map<Long, String> tableIdMapping = new ConcurrentHashMap();

    public CommonEventListener(Map<String, Map<String, ColumnInfo>> schemaTable, BinLogDbProperty binLogDbProperty) {
        this.schemaTable = schemaTable;
        this.binLogDbProperty = binLogDbProperty;
        this.tables = BinLogUtil.getListByStr(binLogDbProperty.getTable());
    }

    @Override
    public void onEvent(Event event) {

        EventType eventType = event.getHeader().getEventType();

        if (eventType == EventType.TABLE_MAP) {

            TableMapEventData tableData = event.getData();
            String table = tableData.getTable();
            long tableId = tableData.getTableId();
            if (!tables.contains(table)) {
                return;
            }
            tableIdMapping.put(tableId, table);
            log.info("监听到binlog table信息：{}", JSON.toJSONString(event));
        }

        // 只处理添加删除更新三种操作
        if (EventType.isWrite(eventType) || EventType.isUpdate(eventType) || EventType.isDelete(eventType)) {

            handle(event, eventType);

        }

    }

    /**
     * 业务处理模板方法
     *
     * @param event
     * @param eventType
     */
    private void handle(Event event, EventType eventType) {
        if (EventType.isWrite(eventType)) {

            EventHeaderV4 header = event.getHeader();
            long nextPosition = header.getNextPosition();

            WriteRowsEventData data = event.getData();
            long tableId = data.getTableId();
            if (!tableIdMapping.containsKey(tableId)) {
                return;
            }

            log.info("监听到binlog add信息：{}", JSON.toJSONString(event));
            String table = tableIdMapping.get(tableId);
            for (Serializable[] row : data.getRows()) {
                BinLogItem item = BinLogItem.itemFromInsertOrDeleted(row, schemaTable.get(table), eventType, nextPosition);

                try {
                    log.info("处理binlog add信息：{}", JSON.toJSONString(item));
                    HandlerFactory.getInstance(table).insertHandle(item);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    log.error("处理binlog add信息失败：{}", JSON.toJSONString(item));
                }

            }
        }
        if (EventType.isUpdate(eventType)) {
            UpdateRowsEventData data = event.getData();
            long tableId = data.getTableId();

            EventHeaderV4 header = event.getHeader();
            long nextPosition = header.getNextPosition();

            if (!tableIdMapping.containsKey(tableId)) {
                return;
            }

            log.info("监听到binlog update信息：{}", JSON.toJSONString(event));
            String table = tableIdMapping.get(tableId);
            for (Map.Entry<Serializable[], Serializable[]> row : data.getRows()) {
                BinLogItem item = BinLogItem.itemFromUpdate(row, schemaTable.get(table), eventType, nextPosition);

                try {
                    log.info("处理binlog update信息：{}", JSON.toJSONString(item));
                    HandlerFactory.getInstance(table).updateHandle(item);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    log.error("处理binlog update信息失败：{}", JSON.toJSONString(item));
                }

            }

        }
        if (EventType.isDelete(eventType)) {
            DeleteRowsEventData data = event.getData();
            long tableId = data.getTableId();

            EventHeaderV4 header = event.getHeader();
            long nextPosition = header.getNextPosition();

            if (!tableIdMapping.containsKey(tableId)) {
                return;
            }

            log.info("监听到binlog delete信息：{}", JSON.toJSONString(event));
            String table = tableIdMapping.get(tableId);
            for (Serializable[] row : data.getRows()) {
                BinLogItem item = BinLogItem.itemFromInsertOrDeleted(row, schemaTable.get(table), eventType, nextPosition);
                try {
                    log.info("处理binlog delete信息：{}", JSON.toJSONString(item));
                    HandlerFactory.getInstance(table).deleteHandle(item);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    log.error("处理binlog delete信息失败：{}", JSON.toJSONString(item));
                }

            }
        }
    }

}
