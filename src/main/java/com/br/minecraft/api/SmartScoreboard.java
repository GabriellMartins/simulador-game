package com.br.minecraft.api;

import com.br.minecraft.api.reflection.SmartScoreboardReflection;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Array;
import java.util.List;
import java.util.Objects;

public final class SmartScoreboard extends SmartScoreboardFramework<String> {

    private static final MethodHandle MESSAGE_FROM_STRING;
    private static final Object EMPTY_MESSAGE;

    static {
        try {
            var craftChatMessageClass = SmartScoreboardReflection.obcClass("util.CraftChatMessage");
            MESSAGE_FROM_STRING = MethodHandles.lookup().unreflect(craftChatMessageClass.getMethod("fromString", String.class));
            EMPTY_MESSAGE = Array.get(MESSAGE_FROM_STRING.invoke(""), 0);
        } catch (Throwable throwable) {
            throw new ExceptionInInitializerError(throwable);
        }
    }

    public SmartScoreboard(Player player) {
        super(player);
    }


    @Override
    public void updateTitle(String title) {
        Objects.requireNonNull(title, "title");

        if (!VersionType.V1_13.isHigherOrEqual() && title.length() > 32)
            throw new IllegalArgumentException("Title is longer than 32 chars");

        super.updateTitle(title);
    }

    public void updateLines(List<String> lines) {
        Objects.requireNonNull(lines, "lines");

        if (!VersionType.V1_13.isHigherOrEqual()) {
            var lineCount = 0;

            for (var line : lines) {
                if (line != null && line.length() > 30)
                    throw new IllegalArgumentException("Line " + lineCount + " is longer than 30 chars");

                lineCount++;
            }
        }
        super.updateLines(lines);
    }

    @Override
    protected void sendLineChange(int score) throws Throwable {
        var maxLength = hasLinesMaxLength() ? 16 : 1024;

        var line = getLineByScore(score);
        var prefix = "";
        var suffix = "";

        if (line == null || line.isEmpty()) {
            prefix = COLOR_CODES[score] + ChatColor.RESET;
        } else if (line.length() <= maxLength) {
            prefix = line;
        } else {
            var index = line.charAt(maxLength - 1) == ChatColor.COLOR_CHAR ? (maxLength - 1) : maxLength;

            prefix = line.substring(0, index);

            var suffixTmp = line.substring(index);

            ChatColor chatColor = null;

            if (suffixTmp.length() >= 2 && suffixTmp.charAt(0) == ChatColor.COLOR_CHAR)
                chatColor = ChatColor.getByChar(suffixTmp.charAt(1));

            var color = ChatColor.getLastColors(prefix);
            var addColor = chatColor == null || chatColor.isFormat();

            suffix = (addColor ? (color.isEmpty() ? ChatColor.RESET.toString() : color) : "") + suffixTmp;
        }
        if (prefix.length() > maxLength || suffix.length() > maxLength) {
            prefix = prefix.substring(0, maxLength);
            suffix = suffix.substring(0, maxLength);
        }
        sendTeamPacket(score, TeamMode.UPDATE, prefix, suffix);
    }

    @Override
    protected Object toMinecraftComponent(String line) throws Throwable {
        if (line == null || line.isEmpty())
            return EMPTY_MESSAGE;

        return Array.get(MESSAGE_FROM_STRING.invoke(line), 0);
    }

    @Override
    protected String serializeLine(String value) {
        return value;
    }

    @Override
    public String emptyLine() {
        return "";
    }

    private boolean hasLinesMaxLength() {
        return !VersionType.V1_13.isHigherOrEqual();
    }

    /**
     * Aplica a scoreboard ao jogador.
     *
     * @param player O jogador ao qual a scoreboard será aplicada.
     */
    public void applyToPlayer(Player player) {
        player.setScoreboard(getPlayer().getScoreboard());
    }
}
