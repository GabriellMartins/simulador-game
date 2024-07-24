package com.br.minecraft.room;

import com.br.minecraft.SimuladorGame;

import java.util.ArrayList;
import java.util.List;

public class RoomManager {

    private final SimuladorGame plugin;
    private final List<GameRoom> rooms;

    public RoomManager(SimuladorGame plugin) {
        this.plugin = plugin;
        this.rooms = new ArrayList<>();
    }

    public void LoadRooms(int count) {
        for (int i = 0; i < count; i++) {
//            GameRoom room = ();
        }
    }
}
