
import java.util.*;

public class Game {
    private final Player player;

    public Game() {
        System.out.println("[INFO] Game initialized — v11 Golden Compass Edition");
        player = new Player();
        Room start = WorldBuilder.createWorld();
        player.setCurrentRoom(start);
        System.out.println("[DEBUG] World created. Starting in Room 1.");
    }

    public String look() {
        Room cur = player.getCurrentRoom();
        StringBuilder sb = new StringBuilder();
        sb.append(cur.getDescription());

        if (!cur.getItems().isEmpty()) {
            sb.append("\n\nItems here: ");
            for (Item it : cur.getItems()) sb.append(it.getName()).append(", ");
            sb.setLength(sb.length()-2);
        }
        Map<String, Room> exits = cur.getExits();
        if (exits != null && !exits.isEmpty()) {
            sb.append("\n\nExits: ").append(String.join(", ", exits.keySet()));
        }
        return sb.toString();
    }

    public String movePlayer(String direction) {
        Room current = player.getCurrentRoom();

        // Castle gate from Room 29
        if (current == WorldBuilder.getRoom(29) && "west".equalsIgnoreCase(direction)) {
            boolean hasEmeraldKey = player.getInventory().stream()
                    .anyMatch(i -> i.getName().equalsIgnoreCase("Emerald Key"));
            if (!hasEmeraldKey) {
                return "A massive black-iron door bars your path. An emerald-shaped slot glows faintly — you need the Emerald Key.";
            } else {
                Room castle = BlackCastleBuilder.createCastle();
                player.setCurrentRoom(castle);
                System.out.println("[DEBUG] Entered Black Castle.");
                return "The emerald key hums in your hand. The door unlocks and swings open.\nYou step into the Black Castle...\n\n" + look();
            }
        }

        Room next = current.getExit(direction);
        if (next == null) return "You can't go that way.";

        // Climb checks
        if (next instanceof ClimbRoom) {
            boolean hasShoes = player.isEquipped("Shoes") || player.isEquipped("Climbing Shoes");
            boolean hasHook  = player.isEquipped("Grappling Hook");
            ClimbRoom cr = (ClimbRoom) next;
            if (!cr.canClimb(hasShoes, hasHook)) {
                player.takeDamage(20);
                return "You try to climb but lack the proper gear. You slip and take 20 damage. (HP: " + player.getHealth() + ")";
            }
        }

        player.setCurrentRoom(next);
        System.out.println("[DEBUG] Moved to a new room.");
        return look();
    }

    public String pickUpItem(String name) {
        Room cur = player.getCurrentRoom();
        Item found = null;
        for (Item it : new ArrayList<>(cur.getItems())) {
            if (it.getName().equalsIgnoreCase(name)) { found = it; break; }
        }
        if (found == null) return "No item named '" + name + "' here.";
        cur.getItems().remove(found); // prevent duplicates on revisit
        player.getInventory().add(found);
        System.out.println("[DEBUG] Picked up item: " + found.getName());
        return "You pick up the " + found.getName() + ".";
    }

    public String unequipItem(String name) {
        if (name == null || name.trim().isEmpty()) return "Unequip what?";
        player.unequipByName(name.trim());
        System.out.println("[DEBUG] Unequipped: " + name.trim());
        return "You unequip " + name.trim() + ".";
    }

    public String checkGear() {
        return player.equippedSummary();
    }

    public String useItem(String name) {
        if (name == null || name.trim().isEmpty()) return "Use what?";
        String target = name.trim();

        // Find the item in inventory
        Item found = null;
        for (Item it : player.getInventory()) {
            if (it.getName().equalsIgnoreCase(target)) { found = it; break; }
        }
        if (found == null) return "You don't have '" + target + "'.";

        // Contextual rule: Rusty Dagger breaks if used on Room 6 "door"
        if (player.getCurrentRoom() == WorldBuilder.getRoom(6) && found.getName().equalsIgnoreCase("Rusty Dagger")) {
            // Break the dagger
            player.getInventory().remove(found);
            player.unequipByName(found.getName());
            System.out.println("[DEBUG] Rusty Dagger broke on Room 6 door.");
            return "You strike the heavy metal door with your Rusty Dagger.\nThe blade snaps in two — it's useless now.";
        }

        // Equip/activate without consuming
        if (found instanceof Weapon) {
            player.equipWeapon((Weapon) found);
            System.out.println("[DEBUG] Equipped weapon: " + found.getName());
            return found.getName() + " equipped.";
        }
        if (found instanceof Armor) {
            player.equipArmor((Armor) found);
            System.out.println("[DEBUG] Equipped armor: " + found.getName());
            return found.getName() + " equipped.";
        }

        // Generic items equip by name (Shoes, Climbing Shoes, Grappling Hook)
        player.equipByName(found.getName());
        System.out.println("[DEBUG] Equipped item: " + found.getName());
        return found.getName() + " equipped.";
    }

    public String attackEnemy() {
        Room cur = player.getCurrentRoom();
        Enemy target = null;
        if (cur instanceof GoblinRoom) {
            target = ((GoblinRoom) cur).getEnemy();
        } else if (cur instanceof DragonRoom) {
            target = ((DragonRoom) cur).getDragon();
        }
        if (target == null) return "There is nothing here to attack.";

        int dmg = player.attack();
        target.takeDamage(dmg);
        if (!target.isAlive()) {
            if (cur instanceof GoblinRoom) ((GoblinRoom) cur).removeEnemy();
            System.out.println("[DEBUG] Enemy defeated: " + target.getName());
            return "You strike for " + dmg + " and defeat the " + target.getName() + "!";
        }
        player.takeDamage(target.getDamage());
        return "You strike for " + dmg + ". The " + target.getName() + " hits back for " + target.getDamage() + ". (HP: " + player.getHealth() + ")";
    }

    public String solvePuzzle(String answer) {
        Room cur = player.getCurrentRoom();
        if (!(cur instanceof PuzzleRoom)) return "There is no puzzle to solve here.";
        PuzzleRoom pr = (PuzzleRoom) cur;
        boolean ok = pr.solveRiddle(answer);
        System.out.println("[DEBUG] Puzzle attempted. Success=" + ok);
        return ok ? "You solve the riddle: " + pr.getRiddle().getQuestion() + "\nA mechanism clicks somewhere."
                  : "That doesn't seem right.";
    }

    public Player getPlayer() { return player; }
}
