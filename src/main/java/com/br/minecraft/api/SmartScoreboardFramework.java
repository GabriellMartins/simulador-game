package com.br.minecraft.api;

import com.br.minecraft.api.reflection.SmartScoreboardReflection;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

public abstract class SmartScoreboardFramework<T> {

    private static final Map<Class<?>, Field[]> PACKETS = new HashMap<>(8);

    protected static final String[] COLOR_CODES = Arrays.stream(ChatColor.values())
            .map(Object::toString)
            .toArray(String[]::new);
    private static final VersionType VERSION_TYPE;

    private static final Class<?> CHAT_COMPONENT_CLASS;
    private static final Class<?> CHAT_FORMAT_ENUM;

    private static final Object RESET_FORMATTING;

    private static final MethodHandle PLAYER_CONNECTION;
    private static final MethodHandle SEND_PACKET;
    private static final MethodHandle PLAYER_GET_HANDLE;
    private static final MethodHandle FIXED_NUMBER_FORMAT;

    private static final SmartScoreboardReflection.PacketConstructor PACKET_SB_OBJ;
    private static final SmartScoreboardReflection.PacketConstructor PACKET_SB_DISPLAY_OBJ;
    private static final SmartScoreboardReflection.PacketConstructor PACKET_SB_TEAM;
    private static final SmartScoreboardReflection.PacketConstructor PACKET_SB_SERIALIZABLE_TEAM;

    private static final MethodHandle PACKET_SB_SET_SCORE;
    private static final MethodHandle PACKET_SB_RESET_SCORE;

    private static final Class<?> DISPLAY_SLOT_TYPE;
    private static final Class<?> ENUM_SB_HEALTH_DISPLAY;
    private static final Class<?> ENUM_SB_ACTION;

    private static final Object BLANK_NUMBER_FORMAT;
    private static final Object SIDEBAR_DISPLAY_SLOT;
    private static final Object ENUM_SB_HEALTH_DISPLAY_INTEGER;
    private static final Object ENUM_SB_ACTION_CHANGE;
    private static final Object ENUM_SB_ACTION_REMOVE;

