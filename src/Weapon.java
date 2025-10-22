package com.lostrelic;

public class Weapon extends Item {
    private final int damage;
    public Weapon(String name, String description, int damage) { super(name, description); this.damage = damage; }
    public int getDamage() { return damage; }
}
