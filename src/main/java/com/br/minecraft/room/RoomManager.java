package com.br.minecraft.room;

import com.br.minecraft.SimuladorGame;
import com.br.minecraft.room.world.VoidGenerator;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;

import java.util.ArrayList;
import java.util.List;

public class RoomManager {

    private final SimuladorGame plugin;
    private final List<GameRoom> rooms;

    public RoomManager(SimuladorGame plugin) {
        this.plugin = plugin;
        this.rooms = new ArrayList<>();
    }

    public void LoadRooms(int count) {
        for (int i = 0; i < count; i++) {
            GameRoom room = createRoom("Room_" + (i + 1));
            if (room != null) {
                rooms.add(room);
                Bukkit.getLogger().info("Room " + room.getName()+ " loaded.");

            }
        }
    }

    public void unloadRooms() {
        for (GameRoom room : rooms) {
            room.unload();
            Bukkit.getLogger().info("Room " + room.getName() + " unloaded");

        }

        rooms.clear();
    }

    private GameRoom getRoom(String name) {
        for (GameRoom room : rooms) {
            if (room.getName().equals(name)) {
                return room;
            }
        }
        return null;
    }

    public List<GameRoom> getRooms() {
        return rooms;
    }

    private GameRoom createRoom(String name) {
        WorldCreator wc = new WorldCreator(name);
        wc.environment(World.Environment.NORMAL);
        wc.type(WorldType.FLAT);
        wc.generator(new VoidGenerator());
        World world = wc.createWorld();
        return new GameRoom(name, world);
    }

}