    static {
        try {
            var lookup = MethodHandles.lookup();

            if (SmartScoreboardReflection.isRepackaged()) {
                VERSION_TYPE = VersionType.V1_17;
            } else if (SmartScoreboardReflection.nmsOptionalClass(null, "ScoreboardServer$Action").isPresent()) {
                VERSION_TYPE = VersionType.V1_13;
            } else if (SmartScoreboardReflection.nmsOptionalClass(null, "IScoreboardCriteria$EnumScoreboardHealthDisplay").isPresent()) {
                VERSION_TYPE = VersionType.V1_8;
            } else {
                VERSION_TYPE = VersionType.V1_7;
            }
            var gameProtocolPackage = "network.protocol.game";

            var craftPlayerClass = SmartScoreboardReflection.obcClass("entity.CraftPlayer");
            var entityPlayerClass = SmartScoreboardReflection.nmsClass("server.level", "EntityPlayer");
            var playerConnectionClass = SmartScoreboardReflection.nmsClass("server.network", "PlayerConnection");
            var packetClass = SmartScoreboardReflection.nmsClass("network.protocol", "Packet");
            var packetSbObjClass = SmartScoreboardReflection.nmsClass(gameProtocolPackage, "PacketPlayOutScoreboardObjective");
            var packetSbDisplayObjClass = SmartScoreboardReflection.nmsClass(gameProtocolPackage, "PacketPlayOutScoreboardDisplayObjective");
            var packetSbScoreClass = SmartScoreboardReflection.nmsClass(gameProtocolPackage, "PacketPlayOutScoreboardScore");
            var packetSbTeamClass = SmartScoreboardReflection.nmsClass(gameProtocolPackage, "PacketPlayOutScoreboardTeam");
            var sbTeamClass = VersionType.V1_17.isHigherOrEqual() ? SmartScoreboardReflection.innerClass(packetSbTeamClass, innerClass -> !innerClass.isEnum()) : null;

            var playerConnectionField = Arrays.stream(entityPlayerClass.getFields())
                    .filter(field -> field.getType().isAssignableFrom(playerConnectionClass))
                    .findFirst()
                    .orElseThrow(NoSuchFieldException::new);

            var sendPacketMethod = Stream.concat(Arrays.stream(playerConnectionClass.getSuperclass().getMethods()), Arrays.stream(playerConnectionClass.getMethods()))
                    .filter(m -> m.getParameterCount() == 1 && m.getParameterTypes()[0] == packetClass)
                    .findFirst()
                    .orElseThrow(NoSuchMethodException::new);

            var displaySlotEnum = SmartScoreboardReflection.nmsOptionalClass("world.scores", "DisplaySlot");

            CHAT_COMPONENT_CLASS = SmartScoreboardReflection.nmsClass("network.chat", "IChatBaseComponent");
            CHAT_FORMAT_ENUM = SmartScoreboardReflection.nmsClass(null, "EnumChatFormat");
            DISPLAY_SLOT_TYPE = displaySlotEnum.orElse(int.class);
            RESET_FORMATTING = SmartScoreboardReflection.enumValueOf(CHAT_FORMAT_ENUM, "RESET", 21);
            SIDEBAR_DISPLAY_SLOT = displaySlotEnum.isPresent() ? SmartScoreboardReflection.enumValueOf(DISPLAY_SLOT_TYPE, "SIDEBAR", 1) : 1;
            PLAYER_GET_HANDLE = lookup.findVirtual(craftPlayerClass, "getHandle", MethodType.methodType(entityPlayerClass));
            PLAYER_CONNECTION = lookup.unreflectGetter(playerConnectionField);
            SEND_PACKET = lookup.unreflect(sendPacketMethod);
            PACKET_SB_OBJ = SmartScoreboardReflection.findPacketConstructor(packetSbObjClass, lookup);
            PACKET_SB_DISPLAY_OBJ = SmartScoreboardReflection.findPacketConstructor(packetSbDisplayObjClass, lookup);

            var numberFormat = SmartScoreboardReflection.nmsOptionalClass("network.chat.numbers", "NumberFormat");

            MethodHandle packetSbSetScore;
            MethodHandle packetSbResetScore = null;
            MethodHandle fixedFormatConstructor = null;

            Object blankNumberFormat = null;

            if (numberFormat.isPresent()) { // 1.20.3
                var blankFormatClass = SmartScoreboardReflection.nmsClass("network.chat.numbers", "BlankFormat");
                var fixedFormatClass = SmartScoreboardReflection.nmsClass("network.chat.numbers", "FixedFormat");
                var resetScoreClass = SmartScoreboardReflection.nmsClass(gameProtocolPackage, "ClientboundResetScorePacket");

                var setScoreType = MethodType.methodType(void.class, String.class, String.class, int.class, CHAT_COMPONENT_CLASS, numberFormat.get());
                var removeScoreType = MethodType.methodType(void.class, String.class, String.class);
                var fixedFormatType = MethodType.methodType(void.class, CHAT_COMPONENT_CLASS);

                var blankField = Arrays.stream(blankFormatClass.getFields())
                        .filter(f -> f.getType() == blankFormatClass)
                        .findAny();

                fixedFormatConstructor = lookup.findConstructor(fixedFormatClass, fixedFormatType);
                packetSbSetScore = lookup.findConstructor(packetSbScoreClass, setScoreType);
                packetSbResetScore = lookup.findConstructor(resetScoreClass, removeScoreType);
                blankNumberFormat = blankField.isPresent() ? blankField.get().get(null) : null;
            } else if (VersionType.V1_17.isHigherOrEqual()) {
                var enumSbAction = SmartScoreboardReflection.nmsClass("server", "ScoreboardServer$Action");

                var scoreType = MethodType.methodType(void.class, enumSbAction, String.class, String.class, int.class);

                packetSbSetScore = lookup.findConstructor(packetSbScoreClass, scoreType);
            } else {
                packetSbSetScore = lookup.findConstructor(packetSbScoreClass, MethodType.methodType(void.class));
            }
            PACKET_SB_SET_SCORE = packetSbSetScore;
            PACKET_SB_RESET_SCORE = packetSbResetScore;
            PACKET_SB_TEAM = SmartScoreboardReflection.findPacketConstructor(packetSbTeamClass, lookup);
            PACKET_SB_SERIALIZABLE_TEAM = sbTeamClass == null ? null : SmartScoreboardReflection.findPacketConstructor(sbTeamClass, lookup);
            FIXED_NUMBER_FORMAT = fixedFormatConstructor;
            BLANK_NUMBER_FORMAT = blankNumberFormat;

            for (var classOf : Arrays.asList(packetSbObjClass, packetSbDisplayObjClass, packetSbScoreClass, packetSbTeamClass, sbTeamClass)) {
                if (classOf == null)
                    continue;

                Field[] fields = Arrays.stream(classOf.getDeclaredFields())
                        .filter(field -> !Modifier.isStatic(field.getModifiers()))
                        .toArray(Field[]::new);

                for (var field : fields)
                    field.setAccessible(true);

                PACKETS.put(classOf, fields);
            }
            if (VersionType.V1_8.isHigherOrEqual()) {
                var enumSbActionClass = VersionType.V1_13.isHigherOrEqual() ? "ScoreboardServer$Action" : "PacketPlayOutScoreboardScore$EnumScoreboardAction";

                ENUM_SB_HEALTH_DISPLAY = SmartScoreboardReflection.nmsClass("world.scores.criteria", "IScoreboardCriteria$EnumScoreboardHealthDisplay");
                ENUM_SB_ACTION = SmartScoreboardReflection.nmsClass("server", enumSbActionClass);
                ENUM_SB_HEALTH_DISPLAY_INTEGER = SmartScoreboardReflection.enumValueOf(ENUM_SB_HEALTH_DISPLAY, "INTEGER", 0);
                ENUM_SB_ACTION_CHANGE = SmartScoreboardReflection.enumValueOf(ENUM_SB_ACTION, "CHANGE", 0);
                ENUM_SB_ACTION_REMOVE = SmartScoreboardReflection.enumValueOf(ENUM_SB_ACTION, "REMOVE", 1);
            } else {
                ENUM_SB_HEALTH_DISPLAY = null;
                ENUM_SB_ACTION = null;
                ENUM_SB_HEALTH_DISPLAY_INTEGER = null;
                ENUM_SB_ACTION_CHANGE = null;
                ENUM_SB_ACTION_REMOVE = null;
            }
        } catch (Throwable throwable) {
            throw new ExceptionInInitializerError(throwable);
        }
    }

