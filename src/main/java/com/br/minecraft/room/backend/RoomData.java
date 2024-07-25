package com.br.minecraft.room.backend;

import lombok.Data;

/**
 * Classe que representa os dados de uma sala (room) no sistema.
 */
@Data
public class RoomData {

    private int id;
    private String name;
    private RoomState state;

    /**
     * Construtor padrão.
     */
    public RoomData() {
    }

    /**
     * Construtor com todos os parâmetros.
     *
     * @param id    Identificador da sala
     * @param name  Nome da sala
     * @param state Estado da sala
     */
    public RoomData(int id, String name, RoomState state) {
        this.id = id;
        this.name = name;
        this.state = state;
    }

    /**
     * Construtor que usa uma string para definir o estado da sala.
     *
     * @param id    Identificador da sala
     * @param name  Nome da sala
     * @param active Estado da sala em formato de string
     */
    public RoomData(int id, String name, String active) {
        this.id = id;
        this.name = name;
        this.state = RoomState.fromString(active);
    }
}
