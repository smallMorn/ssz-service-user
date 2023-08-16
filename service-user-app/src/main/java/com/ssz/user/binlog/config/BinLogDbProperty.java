package com.ssz.user.binlog.config;

import lombok.Data;

@Data
public class BinLogDbProperty {

    /**
     * 数据库链接地址
     */
    private String host;

    /**
     * 数据库链接端口
     */
    private int port;

    /**
     * 数据库链接账号
     */
    private String username;

    /**
     * 数据库链接密码
     */
    private String password;

    /**
     * 数据库名称
     */
    private String schema;

    /**
     * 表名称
     */
    private String table;

}
