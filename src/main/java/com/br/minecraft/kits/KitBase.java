package com.br.minecraft.kits;

public abstract class KitBase {

    private final String name;

    public KitBase(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
    
}
