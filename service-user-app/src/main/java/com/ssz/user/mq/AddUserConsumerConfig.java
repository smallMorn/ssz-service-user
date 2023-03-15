package com.ssz.user.mq;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "rocketmq.producer")
@Slf4j
@RefreshScope
public class AddUserConsumerConfig {

    private String nameServerAddress;

    @Autowired
    private AddUserListenerProcessor addUserListenerProcessor;

    @Resource
    private Environment environment;

    @ConditionalOnProperty(prefix = "rocketmq.producer", name = "autoIsOnOff", havingValue = "autoOn")
    public void addUserConsumer() throws MQClientException {
        log.info("rocketmq consumer addUserConsumer 正在创建---------------------------------------");
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("ssz-user-insert-group");
        consumer.setNamesrvAddr(nameServerAddress);
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
        consumer.subscribe("ssz-user-topic", "user");
        // 设置监听
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgList, ConsumeConcurrentlyContext context) {
                for (MessageExt messageExt : msgList) {
                    String topic = messageExt.getTopic();
                    String tags = messageExt.getTags();
                    String body = new String(messageExt.getBody());
                    log.info("addUserConsumer MQ监听到消息topic:{}, tag:{}, body:{}", topic, tags, body);
                    addUserListenerProcessor.insertUser(body);
                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });

        consumer.start();
        log.info("rocketmq consumer addUserConsumer 创建成功 nameServerAddress:{}",nameServerAddress);
    }

}
