package com.funny.combo.lock.rabbit;

import com.rabbitmq.client.*;

import java.io.IOException;

import static com.funny.combo.lock.rabbit.MQCfg.LOCK_QUEUE;

/**
 * @author fangli
 * @version 1.0
 * @date 2021/3/15
 */
public class MQReceiver {



    public void start() throws Exception {
        Connection connection = MQCfg.getConnection();
        Channel channel = connection.createChannel();
        channel.basicQos(10);
        Consumer consumer = new LockConsumer(channel);
        channel.basicConsume(LOCK_QUEUE, consumer);
    }

    public static class LockConsumer extends DefaultConsumer {
        /**
         * Constructs a new instance and records its association to the passed-in channel.
         *
         * @param channel the channel to which this consumer is attached
         */
        public LockConsumer(Channel channel) {
            super(channel);
        }

        @Override
        public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
            System.out.println("接收消息:" + new String(body));
            //消息确认
            getChannel().basicAck(envelope.getDeliveryTag(), false);
            // 消息重会队列
//            getChannel().basicNack(envelope.getDeliveryTag(), false, true);
        }

    }
}
