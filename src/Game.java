import javax.swing.SwingUtilities;
import java.util.*;

public class Game {
    private final Map<String, Room> rooms = new HashMap<>();
    private final Player player;

    public Game() {
        player = new Player();
        createRooms();
        linkRooms();
        player.setCurrentRoom(rooms.get("Room1"));
    }

    private void createRooms() {
        for (int i = 1; i <= 60; i++) {
            Room room;
            if (i == 3 || i == 10 || i == 14 || i == 31 || i == 45 || i == 57 || i == 58 || i == 60) {
                room = new ClimbRoom("A steep, climbable section (Room " + i + ").", true, false);
            } else if (i == 60) {
                room = new DragonRoom("A towering lair. The final challenge awaits (Room 60).", new Enemy("Leif the Dragon", 250, 30));
            } else if (i == 26) {
                room = new OldLadyRoom("A small hut with an old lady stirring a cauldron (Room 26).");
            } else if (i == 13 || i == 29 || i == 35 || i == 44) {
                Riddle r;
                switch (i) {
                    case 13: r = new Riddle("What has an eye but cannot see?", "needle"); break;
                    case 29: r = new Riddle("One of us always lies, the other always tells the truth. Which is the liar?", "nord"); break;
                    case 35: r = new Riddle("Find the missing puzzle piece", "piece"); break;
                    default: r = new Riddle("I am born in fear, raised in truth.", "courage"); break;
                }
                room = new PuzzleRoom("A room with an inscription. (Room " + i + ")", r);
            } else if (i % 5 == 0 || i == 11 || i == 15 || i == 23 || i == 28 || i == 38 || i == 43 || i == 53) {
                int hp = 18 + (i / 6) * 6;
                int dmg = 4 + (i / 15);
                room = new GoblinRoom("A dank clearing where a goblin lurks (Room " + i + ").", new Enemy("Goblin " + i, hp, dmg));
            } else if (i == 55) {
                room = new DragonRoom("A crater of blackened earth. A huge presence sleeps nearby (Room 55).", new Enemy("Broodwyrm", 160, 20));
            } else {
                room = new GenericRoom("Quiet corridor and mossy stone (Room " + i + ").");
            }

            // Add items progressively
            if (i == 1) {
                room.addItem(new Item("Shoes", "Basic leather shoes (starting gear)."));
                room.addItem(new Weapon("Short Stick", "A blunt stick. Better than nothing.", 3));
            }
            if (i == 4) {
                room.addItem(new Weapon("Rusty Dagger", "Small rusty dagger.", 6));
                room.addItem(new Armor("Leather Armor", "Thin leather armor.", 2));
            }
            if (i == 8) room.addItem(new Item("Rusty Key", "An old, rusty key."));
            if (i == 9) room.addItem(new Item("Apple", "A fresh apple. Restores a small amount."));
            if (i == 15) {
                room.addItem(new Item("Needle", "A small needle (used for a riddle)." ));
                room.addItem(new Item("Red Ruby", "A small red gem." ));
            }
            if (i == 20) room.addItem(new Item("Climbing Shoes", "Shoes suitable for climbing."));
            if (i == 25) {
                room.addItem(new Item("Bone Key", "A brittle bone-shaped key."));
                room.addItem(new Item("Glowing Red Orb", "A mysterious red orb."));
            }
            if (i == 28) {
                room.addItem(new Armor("Fireproof Underpants", "Protective undergarment.", 1));
                room.addItem(new Item("Map of the World", "Marks places you've visited."));
                room.addItem(new Item("Grappling Hook", "Useful to grab distant ledges."));
            }
            if (i == 31) room.addItem(new Item("Binoculars", "Allow you to see distant details."));
            if (i == 47) room.addItem(new Weapon("Platinum Sword", "A gleaming, extremely sharp sword.", 50));
            if (i == 48) room.addItem(new Armor("Steel Gauntlets", "Sturdy gauntlets.", 6));
            if (i == 51) room.addItem(new Item("Climbing Shoes (Fine)", "Better climbing shoes."));
            if (i == 56) room.addItem(new Item("Fire Staff", "Single-use staff that spits flame."));
            if (i % 7 == 0) room.addItem(new HealingPotion("Healing Potion", "Restores 30 HP.", 30));

            rooms.put("Room" + i, room);
        }
    }

