package com.br.minecraft.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

public class GlobalListeners implements Listener {

    @EventHandler
    public void onPlayerConsume(PlayerItemConsumeEvent e) {
        Player player = e.getPlayer();
        ItemStack item = e.getItem();

        if (item.getType() == Material.MUSHROOM_SOUP) {
            double health = player.getHealth() + 4.0;
            player.setHealth(Math.min(health, player.getMaxHealth()));
            item.setType(Material.BOWL);
        }
    }

}
