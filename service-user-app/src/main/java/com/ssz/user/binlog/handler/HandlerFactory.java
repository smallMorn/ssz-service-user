package com.ssz.user.binlog.handler;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Component
public class HandlerFactory {

    @Resource
    private ApplicationContext applicationContext;

    private HandlerFactory() {
    }

    private static Map<String, CommonEventHandler> commonEventHandlerMap = new HashMap<>();

    @PostConstruct
    public void init() {
        Map<String, CommonEventHandler> commonEventListenerHandlerInitMap = applicationContext.getBeansOfType(CommonEventHandler.class);
        if (null != commonEventListenerHandlerInitMap) {
            commonEventListenerHandlerInitMap.values().forEach(commonEventHandler -> {
                commonEventHandlerMap.put(commonEventHandler.getTableName(), commonEventHandler);
            });
        }
    }

    /**
     * 获取监听表的handler
     *
     * @param table 表名称
     * @return handler
     */
    public static CommonEventHandler getInstance(String table) {
        if (!commonEventHandlerMap.containsKey(table)) {
            throw new RuntimeException("表：" + table + "没有对应的handler实现");
        }
        return commonEventHandlerMap.get(table);
    }

}
