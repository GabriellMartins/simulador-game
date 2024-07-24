package com.br.minecraft;

import com.br.minecraft.kits.KitManager;
import com.br.minecraft.kits.register.ExempleKit;
import com.br.minecraft.listeners.GlobalListeners;
import org.bukkit.plugin.java.JavaPlugin;

public class SimuladorGame extends JavaPlugin {

    private KitManager kitManager;

    @Override
    public void onEnable() {
        getLogger().info("Simulador Game habilitado!");
        getServer().getPluginManager().registerEvents(new GlobalListeners(), this);
        kitManager = new KitManager();
        kitManager.registerKit(new ExempleKit());
    }

    @Override
    public void onDisable() {
        getLogger().info("Simulador Game desabilitado!");
    }
}
