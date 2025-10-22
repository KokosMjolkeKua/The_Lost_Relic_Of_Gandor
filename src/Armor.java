package com.lostrelic;

public class Armor extends Item {
    private final int defense;
    public Armor(String name, String description, int defense) { super(name, description); this.defense = defense; }
    public int getDefense() { return defense; }
}
