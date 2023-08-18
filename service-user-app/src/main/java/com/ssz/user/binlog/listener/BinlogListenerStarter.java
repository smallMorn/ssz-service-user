package com.ssz.user.binlog.listener;

import com.alibaba.fastjson.JSON;
import com.github.shyiko.mysql.binlog.BinaryLogClient;
import com.ssz.user.binlog.config.BinLogDbPropertys;
import com.ssz.user.binlog.module.ColumnInfo;
import com.ssz.user.binlog.util.BinLogUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 注册一个监听器 项目启动之后 监听数据库的变化
 */
@Component
@Slf4j
public class BinlogListenerStarter implements CommandLineRunner {

    @Resource
    private BinLogDbPropertys binLogDbPropertys;

    @Override
    public void run(String... args) {

        log.info("监听数据库配置信息：{}", JSON.toJSONString(binLogDbPropertys));
        //每一个数据库用一个线程处理
        ExecutorService executorService = Executors.newFixedThreadPool(binLogDbPropertys.getDatasourceList().size());

        /**
         * binaryLogClient.connect 会马上阻塞，这里要多线程处理下
         */
        binLogDbPropertys.getDatasourceList().stream().forEach(binLogDbProperty -> {
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    // 获取table集合
                    List<String> tableList = BinLogUtil.getListByStr(binLogDbProperty.getTable());
                    if (CollectionUtils.isEmpty(tableList)) {
                        return;
                    }
                    //表结构 表的名称做key Map里面是列的名称做key value是列的信息
                    Map<String, Map<String, ColumnInfo>> schemaTable = new HashMap<>();
                    tableList.stream().forEach(table ->
                            schemaTable.put(table, BinLogUtil.getColMap(binLogDbProperty, table)));
                    // 注册监听
                    log.info("开始 注册监听信息，注册DB：{}，注册表： {}, 结构信息：{}", binLogDbProperty.getSchema(), tableList, JSON.toJSONString(schemaTable));
                    try {
                        Random random = new Random();
                        int serverId = random.nextInt(Integer.MAX_VALUE);
                        log.info("serviceId:{}", serverId);
                        BinaryLogClient binaryLogClient = new BinaryLogClient(
                                binLogDbProperty.getHost(),
                                binLogDbProperty.getPort(),
                                binLogDbProperty.getSchema(),
                                binLogDbProperty.getUsername(),
                                binLogDbProperty.getPassword());
                        binaryLogClient.setServerId(serverId);
                        binaryLogClient.registerEventListener(new CommonEventListener(schemaTable, binLogDbProperty));
                        log.info("成功 注册监听信息，注册DB：{}，注册表： {}", binLogDbProperty.getSchema(), tableList);
                        binaryLogClient.connect();

                    } catch (Exception e) {
                        log.error("BinLog监听异常", e);
                    }
                }
            });

        });

    }
}
