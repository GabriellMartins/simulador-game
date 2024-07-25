package com.br.minecraft.listeners.chat;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        String playerName = event.getPlayer().getName();
        String message = event.getMessage();
        String formattedMessage = ChatColor.GRAY + playerName + ChatColor.RESET + ": " + message;
        event.setFormat(formattedMessage);
    }
}
