import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.*;
import javax.imageio.ImageIO;
import javax.swing.Timer;

public class GameGUI extends JFrame {

    // ======= Config =======
    private static final int FADE_DURATION_MS = 600;     // crossfade duration for images/colors
    private static final int TYPE_DELAY_MS    = 22;      // medium pace typing (lower = faster)
    private static final String IMAGE_DIR     = "images";
    private static final String AVATAR_DIR    = "images/avatars";
    private static final String INTRO_IMAGE   = IMAGE_DIR + "/intro.png";
    private static final String INTRO_TEXT_TXT= IMAGE_DIR + "/intro.txt"; // optional external intro text

    private static final Font STORY_FONT = new Font("Serif", Font.PLAIN, 18);
    private static final Font UI_FONT    = new Font(Font.SANS_SERIF, Font.PLAIN, 14);
    private static final Font MONO_FONT  = new Font(Font.MONOSPACED, Font.PLAIN, 14);

    // ======= Game & State =======
    private final Game game;
    private boolean typingActive = false;

    // ======= UI: Containers =======
    private final JPanel root = new JPanel(new BorderLayout());
    private final JPanel contentFrame = new JPanel(new BorderLayout()); // framed area with panels inside
    private final JPanel leftStack = new JPanel();
    private final JPanel rightSide = new JPanel(new BorderLayout());
    private final JPanel rightTop  = new JPanel(new BorderLayout());
    private final JPanel rightBottom = new JPanel(new BorderLayout());

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

    // Keep a direct reference to the Inventory title so theme changes are easy
    private final JLabel invTitleLabel = new JLabel("Inventory", SwingConstants.LEFT);

    // For color theme fading (applies to *inner* panels; outer stays dark)
    private Color currentBg = new Color(245, 240, 225);
    private Color currentFg = new Color(40, 30, 20);
    private Color currentAccent = new Color(120, 85, 60);
    private Color currentTitleBar = new Color(210, 200, 180);

    public GameGUI(Game game) {
        super("The Lost Relic of Galdor");
        this.game = game;

        initWindow();
        buildLayout();
        wireActions();

        // Start with intro; gameplay loads after intro completes
        playIntroSequence();
        setVisible(true);
    }

