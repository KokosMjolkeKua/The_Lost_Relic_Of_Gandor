
import java.util.HashMap;
import java.util.Map;

public class WorldBuilder {

    private static final Map<Integer, Room> rooms = new HashMap<>();

    public static Room createWorld() {
        // ---------- Instantiate all rooms ----------
        // Using appropriate subclasses when it adds behavior clarity.
        Room r1  = reg(1,  new GenericRoom("You stand in a quiet forest clearing. The way behind is blocked by dense trees. A single path leads north. Your shoes, previously stored in your inventory, ready to be equipped."));
        Room r2  = reg(2,  new GenericRoom("You step north into a mossy forest. Sunlight filters through thick branches. The air smells damp... The path splits leading north and west."));
        Room r3  = reg(3,  new ClimbRoom("The northern path ends at a muddy cliff. Sunlight warms the rocks. You can attempt to climb... But, without shoes, a fall is deadly.", true, false));
        Room r4  = reg(4,  new GenericRoom("Atop the cliff, you find a small campsite with some still good looking items scattered about.. The clearing offers a distant view of an ominous castle. The only path leads back where you came from, you can climb down using the rope attached to the campsite."));
        Room r5  = reg(5,  new GenericRoom("Heading west, vegetation thickens. The wind rustles in the leaves, but nothing is visible... The trail continues west."));
        Room r6  = reg(6,  new GenericRoom("You close in on a clearing and see a big metal and wood door with a rusty keyhole.. the door leads north. There is also a path to the west. Even if you tried, the door doesnt look movable.."));
        Room r7  = reg(7,  new PuzzleRoom("A stone wall features a mural depicting a great battle. Something small glimmers within. There are paths leading north and west.", new Riddle("What do you use to pry the glimmering square?", "rusty dagger")));
        Room r8  = reg(8,  new GenericRoom("A small clearing opens up, and you find a pile of burnt clothes with something shimmering atop it."));
        Room r9  = reg(9,  new GenericRoom("Brush and barbed wire fill the area. A single Apple hangs in the brush tangled in the wire."));
        Room r10 = reg(10, new GenericRoom("Past the unlocked door is a long corridor. At the end a clearing opens up to reveal the path continuing north. To the east you also see a climbable hill, but it seems to be much more advanced than what you've faced before... If you insist on climbing ill‑prepared, you will fall.")); //(needs climbing shoes or a grappling hook)
        Room r11 = reg(11, new GoblinRoom("A goblin is here. You hide in a bush to decide what to do. The path to the west is blocked by the goblin.. There is a path to the north.", new Enemy("Goblin", 12, 3)));
        Room r12 = reg(12, new GenericRoom("A clearing with butterflies drifting lazily... Its quite beautiful. The path continues north."));
        Room r13 = reg(13, new PuzzleRoom("A rock wall blocks the way. There is an inscription: 'What has an eye but cannot see?'", new Riddle("What has an eye but cannot see?", "needle")));
        Room r14 = reg(14, new GenericRoom("After climbing the treacherous hill you make it to the top. You take a second to look at the scenery and see the castle much clearer than before. The path continues north and west.")); //you reach this path from climbing from room 10
        Room r15 = reg(15, new GoblinRoom("A GOBLIN JUMPS OUT FROM THE SHADOWS! If you hesitate for too long, you will give him the upper hand! There is no way to escape other than the way you came!", new Enemy("Goblin", 16, 4))); //Defeating it yields a needle and a red ruby.
        Room r16 = reg(16, new GenericRoom("Dense forest under light rain. Paths lead south, east, and north."));
        Room r17 = reg(17, new GenericRoom("There is a small clearing. A mural among the flowers gives you chills..."));
        Room r18 = reg(18, new GenericRoom("A harmless slime crawls toward you. You think he looks friendly enough..?"));
        Room r19 = reg(19, new GenericRoom("More dense forest... Paths lead south and east."));
        Room r20 = reg(20, new GenericRoom("A long-dead campfire. A skeleton rests beside a small sack..."));
        Room r21 = reg(21, new GenericRoom("A goblin spots you with wide eyes and darts east. The path continue east and north."));
        Room r22 = reg(22, new GenericRoom("A path stretches long with murky waters on either side — swamp-like and dreary... You should be attentive to where you plant your feet.. The path continues north."));
        Room r23 = reg(23, new GoblinRoom("A large battle hardened goblin stands before you. You need to defeat him to pass. He seems slow, as long as you defeat him quickly..", new Enemy("Hardened Goblin", 20, 5))); //He drops arrows and a broken bow. A path leads east.
        Room r24 = reg(24, new GenericRoom("Swampy ground underfoot. Paths lead east and south."));
        Room r25 = reg(25, new GenericRoom("A small chest is hidden in brush. Inside are a glowing red orb, a bone key, and two rubies."));
        Room r26 = reg(26, new OldLadyRoom("A crooked hut in the swamp. Eerie music and cackling seep through the window. An old lady stirs a pot inside… (multiple approaches possible)."));
        Room r27 = reg(27, new GenericRoom("A clearing. To the south sprawls a goblin camp; the goblin you chased earlier sprints toward its gate. Only a path south."));
        Room r28 = reg(28, new GenericRoom("GOBLIN CAMP — small but fortified. Entrance guarded by three goblins."));
        Room r29 = reg(29, new PuzzleRoom("Two golems flank a massive northern door. Solve their riddle and present a bone key and sapphire to pass. Fighting is pointless.", new Riddle("One of us tells the truth and the other lies — which of us lies?", "nord")));
        Room r30 = reg(30, new GenericRoom("A clearing with a path south."));
        Room r31 = reg(31, new ClimbRoom("A steep, climbable hill. From the top you spot the menacing castle to the west, and a secret northwest path between 32 and 37.", true, false));
        Room r32 = reg(32, new GenericRoom("Dense forest. A path heads north. If you visited room 31, a secret path appears to the east."));
        Room r33 = reg(33, new GenericRoom("Intersections of forest trails. Paths branch east and west."));
        Room r34 = reg(34, new GenericRoom("A quiet lane. A path runs south."));
        Room r35 = reg(35, new PuzzleRoom("A stone formation resembling a jigsaw with one piece missing. Completing it reveals a mural: a scantily clad figure kneeling before a dragon with a red orb.", new Riddle("What completes the puzzle?", "piece")));
        Room r36 = reg(36, new GenericRoom("A narrow trail with a path north."));
        Room r37 = reg(37, new GenericRoom("A secret path opens into a small clearing. You hear rustling to the south. Paths: east, north, south."));
        Room r38 = reg(38, new GenericRoom("A goblin holds prisoners with his back turned. Act fast to save them!"));
        Room r39 = reg(39, new GenericRoom("The path ends at rock. A glimmer hides a tiny chest with an apple."));
        Room r40 = reg(40, new GenericRoom("A cliff edge with a rusted knight's remains. Only a steel helmet survived."));
        Room r41 = reg(41, new GenericRoom("A goblin patrols here. With a black robe you can sneak by. A path continues north."));
        Room r42 = reg(42, new GenericRoom("A broad clearing. Paths lead east and west."));
        Room r43 = reg(43, new GenericRoom("A goblin guards this area. Fight for a ruby, or sneak by with the black robe. A path continues north."));
        Room r44 = reg(44, new PuzzleRoom("A chest bears a riddle: 'I am born in fear, raised in truth, and come to my own in deed.'", new Riddle("I am born in fear, raised in truth, and come to my own in deed. What am I?", "courage")));
        Room r45 = reg(45, new ClimbRoom("A climbable southern face (needs climbing shoes AND grappling hook). A path also leads north.", true, true));
        Room r46 = reg(46, new GenericRoom("Atop the rise. You cannot go back the way you came. Paths lead north and west."));
        Room r47 = reg(47, new GenericRoom("A small rock holds a platinum sword embedded within. It stirs only for the worthy."));
        Room r48 = reg(48, new GenericRoom("Thick brush conceals a tight crevice to the east. Between two rocks lies a pair of steel gauntlets."));
        Room r49 = reg(49, new GenericRoom("A northern track continues (or you squeeze here from the crevice in room 48; one-way from 48→49)."));
        Room r50 = reg(50, new GenericRoom("Paths lead north and east."));
        Room r51 = reg(51, new GenericRoom("A peaceful flower garden. If you sit for a while, you might notice something hidden."));
        Room r52 = reg(52, new GenericRoom("A crossroads: west leads toward a barren canyon; paths also run north and east."));
        Room r53 = reg(53, new GenericRoom("A skittish goblin is here. He might befriend you… or drop a ruby if slain."));
        Room r54 = reg(54, new GenericRoom("A mural with a recess sized for a ruby. Inset the gem to reveal an image of a woman holding an apple."));
        Room r55 = reg(55, new DragonRoom("The Dragon’s Den: a vast scorched crater. A black‑and‑red dragon sleeps amid embers.", new Enemy("Leif, the Dragon", 200, 20)));
        Room r56 = reg(56, new GenericRoom("A lone staff rests here. Perhaps someone wise could reveal its power."));
        Room r57 = reg(57, new ClimbRoom("Southward ascent (section 1). Any climbing shoes will do here.", true, false));
        Room r58 = reg(58, new ClimbRoom("Section 2 of the climb. A path west to a cliff; further south requires shoes and a grappling hook.", true, true));
        Room r59 = reg(59, new GenericRoom("A high cliff. The castle looms to the south. A pair of binoculars lies nearby."));
        Room r60 = reg(60, new GenericRoom("The mountain top overlooks the crater to the north and the castle to the south. With binoculars you can spot a staircase into the castle."));

        System.out.println("[DEBUG] Registering rooms and placing items...");
        // ---------- Place Items (key highlights) ----------
        // r1 starting gear is assumed on player; keep world sparse to avoid dupes.
        r4.addItem(new Weapon("Rusty Dagger", "A small, pitted dagger. Might pry things loose.", 6));
        r4.addItem(new Armor("Leather Armour", "Thin leather armor.", 2));
        r4.addItem(new Armor("Fur Gauntlets", "Warm, fuzzy gauntlets.", 1));
        r8.addItem(new Item("Rusty Key", "An old key with a corroded bit."));
        r9.addItem(new Item("Apple", "A crisp red apple."));
        // Door in r6 uses Rusty Key (handled in command logic).
        // r10 climb east requires either shoes or grappling hook (handle as logic in Game).
        r15.addItem(new Item("Needle", "Small needle (riddle key)."));
        r15.addItem(new Item("Red Ruby", "A bright red gemstone."));
        r20.addItem(new Item("Climbing Shoes", "Shoes suitable for climbing."));
        r23.addItem(new Item("Arrows x5", "A small bundle of arrows."));
        r23.addItem(new Item("Broken Bow", "A bow with a snapped limb."));
        r25.addItem(new Item("Glowing Red Orb", "It pulses faintly with inner light."));
        r25.addItem(new Item("Bone Key", "A key carved from bone."));
        r25.addItem(new Item("Ruby", "A small red gem."));
        r25.addItem(new Item("Ruby", "A small red gem."));
        r40.addItem(new Armor("Steel Helmet", "Sturdy and polished despite age.", 4));
        r43.addItem(new Item("Ruby", "A small red gem."));
        r44.addItem(new Item("Jigsaw Piece", "A stone puzzle piece — seems important."));
        r48.addItem(new Armor("Steel Gauntlets", "Heavy gauntlets of steel.", 5));
        r51.addItem(new Item("Climbing Shoes", "Another pair hidden among flowers."));
        r55.addItem(new Item("Emerald Key", "A gleaming green key."));
        r55.addItem(new Armor("Royal Cape", "A regal cape that billows dramatically.", 1));
        r55.addItem(new Item("Dragon Horn", "A horn to summon the dragon — one time."));
        r56.addItem(new Weapon("Fire Staff", "A staff that spits fire. Rumored one-time use.", 999));
        r59.addItem(new Item("Binoculars", "You can see very far with these."));

        // Optional: world map and other rewards from r28 after boss logic
        r28.addItem(new Item("World Map", "Records your explored paths."));
        r28.addItem(new Item("Fireproof Underpants", "Remarkably heat resistant."));
        r28.addItem(new Item("Ruby", "A small red gem."));
        r28.addItem(new Item("Ruby", "A small red gem."));
        r28.addItem(new Item("Ruby", "A small red gem."));
        r28.addItem(new Item("Ruby", "A small red gem."));
        r28.addItem(new Item("Ruby", "A small red gem."));
        r28.addItem(new Item("Stone Key", "A heavy key carved of stone."));

        // ---------- Link Exits (per design doc) ----------
        // Early region (1–10)
        r1.setExit("north", r2);
        r2.setExit("south", r1);
        r2.setExit("north", r3);
        r2.setExit("west", r5);
        r3.setExit("south", r2);
        r3.setExit("north", r4);
        r4.setExit("south", r3);
        r5.setExit("east", r2);
        r5.setExit("west", r6);
        r6.setExit("east", r5);
        r6.setExit("west", r7);
        r6.setExit("north", r10); // behind locked door
        r29.setExit("west", BlackCastleBuilder.createCastle()); // two-way connection
        r7.setExit("east", r6);
        r7.setExit("north", r8);
        r7.setExit("west", r7); // mural wall; no true exit west but keep placeholder
        r8.setExit("south", r7);
        r8.setExit("north", r9);
        r9.setExit("south", r8);
        r10.setExit("south", r6);
        r10.setExit("north", r11);
        r10.setExit("east", r14);
        r14.setExit("west", r10); // added reverse climb path // climb path up to r14

        // Mid forest (11–21)
        r11.setExit("south", r10);
        r11.setExit("north", r12);
        r12.setExit("south", r11);
        r12.setExit("north", r13);
        r13.setExit("south", r12);
        // r13 north is gated by Needle + riddle; when solved -> r29
        r14.setExit("west", r11);
        r11.setExit("east", r14); // added reverse path
        r14.setExit("north", r15);
        r15.setExit("south", r14);
        r11.setExit("east", r16); // unblock via r14 perspective
        r16.setExit("west", r11);
        r16.setExit("south", r17);
        r16.setExit("east", r19);
        r16.setExit("north", r18);
        r17.setExit("north", r16);
        r18.setExit("south", r16);
        r19.setExit("west", r16);
        r19.setExit("south", r20);
        r19.setExit("east", r21);
        r20.setExit("north", r19);
        r21.setExit("west", r19);
        r21.setExit("east", r27);
        r21.setExit("north", r22);

        // Swamp strip (22–26)
        r22.setExit("south", r21);
        r22.setExit("north", r23);
        r23.setExit("south", r22);
        r23.setExit("east", r24);
        r24.setExit("west", r23);
        r24.setExit("south", r26);
        r24.setExit("east", r25);
        r25.setExit("west", r24);
        r26.setExit("north", r24);

        // Goblin camp branch (27–30)
        r27.setExit("south", r28);
        r27.setExit("west", r21);
        r28.setExit("north", r27);
        r29.setExit("south", r13); // back toward the riddle door
        r29.setExit("east", r30);
        r30.setExit("west", r29);
        r30.setExit("south", r31);
        r31.setExit("north", r30);

        // Secret path reveal area (32–40)
        r29.setExit("north", r32); // after solving and presenting items
        r32.setExit("south", r29);
        r32.setExit("north", r33);
        r32.setExit("east", r37); // appears only after visiting r31 (handle in logic)
        r33.setExit("south", r32);
        r33.setExit("east", r36);
        r33.setExit("west", r34);
        r34.setExit("north", r33);
        r34.setExit("south", r35);
        r35.setExit("north", r34);
        r36.setExit("north", r41);
        r36.setExit("south", r33);
        r37.setExit("east", r39);
        r37.setExit("north", r40);
        r37.setExit("south", r38);
        r37.setExit("west", r32);
        r38.setExit("north", r37);
        r39.setExit("west", r37);
        r40.setExit("south", r37);

        // Robe/sneak arc (41–46)
        r41.setExit("south", r36);
        r41.setExit("north", r42);
        r42.setExit("west", r45);
        r42.setExit("east", r43);
        r42.setExit("south", r41);
        r43.setExit("west", r42);
        r43.setExit("north", r44);
        r44.setExit("south", r43);
        r45.setExit("north", r42);
        r45.setExit("south", r46); // climb requires shoes+hook
        r46.setExit("north", r45);
        r46.setExit("west", r47);
        r46.setExit("east", r48);
        r47.setExit("east", r46);
        r48.setExit("west", r46);
        r48.setExit("east", r49);

        // Cliff & garden arc (49–60)
        r49.setExit("north", r50);
        r50.setExit("south", r49);
        r50.setExit("east", r51);
        r50.setExit("north", r52);
        r51.setExit("west", r50);
        r52.setExit("south", r50);
        r52.setExit("east", r54);
        r52.setExit("north", r53);
        r52.setExit("west", r55);
        r53.setExit("south", r52);
        r54.setExit("west", r52);
        r55.setExit("east", r52);
        r55.setExit("west", r56);
        r55.setExit("south", r57); // climb sequence downward
        r56.setExit("east", r55);
        r57.setExit("north", r55);
        r57.setExit("south", r58);
        r58.setExit("north", r57);
        r58.setExit("west", r59);
        r58.setExit("south", r60);
        r59.setExit("east", r58);
        r60.setExit("north", r58);

        System.out.println("[DEBUG] WorldBuilder complete. Start at Room 1.");
        return r1;
    }

    /** Helper to register by id */
    private static Room reg(int id, Room room) {
        rooms.put(id, room);
        return room;
    }

    /** Access a room by numeric id (optional) */
    public static Room getRoom(int id) {
        return rooms.get(id);
    }
}
