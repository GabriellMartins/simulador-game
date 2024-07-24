package com.br.minecraft.kits.register;

import com.br.minecraft.kits.Kit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ExempleKit extends Kit {

    public ExempleKit() {
        super("exemplekit");
    }

    @Override
    public void apply(Player player) {
        giveItems(player,
                new ItemStack(Material.ICE));
    }
}
