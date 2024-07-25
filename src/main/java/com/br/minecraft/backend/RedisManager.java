package com.br.minecraft.backend;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import redis.clients.jedis.Jedis;

import java.io.File;
import java.io.IOException;

public class RedisManager {
    private static Jedis jedis;

    static {
        File configFile = new File("C:\\Users\\Anderson\\Desktop\\Prisma Server\\lobby1\\plugins\\lobby\\config.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);

        String redisHost = config.getString("redis.host", "localhost");
        int redisPort = config.getInt("redis.port", 6379);

        jedis = new Jedis(redisHost, redisPort);
    }

    public static Jedis getJedis() {
        return jedis;
    }
}
