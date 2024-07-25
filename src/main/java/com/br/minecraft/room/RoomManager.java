package com.br.minecraft.room;

import com.br.minecraft.SimuladorGame;
import com.br.minecraft.room.world.VoidGenerator;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class RoomManager {

    private final SimuladorGame plugin;
    private final List<GameRoom> rooms;
    private final File mapDirectory;

    public RoomManager(SimuladorGame plugin) {
        this.plugin = plugin;
        this.rooms = new ArrayList<>();
        this.mapDirectory = new File(plugin.getDataFolder(), "simulador-maps");
        if (!mapDirectory.exists()) {
            mapDirectory.mkdir();
        }
    }

    public void loadRooms(int count) {
        File[] mapFolders = mapDirectory.listFiles(File::isDirectory);
        if (mapFolders != null) {
            for (int i = 0; i < count && i < mapFolders.length; i++) {
                File mapFolder = mapFolders[i];
                GameRoom room = createRoom(mapFolder.getName());
                if (room != null) {
                    rooms.add(room);
                    Bukkit.getLogger().info("Room " + room.getName() + " loaded.");
                }
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
