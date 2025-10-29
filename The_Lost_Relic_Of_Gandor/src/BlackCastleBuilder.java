
import java.util.HashMap;
import java.util.Map;

/**
 * Builds the three levels of the Black Castle.
 * Extends WorldBuilder to use shared registration utilities and Room behaviors.
 * Includes riddles, enemies, and combat rewards per story design.
 */
public class BlackCastleBuilder extends WorldBuilder {

    private static final Map<Integer, Room> castleRooms = new HashMap<>();

    /** Build all castle levels and return the entrance (Level 1) */
    public static Room createCastle() {

        // ---------- Level 1 ----------
        Room lvl1 = regC(1, new PuzzleRoom(
            "BLACK CASTLE — Level 1:\n" +
            "You step through the emerald door into a vast, candlelit hall. " +
            "A chandelier glows above a grand table overflowing with fruit and bread — yet it feels wrong. " +
            "The air is heavy and still. A massive stone golem blocks the staircase to the north.",
            new Riddle("Three riddles bar your path. You may fight or answer.", "your word")));

        // The guardian golem
        Enemy stoneGolem = new Enemy("Stone Golem", 120, 15);
        Room golemRoom = regC(2, new GoblinRoom(
            "The Stone Golem towers before the staircase. Its eyes gleam faintly with runes. " +
            "You sense you can either answer riddles... or fight.",
            stoneGolem));
        // Items rewarded if beaten
        golemRoom.addItem(new Item("Goblet of Gandor", "A relic that radiates ancient power."));
        golemRoom.addItem(new Item("Poisonous Darts", "Small, deadly darts coated with venom."));

        // ---------- Level 2 ----------
        Enemy treeMonster = new Enemy("Twisted Tree Guardian", 180, 22);
        Room lvl2 = regC(3, new DragonRoom(
            "BLACK CASTLE — Level 2:\n" +
            "You ascend a winding staircase into a high walkway encircling the vast dining hall below. " +
            "Vines creep along the walls. A silver necklace glints in the shadows — it looks cursed.\n" +
            "As you turn toward the next level, roots writhe and merge into a giant, living tree!",
            treeMonster));

        // ---------- Level 3 ----------
        Enemy finalBoss = new Enemy("Spectral Titan", 250, 30);
        Room lvl3 = regC(4, new DragonRoom(
            "BLACK CASTLE — Level 3:\n" +
            "You enter the final chamber. A grand altar rests in the center, its surface engraved with the sigil of Gandor. " +
            "Ghostly light fills the room. Placing the Goblet of Gandor on the altar awakens a divine figure — " +
            "then a colossal shadow merges with it: the Spectral Titan!",
            finalBoss));

        // ---------- Item placement ----------
        lvl2.addItem(new Item("Silver Necklace", "A cursed trinket best left untouched."));
        lvl3.addItem(new Armor("Steel Armour", "Heavy armor fit for a champion.", 8));
        lvl3.addItem(new Armor("Royal Cape", "A flowing cape of nobility.", 2));
        lvl3.addItem(new Item("Emerald Key", "A symbol of triumph — opens the castle door from within."));

        // ---------- Exits ----------
        lvl1.setExit("north", golemRoom);
        golemRoom.setExit("south", lvl1);
        golemRoom.setExit("north", lvl2);
        lvl2.setExit("south", golemRoom);
        lvl2.setExit("north", lvl3);
        lvl3.setExit("south", lvl2);

        // ---------- Register riddles ----------
        PuzzleRoom riddleEntry = (PuzzleRoom) lvl1;
        riddleEntry.addItem(new Item("Note", "The golem rumbles: 'What must you keep when you give it away?' — answer: 'your word'."));

                lvl1.setExit("south", WorldBuilder.getRoom(29));
// ---------- Return castle entry ----------
        return lvl1;
    }

    /** Helper to register castle rooms separately */
    private static Room regC(int id, Room room) {
        castleRooms.put(id, room);
        return room;
    }

    public static Room getCastleRoom(int id) {
        return castleRooms.get(id);
    }
}