    private final Player player;

    private String id;

    private final List<T> lines = new ArrayList<>();
    private final List<T> scores = new ArrayList<>();

    private T title = emptyLine();

    private boolean deleted = false;

    protected SmartScoreboardFramework(Player player) {
        this.player = Objects.requireNonNull(player, "player");
        this.id = "sb-" + Integer.toHexString(ThreadLocalRandom.current().nextInt());

        try {
            sendObjectivePacket(ObjectiveMode.CREATE);
            sendDisplayObjectivePacket();
        } catch (Throwable throwable) {
            throw new RuntimeException("Unable to create scoreboard", throwable);
        }
    }

    protected abstract void sendLineChange(int score) throws Throwable;

    protected abstract Object toMinecraftComponent(T value) throws Throwable;

    protected abstract String serializeLine(T value);

    protected abstract T emptyLine();

    public boolean customScoresSupported() {
        return BLANK_NUMBER_FORMAT != null;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public Player getPlayer() {
        return player;
    }

    public String getId() {
        return id;
    }

    public T getTitle() {
        return title;
    }

    public List<T> getLines() {
        return new ArrayList<>(lines);
    }

    public T getLine(int line) {
        checkLineNumber(line, true, false);

        return lines.get(line);
    }

    public Optional<T> getScore(int line) {
        checkLineNumber(line, true, false);

        return Optional.ofNullable(scores.get(line));
    }

    public void updateTitle(T title) {
        if (this.title.equals(Objects.requireNonNull(title, "title")))
            return;

        this.title = title;

        try {
            sendObjectivePacket(ObjectiveMode.UPDATE);
        } catch (Throwable throwable) {
            throw new RuntimeException("Unable to update scoreboard title", throwable);
        }
    }

    public synchronized void updateLine(int line, T text) {
        updateLine(line, text, null);
    }

    public synchronized void updateLine(int line, T text, T scoreText) {
        checkLineNumber(line, false, false);

        try {
            if (line < lines.size()) {
                lines.set(line, text);
                scores.set(line, scoreText);

                sendLineChange(getScoreByLine(line));

                if (customScoresSupported())
                    sendScorePacket(getScoreByLine(line), ScoreboardAction.CHANGE);
                return;
            }
            var newLines = new ArrayList<>(lines);
            var newScores = new ArrayList<>(scores);

            if (line > lines.size()) {
                for (var index = lines.size(); index < line; index++) {
                    newLines.add(emptyLine());
                    newScores.add(null);
                }
            }
            newLines.add(text);
            newScores.add(scoreText);

            updateLines(newLines, newScores);
        } catch (Throwable throwable) {
            throw new RuntimeException("Unable to update scoreboard lines", throwable);
        }
    }

    public synchronized void removeLine(int line) {
        checkLineNumber(line, false, false);

        if (line >= lines.size())
            return;

        var newLines = new ArrayList<>(lines);
        var newScores = new ArrayList<>(scores);

        newLines.remove(line);
        newScores.remove(line);

        updateLines(newLines, newScores);
    }

    public synchronized void updateLines(Collection<T> lines) {
        updateLines(lines, null);
    }

    public synchronized void updateLines(Collection<T> lines, Collection<T> scores) {
        Objects.requireNonNull(lines, "lines");

        checkLineNumber(lines.size(), false, true);

        if (scores != null && scores.size() != lines.size())
            throw new IllegalArgumentException("The size of the scores must match the size of the board");

        var oldLines = new ArrayList<>(this.lines);

        this.lines.clear();
        this.lines.addAll(lines);

        var oldScores = new ArrayList<>(this.scores);

        this.scores.clear();
        this.scores.addAll(scores != null ? scores : Collections.nCopies(lines.size(), null));

        var linesSize = this.lines.size();

        try {
            if (oldLines.size() != linesSize) {
                var oldLinesCopy = new ArrayList<>(oldLines);

                if (oldLines.size() > linesSize) {
                    for (var index = oldLinesCopy.size(); index > linesSize; index--) {
                        sendTeamPacket(index - 1, TeamMode.REMOVE);
                        sendScorePacket(index - 1, ScoreboardAction.REMOVE);

                        oldLines.remove(0);
                    }
                } else {
                    for (var index = oldLinesCopy.size(); index < linesSize; index++) {
                        sendScorePacket(index, ScoreboardAction.CHANGE);
                        sendTeamPacket(index, TeamMode.CREATE, null, null);
                    }
                }
            }
            for (var index = 0; index < linesSize; index++) {
                if (!Objects.equals(getLineByScore(oldLines, index), getLineByScore(index)))
                    sendLineChange(index);

                if (!Objects.equals(getLineByScore(oldScores, index), getLineByScore(this.scores, index)))
                    sendScorePacket(index, ScoreboardAction.CHANGE);
            }
        } catch (Throwable throwable) {
            throw new RuntimeException("Unable to update scoreboard lines", throwable);
        }
    }

    public synchronized void updateScore(int line, T text) {
        checkLineNumber(line, true, false);

        scores.set(line, text);

        try {
            if (customScoresSupported())
                sendScorePacket(getScoreByLine(line), ScoreboardAction.CHANGE);
        } catch (Throwable throwable) {
            throw new RuntimeException("Unable to update line score", throwable);
        }
    }

    public synchronized void removeScore(int line) {
        updateScore(line, null);
    }

    @SafeVarargs
    public final synchronized void updateScores(T... texts) {
        updateScores(Arrays.asList(texts));
    }

    public synchronized void updateScores(Collection<T> texts) {
        Objects.requireNonNull(texts, "texts");

        if (scores.size() != lines.size())
            throw new IllegalArgumentException("The size of the scores must match the size of the board");

        var newScores = new ArrayList<>(texts);

        for (var index = 0; index < scores.size(); index++) {
            if (Objects.equals(scores.get(index), newScores.get(index)))
                continue;

            scores.set(index, newScores.get(index));

            try {
                if (customScoresSupported())
                    sendScorePacket(getScoreByLine(index), ScoreboardAction.CHANGE);
            } catch (Throwable throwable) {
                throw new RuntimeException("Unable to update scores", throwable);
            }
        }
    }

    public void delete() {
        try {
            for (var index = 0; index < lines.size(); index++)
                sendTeamPacket(index, TeamMode.REMOVE);

            sendObjectivePacket(ObjectiveMode.REMOVE);
        } catch (Throwable throwable) {
            throw new RuntimeException("Unable to delete scoreboard", throwable);
        }
        this.deleted = true;
    }

    private void checkLineNumber(int line, boolean checkInRange, boolean checkMax) {
        if (line < 0)
            throw new IllegalArgumentException("Line number must be positive");

        if (checkInRange && line >= lines.size())
            throw new IllegalArgumentException("Line number must be under " + lines.size());

        if (checkMax && line >= COLOR_CODES.length - 1)
            throw new IllegalArgumentException("Line number is too high: " + line);
    }

    protected int getScoreByLine(int line) {
        return this.lines.size() - line - 1;
    }

    protected T getLineByScore(int score) {
        return getLineByScore(this.lines, score);
    }

    protected T getLineByScore(List<T> lines, int score) {
        return score < lines.size() ? lines.get(lines.size() - score - 1) : null;
    }

    protected void sendObjectivePacket(ObjectiveMode mode) throws Throwable {
        var packet = PACKET_SB_OBJ.invoke();

        setField(packet, String.class, this.id);
        setField(packet, int.class, mode.ordinal());

        if (!mode.equals(ObjectiveMode.REMOVE)) {
            setComponentField(packet, this.title, 1);

            if (VersionType.V1_8.isHigherOrEqual())
                setField(packet, ENUM_SB_HEALTH_DISPLAY, ENUM_SB_HEALTH_DISPLAY_INTEGER);
        } else if (VERSION_TYPE == VersionType.V1_7) {
            setField(packet, String.class, "", 1);
        }
        sendPacket(packet);
    }

    protected void sendDisplayObjectivePacket() throws Throwable {
        var packet = PACKET_SB_DISPLAY_OBJ.invoke();

        setField(packet, DISPLAY_SLOT_TYPE, SIDEBAR_DISPLAY_SLOT);
        setField(packet, String.class, this.id);

        sendPacket(packet);
    }

    protected void sendScorePacket(int score, ScoreboardAction action) throws Throwable {
        if (VersionType.V1_17.isHigherOrEqual()) {
            sendModernScorePacket(score, action);
            return;
        }
        var packet = PACKET_SB_SET_SCORE.invoke();

        setField(packet, String.class, COLOR_CODES[score], 0); // Player Name

        if (VersionType.V1_8.isHigherOrEqual()) {
            var enumAction = action.equals(ScoreboardAction.REMOVE) ? ENUM_SB_ACTION_REMOVE : ENUM_SB_ACTION_CHANGE;

            setField(packet, ENUM_SB_ACTION, enumAction);
        } else {
            setField(packet, int.class, action.ordinal(), 1); // Action
        }
        if (action.equals(ScoreboardAction.CHANGE)) {
            setField(packet, String.class, this.id, 1); // Objective Name
            setField(packet, int.class, score); // Score
        }
        sendPacket(packet);
    }

    private void sendModernScorePacket(int score, ScoreboardAction action) throws Throwable {
        var objName = COLOR_CODES[score];

        var enumAction = action.equals(ScoreboardAction.REMOVE) ? ENUM_SB_ACTION_REMOVE : ENUM_SB_ACTION_CHANGE;

        if (PACKET_SB_RESET_SCORE == null) { // Pre 1.20.3
            sendPacket(PACKET_SB_SET_SCORE.invoke(enumAction, id, objName, score));
            return;
        }
        if (action.equals(ScoreboardAction.REMOVE)) {
            sendPacket(PACKET_SB_RESET_SCORE.invoke(objName, id));
            return;
        }
        var scoreFormat = getLineByScore(scores, score);

        var format = scoreFormat != null ? FIXED_NUMBER_FORMAT.invoke(toMinecraftComponent(scoreFormat)) : BLANK_NUMBER_FORMAT;

        sendPacket(PACKET_SB_SET_SCORE.invoke(objName, id, score, null, format));
    }

    protected void sendTeamPacket(int score, TeamMode mode) throws Throwable {
        sendTeamPacket(score, mode, null, null);
    }

    protected void sendTeamPacket(int score, TeamMode mode, T prefix, T suffix) throws Throwable {
        if (mode.equals(TeamMode.ADD_PLAYERS) || mode.equals(TeamMode.REMOVE_PLAYERS))
            throw new UnsupportedOperationException();

        var packet = PACKET_SB_TEAM.invoke();

        setField(packet, String.class, this.id + ':' + score); // Team name
        setField(packet, int.class, mode.ordinal(), VERSION_TYPE.equals(VersionType.V1_8) ? 1 : 0); // Update mode

        if (mode.equals(TeamMode.REMOVE)) {
            sendPacket(packet);
            return;
        }
        if (VersionType.V1_17.isHigherOrEqual()) {
            var team = PACKET_SB_SERIALIZABLE_TEAM.invoke();

            // Since the packet is initialized with null values, we need to change more things.
            setComponentField(team, null, 0); // Display name
            setField(team, CHAT_FORMAT_ENUM, RESET_FORMATTING); // Color
            setComponentField(team, prefix, 1); // Prefix
            setComponentField(team, suffix, 2); // Suffix
            setField(team, String.class, "always", 0); // Visibility
            setField(team, String.class, "always", 1); // Collisions
            setField(packet, Optional.class, Optional.of(team));
        } else {
            setComponentField(packet, prefix, 2); // Prefix
            setComponentField(packet, suffix, 3); // Suffix
            setField(packet, String.class, "always", 4); // Visibility for 1.8+
            setField(packet, String.class, "always", 5); // Collisions for 1.9+
        }
        if (mode.equals(TeamMode.CREATE))
            setField(packet, Collection.class, Collections.singletonList(COLOR_CODES[score])); // Players in the team

        sendPacket(packet);
    }

    private void sendPacket(Object packet) throws Throwable {
        if (this.deleted)
            throw new IllegalStateException("This scoreboard is deleted");

        if (this.player.isOnline()) {
            var entityPlayer = PLAYER_GET_HANDLE.invoke(player);

            var playerConnection = PLAYER_CONNECTION.invoke(entityPlayer);

            SEND_PACKET.invoke(playerConnection, packet);
        }
    }

    private void setField(Object object, Class<?> fieldType, Object value) throws ReflectiveOperationException {
        setField(object, fieldType, value, 0);
    }

    private void setField(Object packet, Class<?> fieldType, Object value, int count) throws ReflectiveOperationException {
        var index = 0;

        for (var field : PACKETS.get(packet.getClass())) {
            if (field.getType() == fieldType && count == index++)
                field.set(packet, value);
        }
    }

    private void setComponentField(Object packet, T value, int count) throws Throwable {
        if (!VersionType.V1_13.isHigherOrEqual()) {
            var line = value != null ? serializeLine(value) : "";

            setField(packet, String.class, line, count);
            return;
        }
        var index = 0;

        for (var field : PACKETS.get(packet.getClass())) {
            if ((field.getType() == String.class || field.getType() == CHAT_COMPONENT_CLASS) && count == index++)
                field.set(packet, toMinecraftComponent(value));
        }
    }

    public enum ObjectiveMode {

        CREATE,
        REMOVE,
        UPDATE;
    }

    public enum TeamMode {

        CREATE,
        REMOVE,
        UPDATE,
        ADD_PLAYERS,
        REMOVE_PLAYERS;
    }

    public enum ScoreboardAction {

        CHANGE,
        REMOVE;
    }

    enum VersionType {

        V1_7,
        V1_8,
        V1_13,
        V1_17;

        public boolean isHigherOrEqual() {
            return VERSION_TYPE.ordinal() >= ordinal();
        }
    }
}