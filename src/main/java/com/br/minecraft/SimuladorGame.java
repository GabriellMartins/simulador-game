package com.br.minecraft;

import com.br.minecraft.listeners.GlobalListeners;
import org.bukkit.plugin.java.JavaPlugin;

public class SimuladorGame extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("Simulador Game habilitado!");
        getServer().getPluginManager().registerEvents(new GlobalListeners(), this);
    }

    @Override
    public void onDisable() {
        getLogger().info("Simulador Game desabilitado!");
    }
}
