package com.lostrelic;

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

        // Dark fantasy look: apply dark colors
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}
        // Simple dark theme tweaks
        UIManager.put("Panel.background", new Color(30, 30, 35));
        UIManager.put("TextArea.background", new Color(20, 20, 25));
        UIManager.put("TextArea.foreground", new Color(220, 220, 230));
        UIManager.put("Label.foreground", new Color(220, 220, 230));
        UIManager.put("Button.background", new Color(50, 50, 60));
        UIManager.put("Button.foreground", new Color(220, 220, 230));

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLayout(new BorderLayout(8,8));

        output = new JTextArea();
        output.setEditable(false);
        output.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        output.setLineWrap(true);
        output.setWrapStyleWord(true);
        output.setBackground(new Color(10,10,12));
        output.setForeground(new Color(220,220,230));
        JScrollPane scroll = new JScrollPane(output);
        scroll.setBorder(BorderFactory.createEmptyBorder(8,8,8,8));
        add(scroll, BorderLayout.CENTER);

        JPanel right = new JPanel();
        right.setPreferredSize(new Dimension(240, 0));
        right.setLayout(new BorderLayout(6,6));
        right.setBackground(new Color(30,30,35));

        hpLabel = new JLabel();
        hpLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        hpLabel.setHorizontalAlignment(SwingConstants.CENTER);
        right.add(hpLabel, BorderLayout.NORTH);

        inventoryModel = new DefaultListModel<>();
        JList<String> invList = new JList<>(inventoryModel);
        invList.setBackground(new Color(18,18,20));
        invList.setForeground(new Color(220,220,230));
        right.add(new JScrollPane(invList), BorderLayout.CENTER);

        JPanel actions = new JPanel(new GridLayout(0,1,6,6));
        actions.setBackground(new Color(30,30,35));
        String[] btns = {"North","South","East","West","Attack","Solve","Pick Up","Use Item","Equip","Inventory"};
        for (String b : btns) {
            JButton btn = new JButton(b);
            btn.setBackground(new Color(60,50,60));
            btn.addActionListener(new ButtonHandler());
            actions.add(btn);
        }
        right.add(actions, BorderLayout.SOUTH);

        add(right, BorderLayout.EAST);

        // display starting room
        refreshStatus();
        append(game.getPlayer().getCurrentRoom().getDescription());

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void append(String s) {
        output.append(s + "\n\n");
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
            String cmd = ((JButton)e.getSource()).getText();
            switch (cmd) {
                case "North": move("north"); break;
                case "South": move("south"); break;
                case "East": move("east"); break;
                case "West": move("west"); break;
                case "Attack": append(game.attackEnemy()); break;
                case "Solve": {
                    String ans = JOptionPane.showInputDialog(GameGUI.this, "Enter riddle answer:");
                    if (ans != null) append(game.solvePuzzle(ans));
                    break;
                }
                case "Pick Up": {
                    String item = JOptionPane.showInputDialog(GameGUI.this, "Item name to pick up:");
                    if (item != null) append(game.pickUpItem(item));
                    break;
                }
                case "Use Item": {
                    String item = JOptionPane.showInputDialog(GameGUI.this, "Item name to use:");
                    if (item != null) append(game.useItem(item));
                    break;
                }
                case "Equip": {
                    String item = JOptionPane.showInputDialog(GameGUI.this, "Item name to equip (weapon/armor):");
                    if (item != null) append(game.useItem(item));
                    break;
                }
                case "Inventory": {
                    StringBuilder sb = new StringBuilder("Inventory:\n");
                    for (Item it : game.getPlayer().getInventory()) sb.append("- ").append(it.getName()).append("\n");
                    append(sb.toString());
                    break;
                }
            }
            refreshStatus();
        }
    }
}
