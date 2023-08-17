package com.ssz.user.binlog.util;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.ssz.user.binlog.config.BinLogDbProperty;
import com.ssz.user.binlog.module.ColumnInfo;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class BinLogUtil {

    private static final String DRIVER_ADDRESS = "jdbc:mysql://%s:%s/%s?serverTimezone=Asia/Shanghai";

    /**
     * 获取columns集合
     */
    public static Map<String, ColumnInfo> getColMap(BinLogDbProperty conf, String table) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            // 保存当前注册的表的colum信息
            Connection connection = DriverManager.getConnection(String.format(DRIVER_ADDRESS, conf.getHost(), conf.getPort(), conf.getSchema()), conf.getUsername(), conf.getPassword());
            // 执行sql
            String preSql = "SELECT TABLE_SCHEMA, TABLE_NAME, COLUMN_NAME, DATA_TYPE, ORDINAL_POSITION, COLUMN_KEY FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = ? and TABLE_NAME = ?";
            PreparedStatement ps = connection.prepareStatement(preSql);
            ps.setString(1, conf.getSchema());
            ps.setString(2, table);
            ResultSet rs = ps.executeQuery();
            Map<String, ColumnInfo> map = new HashMap<>(rs.getRow());
            while (rs.next()) {
                String schema = rs.getString("TABLE_SCHEMA");
                String tableName = rs.getString("TABLE_NAME");
                String columnName = rs.getString("COLUMN_NAME");
                String index = rs.getString("COLUMN_KEY");
                //表明该字段是第几列 从1开始
                int position = rs.getInt("ORDINAL_POSITION");
                String dataType = rs.getString("DATA_TYPE");
                if (columnName != null && position >= 1) {
                    map.put(columnName, new ColumnInfo(position - 1, columnName, dataType, schema, tableName, index));
                }
            }
            ps.close();
            rs.close();
            return map;
        } catch (Exception e) {
            log.error("load db conf error, db_table={}:{} ", conf.getSchema(), table, e);
        }
        return null;
    }

    /**
     * 将逗号拼接字符串转List 并去除首尾空格
     *
     * @param str
     * @return
     */
    public static List<String> getListByStr(String str) {
        if (Strings.isNullOrEmpty(str)) {
            return Lists.newArrayList();
        }
        return Arrays.asList(str.split(",")).stream().map(String::trim).collect(Collectors.toList());
    }

}
