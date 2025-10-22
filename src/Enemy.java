package com.lostrelic;

public class Enemy {
    private final String name;
    private int health;
    private final int damage;

    public Enemy(String name, int health, int damage) {
        this.name = name; this.health = health; this.damage = damage;
    }

    public String getName() { return name; }
    public int getHealth() { return health; }
    public int getDamage() { return damage; }
    public void takeDamage(int amt) { health -= amt; if (health < 0) health = 0; }
    public boolean isAlive() { return health > 0; }
}
