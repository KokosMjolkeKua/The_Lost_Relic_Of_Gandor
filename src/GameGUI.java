import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import java.util.*;
import javax.imageio.ImageIO;
import javax.swing.Timer;


public class GameGUI extends JFrame {

    // ======= Config =======
    private static final int FADE_DURATION_MS = 600;     // crossfade duration for images/colors
    private static final String IMAGE_DIR = "images";     // where you store room/scene images
    private static final String AVATAR_DIR = "images/avatars"; // optional avatar images by gear
    private static final Font FONT_MONO = new Font(Font.MONOSPACED, Font.PLAIN, 14);
    private static final Font FONT_UI   = new Font("Serif", Font.PLAIN, 16);

    // ======= Game & State =======
    private final Game game;

    // ======= UI: Left/Center =======
    private final CrossfadeImagePanel sceneImagePanel = new CrossfadeImagePanel();
    private final JTextArea storyArea = new JTextArea();
    private final JTextField commandField = new JTextField();

    // ======= UI: Right =======
    private final JProgressBar hpBar = new JProgressBar(0, 100);
    private final DefaultListModel<String> inventoryModel = new DefaultListModel<>();
    private final JList<String> inventoryList = new JList<>(inventoryModel);
    private final JLabel avatarLabel = new JLabel("", SwingConstants.CENTER);

    private final JButton btnInventory = new JButton("Inventory");
    private final JButton btnHelp      = new JButton("Help");
    private final JButton btnHint      = new JButton("Hint");
    private final JButton btnSpeak     = new JButton("Speak");

    // Containers for easy theme application
    private final JPanel root = new JPanel(new BorderLayout());
    private final JPanel leftStack = new JPanel();
    private final JPanel rightSide = new JPanel(new BorderLayout());
    private final JPanel rightTop  = new JPanel(new BorderLayout());
    private final JPanel rightBottom = new JPanel(new BorderLayout());

    // For color theme fading
    private Color currentBg = new Color(245, 240, 225);
    private Color currentFg = new Color(40, 30, 20);
    private Color currentAccent = new Color(120, 85, 60);

    public GameGUI(Game game) {
        super("The Lost Relic of Galdor");
        this.game = game;

        initWindow();
        buildLayout();
        wireActions();

        // Show initial state
        appendStory(game.look());
        updateHP();
        refreshInventory();
        applyThemeForCurrentRoom(true);
        updateSceneImageForCurrentRoom(true);

        setVisible(true);
    }

