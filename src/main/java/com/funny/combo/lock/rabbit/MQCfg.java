package com.funny.combo.lock.rabbit;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MQCfg {
    private static final Logger logger = LoggerFactory.getLogger(MQCfg.class);

    public static String HOST = "127.0.0.1";
    public static String USER_NAME = "test";
    public static String PASS_WD = "test";
    public static String VIRTUAL_HOST = "lock";

    private static Connection connection;

    public MQCfg(String host, String userName, String passWd, String virtualHost) {
        HOST = host;
        USER_NAME = userName;
        PASS_WD = passWd;
        VIRTUAL_HOST = virtualHost;
    }

    private static final String DEFAULT_EXCHAGE_TYPE = "direct";
    public static final String LOCK_EXCHANGE = "combo-lock-exchange";
    public static final String LOCK_QUEUE = "combo-lock-queue";
    public static final String LOCK_KEY = "combo-lock-key";

    public static synchronized Connection getConnection() {
        if (connection == null) {
            try {
                ConnectionFactory factory = new ConnectionFactory();
                factory.setHost(MQCfg.HOST);
                factory.setUsername(MQCfg.USER_NAME);
                factory.setPassword(MQCfg.PASS_WD);
                factory.setVirtualHost(MQCfg.VIRTUAL_HOST);
                factory.setRequestedHeartbeat(60);
                //自动重连
                factory.setAutomaticRecoveryEnabled(true);
                connection = factory.newConnection();
                Channel channel = connection.createChannel();
                try {
                    channel.exchangeDeclare(LOCK_EXCHANGE, DEFAULT_EXCHAGE_TYPE, true);
                    channel.queueDeclare(LOCK_QUEUE, true, false, false, null);
                    channel.queueBind(LOCK_QUEUE, LOCK_EXCHANGE, LOCK_KEY);
                } catch (Exception e) {
                    logger.error("error when declare lock queue exchange bind");
                }
                channel.close();
            } catch (Exception e) {
                e.printStackTrace();
                logger.error("get connection exception ===========>", e);
            }
        }
        return connection;
    }
}
