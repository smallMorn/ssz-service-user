package com.ssz.user.binlog.module;

import lombok.Data;

/**
 * 存储表的字段信息
 */
@Data
public class ColumnInfo {

    /**
     * 存储的是数据库第几列
     */
    public int position;

    /**
     * 列名
     */
    public String columnName;
    /**
     * 类型
     */
    public String dataType;
    /**
     * 数据库
     */
    public String schema;
    /**
     * 表名
     */
    public String tableName;

    /**
     * 索引类型
     */
    private String index;

    public ColumnInfo(int position, String columnName, String dataType, String schema, String tableName, String index) {
        this.position = position;
        this.columnName = columnName;
        this.dataType = dataType;
        this.schema = schema;
        this.tableName = tableName;
        this.index = index;
    }
}
