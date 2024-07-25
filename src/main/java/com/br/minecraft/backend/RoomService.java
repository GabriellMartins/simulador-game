package com.br.minecraft.backend;

import com.br.minecraft.room.backend.RoomData;
import com.google.gson.Gson;
import redis.clients.jedis.Jedis;

/**
 * Serviço para manipulação dos dados das salas no Redis.
 */
public class RoomService {
    private static final Gson gson = new Gson();

    /**
     * Salva os dados de uma sala no Redis.
     *
     * @param roomId    Identificador da sala
     * @param roomData  Dados da sala
     */
    public static void saveRoomData(int roomId, RoomData roomData) {
        Jedis jedis = RedisManager.getJedis();
        String roomDataJson = gson.toJson(roomData);
        jedis.set("room:" + roomId, roomDataJson);
    }

    /**
     * Obtém os dados de uma sala do Redis.
     *
     * @param roomId Identificador da sala
     * @return Dados da sala
     */
    public static RoomData getRoomData(int roomId) {
        Jedis jedis = RedisManager.getJedis();
        String roomDataJson = jedis.get("room:" + roomId);
        if (roomDataJson == null) {
            return null;
        }
        return gson.fromJson(roomDataJson, RoomData.class);
    }
}
