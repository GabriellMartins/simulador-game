package com.br.minecraft.api.tag;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class TagManager {

    private final Scoreboard scoreboard;

    public TagManager(Scoreboard scoreboard) {
        this.scoreboard = scoreboard;
        createTeams();
    }

    private void createTeams() {
        createTeam("enemy", ChatColor.RED);
        createTeam("ally", ChatColor.GREEN);
    }

    private void createTeam(String teamName, ChatColor color) {
        Team team = scoreboard.getTeam(teamName);
        if (team == null) {
            team = scoreboard.registerNewTeam(teamName);
        }
        team.setPrefix(color.toString());
        team.setCanSeeFriendlyInvisibles(true);
    }

    public void setPlayerTag(Player player, boolean isEnemy) {
        String teamName = isEnemy ? "enemy" : "ally";
        Team team = scoreboard.getTeam(teamName);
        if (team == null) {
            throw new IllegalStateException("Team " + teamName + " does not exist.");
        }
        team.addEntry(player.getName());
    }
}
