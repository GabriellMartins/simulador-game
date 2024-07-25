package com.br.minecraft.room;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public class GameRoom {

    private final String name;
    private final World world;
    private final Set<Player> players;

    public  GameRoom(String name, World world) {
        this.name = name;
        this.world = world;
        this.players = new HashSet<>();
    }

    public String getName() {
        return name;
    }

    public World getWorld() {
        return world;
    }

    public void addPlayer(Player player) {
        players.add(player);
        player.teleport(world.getSpawnLocation());
    }

    public void removePlayer(Player player) {
        players.remove(player);
        player.teleport(Bukkit.getWorld("world").getSpawnLocation());
    }

    public Set<Player> getPlayers() {
        return players;
    }
    public void unload() {
        for (Player player :  new HashSet<>(players)) {
            removePlayer(player);
        }

        Bukkit.unloadWorld(world, false);
    }
}
