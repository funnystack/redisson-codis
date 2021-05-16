/**
 * Copyright (c) 2013-2021 Nikita Koksharov
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.funny.combo.lock.redis;

import com.funny.combo.lock.api.CodisLock;
import com.funny.combo.lock.rabbit.MQCfg;
import com.funny.combo.lock.rabbit.MQReceiver;
import com.funny.combo.lock.rabbit.MQSender;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Consumer;
import redis.clients.jedis.Jedis;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.funny.combo.lock.rabbit.MQCfg.LOCK_QUEUE;


public class ComboLock implements CodisLock {
    private static final String LOCK_PERFIX = "LOCK-";

    private static ThreadPoolExecutor executor = new ThreadPoolExecutor(4, 10, 10, TimeUnit.SECONDS,
            new ArrayBlockingQueue<Runnable>(5000));

    public boolean acquire(String key) {
        boolean lockResult = false;
        try {
            Jedis jedis = RedisConfig.getJedis();
            Long res = jedis.setnx(LOCK_PERFIX + key, Thread.currentThread().getId() + "");
            if (res.equals(1L)) {
                lockResult = true;
                return lockResult;
            }

            //加锁失败的需要监听rabbitmq的消息等待锁。
            Future<Boolean> future = executor.submit(() -> {
                Connection connection = MQCfg.getConnection();
                Channel channel = connection.createChannel();
                channel.basicQos(1);
                Consumer consumer = new MQReceiver.LockConsumer(channel);
                channel.basicConsume(LOCK_QUEUE, consumer);
                return false;
            });
            return future.get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lockResult;
    }


    public boolean release(String key) {
        boolean unlockResult = true;
        try {
            Jedis jedis = RedisConfig.getJedis();
            jedis.del(LOCK_PERFIX + key);
            // 释放锁后，需要发送mq消息通知其他线程获取锁

            MQSender.sendMessage(LOCK_PERFIX + key);

        } catch (Exception e) {
            e.printStackTrace();
            unlockResult = false;
        }
        return unlockResult;
    }

}
