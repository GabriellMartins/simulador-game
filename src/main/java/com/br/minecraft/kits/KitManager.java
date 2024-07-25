package com.br.minecraft.kits;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class KitManager {

    private final Map<String, Kit> kits = new HashMap<>();
    private final Map<Player, Kit> playerKits = new HashMap<>(); // Armazenar o kit aplicado ao jogador

    public void registerKit(Kit kit) {
        kits.put(kit.getName(), kit);
    }

    public Kit getKit(String name) {
        return kits.get(name);
    }

    public void applyKit(Player player, String kitName) {
        Kit kit = kits.get(kitName);
        if (kit != null) {
            kit.apply(player);
            playerKits.put(player, kit); // Armazena o kit aplicado ao jogador
        }
    }

    public Kit getPlayerKit(Player player) {
        return playerKits.get(player);
    }
}
