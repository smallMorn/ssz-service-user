package com.ssz.user.binlog.module;

import com.github.shyiko.mysql.binlog.event.EventType;
import com.google.common.collect.Maps;
import com.ssz.user.binlog.config.BinLogDbProperty;
import com.ssz.user.binlog.util.BinLogUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.Map;

@Data
@Slf4j
public class BinLogItem {

    private String schema;
    private String table;
    private EventType eventType;
    private Long serverId = null;
    private Long positionId = null;
    //存储字段-之前的值 key-表列的名称
    private Map<String, Serializable> before = null;
    //存储字段-之后的值 key-表列的名称
    private Map<String, Serializable> after = null;
    // 存储字段--类型
    private Map<String, ColumnInfo> colums = null;

    /**
     * 新增或者删除操作数据格式化
     */
    public static BinLogItem itemFromInsertOrDeleted(Serializable[] row, Map<String, ColumnInfo> columMap, EventType eventType, long nextPosition) {
        if (null == row || null == columMap) {
            return null;
        }
        // 初始化Item
        BinLogItem item = new BinLogItem();
        item.eventType = eventType;
        item.colums = columMap;
        item.before = Maps.newHashMap();
        item.after = Maps.newHashMap();

        item.positionId = nextPosition;

        Map<String, Serializable> beOrAf = Maps.newHashMap();

        columMap.entrySet().forEach(entry -> {
            String key = entry.getKey();
            ColumnInfo tableColum = entry.getValue();
            beOrAf.put(key, row[tableColum.position - 1]);
        });

        // 写操作放after，删操作放before
        if (EventType.isWrite(eventType)) {
            item.after = beOrAf;
        }
        if (EventType.isDelete(eventType)) {
            item.before = beOrAf;
        }

        return item;
    }

    /**
     * 更新操作数据格式化
     */
    public static BinLogItem itemFromUpdate(Map.Entry<Serializable[], Serializable[]> mapEntry, Map<String, ColumnInfo> columMap, EventType eventType, long nextPosition) {
        if (null == mapEntry || null == columMap) {
            return null;
        }
        // 初始化Item
        BinLogItem item = new BinLogItem();
        item.eventType = eventType;
        item.colums = columMap;
        item.before = Maps.newHashMap();
        item.after = Maps.newHashMap();
        item.positionId = nextPosition;
        Map<String, Serializable> be = Maps.newHashMap();
        Map<String, Serializable> af = Maps.newHashMap();

        columMap.entrySet().forEach(entry -> {
            String key = entry.getKey();
            ColumnInfo tableColum = entry.getValue();
            be.put(key, mapEntry.getKey()[tableColum.position - 1]);
            af.put(key, mapEntry.getValue()[tableColum.position - 1]);
        });

        item.before = be;
        item.after = af;
        return item;
    }
}
