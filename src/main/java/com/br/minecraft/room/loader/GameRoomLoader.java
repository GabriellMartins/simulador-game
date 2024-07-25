package com.br.minecraft.room.loader;

import com.br.minecraft.backend.RoomService;
import com.br.minecraft.room.GameRoom;
import com.br.minecraft.room.backend.RoomData;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.HashSet;

/**
 * Loader para carregar salas a partir dos dados armazenados.
 */
public class GameRoomLoader {

    /**
     * Carrega uma sala a partir do Redis.
     *
     * @param roomId Identificador da sala
     * @return A sala carregada
     */
    public static GameRoom loadRoom(int roomId) {
        RoomData roomData = RoomService.getRoomData(roomId);
        if (roomData == null) {
            return null;
        }

        World world = Bukkit.getWorld(roomData.getName());
        if (world == null) {
            return null;
        }

        return new GameRoom(roomData.getId(), roomData.getName(), world, new HashSet<>());
    }
}
