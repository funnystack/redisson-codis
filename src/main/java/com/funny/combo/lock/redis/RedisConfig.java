package com.funny.combo.lock.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisConfig {


    private static JedisPool jedisPool;
    private static String ip = "127.0.0.1";
    private static int port = 6379;
    private static int timeout = 1000;
    private static int maxTotal = 3000;
    private static int maxIdle = 200;

    private static JedisPoolConfig initPoolConfig() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        // 控制一个pool最多有多少个状态为idle的jedis实例
        jedisPoolConfig.setMaxTotal(maxTotal);
        // 最大能够保持空闲状态的对象数
        jedisPoolConfig.setMaxIdle(maxIdle);
        // 超时时间
        jedisPoolConfig.setMaxWaitMillis(timeout);
        // 在borrow一个jedis实例时，是否提前进行alidate操作；如果为true，则得到的jedis实例均是可用的；
        jedisPoolConfig.setTestOnBorrow(true);
        // 在还会给pool时，是否提前进行validate操作
        jedisPoolConfig.setTestOnReturn(true);

        return jedisPoolConfig;
    }



    public static Jedis getJedis() {
        if(jedisPool == null){
            JedisPoolConfig jedisPoolConfig = initPoolConfig();
            jedisPool = new JedisPool(jedisPoolConfig, ip, port, timeout);
        }
        return jedisPool.getResource();
    }


    public static void shutdown(){
        if(jedisPool != null){
            jedisPool.destroy();
        }
    }

}
