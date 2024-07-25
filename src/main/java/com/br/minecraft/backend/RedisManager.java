package com.br.minecraft.backend;

import redis.clients.jedis.Jedis;

public class RedisManager {
    private static final String REDIS_HOST = "localhost";
    private static final int REDIS_PORT = 6379;

    private static Jedis jedis = new Jedis(REDIS_HOST, REDIS_PORT);

    public static Jedis getJedis() {
        return jedis;
    }
}
