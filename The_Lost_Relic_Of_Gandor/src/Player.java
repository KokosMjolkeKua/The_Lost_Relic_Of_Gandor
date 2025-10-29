
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Player {
    private Room currentRoom;
    private int health = 100;
    private Weapon equippedWeapon;
    private Armor equippedArmor;
    private final List<Item> inventory = new ArrayList<>();
    private final Set<String> equipped = new HashSet<>(); // track equipped items by name (Shoes, Climbing Shoes, etc.)

    public Player() {
        // Start with basic shoes equipped
        Item shoes = new Item("Shoes", "Basic leather shoes (starting gear).");
        inventory.add(shoes);
        equipped.add("Shoes");
        System.out.println("[DEBUG] Player starts with Shoes equipped.");
    }

    public void setCurrentRoom(Room room) { this.currentRoom = room; }
    public Room getCurrentRoom() { return currentRoom; }

    public int getHealth() { return health; }
    public void heal(int amount) { health += amount; if (health > 100) health = 100; }
    public void takeDamage(int dmg) { health -= dmg; if (health < 0) health = 0; }
    public boolean isAlive() { return health > 0; }

    public List<Item> getInventory() { return inventory; }

    public void equipWeapon(Weapon w) { this.equippedWeapon = w; equipped.add(w.getName()); }
    public void equipArmor(Armor a) { this.equippedArmor = a; equipped.add(a.getName()); }

    public Weapon getEquippedWeapon() { return equippedWeapon; }
    public Armor getEquippedArmor() { return equippedArmor; }

    public int attack() {
        return (equippedWeapon != null) ? equippedWeapon.getDamage() : 3;
    }

    // Generic equip/unequip by name marker
    public void equipByName(String name) { equipped.add(name); }
    public void unequipByName(String name) {
        equipped.remove(name);
        if (equippedWeapon != null && equippedWeapon.getName().equalsIgnoreCase(name)) equippedWeapon = null;
        if (equippedArmor  != null && equippedArmor.getName().equalsIgnoreCase(name)) equippedArmor = null;
    }
    public boolean isEquipped(String name) {
        for (String s : equipped) if (s.equalsIgnoreCase(name)) return true;
        return false;
    }

    public String equippedSummary() {
        if (equipped.isEmpty()) return "You are not wearing or wielding anything.";
        StringBuilder sb = new StringBuilder("You are wearing/wielding:\n");
        for (String s : equipped) sb.append("- ").append(s).append("\n");
        return sb.toString().trim();
    }
}