    private void linkRooms() {
        final int width = 10;
        final int height = 6;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int num = y * width + x + 1;
                Room r = rooms.get("Room" + num);
                if (r == null) continue;
                if (y > 0) r.setExit("north", rooms.get("Room" + ((y - 1) * width + x + 1)));
                if (y < height - 1) r.setExit("south", rooms.get("Room" + ((y + 1) * width + x + 1)));
                if (x > 0) r.setExit("west", rooms.get("Room" + (num - 1)));
                if (x < width - 1) r.setExit("east", rooms.get("Room" + (num + 1)));
            }
        }

        // extra branching links
        rooms.get("Room31").setExit("northwest", rooms.get("Room37"));
        rooms.get("Room4").setExit("rope-down", rooms.get("Room3"));
        rooms.get("Room3").setExit("rope-up", rooms.get("Room4"));
        rooms.get("Room29").setExit("west", rooms.get("Room55"));
        rooms.get("Room10").setExit("up", rooms.get("Room14"));
        rooms.get("Room14").setExit("down", rooms.get("Room10"));
        rooms.get("Room48").setExit("crevice", rooms.get("Room49"));
        rooms.get("Room58").setExit("up", rooms.get("Room60"));
        rooms.get("Room60").setExit("down", rooms.get("Room55"));
        rooms.get("Room28").setExit("east", rooms.get("Room30"));
        rooms.get("Room22").setExit("secret-east", rooms.get("Room24"));
        rooms.get("Room37").setExit("south", rooms.get("Room38"));
        rooms.get("Room52").setExit("west", rooms.get("Room55"));
    }

    public String movePlayer(String direction) {
        Room current = player.getCurrentRoom();
        Room next = current.getExit(direction);
        if (next == null) return "You can't go that way.";

        if (next instanceof ClimbRoom) {
            boolean hasShoes = player.getInventory().stream().anyMatch(i -> i.getName().toLowerCase().contains("climbing"));
            boolean hasHook = player.getInventory().stream().anyMatch(i -> i.getName().toLowerCase().contains("grappling"));
            ClimbRoom cr = (ClimbRoom) next;
            if (!cr.canClimb(hasShoes, hasHook)) {
                player.takeDamage(20);
                return "You try to climb but lack the gear. You slip and take 20 damage. (HP: " + player.getHealth() + ")";
            }
        }

        player.setCurrentRoom(next);
        return next.getDescription();
    }

    public String attackEnemy() {
        Room current = player.getCurrentRoom();
        if (current instanceof GoblinRoom) {
            GoblinRoom goblinRoom = (GoblinRoom) current;
            Enemy e = goblinRoom.getEnemy();
            if (e == null || !e.isAlive()) return "There's no living enemy here.";
            int playerDmg = player.attack();
            goblinRoom.attackEnemy(playerDmg);
            StringBuilder sb = new StringBuilder();
            sb.append("You hit ").append(e.getName()).append(" for ").append(playerDmg).append(" damage.\n");
            if (!e.isAlive()) {
                sb.append("You defeated ").append(e.getName()).append("!\n");
                for (Item it : new ArrayList<>(goblinRoom.getItems())) {
                    player.addItem(it);
                    // optional: goblinRoom.getItems().remove(it);
                }
                return sb.toString();
            } else {
                int dmg = e.getDamage();
                player.takeDamage(dmg);
                sb.append(e.getName()).append(" hits you for ").append(dmg).append(" damage. (HP: ").append(player.getHealth()).append(")\n");
                if (!player.isAlive()) sb.append("You have been slain. Game Over.");
                return sb.toString();
            }
        }

        if (current instanceof DragonRoom) {
            DragonRoom dr = (DragonRoom) current;
            Enemy e = dr.getDragon();
            if (e == null || !e.isAlive()) return "There's no living dragon here.";
            int playerDmg = player.attack();
            dr.attackDragon(playerDmg);
            StringBuilder sb = new StringBuilder();
            sb.append("You strike ").append(e.getName()).append(" for ").append(playerDmg).append(" damage.\n");
            if (!e.isAlive()) {
                sb.append("You felled ").append(e.getName()).append("! The final challenge is complete — you win!\n");
                sb.append("Congratulations — you reclaimed the Lost Relic of Galdor!");
                return sb.toString();
            } else {
                int dmg = e.getDamage();
                player.takeDamage(dmg);
                sb.append(e.getName()).append(" breathes fire and hits you for ").append(dmg).append(" damage. (HP: ").append(player.getHealth()).append(")\n");
                if (!player.isAlive()) sb.append("You have been burnt to ash. Game Over.");
                return sb.toString();
            }
        }

        return "There is nothing to attack here.";
    }

    public String solvePuzzle(String attempt) {
        Room current = player.getCurrentRoom();
        if (current instanceof PuzzleRoom) {
            PuzzleRoom pr = (PuzzleRoom) current;
            boolean ok = pr.solveRiddle(attempt);
            if (ok) {
                for (Item it : new ArrayList<>(pr.getItems())) {
                    player.addItem(it);
                }
                return "Correct — the riddle yields a reward and a secret opens.";
            } else {
                return "Incorrect answer. The inscription remains silent.";
            }
        }
        return "There is no riddle to solve here.";
    }

    public String pickUpItem(String itemName) {
        Room current = player.getCurrentRoom();
        Item found = null;
        for (Item it : current.getItems()) {
            if (it.getName().equalsIgnoreCase(itemName)) { found = it; break; }
        }
        if (found == null) return "There is no '" + itemName + "' here.";
        current.getItems().remove(found);
        player.addItem(found);
        return "You picked up: " + found.getName();
    }

    public String useItem(String itemName) {
        Item found = null;
        for (Item it : player.getInventory()) {
            if (it.getName().equalsIgnoreCase(itemName)) { found = it; break; }
        }
        if (found == null) return "You don't have a '" + itemName + "'.";
        if (found instanceof HealingPotion) {
            HealingPotion hp = (HealingPotion) found;
            player.getInventory().remove(found);
            player.heal(hp.getHealAmount());
            return "You drink the potion and restore " + hp.getHealAmount() + " HP. (HP: " + player.getHealth() + ")";
        }
        if (found instanceof Weapon) {
            player.equipWeapon((Weapon) found);
            return "You equip the " + found.getName() + ".";
        }
        if (found instanceof Armor) {
            player.equipArmor((Armor) found);
            return "You put on " + found.getName() + ".";
        }
        return "You can't use that item right now.";
    }

    public Player getPlayer() { return player; }
    public Room getRoom(String key) { return rooms.get(key); }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Game());
    }
}
