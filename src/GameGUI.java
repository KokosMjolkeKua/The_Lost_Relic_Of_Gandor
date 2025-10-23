
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class GameGUI extends JFrame {
    // ---- Adjustable fade speed for both image + text ----
    private static final int FADE_DURATION_MS = 800; // tweak 600–1200 for pacing

    private final Game game;
    private final JTextArea output;
    private final JLabel hpLabel;
    private final DefaultListModel<String> inventoryModel;
    private final List<JButton> allButtons = new ArrayList<>();

    // Use custom panel that can crossfade between images
    private FadeImagePanel imageLabel;

    // Alpha for text fade (0..1)
    private float currentTextAlpha = 1f;

    public GameGUI(Game game) {
        super("The Lost Relic of Galdor — Cinematic Edition");
        this.game = game;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(8,8));
        setSize(980, 640);

        // Output area
        output = new JTextArea();
        output.setEditable(false);
        output.setLineWrap(true);
        output.setWrapStyleWord(true);
        output.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 15));
        output.setBackground(new Color(20,20,20));
        output.setForeground(new Color(235,235,235));
        JScrollPane scroll = new JScrollPane(output);
        add(scroll, BorderLayout.CENTER);

        // Right panel
        JPanel right = new JPanel(new BorderLayout(6,6));
        right.setPreferredSize(new Dimension(300, 0));
        add(right, BorderLayout.EAST);

        // Intro starts dark (Option A): black image that later fades to scene art
        imageLabel = new FadeImagePanel("images/black.png");
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setBorder(BorderFactory.createEmptyBorder(6,6,6,6));
        right.add(imageLabel, BorderLayout.NORTH);

        // Status
        JPanel status = new JPanel(new GridLayout(0,1));
        status.setBackground(new Color(30,30,35));
        status.setBorder(BorderFactory.createEmptyBorder(6,6,6,6));
        hpLabel = new JLabel();
        hpLabel.setForeground(Color.WHITE);
        status.add(hpLabel);
        right.add(status, BorderLayout.CENTER);

        // Inventory list (compact)
        inventoryModel = new DefaultListModel<>();
        JList<String> invList = new JList<>(inventoryModel);
        invList.setVisibleRowCount(8);
        invList.setBackground(new Color(28,28,32));
        invList.setForeground(new Color(230,230,230));
        right.add(new JScrollPane(invList), BorderLayout.WEST);

        // Controls
        JPanel controls = new JPanel(new GridLayout(0,1,6,6));
        controls.setBorder(BorderFactory.createEmptyBorder(6,6,6,6));

        JButton north = btn("North");
        JButton south = btn("South");
        JButton east  = btn("East");
        JButton west  = btn("West");
        JButton look  = btn("Look Around");
        JButton attack= btn("Attack");
        JButton pickup= btn("Pick Up…");
        JButton use   = btn("Use…");
        JButton unequip = btn("Unequip…");
        JButton check = btn("Check Gear");
        JButton solve = btn("Solve…");
        JButton inventory = btn("Inventory");
        JButton help = btn("Help");
        JButton save = btn("Save Game");
        JButton load = btn("Load Game");

        for (JButton b : new JButton[]{north,south,east,west,look,attack,pickup,use,unequip,check,solve,inventory,help,save,load}) {
            b.addActionListener(new ButtonHandler());
            controls.add(b);
            allButtons.add(b);
        }
        right.add(controls, BorderLayout.SOUTH);

        // Initial UI update
        refreshUI();

        // ---- Title card + cinematic intro (Option A: dark background) ----
        disableButtons();
        showTitleThenIntro();

        setLocationRelativeTo(null);
        setVisible(true);
    }

    /** Olive-green, high-contrast button */
    private JButton btn(String label) {
        JButton b = new JButton(label);
        b.setBackground(new Color(107,142,35)); // olive green
        b.setForeground(new Color(250,250,250)); // white text
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(90,120,30)),
                BorderFactory.createEmptyBorder(6,10,6,10)
        ));
        b.setFont(b.getFont().deriveFont(Font.BOLD, 13f));
        return b;
    }

    private void refreshUI() {
        hpLabel.setText("HP: " + game.getPlayer().getHealth());
        inventoryModel.clear();
        for (Item it : game.getPlayer().getInventory()) inventoryModel.addElement(it.getName());
    }

    private class ButtonHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String cmd = ((JButton)e.getSource()).getText();
            String res = null;
            switch (cmd) {
                case "North": res = game.movePlayer("north"); break;
                case "South": res = game.movePlayer("south"); break;
                case "East":  res = game.movePlayer("east");  break;
                case "West":  res = game.movePlayer("west");  break;
                case "Look Around": res = game.look(); break;
                case "Attack": res = game.attackEnemy(); break;
                case "Pick Up…":
                    String item = JOptionPane.showInputDialog(GameGUI.this, "Pick up what item? (name)");
                    if (item != null && !item.trim().isEmpty()) res = game.pickUpItem(item.trim());
                    break;
                case "Use…":
                    String use = JOptionPane.showInputDialog(GameGUI.this, "Use which item from inventory? (name)");
                    if (use != null && !use.trim().isEmpty()) res = game.useItem(use.trim());
                    break;
                case "Unequip…": {
                    String uneq = JOptionPane.showInputDialog(GameGUI.this, "Unequip which item? (name)");
                    if (uneq != null && !uneq.trim().isEmpty()) res = game.unequipItem(uneq.trim());
                    break; }
                case "Check Gear":
                    res = game.checkGear(); break;
                case "Solve…":
                    String ans = JOptionPane.showInputDialog(GameGUI.this, "Your answer:");
                    if (ans != null) res = game.solvePuzzle(ans);
                    break;
                case "Inventory":
                    StringBuilder invList = new StringBuilder();
                    for (Item it : game.getPlayer().getInventory()) {
                        invList.append(it.getName()).append(" - ").append(it.getDescription()).append("\n");
                    }
                    if (invList.length() == 0) invList.append("Your inventory is empty.");
                    JOptionPane.showMessageDialog(GameGUI.this, invList.toString(), "Inventory", JOptionPane.INFORMATION_MESSAGE);
                    break;
                case "Help":
                    JOptionPane.showMessageDialog(GameGUI.this,
                        "Controls:\n" +
                        "- Move: North, South, East, West\n" +
                        "- Look Around: Re-describe your surroundings\n" +
                        "- Attack: Fight enemies in the current room\n" +
                        "- Pick Up / Use / Unequip / Solve: Interact with items and puzzles\n" +
                        "Tips:\n" +
                        "- Equip shoes or a grappling hook before climbing\n" +
                        "- Use the Emerald Key to enter the Black Castle\n" +
                        "- Potions restore your HP!", "Help", JOptionPane.INFORMATION_MESSAGE);
                    break;
                case "Save Game":
                    JOptionPane.showMessageDialog(GameGUI.this, "Saving feature not yet implemented.", "Save Game", JOptionPane.INFORMATION_MESSAGE);
                    break;
                case "Load Game":
                    JOptionPane.showMessageDialog(GameGUI.this, "Loading feature not yet implemented.", "Load Game", JOptionPane.INFORMATION_MESSAGE);
                    break;
            }
            if (res != null) {
                // Dynamic scene update + typewriter with text fade
                disableButtons();
                updateImage(res);
                typeText(res, 18, new Runnable() {
                    @Override public void run() {
                        refreshUI();
                        enableButtons();
                    }
                });
            }
        }
    }

    // ----- Cinematic Title + Intro -----
    private void showTitleThenIntro() {
        System.out.println("[DEBUG] Showing title and intro narrative...");
        // Title card
        String title = "\n\n\n        THE LOST RELIC OF GALDOR\n";
        // Clear output and fade text from transparent
        output.setText("");
        currentTextAlpha = 0f;
        output.setForeground(new Color(235,235,235, 0));

        // Fade in black background already set; show title with typing + fade
        typeText(title, 12, new Runnable() {
            @Override public void run() {
                // Brief pause before intro lines
                Timer pause = new Timer(700, null);
                pause.addActionListener(ev -> {
                    ((Timer)ev.getSource()).stop();
                    runNarrative();
                });
                pause.setRepeats(false);
                pause.start();
            }
        });
    }

    private void runNarrative() {
        String[] intro = new String[]{
            "*A whisper stirs the stillness of the old woods…*",
            "The wind carries tales of a relic — ancient, forgotten, and feared.",
            "",
            "You are no hero, not yet. Only a wanderer chasing echoes of glory.",
            "",
            "The moss beneath your boots remembers the march of kingdoms long gone.",
            "The trees lean close, listening, as if they too wait for your choice.",
            "",
            "Somewhere beyond the mist, a castle sleeps — black stone against dying light.",
            "Legends say it guards the Lost Relic of Galdor.",
            "",
            "And so, with breath steady and heart uncertain, you take your first step north."
        };

        // Fade image from black to forest scene as the narrative starts
        ((FadeImagePanel)imageLabel).fadeTo("images/forest_castle.png", FADE_DURATION_MS);
        // Lines typed with fade
        typeLines(intro, 28, new Runnable() {
            @Override public void run() {
                System.out.println("[DEBUG] Intro finished → Starting game world.");
                // After intro, show current room and enable controls
                String first = game.look();
                disableButtons(); // keep buttons paused during scene reveal
                updateImage(first);
                typeText(first, 18, new Runnable() {
                    @Override public void run() {
                        enableButtons();
                    }
                });
            }
        });
    }

    // ----- Typing / spacing helpers -----
    private void appendWithSpacing(String text) {
        if (output.getText().length() > 0) output.append("\n\n");
        output.append(text);
        output.setCaretPosition(output.getDocument().getLength());
    }

    private void disableButtons() { for (JButton b : allButtons) b.setEnabled(false); }
    private void enableButtons()  { for (JButton b : allButtons) b.setEnabled(true); }

    /** Type out multiple lines one after another */
    private void typeLines(String[] lines, int delayMs, Runnable onDone) {
        if (lines == null || lines.length == 0) { if (onDone != null) onDone.run(); return; }
        final int[] idx = {0};
        typeText(lines[idx[0]], delayMs, new Runnable() {
            @Override public void run() {
                idx[0]++;
                if (idx[0] < lines.length) {
                    typeText(lines[idx[0]], delayMs, this);
                } else if (onDone != null) {
                    onDone.run();
                }
            }
        });
    }

    /** Typewriter effect with alpha fade-in overlay for text */
    private void typeText(String text, int delayMs, Runnable onDone) {
        // Start with transparent text for fade
        currentTextAlpha = 0f;
        output.setForeground(new Color(235,235,235, 0));

        if (output.getText().length() > 0) output.append("\n\n");
        final String s = text;
        final int[] i = {0};
        Timer t = new Timer(delayMs, null);
        t.addActionListener(ev -> {
            if (i[0] < s.length()) {
                int a = Math.min(255, (int)(currentTextAlpha * 255));
                output.setForeground(new Color(235,235,235, a));
                currentTextAlpha = Math.min(1f, currentTextAlpha + (30f / Math.max(FADE_DURATION_MS, 1)));
                output.append(String.valueOf(s.charAt(i[0]++)));
                output.setCaretPosition(output.getDocument().getLength());
            } else {
                ((Timer)ev.getSource()).stop();
                // ensure fully visible at end
                output.setForeground(new Color(235,235,235, 255));
                if (onDone != null) onDone.run();
            }
        });
        t.start();
    }

    // --- Dynamic scene detection & fade ---
    private void updateImage(String desc) {
        if (desc == null) return;
        String lower = desc.toLowerCase();
        String path = "images/forest_castle.png"; // default
        if (lower.contains("swamp") || lower.contains("murky") || lower.contains("bog") || lower.contains("marsh")) {
            path = "images/swamp_area.png";
        } else if (lower.contains("cliff") || lower.contains("mountain") || lower.contains("hill") || lower.contains("rock")) {
            path = "images/mountain_path.png";
        } else if (lower.contains("goblin") && lower.contains("camp")) {
            path = "images/goblin_camp.png";
        } else if (lower.contains("dragon") || lower.contains("crater") || lower.contains("lava") || lower.contains("fire")) {
            path = "images/dragon_den.png";
        } else if (lower.contains("castle") && (lower.contains("gate") || lower.contains("door"))) {
            path = "images/castle_gate.png";
        } else if (lower.contains("chandelier") || lower.contains("altar") || lower.contains("corridor") || lower.contains("golem") || lower.contains("black castle")) {
            path = "images/castle_interior.png";
        } else if (lower.contains("butterfl") || lower.contains("villager") || lower.contains("peaceful") || lower.contains("village")) {
            path = "images/village_clearing.png";
        }
        System.out.println("[DEBUG] Scene identified → " + path);
        imageLabel.fadeTo(path, FADE_DURATION_MS);
    }

    // --- Custom panel that crossfades between images ---
    private static class FadeImagePanel extends JLabel {
        private Image currentImg;
        private Image nextImg;
        private float alpha = 1f;
        private Timer timer;

        public FadeImagePanel(String initialPath) {
            setPath(initialPath);
        }

        public void setPath(String path) {
            this.currentImg = new ImageIcon(path).getImage();
            repaint();
        }

        public void fadeTo(String path, int durationMs) {
            this.nextImg = new ImageIcon(path).getImage();
            alpha = 0f;
            int steps = Math.max(1, durationMs / 30);
            if (timer != null && timer.isRunning()) timer.stop();
            timer = new Timer(30, null);
            final int[] count = {0};
            timer.addActionListener(e -> {
                count[0]++;
                alpha = Math.min(1f, count[0] / (float) steps);
                repaint();
                if (alpha >= 1f) {
                    timer.stop();
                    currentImg = nextImg;
                    nextImg = null;
                    repaint();
                }
            });
            timer.start();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            if (currentImg != null) {
                g2.drawImage(currentImg, 0, 0, getWidth(), getHeight(), this);
            }
            if (nextImg != null) {
                Composite old = g2.getComposite();
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
                g2.drawImage(nextImg, 0, 0, getWidth(), getHeight(), this);
                g2.setComposite(old);
            }
            g2.dispose();
        }
    }
}