    private void initWindow() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1120, 760);
        setLocationRelativeTo(null);
        setContentPane(root);

        // Outer background stays dark/black to frame the UI nicely
        root.setBackground(new Color(12, 12, 12));
        root.setBorder(new EmptyBorder(10, 10, 10, 10));
    }

    private JPanel framed(JComponent inner, int thickness) {
        JPanel wrap = new JPanel(new BorderLayout());
        wrap.add(inner, BorderLayout.CENTER);
        wrap.setBorder(new LineBorder(Color.BLACK, thickness));
        return wrap;
    }

    private void buildLayout() {
        // Content frame holds the left/right panels with spacing
        contentFrame.setOpaque(false);
        contentFrame.setBorder(new EmptyBorder(0, 0, 0, 0));
        root.add(contentFrame, BorderLayout.CENTER);

        // ----- LEFT/CENTER STACK -----
        leftStack.setLayout(new BoxLayout(leftStack, BoxLayout.Y_AXIS));
        leftStack.setOpaque(true);

        // (1) Scene image (framed)
        sceneImagePanel.setPreferredSize(new Dimension(720, 260));
        JPanel sceneFrame = framed(sceneImagePanel, 3);

        // (2) Story text (framed)
        storyArea.setEditable(false);
        storyArea.setWrapStyleWord(true);
        storyArea.setLineWrap(true);
        storyArea.setFont(STORY_FONT);
        storyArea.setBorder(new EmptyBorder(12, 12, 12, 12));
        JScrollPane storyScroll = new JScrollPane(storyArea);
        storyScroll.setBorder(null);
        JPanel storyFrame = framed(storyScroll, 3);
        storyFrame.setPreferredSize(new Dimension(720, 320));

        // (3) Command input (framed)
        commandField.setFont(MONO_FONT);
        commandField.setBorder(new EmptyBorder(10, 12, 10, 12));
        commandField.setToolTipText("Type commands: north | take key | use potion | equip sword | solve riddle");
        JPanel commandFrame = framed(commandField, 3);
        commandFrame.setPreferredSize(new Dimension(720, 46));

        leftStack.add(sceneFrame);
        leftStack.add(Box.createVerticalStrut(10));
        leftStack.add(storyFrame);
        leftStack.add(Box.createVerticalStrut(10));
        leftStack.add(commandFrame);

        // ----- RIGHT SIDE -----
        // (4) Top: HP + Inventory (outer framed)
        JPanel hpPanel = new JPanel(new BorderLayout());
        hpPanel.setOpaque(false);
        JLabel hpLbl = new JLabel("HP", SwingConstants.LEFT);
        hpLbl.setBorder(new EmptyBorder(8, 10, 4, 10));
        hpLbl.setFont(UI_FONT.deriveFont(Font.BOLD));
        hpPanel.add(hpLbl, BorderLayout.NORTH);
        hpBar.setStringPainted(true);
        hpBar.setValue(100);
        hpBar.setPreferredSize(new Dimension(300, 24));
        hpPanel.add(hpBar, BorderLayout.CENTER);
        hpPanel.setBorder(new EmptyBorder(6, 10, 8, 10));

        // Inventory with attached title bar and inner 2px border
        invTitleLabel.setOpaque(true); // so it looks like a title bar
        invTitleLabel.setBorder(new EmptyBorder(8, 10, 8, 10));
        invTitleLabel.setFont(UI_FONT.deriveFont(Font.BOLD));

        inventoryList.setVisibleRowCount(10);
        JScrollPane invScroll = new JScrollPane(inventoryList);
        invScroll.setBorder(new LineBorder(Color.BLACK, 2)); // inner 2px border

        JPanel invStack = new JPanel(new BorderLayout());
        invStack.add(invTitleLabel, BorderLayout.NORTH);
        invStack.add(invScroll, BorderLayout.CENTER);
        invStack.setBorder(new EmptyBorder(4, 4, 4, 4));

        JPanel inventoryOuterFrame = framed(invStack, 3); // outer 3px border

        JPanel rightTopInner = new JPanel(new BorderLayout());
        rightTopInner.add(hpPanel, BorderLayout.NORTH);
        rightTopInner.add(inventoryOuterFrame, BorderLayout.CENTER);
        JPanel rightTopFrame = framed(rightTopInner, 3);

        // (5) Bottom: Buttons + Avatar (framed)
        JPanel buttonRow = new JPanel(new GridLayout(2, 2, 8, 8));
        for (JButton b : new JButton[]{btnInventory, btnHelp, btnHint, btnSpeak}) {
            b.setFocusPainted(false);
            b.setFont(UI_FONT);
            buttonRow.add(b);
        }
        JPanel buttonWrap = new JPanel(new BorderLayout());
        buttonWrap.add(buttonRow, BorderLayout.CENTER);
        buttonWrap.setBorder(new EmptyBorder(10, 10, 10, 10));

        avatarLabel.setPreferredSize(new Dimension(120, 120));
        JPanel avatarWrap = new JPanel(new BorderLayout());
        avatarWrap.add(avatarLabel, BorderLayout.CENTER);
        avatarWrap.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel bottomInner = new JPanel(new BorderLayout(10, 10));
        bottomInner.add(buttonWrap, BorderLayout.CENTER);
        bottomInner.add(avatarWrap, BorderLayout.EAST);
        JPanel rightBottomFrame = framed(bottomInner, 3);

        // Assemble right side
        JPanel rightStack = new JPanel();
        rightStack.setLayout(new BoxLayout(rightStack, BoxLayout.Y_AXIS));
        rightStack.setOpaque(false);
        rightStack.add(rightTopFrame);
        rightStack.add(Box.createVerticalStrut(10));
        rightStack.add(rightBottomFrame);

        rightSide.setOpaque(false);
        rightSide.add(rightStack, BorderLayout.CENTER);
        rightSide.setPreferredSize(new Dimension(360, 0));

        // Add to content frame
        contentFrame.add(leftStack, BorderLayout.CENTER);
        contentFrame.add(rightSide, BorderLayout.EAST);

        // Initial theme apply to inner panels only
        applyThemeForCurrentRoom(true);
        updatePanelBordersAndBackgrounds();
    }

    private void wireActions() {
        // Enter submits command
        commandField.addActionListener(e -> {
            if (typingActive) return; // avoid overlapping while typing
            String cmd = commandField.getText().trim();
            if (!cmd.isEmpty()) {
                handleCommand(cmd);
            }
            commandField.setText("");
        });

        btnInventory.addActionListener(e -> {
            if (typingActive) return;
            String inv = getInventoryText();
            appendStoryTypewriter(inv.isEmpty() ? "Your inventory is empty." : "Inventory:\n" + inv);
        });

        btnHelp.addActionListener(e -> { if (!typingActive) showHelp(); });
        btnHint.addActionListener(e -> { if (!typingActive) appendStoryTypewriter("Hint: Explore thoroughly. Try LOOK, and experiment with USE, EQUIP, and SOLVE in puzzle rooms."); });
        btnSpeak.addActionListener(e -> { if (!typingActive) performSpeakAction(); });
    }

    // ======= Intro =======
    private void playIntroSequence() {
        setControlsEnabled(false);
        // Clear text and show intro image
        storyArea.setText("");
        updatePanelBordersAndBackgrounds();
        try {
            if (new File(INTRO_IMAGE).exists()) {
                BufferedImage img = ImageIO.read(new File(INTRO_IMAGE));
                sceneImagePanel.setImage(img, FADE_DURATION_MS);
            } else {
                sceneImagePanel.setImage(null, 0);
            }
        } catch (Exception ex) {
            sceneImagePanel.setImage(null, 0);
        }

        // Title + intro text
        String title = "THE LOST RELIC OF GALDOR\n\n";
        String intro = loadIntroText();
        appendStoryTypewriter(title + intro, () -> {
            // After intro, initialize gameplay view
            updateHP();
            refreshInventory();
            applyThemeForCurrentRoom(false);
            updateSceneImageForCurrentRoom(false);
            appendStoryTypewriter(game.look(), this::finishIntroEnableControls);
        });
    }

    private void finishIntroEnableControls() {
        setControlsEnabled(true);
        commandField.requestFocusInWindow();
    }

    private String loadIntroText() {
        try {
            if (Files.exists(Paths.get(INTRO_TEXT_TXT))) {
                return new String(Files.readAllBytes(Paths.get(INTRO_TEXT_TXT)));
            }
        } catch (Exception ignored) {}
        // Fallback: placeholder text. Replace with your original intro by creating images/intro.txt
        return "A whisper stirs the stillness of the old woods. Lanterns sway where no wind moves, and somewhere, a relic dreams of being found...\n" +
                "Legends call it the Galdor Relic — a light for the lost and a doom for the unworthy. Few who sought it ever returned.\n" +
                "Tonight, fate turns its gaze to you.";
    }

    private void setControlsEnabled(boolean enabled) {
        commandField.setEnabled(enabled);
        btnInventory.setEnabled(enabled);
        btnHelp.setEnabled(enabled);
        btnHint.setEnabled(enabled);
        btnSpeak.setEnabled(enabled);
    }

    // ======= Command handling =======
    private void handleCommand(String input) {
        String cmd = input.toLowerCase(Locale.ROOT).trim();
        String out;

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
            out = game.useItem(item);
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

        appendStoryTypewriter(out);
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
        String msg;
        if (roomType.equalsIgnoreCase("OldLadyRoom")) {
            msg = "You greet the old lady. She eyes you kindly and mutters about 'paths hidden in plain sight.'";
        } else if (roomType.equalsIgnoreCase("GoblinRoom")) {
            msg = "You try speaking to the goblin. It snarls back — not very conversational.";
        } else if (roomType.equalsIgnoreCase("DragonRoom")) {
            msg = "You address the dragon. It rumbles, smoke curling from its nostrils... Best choose your next action wisely.";
        } else {
            msg = "Not very effective.";
        }
        appendStoryTypewriter(msg);
        return msg;
    }

    // ======= Story / Output (typewriter) =======
    private void appendStory(String text) {
        if (text == null || text.isEmpty()) return;
        storyArea.append(text + "\n\n");
        storyArea.setCaretPosition(storyArea.getDocument().getLength());
    }

    private void appendStoryTypewriter(String text) { appendStoryTypewriter(text, null); }

    private void appendStoryTypewriter(String text, Runnable onDone) {
        if (text == null || text.isEmpty()) { if (onDone != null) onDone.run(); return; }
        typingActive = true;
        // Ensure spacing between chunks
        if (storyArea.getDocument().getLength() > 0 && !storyArea.getText().endsWith("\n\n")) {
            storyArea.append("\n");
        }
        final char[] chars = (text + "\n\n").toCharArray();
        final int[] idx = {0};

        Timer t = new Timer(TYPE_DELAY_MS, null);
        t.addActionListener(e -> {
            storyArea.append(String.valueOf(chars[idx[0]]));
            idx[0]++;
            storyArea.setCaretPosition(storyArea.getDocument().getLength());
            if (idx[0] >= chars.length) {
                ((Timer)e.getSource()).stop();
                typingActive = false;
                if (onDone != null) onDone.run();
            }
        });
        t.start();
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
        final Color bg, fg, accent, titleBar;
        Theme(Color bg, Color fg, Color accent, Color titleBar) {
            this.bg = bg; this.fg = fg; this.accent = accent; this.titleBar = titleBar;
        }
    }

    private Theme themeForRoom(Room r) {
        String name = r.getClass().getSimpleName();

        Theme regular = new Theme(new Color(245,240,225), new Color(40,30,20), new Color(120,85,60), new Color(210,200,180));
        Theme oldLady = new Theme(new Color(60,70,60),   new Color(220,230,220), new Color(100,130,100), new Color(70,85,70));
        Theme dragon  = new Theme(new Color(20,15,20),   new Color(240,220,220), new Color(180,20,20),   new Color(40,20,20));
        Theme puzzle  = new Theme(new Color(230,235,245),new Color(40,40,60),    new Color(180,160,210), new Color(210,215,230));
        Theme golem   = new Theme(new Color(240,235,170),new Color(50,50,30),    new Color(180,180,180), new Color(230,225,170));
        Theme goblin  = new Theme(new Color(90,85,70),   new Color(230,220,210), new Color(80,110,80),   new Color(100,95,80));
        Theme goblinCamp = new Theme(blend(regular.bg, oldLady.bg, 0.5), blend(regular.fg, oldLady.fg, 0.5),
                blend(regular.accent, oldLady.accent, 0.5), blend(regular.titleBar, oldLady.titleBar, 0.5));

        if ("DragonRoom".equalsIgnoreCase(name)) return dragon;
        if ("OldLadyRoom".equalsIgnoreCase(name)) return oldLady;
        if ("PuzzleRoom".equalsIgnoreCase(name)) return puzzle;
        if ("GoblinRoom".equalsIgnoreCase(name)) return goblin;
        if (name.toLowerCase().contains("golem")) return golem;
        if (name.toLowerCase().contains("camp") && name.toLowerCase().contains("goblin")) return goblinCamp;
        return regular;
    }

    private static Color blend(Color a, Color b, double t) {
        int r = (int)Math.round(a.getRed()   * (1-t) + b.getRed()   * t);
        int g = (int)Math.round(a.getGreen() * (1-t) + b.getGreen() * t);
        int bl= (int)Math.round(a.getBlue()  * (1-t) + b.getBlue()  * t);
        return new Color(r,g,bl);
    }

    private void applyThemeForCurrentRoom(boolean instant) {
        Theme t = themeForRoom(game.getPlayer().getCurrentRoom());
        fadeThemeTo(t.bg, t.fg, t.accent, t.titleBar, instant ? 0 : FADE_DURATION_MS);
    }

    private void applyColors(Color bg, Color fg, Color accent, Color titleBar) {
        // Only inner panels get themed. Outer root stays dark.
        leftStack.setBackground(bg);
        rightSide.setBackground(bg);
        storyArea.setBackground(blend(bg, Color.WHITE, 0.06));
        storyArea.setForeground(fg);
        commandField.setBackground(blend(bg, Color.WHITE, 0.10));
        commandField.setForeground(fg);
        inventoryList.setBackground(blend(bg, Color.WHITE, 0.08));
        inventoryList.setForeground(fg);
        hpBar.setBackground(bg);
        hpBar.setForeground(accent);

        // Inventory title bar
        invTitleLabel.setBackground(titleBar);
        invTitleLabel.setForeground(fg);

        // Buttons
        for (JButton b : new JButton[]{btnInventory, btnHelp, btnHint, btnSpeak}) {
            b.setBackground(blend(bg, accent, 0.15));
            b.setForeground(fg);
        }

        currentBg = bg; currentFg = fg; currentAccent = accent; currentTitleBar = titleBar;
        repaint();
    }

    private void fadeThemeTo(Color bg, Color fg, Color accent, Color titleBar, int durationMs) {
        if (durationMs <= 0) { applyColors(bg, fg, accent, titleBar); return; }

        final Color startBg = currentBg, startFg = currentFg, startAc = currentAccent, startTitle = currentTitleBar;
        final long start = System.currentTimeMillis();

        Timer timer = new Timer(16, null);
        timer.addActionListener(e -> {
            float t = (System.currentTimeMillis() - start) / (float) durationMs;
            if (t >= 1f) { t = 1f; timer.stop(); }
            Color ibg = blend(startBg, bg, t);
            Color ifg = blend(startFg, fg, t);
            Color iac = blend(startAc, accent, t);
            Color ititle = blend(startTitle, titleBar, t);
            // Apply tweened colors
            leftStack.setBackground(ibg);
            rightSide.setBackground(ibg);
            storyArea.setBackground(blend(ibg, Color.WHITE, 0.06));
            storyArea.setForeground(ifg);
            commandField.setBackground(blend(ibg, Color.WHITE, 0.10));
            commandField.setForeground(ifg);
            inventoryList.setBackground(blend(ibg, Color.WHITE, 0.08));
            inventoryList.setForeground(ifg);
            hpBar.setBackground(ibg);
            hpBar.setForeground(iac);
            invTitleLabel.setBackground(ititle);
            invTitleLabel.setForeground(ifg);

            currentBg = ibg; currentFg = ifg; currentAccent = iac; currentTitleBar = ititle;
            repaint();
        });
        timer.start();
    }

    private void updatePanelBordersAndBackgrounds() {
        // Keep outer root dark
        root.setBackground(new Color(12, 12, 12));
        contentFrame.setOpaque(false);
        rightSide.setOpaque(true);
        leftStack.setOpaque(true);
    }

    // ======= Scene image handling =======
    private void updateSceneImageForCurrentRoom(boolean instant) {
        Room r = game.getPlayer().getCurrentRoom();
        String name = r.getClass().getSimpleName().toLowerCase(Locale.ROOT);
        String filename = IMAGE_DIR + "/" + name + ".png"; // e.g., images/dragonroom.png
        if (!new File(filename).exists()) {
            filename = IMAGE_DIR + "/generic.png";
        }
        try {
            BufferedImage img = ImageIO.read(new File(filename));
            sceneImagePanel.setImage(img, instant ? 0 : FADE_DURATION_MS);
        } catch (Exception ex) {
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
        appendStoryTypewriter(help);
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
            g2.setColor(Color.BLACK);
            g2.fillRect(0,0,w,h);

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
