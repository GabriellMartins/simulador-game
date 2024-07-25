package com.br.minecraft.room.backend;

/**
 * Enum que representa os estados possíveis de uma sala.
 */
public enum RoomState {
    START("Iniciado"),
    STOP("Parado"),
    IN_PROGRESS("Em Partida");

    private final String displayName;

    RoomState(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Converte uma string para o enum RoomState correspondente.
     *
     * @param name O nome do estado em string.
     * @return O RoomState correspondente, ou null se não encontrado.
     */
    public static RoomState fromString(String name) {
        for (RoomState state : RoomState.values()) {
            if (state.displayName.equalsIgnoreCase(name)) {
                return state;
            }
        }
        return null;
    }
}
