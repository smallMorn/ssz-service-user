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

    private static Map<String, CommonEventHandlerListener> commonEventListenerHandlerMap = new HashMap<>();

    @PostConstruct
    public void init() {
        Map<String, CommonEventHandlerListener> commonEventListenerHandlerInitMap = applicationContext.getBeansOfType(CommonEventHandlerListener.class);
        if (null != commonEventListenerHandlerInitMap) {
            commonEventListenerHandlerInitMap.values().forEach(commonEventListenerHandler -> {
                commonEventListenerHandlerMap.put(commonEventListenerHandler.getTableName(), commonEventListenerHandler);
            });
        }
    }

    /**
     * 获取监听表的handler
     *
     * @param table 表名称
     * @return handler
     */
    public static CommonEventHandlerListener getInstance(String table) {
        if (!commonEventListenerHandlerMap.containsKey(table)) {
            throw new RuntimeException("表：" + table + "没有对应的handler实现");
        }
        return commonEventListenerHandlerMap.get(table);
    }

}
