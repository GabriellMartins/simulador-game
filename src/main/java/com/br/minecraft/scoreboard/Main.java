package com.br.minecraft.scoreboard;

import com.br.minecraft.api.SmartScoreboard;
import com.br.minecraft.kits.Kit;
import com.br.minecraft.kits.KitManager;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class Main {

    public void displayGameStarted(Player player, String opponentName, KitManager kitManager) {
        var scoreboard = new SmartScoreboard(player); // Assuming this constructor requires a Player
        var kit = kitManager.getKit(player.getName());
        var kitName = (kit != null) ? kit.getName() : "Nenhum Kit";

        var lines = new ArrayList<String>();
        lines.add("§b§lSIMULADOR-GAME");
        lines.add("§f");
        lines.add("§fAdiversario: §c" + opponentName);
        lines.add("§fKit: §a" + kitName);
        lines.add("§f");
        lines.add("§fModo: §b SOLO");
        lines.add("§f");
        lines.add("§awww.lostmc.com.br");

        scoreboard.updateLines(lines);
        scoreboard.applyToPlayer(player);
    }

    public void displayCountdown(Player player, int minutesLeft, KitManager kitManager) {
        var scoreboard = new SmartScoreboard(player); // Assuming this constructor requires a Player
        var kit = kitManager.getKit(player.getName());
        var kitName = (kit != null) ? kit.getName() : "Nenhum Kit";

        var lines = new ArrayList<String>();
        lines.add("§b§lSIMULADOR-GAME");
        lines.add("§f");
        lines.add("§fAguarde: §a" + minutesLeft + " minutos");
        lines.add("§f");
        lines.add("§fJogadores: §a" + player.getWorld().getPlayers().size() + "/2");
        lines.add("§fKit: §a" + kitName);
        lines.add("§f");
        lines.add("§awww.lostmc.com.br");

        scoreboard.updateLines(lines);
        scoreboard.applyToPlayer(player);
    }

    public void displayGameEnded(Player player, boolean won) {
        var scoreboard = new SmartScoreboard(player); // Assuming this constructor requires a Player

        var lines = new ArrayList<String>();
        lines.add("§b§lSIMULADOR-GAME");
        lines.add("§f");
        lines.add((won ? "§a§lVOCÊ VENCEU" : "§c§lVOCÊ PERDEU"));
        lines.add("§f");
        lines.add("§awww.lostmc.com.br");

        scoreboard.updateLines(lines);
        scoreboard.applyToPlayer(player);
    }
}