    private void initWindow() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 750);
        setLocationRelativeTo(null);
        setContentPane(root);
    }

    private void buildLayout() {
        // Root spacing
        root.setBorder(new EmptyBorder(10, 10, 10, 10));

        // ----- LEFT/CENTER STACK -----
        leftStack.setLayout(new BoxLayout(leftStack, BoxLayout.Y_AXIS));

        // (1) Scene image on top
        sceneImagePanel.setPreferredSize(new Dimension(700, 260));
        sceneImagePanel.setBorder(BorderFactory.createLineBorder(new Color(0,0,0,40)));

        // (2) Story text area
        storyArea.setEditable(false);
        storyArea.setWrapStyleWord(true);
        storyArea.setLineWrap(true);
        storyArea.setFont(FONT_UI);
        storyArea.setBorder(new EmptyBorder(10, 10, 10, 10));
        JScrollPane storyScroll = new JScrollPane(storyArea);
        storyScroll.setPreferredSize(new Dimension(700, 320));

        // (3) Command input field
        commandField.setFont(FONT_MONO);
        commandField.setBorder(new EmptyBorder(8, 10, 8, 10));
        commandField.setToolTipText("Type commands like: north | take key | use potion | equip sword | solve riddle");

        // Add to left stack
        leftStack.add(sceneImagePanel);
        leftStack.add(Box.createVerticalStrut(10));
        leftStack.add(storyScroll);
        leftStack.add(Box.createVerticalStrut(10));
        leftStack.add(commandField);

        // ----- RIGHT SIDE -----
        // (4) Top: HP + Inventory
        JPanel hpPanel = new JPanel(new BorderLayout());
        JLabel hpLbl = new JLabel("HP", SwingConstants.LEFT);
        hpLbl.setBorder(new EmptyBorder(0, 0, 5, 0));
        hpPanel.add(hpLbl, BorderLayout.NORTH);
        hpBar.setStringPainted(true);
        hpBar.setValue(100);
        hpBar.setPreferredSize(new Dimension(300, 24));
        hpPanel.add(hpBar, BorderLayout.CENTER);
        hpPanel.setBorder(new EmptyBorder(0, 0, 10, 0));

        JLabel invLbl = new JLabel("Inventory", SwingConstants.LEFT);
        invLbl.setBorder(new EmptyBorder(10, 0, 5, 0));
        inventoryList.setVisibleRowCount(10);
        JScrollPane invScroll = new JScrollPane(inventoryList);

        JPanel rightTopInner = new JPanel();
        rightTopInner.setLayout(new BoxLayout(rightTopInner, BoxLayout.Y_AXIS));
        rightTopInner.add(hpPanel);
        rightTopInner.add(invLbl);
        rightTopInner.add(invScroll);
        rightTopInner.setBorder(new EmptyBorder(0, 10, 10, 10));
        rightTop.add(rightTopInner, BorderLayout.CENTER);

        // (5) Bottom: Buttons + Avatar
        JPanel buttonRow = new JPanel(new GridLayout(2, 2, 8, 8));
        for (JButton b : new JButton[]{btnInventory, btnHelp, btnHint, btnSpeak}) {
            b.setFocusPainted(false);
            buttonRow.add(b);
        }

        avatarLabel.setPreferredSize(new Dimension(120, 120));
        avatarLabel.setBorder(BorderFactory.createLineBorder(new Color(0,0,0,40)));
        JPanel avatarWrap = new JPanel(new BorderLayout());
        avatarWrap.add(avatarLabel, BorderLayout.CENTER);
        avatarWrap.setBorder(new EmptyBorder(0, 10, 0, 10));

        JPanel bottomInner = new JPanel(new BorderLayout(10, 10));
        bottomInner.add(buttonRow, BorderLayout.CENTER);
        bottomInner.add(avatarWrap, BorderLayout.EAST);
        bottomInner.setBorder(new EmptyBorder(10, 10, 10, 10));
        rightBottom.add(bottomInner, BorderLayout.CENTER);

        // Assemble right side
        rightSide.add(rightTop, BorderLayout.CENTER);
        rightSide.add(rightBottom, BorderLayout.SOUTH);
        rightSide.setPreferredSize(new Dimension(360, 0));

        // Add to root
        root.add(leftStack, BorderLayout.CENTER);
        root.add(rightSide, BorderLayout.EAST);
    }

    private void wireActions() {
        // Enter submits command
        commandField.addActionListener(e -> {
            String cmd = commandField.getText().trim();
            if (!cmd.isEmpty()) {
                handleCommand(cmd);
            }
            commandField.setText("");
        });

        btnInventory.addActionListener(e -> {
            String inv = getInventoryText();
            appendStory(inv.isEmpty() ? "Your inventory is empty." : "Inventory:\n" + inv);
        });

        btnHelp.addActionListener(e -> showHelp());
        btnHint.addActionListener(e -> appendStory("Hint: Explore thoroughly. Try LOOK, and experiment with USE, EQUIP, and SOLVE in puzzle rooms."));
        btnSpeak.addActionListener(e -> performSpeakAction());
    }

    // ======= Command handling =======
    private void handleCommand(String input) {
        String cmd = input.toLowerCase(Locale.ROOT).trim();
        String out = null;

        // Movement
        if (cmd.matches("^(north|south|east|west)$")) {
            out = game.movePlayer(cmd);
        } else if (cmd.startsWith("go ") || cmd.startsWith("move ")) {
            String dir = cmd.replaceFirst("^(go|move)\\s+", "");
            out = game.movePlayer(dir);
        } else if (cmd.equals("look") || cmd.equals("l")) {
            out = game.look();
        } else if (cmd.startsWith("take ") || cmd.startsWith("pickup ") || cmd.startsWith("pick up ")) {
            String item = cmd.replaceFirst("^(take|pickup|pick up)\\s+", "");
            out = game.pickUpItem(item);
        } else if (cmd.startsWith("use ") || cmd.startsWith("drink ")) {
            String item = cmd.replaceFirst("^(use|drink)\\s+", "");
            out = game.useItem(item);
        } else if (cmd.startsWith("equip ")) {
            String item = cmd.replaceFirst("^equip\\s+", "");
            out = game.useItem(item); // equipping is handled by useItem if it's a Weapon/Armor
        } else if (cmd.startsWith("unequip ")) {
            String item = cmd.replaceFirst("^unequip\\s+", "");
            out = game.unequipItem(item);
        } else if (cmd.equals("attack") || cmd.startsWith("attack ")) {
            out = game.attackEnemy();
        } else if (cmd.startsWith("solve ")) {
            String answer = cmd.replaceFirst("^solve\\s+", "");
            out = game.solvePuzzle(answer);
        } else if (cmd.equals("inventory") || cmd.equals("inv") || cmd.equals("i")) {
            String inv = getInventoryText();
            out = inv.isEmpty() ? "Your inventory is empty." : "Inventory:\n" + inv;
        } else if (cmd.equals("gear") || cmd.equals("equipment")) {
            out = game.checkGear();
        } else if (cmd.equals("speak")) {
            out = performSpeakAction();
        } else {
            out = "I don't understand that. Try: look, north/south/east/west, take <item>, use <item>, equip <item>, solve <answer>.";
        }

        appendStory(out);
        updateHP();
        refreshInventory();
        applyThemeForCurrentRoom(false);
        updateSceneImageForCurrentRoom(false);
        updateAvatarForGear();
    }

    // ======= Speak button/command =======
    private String performSpeakAction() {
        Room cur = game.getPlayer().getCurrentRoom();
        String roomType = cur.getClass().getSimpleName();
        // Very lightweight logic: only a few rooms have someone to talk to
        if (roomType.equalsIgnoreCase("OldLadyRoom")) {
            String msg = "You greet the old lady. She eyes you kindly and mutters about 'paths hidden in plain sight.'";
            appendStory(msg);
            return msg;
        } else if (roomType.equalsIgnoreCase("GoblinRoom")) {
            String msg = "You try speaking to the goblin. It snarls back — not very conversational.";
            appendStory(msg);
            return msg;
        } else if (roomType.equalsIgnoreCase("DragonRoom")) {
            String msg = "You address the dragon. It rumbles, smoke curling from its nostrils... Best choose your next action wisely.";
            appendStory(msg);
            return msg;
        }
        String msg = "Not very effective.";
        appendStory(msg);
        return msg;
    }

    // ======= Story / Output =======
    private void appendStory(String text) {
        if (text == null || text.isEmpty()) return;
        storyArea.append(text + "\n\n");
        storyArea.setCaretPosition(storyArea.getDocument().getLength());
    }

    // ======= HP / Inventory =======
    private void updateHP() {
        int hp = game.getPlayer().getHealth();
        hpBar.setValue(hp);
        hpBar.setString(hp + " / 100");
    }

    private void refreshInventory() {
        inventoryModel.clear();
        List<Item> inv = game.getPlayer().getInventory();
        for (Item it : inv) inventoryModel.addElement(it.getName());
    }

    private String getInventoryText() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < inventoryModel.size(); i++) {
            sb.append("- ").append(inventoryModel.get(i)).append("\n");
        }
        return sb.toString().trim();
    }

    private void updateAvatarForGear() {
        // Optional: change avatar image based on equipped items
        // For now, display a simple placeholder and future-proof hook.
        tryLoadImageToLabel(avatarLabel, AVATAR_DIR + "/default.png");
    }

    // ======= Themes =======
    private static class Theme {
        final Color bg, fg, accent;
        Theme(Color bg, Color fg, Color accent) {
            this.bg = bg; this.fg = fg; this.accent = accent;
        }
    }

    private Theme themeForRoom(Room r) {
        String name = r.getClass().getSimpleName();

        // Base palettes
        Theme regular = new Theme(new Color(245,240,225), new Color(40,30,20), new Color(120,85,60));  // parchment
        Theme oldLady = new Theme(new Color(60,70,60), new Color(220,230,220), new Color(100,130,100)); // grey + moss
        Theme dragon  = new Theme(new Color(20,15,20), new Color(240,220,220), new Color(180,20,20));   // black + red
        Theme puzzle  = new Theme(new Color(230,235,245), new Color(40,40,60), new Color(180,160,210)); // pastel-ish
        Theme golem   = new Theme(new Color(240,235,170), new Color(50,50,30), new Color(180,180,180)); // yellow + light gray
        Theme goblin  = new Theme(new Color(90,85,70),  new Color(230,220,210), new Color(80,110,80));  // darker fantasy
        // Blend regular + oldLady for camp
        Theme goblinCamp = new Theme(blend(regular.bg, oldLady.bg, 0.5), blend(regular.fg, oldLady.fg, 0.5), blend(regular.accent, oldLady.accent, 0.5));

        if ("DragonRoom".equalsIgnoreCase(name)) return dragon;
        if ("OldLadyRoom".equalsIgnoreCase(name)) return oldLady;
        if ("PuzzleRoom".equalsIgnoreCase(name)) return puzzle;
        if ("GoblinRoom".equalsIgnoreCase(name)) return goblin;
        if (name.toLowerCase().contains("golem")) return golem;
        if (name.toLowerCase().contains("camp") && name.toLowerCase().contains("goblin")) return goblinCamp;
        // Fallback regular
        return regular;
    }

    private void applyThemeForCurrentRoom(boolean instant) {
        Theme t = themeForRoom(game.getPlayer().getCurrentRoom());
        fadeThemeTo(t.bg, t.fg, t.accent, instant ? 0 : FADE_DURATION_MS);
    }

    private static Color blend(Color a, Color b, double t) {
        int r = (int)Math.round(a.getRed()   * (1-t) + b.getRed()   * t);
        int g = (int)Math.round(a.getGreen() * (1-t) + b.getGreen() * t);
        int bl= (int)Math.round(a.getBlue()  * (1-t) + b.getBlue()  * t);
        return new Color(r,g,bl);
    }

    private void applyColors(Color bg, Color fg, Color accent) {
        // Update containers
        root.setBackground(bg);
        leftStack.setBackground(bg);
        rightSide.setBackground(bg);
        rightTop.setBackground(bg);
        rightBottom.setBackground(bg);
        sceneImagePanel.setBackground(bg);

        storyArea.setBackground(blend(bg, Color.WHITE, 0.06));
        storyArea.setForeground(fg);

        commandField.setBackground(blend(bg, Color.WHITE, 0.10));
        commandField.setForeground(fg);

        inventoryList.setBackground(blend(bg, Color.WHITE, 0.08));
        inventoryList.setForeground(fg);

        for (JButton b : Arrays.asList(btnInventory, btnHelp, btnHint, btnSpeak)) {
            b.setBackground(blend(bg, accent, 0.15));
            b.setForeground(fg);
        }

        currentBg = bg; currentFg = fg; currentAccent = accent;
        repaint();
    }

    private void fadeThemeTo(Color bg, Color fg, Color accent, int durationMs) {
        if (durationMs <= 0) { applyColors(bg, fg, accent); return; }

        final Color startBg = currentBg, startFg = currentFg, startAc = currentAccent;
        final long start = System.currentTimeMillis();

        Timer timer = new Timer(16, null);
        timer.addActionListener(e -> {
            float t = (System.currentTimeMillis() - start) / (float) durationMs;
            if (t >= 1f) { t = 1f; timer.stop(); }
            Color ibg = blend(startBg, bg, t);
            Color ifg = blend(startFg, fg, t);
            Color iac = blend(startAc, accent, t);
            applyColors(ibg, ifg, iac);
        });
        timer.start();
    }

    // ======= Scene image handling =======
    private void updateSceneImageForCurrentRoom(boolean instant) {
        Room r = game.getPlayer().getCurrentRoom();
        String name = r.getClass().getSimpleName().toLowerCase(Locale.ROOT);
        String filename = IMAGE_DIR + "/" + name + ".png"; // e.g., images/dragonroom.png
        if (!new File(filename).exists()) {
            // fallback to generic
            filename = IMAGE_DIR + "/generic.png";
        }
        try {
            BufferedImage img = ImageIO.read(new File(filename));
            sceneImagePanel.setImage(img, instant ? 0 : FADE_DURATION_MS);
        } catch (Exception ex) {
            // no image — clear panel
            sceneImagePanel.setImage(null, 0);
        }
    }

    private void tryLoadImageToLabel(JLabel label, String path) {
        try {
            if (new File(path).exists()) {
                Image img = ImageIO.read(new File(path));
                label.setIcon(new ImageIcon(img.getScaledInstance(label.getWidth() > 0 ? label.getWidth() : 120,
                        label.getHeight() > 0 ? label.getHeight() : 120,
                        Image.SCALE_SMOOTH)));
            } else {
                label.setIcon(null);
            }
        } catch (Exception ignored) {
            label.setIcon(null);
        }
    }

    // ======= Help text =======
    private void showHelp() {
        String help = String.join("\n",
                "Commands you can type:",
                "  look                 — describe the room again",
                "  north/south/east/west or go <dir>",
                "  take <item>          — pick up an item",
                "  use <item> / drink <item>",
                "  equip <item> / unequip <item>",
                "  inventory|inv|i      — list items you carry",
                "  gear                 — what you're wearing/wielding",
                "  attack               — fight if there's an enemy",
                "  solve <answer>       — answer riddles in puzzle rooms",
                "  speak                — try talking (if there's someone)"
        );
        appendStory(help);
    }

    // ======= Crossfade image panel =======
    private static class CrossfadeImagePanel extends JComponent {
        private BufferedImage currentImg;
        private BufferedImage nextImg;
        private float alpha = 0f;
        private Timer fadeTimer;

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

            int w = getWidth(), h = getHeight();
            if (currentImg != null) {
                g2.drawImage(currentImg, 0, 0, w, h, null);
            }
            if (nextImg != null && alpha > 0f) {
                Composite old = g2.getComposite();
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
                g2.drawImage(nextImg, 0, 0, w, h, null);
                g2.setComposite(old);
            }
            g2.dispose();
        }

        void setImage(BufferedImage newImg, int durationMs) {
            if (durationMs <= 0 || currentImg == null) {
                currentImg = newImg;
                nextImg = null;
                alpha = 0f;
                repaint();
                return;
            }
            nextImg = newImg;
            alpha = 0f;
            if (fadeTimer != null && fadeTimer.isRunning()) fadeTimer.stop();
            final long start = System.currentTimeMillis();
            fadeTimer = new Timer(16, e -> {
                float t = (System.currentTimeMillis() - start) / (float) durationMs;
                if (t >= 1f) {
                    t = 1f;
                    currentImg = nextImg;
                    nextImg = null;
                    alpha = 0f;
                    ((Timer)e.getSource()).stop();
                } else {
                    alpha = t;
                }
                repaint();
            });
            fadeTimer.start();
        }
    }
}
