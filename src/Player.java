import java.util.ArrayList;
import java.util.List;

public class Player {
    private Room currentRoom;
    private int health = 100;
    private Weapon equippedWeapon;
    private Armor equippedArmor;
    private final List<Item> inventory = new ArrayList<>();

    public void setCurrentRoom(Room room) { this.currentRoom = room; }
    public Room getCurrentRoom() { return currentRoom; }

    public int getHealth() { return health; }
    public void heal(int amount) { health += amount; if (health > 100) health = 100; }
    public void takeDamage(int dmg) { health -= dmg; if (health < 0) health = 0; }

    public void addItem(Item it) { inventory.add(it); }
    public List<Item> getInventory() { return inventory; }

    public void equipWeapon(Weapon w) { this.equippedWeapon = w; }
    public void equipArmor(Armor a) { this.equippedArmor = a; }

    public Weapon getEquippedWeapon() { return equippedWeapon; }
    public Armor getEquippedArmor() { return equippedArmor; }

    public int attack() {
        return (equippedWeapon != null) ? equippedWeapon.getDamage() : 3;
    }

    public boolean isAlive() { return health > 0; }
}
