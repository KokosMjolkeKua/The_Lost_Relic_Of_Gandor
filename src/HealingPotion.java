package com.lostrelic;

public class HealingPotion extends Item {
    private final int healAmount;
    public HealingPotion(String name, String description, int healAmount) { super(name, description); this.healAmount = healAmount; }
    public int getHealAmount() { return healAmount; }
}
