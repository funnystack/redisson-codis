package com.funny.combo.lock;

import org.junit.Test;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

public class LockTest {


    @Test
    public void testLock() {
        RedissonClient redissonClient = Redisson.create();

        RLock rLock = redissonClient.getLock("anylock");
        rLock.lock();

        rLock.unlock();

    }


}
