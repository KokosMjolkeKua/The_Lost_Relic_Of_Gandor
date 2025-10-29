
import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class GameGUI extends JFrame {
    private static final int FADE_DURATION_MS = 800; // background image crossfade

    private final Game game;
    private final JTextArea output;
    private final JLabel hpLabel;
    private final DefaultListModel<String> inventoryModel;
    private final List<AbstractButton> allButtons = new ArrayList<>();
    private FadeImagePanel backgroundPanel;

    public GameGUI(Game game) {
        super("The Lost Relic of Galdor");
        this.game = game;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(8,8));
        setSize(1100, 720);

        // Center output
        output = new JTextArea();
        output.setEditable(false);
        output.setLineWrap(true);
        output.setWrapStyleWord(true);
        output.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 15));
        output.setBackground(new Color(20,20,20, 210));
        output.setForeground(new Color(235,235,235));
        output.setMargin(new Insets(6,10,6,10));
        JScrollPane scroll = new JScrollPane(output);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        add(scroll, BorderLayout.CENTER);

        // Right background panel hosts UI
        backgroundPanel = new FadeImagePanel("images/black.png");
        backgroundPanel.setLayout(new BorderLayout(6,6));
        backgroundPanel.setBorder(BorderFactory.createEmptyBorder(6,6,6,6));
        backgroundPanel.setPreferredSize(new Dimension(360, 0));
        add(backgroundPanel, BorderLayout.EAST);

        // Status (solid)
        JPanel status = new JPanel(new GridLayout(0,1));
        status.setBackground(new Color(30,30,35));
        status.setBorder(BorderFactory.createEmptyBorder(6,6,6,6));
        hpLabel = new JLabel();
        hpLabel.setForeground(Color.WHITE);
        status.add(hpLabel);
        backgroundPanel.add(status, BorderLayout.NORTH);

        // Inventory (semi-transparent)
        inventoryModel = new DefaultListModel<>();
        JList<String> invList = new JList<>(inventoryModel);
        invList.setVisibleRowCount(10);
        invList.setBackground(new Color(0,0,0,130));
        invList.setForeground(new Color(230,230,230));
        JScrollPane invScroll = new JScrollPane(invList);
        invScroll.getViewport().setOpaque(false);
        invScroll.setOpaque(false);
        invScroll.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(90,120,30)), "Inventory",
                0, 0, new Font("SansSerif", Font.BOLD, 12), new Color(230,230,230)));
        backgroundPanel.add(invScroll, BorderLayout.CENTER);

        // Controls wrapper
        JPanel controlsWrapper = new JPanel(new BorderLayout(6,6));
        controlsWrapper.setOpaque(false);
        backgroundPanel.add(controlsWrapper, BorderLayout.SOUTH);

        // Compass (100x100 circular)
        JPanel compass = new JPanel(new GridBagLayout());
        compass.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4,4,4,4);

        RoundButton btnN = new RoundButton("N");
        RoundButton btnS = new RoundButton("S");
        RoundButton btnE = new RoundButton("E");
        RoundButton btnW = new RoundButton("W");

        btnN.addActionListener(e -> handleCommand("North"));
        btnS.addActionListener(e -> handleCommand("South"));
        btnE.addActionListener(e -> handleCommand("East"));
        btnW.addActionListener(e -> handleCommand("West"));

        c.gridx = 1; c.gridy = 0; compass.add(btnN, c);
        c.gridx = 0; c.gridy = 1; compass.add(btnW, c);
        c.gridx = 2; c.gridy = 1; compass.add(btnE, c);
        c.gridx = 1; c.gridy = 2; compass.add(btnS, c);

        controlsWrapper.add(compass, BorderLayout.NORTH);

        // Actions grid (rounded, semi-transparent)
        JPanel actions = new JPanel(new GridLayout(0,2,6,6));
        actions.setOpaque(false);
        controlsWrapper.add(actions, BorderLayout.CENTER);

        SoftButton look   = new SoftButton("Look Around");
        SoftButton attack = new SoftButton("Attack");
        SoftButton pickup = new SoftButton("Pick Up…");
        SoftButton use    = new SoftButton("Use…");
        SoftButton unequip= new SoftButton("Unequip…");
        SoftButton check  = new SoftButton("Check Gear");
        SoftButton solve  = new SoftButton("Solve…");
        SoftButton invBtn = new SoftButton("Inventory");
        SoftButton help   = new SoftButton("Help");
        SoftButton save   = new SoftButton("Save Game");
        SoftButton load   = new SoftButton("Load Game");

        for (AbstractButton b : new AbstractButton[]{look,attack,pickup,use,unequip,check,solve,invBtn,help,save,load}) {
            b.addActionListener(e -> handleCommand(((JButton)b).getText()));
            actions.add(b);
            allButtons.add(b);
        }
        for (AbstractButton b : new AbstractButton[]{btnN,btnS,btnE,btnW}) allButtons.add(b);

        // Init UI + intro
        refreshUI();
        disableButtons();
        showTitleThenIntro();

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void refreshUI() {
        hpLabel.setText("HP: " + game.getPlayer().getHealth());
        inventoryModel.clear();
        for (Item it : game.getPlayer().getInventory()) inventoryModel.addElement(it.getName());
    }

    private void handleCommand(String cmd) {
        String res = null;
        switch (cmd) {
            case "North": res = game.movePlayer("north"); break;
            case "South": res = game.movePlayer("south"); break;
            case "East":  res = game.movePlayer("east");  break;
            case "West":  res = game.movePlayer("west");  break;
            case "Look Around": res = game.look(); break;
            case "Attack": res = game.attackEnemy(); break;
            case "Pick Up…":
                String item = JOptionPane.showInputDialog(this, "Pick up what item? (name)");
                if (item != null && !item.trim().isEmpty()) res = game.pickUpItem(item.trim());
                break;
            case "Use…":
                String use = JOptionPane.showInputDialog(this, "Use which item from inventory? (name)");
                if (use != null && !use.trim().isEmpty()) res = game.useItem(use.trim());
                break;
            case "Unequip…":
                String uneq = JOptionPane.showInputDialog(this, "Unequip which item? (name)");
                if (uneq != null && !uneq.trim().isEmpty()) res = game.unequipItem(uneq.trim());
                break;
            case "Check Gear":
                res = game.checkGear(); break;
            case "Solve…":
                String ans = JOptionPane.showInputDialog(this, "Your answer:");
                if (ans != null) res = game.solvePuzzle(ans);
                break;
            case "Inventory":
                StringBuilder sb = new StringBuilder();
                for (Item it : game.getPlayer().getInventory()) sb.append(it.getName()).append("\\n");
                if (sb.length()==0) sb.append("Your inventory is empty.");
                JOptionPane.showMessageDialog(this, sb.toString(), "Inventory", JOptionPane.INFORMATION_MESSAGE);
                break;
            case "Help":
                JOptionPane.showMessageDialog(this,
                    "Controls:\\n" +
                    "- Move: North, South, East, West\\n" +
                    "- Look Around: Re-describe your surroundings\\n" +
                    "- Attack, Pick Up, Use, Unequip, Check Gear, Solve\\n" +
                    "Tips:\\n" +
                    "- Equip shoes or a grappling hook before climbing\\n" +
                    "- Emerald Key opens the Black Castle\\n" +
                    "- Potions restore your HP!", "Help", JOptionPane.INFORMATION_MESSAGE);
                break;
            case "Save Game":
                JOptionPane.showMessageDialog(this, "Saving feature not yet implemented.", "Save Game", JOptionPane.INFORMATION_MESSAGE);
                break;
            case "Load Game":
                JOptionPane.showMessageDialog(this, "Loading feature not yet implemented.", "Load Game", JOptionPane.INFORMATION_MESSAGE);
                break;
        }
        if (res != null) {
            disableButtons();
            updateImage(res);
            typeText(res, 18, () -> { refreshUI(); enableButtons(); });
        }
    }

    // ---- Intro title + narrative ----
    private void showTitleThenIntro() {
        System.out.println("[DEBUG] Showing title and intro narrative...");
        output.setText("");
        typeText("\\n\\n\\n        THE LOST RELIC OF GALDOR\\n", 12, () -> {
            Timer pause = new Timer(700, null);
            pause.addActionListener(ev -> { ((Timer)ev.getSource()).stop(); runNarrative(); });
            pause.setRepeats(false);
            pause.start();
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
        backgroundPanel.fadeTo("images/forest_castle.png", FADE_DURATION_MS);
        typeLines(intro, 28, () -> {
            System.out.println("[DEBUG] Intro finished → Starting game world.");
            String first = game.look();
            disableButtons();
            updateImage(first);
            typeText(first, 18, this::enableButtons);
        });
    }

    private void typeLines(String[] lines, int delayMs, Runnable onDone) {
        if (lines==null || lines.length==0) { if (onDone!=null) onDone.run(); return; }
        final int[] idx = {0};
        typeText(lines[idx[0]], delayMs, new Runnable() {
            @Override public void run() {
                idx[0]++;
                if (idx[0] < lines.length) typeText(lines[idx[0]], delayMs, this);
                else if (onDone != null) onDone.run();
            }
        });
    }
    private void typeText(String text, int delayMs, Runnable onDone) {
        if (output.getText().length() > 0) output.append("\\n\\n");
        final String s = text;
        final int[] i = {0};
        Timer t = new Timer(delayMs, null);
        t.addActionListener(ev -> {
            if (i[0] < s.length()) {
                output.append(String.valueOf(s.charAt(i[0]++)));
                output.setCaretPosition(output.getDocument().getLength());
            } else {
                ((Timer)ev.getSource()).stop();
                if (onDone != null) onDone.run();
            }
        });
        t.start();
    }

    private void disableButtons() { for (AbstractButton b : allButtons) b.setEnabled(false); }
    private void enableButtons() { for (AbstractButton b : allButtons) b.setEnabled(true); }

    // ---- Dynamic scene detection + fade ----
    private void updateImage(String desc) {
        if (desc == null) return;
        String lower = desc.toLowerCase();
        String path = "images/forest_castle.png";
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
        backgroundPanel.fadeTo(path, FADE_DURATION_MS);
    }

    // ---- Custom UI components ----
    private static class RoundButton extends JButton {
        private float shimmer = 0f;
        private Timer shimmerTimer;
        public RoundButton(String text) {
            super(text);
            setPreferredSize(new Dimension(100,100));
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setForeground(new Color(250,250,250));
            setFont(getFont().deriveFont(Font.BOLD, 16f));
            setUI(new BasicButtonUI());
            setOpaque(false);
            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { startShimmer(); }
                @Override public void mouseExited(MouseEvent e) { stopShimmer(); }
            });
        }
        private void startShimmer() {
            stopShimmer();
            shimmer = 1f;
            shimmerTimer = new Timer(20, e -> {
                shimmer -= 0.1f;
                if (shimmer <= 0f) { shimmer = 0f; ((Timer)e.getSource()).stop(); }
                repaint();
            });
            shimmerTimer.start();
        }
        private void stopShimmer() { if (shimmerTimer!=null) shimmerTimer.stop(); shimmer = 0f; repaint(); }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight();
            g2.setColor(new Color(107,142,35,180));
            g2.fillOval(0,0,w,h);
            g2.setColor(new Color(90,120,30,200));
            g2.setStroke(new BasicStroke(2f));
            g2.drawOval(1,1,w-2,h-2);
            if (shimmer > 0f) {
                float alpha = Math.min(0.6f, shimmer * 0.6f);
                g2.setPaint(new RadialGradientPaint(new Point(w/2, h/2), Math.max(w,h)/2f,
                    new float[]{0f,1f}, new Color[]{new Color(255,215,120,(int)(alpha*255)), new Color(255,215,120,0)}));
                g2.fillOval(0,0,w,h);
            }
            FontMetrics fm = g2.getFontMetrics();
            int tw = fm.stringWidth(getText());
            int th = fm.getAscent();
            g2.setColor(Color.WHITE);
            g2.drawString(getText(), (w - tw)/2, (h + th/2)/2 + 20);
            g2.dispose();
        }
    }

    private static class SoftButton extends JButton {
        private float shimmer = 0f;
        private Timer shimmerTimer;
        public SoftButton(String text) {
            super(text);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setForeground(new Color(250,250,250));
            setFont(getFont().deriveFont(Font.BOLD, 13f));
            setUI(new BasicButtonUI());
            setOpaque(false);
            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { startShimmer(); }
                @Override public void mouseExited(MouseEvent e) { stopShimmer(); }
            });
        }
        private void startShimmer() {
            stopShimmer();
            shimmer = 1f;
            shimmerTimer = new Timer(20, e -> {
                shimmer -= 0.1f;
                if (shimmer <= 0f) { shimmer = 0f; ((Timer)e.getSource()).stop(); }
                repaint();
            });
            shimmerTimer.start();
        }
        private void stopShimmer() { if (shimmerTimer!=null) shimmerTimer.stop(); shimmer = 0f; repaint(); }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight();
            g2.setColor(new Color(107,142,35,180));
            g2.fillRoundRect(0,0,w,h,16,16);
            g2.setColor(new Color(90,120,30,200));
            g2.setStroke(new BasicStroke(2f));
            g2.drawRoundRect(1,1,w-2,h-2,16,16);
            if (shimmer > 0f) {
                float alpha = Math.min(0.6f, shimmer * 0.6f);
                g2.setPaint(new RadialGradientPaint(new Point(w/2, h/2), Math.max(w,h)/2f,
                    new float[]{0f,1f}, new Color[]{new Color(255,215,120,(int)(alpha*255)), new Color(255,215,120,0)}));
                g2.fillRoundRect(0,0,w,h,16,16);
            }
            FontMetrics fm = g2.getFontMetrics();
            int tw = fm.stringWidth(getText());
            int th = fm.getAscent();
            g2.setColor(Color.WHITE);
            g2.drawString(getText(), (w - tw)/2, (h + th)/2);
            g2.dispose();
        }
    }

    // Background panel with crossfade
    private static class FadeImagePanel extends JPanel {
        private Image currentImg;
        private Image nextImg;
        private float alpha = 1f;
        private Timer timer;
        public FadeImagePanel(String initialPath) {
            setPath(initialPath);
            setOpaque(false);
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
        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            if (currentImg != null) g2.drawImage(currentImg, 0, 0, getWidth(), getHeight(), this);
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
