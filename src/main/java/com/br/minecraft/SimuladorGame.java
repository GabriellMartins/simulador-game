package com.br.minecraft;

import com.br.minecraft.kits.KitManager;
import com.br.minecraft.kits.register.ExempleKit;
import com.br.minecraft.listeners.GlobalListeners;
import com.br.minecraft.room.RoomManager;
import org.bukkit.plugin.java.JavaPlugin;

public class SimuladorGame extends JavaPlugin {

    private KitManager kitManager;
    private static SimuladorGame instance;
    private RoomManager roomManager;

    @Override
    public void onEnable() {
        getLogger().info("Simulador Game habilitado!");
        getServer().getPluginManager().registerEvents(new GlobalListeners(), this);
        kitManager = new KitManager();
        kitManager.registerKit(new ExempleKit());
        this.roomManager = new RoomManager(this);
        roomManager.loadRooms(5);
    }

    @Override
    public void onDisable() {
        roomManager.unloadRooms();
        getLogger().info("Simulador Game desabilitado!");
    }

    public static SimuladorGame getInstance() {
        return instance;
    }

    public RoomManager getRoomManager() {
        return roomManager;
    }
}
