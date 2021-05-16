package com.funny.combo.lock.rabbit;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.funny.combo.lock.rabbit.MQCfg.LOCK_EXCHANGE;
import static com.funny.combo.lock.rabbit.MQCfg.LOCK_KEY;

/**
 *
 */
public class MQSender {
    private static final Logger log = LoggerFactory.getLogger(MQSender.class);

    /**
     * MQ发送消息的方法，只需要在相应工程下引入 mqCfg 配置 直接调用此方法即可发送消息
     *
     * @param msg
     * @return
     */
    public static boolean sendMessage(String msg) {
        log.info("sendMessage start,msg=" + msg);
        boolean sendResult = false;
        if (StringUtils.isNotBlank(msg)) {
            try {
                Connection connection = MQCfg.getConnection();
                Channel channel = connection.createChannel();
                channel.basicPublish(LOCK_EXCHANGE, LOCK_KEY, true, null, msg.getBytes("utf-8"));
                sendResult = true;
                channel.close();
                connection.close();
            } catch (Exception e) {
                log.error("send message exception, msg = " + msg, e);
            }
        }
        log.info("sendMessage end,msg=" + msg);
        return sendResult;
    }


}
