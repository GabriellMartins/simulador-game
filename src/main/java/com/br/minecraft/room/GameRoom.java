package com.br.minecraft.room;

import com.br.minecraft.backend.RoomService;
import com.br.minecraft.room.backend.RoomData;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public class GameRoom {

    private final String name;
    private final World world;
    private final Set<Player> players;
    private final int id;

    private static final int MAX_PLAYERS = 2;

    public GameRoom(String name, World world) {
        this.name = name;
        this.world = world;
        this.players = new HashSet<>();
        this.id = -1;

        RoomData roomData = new RoomData(id, name, "active");
        RoomService.saveRoomData(id, roomData);
    }

    public GameRoom(int id, String name, World world, Set<Player> players) {
        this.name = name;
        this.world = world;
        this.players = players;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public World getWorld() {
        return world;
    }

    public void addPlayer(Player player) {
        if (players.size() < MAX_PLAYERS) {
            players.add(player);
            player.teleport(world.getSpawnLocation());
        } else {
            player.sendMessage("A sala já está cheia!");
        }
    }

    public void removePlayer(Player player) {
        players.remove(player);
        player.teleport(Bukkit.getWorld("world").getSpawnLocation());
    }

    public Set<Player> getPlayers() {
        return players;
    }

    public void unload() {
        for (Player player : new HashSet<>(players)) {
            removePlayer(player);
        }

        Bukkit.unloadWorld(world, false);
    }
}
