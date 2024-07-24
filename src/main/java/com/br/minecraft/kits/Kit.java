package com.br.minecraft.kits;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class Kit {

    private final String name;

    public Kit(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public abstract void apply(Player player);

    protected void giveItems(Player player, ItemStack... items) {
        player.getInventory().addItem(items);
    }
    
}
