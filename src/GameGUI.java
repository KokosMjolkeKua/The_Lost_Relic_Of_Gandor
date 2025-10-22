import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameGUI extends JFrame {
    private final Game game;
    private final JTextArea output;
    private final JLabel hpLabel;
    private final DefaultListModel<String> inventoryModel;

    public GameGUI(Game game) {
        super("The Lost Relic of Galdor");
        this.game = game;

        // Set default look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLayout(new BorderLayout(8, 8));

        // Main output area
        output = new JTextArea();
        output.setEditable(false);
        output.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        output.setLineWrap(true);
        output.setWrapStyleWord(true);
        output.setBackground(new Color(20, 20, 20)); // dark grey/black
        output.setForeground(Color.WHITE);           // white text
        output.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));

        JScrollPane scroll = new JScrollPane(output);
        scroll.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        add(scroll, BorderLayout.CENTER);

        // Right panel for status and actions
        JPanel right = new JPanel();
        right.setPreferredSize(new Dimension(260, 0));
        right.setLayout(new BorderLayout(6, 6));
        right.setBackground(new Color(30, 30, 35)); // dark panel background

        // HP label
        hpLabel = new JLabel();
        hpLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        hpLabel.setHorizontalAlignment(SwingConstants.CENTER);
        hpLabel.setForeground(Color.WHITE);
        right.add(hpLabel, BorderLayout.NORTH);

        // Inventory list
        inventoryModel = new DefaultListModel<>();
        JList<String> invList = new JList<>(inventoryModel);
        invList.setBackground(new Color(20, 20, 20));
        invList.setForeground(Color.WHITE);
        invList.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        right.add(new JScrollPane(invList), BorderLayout.CENTER);

        // Action buttons
        JPanel actions = new JPanel(new GridLayout(0, 1, 6, 6));
        actions.setBackground(new Color(30, 30, 35));
        String[] btns = {
                "North", "South", "East", "West",
                "Attack", "Solve Puzzle", "Pick Up", "Use Item", "Equip", "Inventory"
        };
        for (String b : btns) {
            JButton btn = new JButton(b);
            btn.setBackground(Color.WHITE);
            btn.setForeground(Color.BLACK);
            btn.setFocusPainted(false);
            btn.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            btn.addActionListener(new ButtonHandler());
            actions.add(btn);
        }
        right.add(actions, BorderLayout.SOUTH);
        add(right, BorderLayout.EAST);

        // Display starting room
        refreshStatus();
        append(game.getPlayer().getCurrentRoom().getDescription());

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void append(String text) {
        output.append(text + "\n\n");
        output.setCaretPosition(output.getDocument().getLength());
    }

    private void refreshStatus() {
        hpLabel.setText("HP: " + game.getPlayer().getHealth());
        inventoryModel.clear();
        for (Item it : game.getPlayer().getInventory()) inventoryModel.addElement(it.getName());
    }

    private class ButtonHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            Object src = e.getSource();
            if (!(src instanceof JButton)) return;

            String cmd = ((JButton) src).getText();

            switch (cmd) {
                // Movement buttons
                case "North": append(game.movePlayer("north")); break;
                case "South": append(game.movePlayer("south")); break;
                case "East": append(game.movePlayer("east")); break;
                case "West": append(game.movePlayer("west")); break;

                // Combat
                case "Attack":
                    append(game.attackEnemy());
                    break;

                // Puzzle solving
                case "Solve Puzzle":
                    String ans = JOptionPane.showInputDialog(GameGUI.this, "Enter riddle answer:");
                    if (ans != null) append(game.solvePuzzle(ans));
                    break;

                // Item interactions
                case "Pick Up":
                    String itemName = JOptionPane.showInputDialog(GameGUI.this, "Enter item name to pick up:");
                    if (itemName != null) append(game.pickUpItem(itemName));
                    break;

                case "Use Item":
                case "Equip":
                    String useItem = JOptionPane.showInputDialog(GameGUI.this, "Enter item name to use/equip:");
                    if (useItem != null) append(game.useItem(useItem));
                    break;

                // Inventory display
                case "Inventory":
                    displayInventory();
                    break;

                default:
                    System.out.println("Unknown action: " + cmd);
            }

            refreshStatus();
        }

        private void displayInventory() {
            StringBuilder sb = new StringBuilder("Inventory:\n");
            for (Item it : game.getPlayer().getInventory()) {
                sb.append("- ").append(it.getName()).append("\n");
            }
            append(sb.toString());
        }
    }
}